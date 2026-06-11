package com.sso.backend.service;

import com.sso.backend.dto.InventoryDTO;
import com.sso.backend.entity.Inventory;
import com.sso.backend.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.authorization.client.AuthorizationDeniedException;
import org.keycloak.representations.idm.authorization.AuthorizationRequest;
import org.keycloak.representations.idm.authorization.AuthorizationResponse;
import org.keycloak.representations.idm.authorization.ResourceRepresentation;
import org.keycloak.representations.idm.authorization.ScopeRepresentation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final AuthzClient authzClient;

    /**
     * Get all inventories (Global Read allowed by UMA)
     */
    public List<InventoryDTO.Response> getAllInventories() {
        return inventoryRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Get a single inventory by ID
     */
    public InventoryDTO.Response getInventory(Long id) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory not found"));
        return toResponse(inventory);
    }

    /**
     * Create a new inventory for the user and register it in Keycloak
     */
    public InventoryDTO.Response createInventory(InventoryDTO.CreateRequest request, String userId, String accessToken) {
        Inventory inventory = Inventory.builder()
                .name(request.name())
                .description(request.description())
                .userId(userId)
                .build();

        Inventory saved = inventoryRepository.save(inventory);

        // Register the resource in Keycloak UMA
        try {
            ResourceRepresentation resource = new ResourceRepresentation();
            resource.setName("Inventory-" + saved.getId());
            resource.setType("Inventory"); // Matches the type used in Keycloak Policy
            resource.addScope(new ScopeRepresentation("inventory:view"));
            resource.addScope(new ScopeRepresentation("inventory:edit"));
            resource.addScope(new ScopeRepresentation("inventory:delete"));
            resource.setOwner(userId);
            resource.setOwnerManagedAccess(true); // Allows owner to share access!
            
            // The AuthzClient uses its own client credentials to register this resource
            authzClient.protection().resource().create(resource);
        } catch (Exception e) {
            // Rollback inventory creation if Keycloak resource creation fails
            throw new RuntimeException("Failed to register resource in Keycloak: " + e.getMessage(), e);
        }

        return toResponse(saved);
    }

    /**
     * Update an existing inventory (Checks UMA permission first)
     */
    public InventoryDTO.Response updateInventory(Long id, InventoryDTO.CreateRequest request, String userId, String accessToken) {
        checkPermission("Inventory-" + id, "inventory:edit", accessToken, userId);

        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory not found"));

        inventory.setName(request.name());
        inventory.setDescription(request.description());

        Inventory saved = inventoryRepository.save(inventory);
        return toResponse(saved);
    }

    /**
     * Delete an inventory (Checks UMA permission first and deletes Keycloak resource)
     */
    public void deleteInventory(Long id, String userId, String accessToken) {
        checkPermission("Inventory-" + id, "inventory:delete", accessToken, userId);

        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory not found"));
                
        inventoryRepository.delete(inventory);

        try {
            // Clean up the Keycloak Resource
            ResourceRepresentation resource = authzClient.protection().resource().findByName("Inventory-" + id);
            if (resource != null) {
                authzClient.protection().resource().delete(resource.getId());
            }
        } catch (Exception e) {
            System.err.println("Failed to delete Keycloak resource: " + e.getMessage());
        }
    }

    /**
     * Helper: Get the raw entity (used by child services like CategoryService)
     */
    public Inventory getInventoryEntity(Long id) {
        return inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory not found"));
    }
    
    /**
     * Make a request to Keycloak to check if the current user (accessToken)
     * has the required scope on the specific resource.
     * Skips check if the user is the owner of the resource.
     */
    public void checkPermission(String resourceName, String scope, String accessToken, String userId) {
        Long inventoryId = Long.parseLong(resourceName.split("-")[1]);
        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new RuntimeException("Inventory not found"));

        // Bypass UMA check if the user is the owner!
        if (inventory.getUserId().equals(userId)) {
            return;
        }

        try {
            AuthorizationRequest authzRequest = new AuthorizationRequest();
            authzRequest.addPermission(resourceName, scope);
            AuthorizationResponse response = authzClient.authorization(accessToken).authorize(authzRequest);
            if (response == null || response.getToken() == null) {
                throw new RuntimeException("Access Denied: You do not have '" + scope + "' permission for " + resourceName);
            }
        } catch (AuthorizationDeniedException e) {
            throw new RuntimeException("Access Denied: You do not have '" + scope + "' permission for " + resourceName, e);
        } catch (Exception e) {
             throw new RuntimeException("Authorization check failed: " + e.getMessage(), e);
        }
    }

    private InventoryDTO.Response toResponse(Inventory inventory) {
        return new InventoryDTO.Response(
                inventory.getId(),
                inventory.getName(),
                inventory.getDescription(),
                inventory.getUserId(),
                inventory.getCategories() != null ? inventory.getCategories().size() : 0,
                inventory.getCreatedAt() != null ? inventory.getCreatedAt().toString() : null,
                inventory.getUpdatedAt() != null ? inventory.getUpdatedAt().toString() : null
        );
    }
}
