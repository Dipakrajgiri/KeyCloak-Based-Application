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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final AuthzClient authzClient;
    private final RestTemplate restTemplate;

    @Value("${keycloak.issuer-uri}")
    private String keycloakIssuerUri;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    /**
     * Get all inventories (All authenticated users can see all inventories)
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
     * Create a new inventory for the user.
     * Registers it in Keycloak with CLIENT as owner (not user).
     * ownerManagedAccess=false so users cannot share via Account Console.
     */
    public InventoryDTO.Response createInventory(InventoryDTO.CreateRequest request, String userId) {
        Inventory inventory = Inventory.builder()
                .name(request.name())
                .description(request.description())
                .userId(userId)  // DB-level owner tracking
                .build();

        Inventory saved = inventoryRepository.save(inventory);

        // Register the resource in Keycloak — Client is the owner, NOT the user
        try {
            ResourceRepresentation resource = new ResourceRepresentation();
            resource.setName("Inventory-" + saved.getId());
            resource.setType("Inventory");
            resource.addScope(new ScopeRepresentation("inventory:view"));
            resource.addScope(new ScopeRepresentation("inventory:edit"));
            resource.addScope(new ScopeRepresentation("inventory:delete"));
            // NO setOwner(userId) — Client owns it in Keycloak
            // NO setOwnerManagedAccess(true) — Users cannot share via Account Console
            resource.setOwnerManagedAccess(false);

            authzClient.protection().resource().create(resource);
        } catch (Exception e) {
            inventoryRepository.delete(saved); // Rollback
            throw new RuntimeException("Failed to register resource in Keycloak: " + e.getMessage(), e);
        }

        return toResponse(saved);
    }

    /**
     * Update an existing inventory (owner check in DB + Keycloak RPT for non-owners)
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
     * Delete an inventory (owner check in DB + Keycloak RPT for non-owners)
     */
    public void deleteInventory(Long id, String userId, String accessToken) {
        checkPermission("Inventory-" + id, "inventory:delete", accessToken, userId);

        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory not found"));

        inventoryRepository.delete(inventory);

        // Clean up the Keycloak Resource
        try {
            ResourceRepresentation resource = authzClient.protection().resource().findByName("Inventory-" + id);
            if (resource != null) {
                authzClient.protection().resource().delete(resource.getId());
            }
        } catch (Exception e) {
            System.err.println("Warning: Failed to delete Keycloak resource: " + e.getMessage());
        }
    }

    /**
     * Share inventory with another user.
     * Only the DB-level owner can call this. Backend uses PAT to create policy in Keycloak.
     * User does NOT interact with Keycloak directly — Backend manages it centrally.
     *
     * @param inventoryId  The inventory to share
     * @param targetUserId The Keycloak user ID to grant access to
     * @param scopes       Scopes to grant e.g. ["inventory:view", "inventory:edit"]
     * @param requesterId  The user requesting the share (must be DB owner)
     */
    public void shareInventory(Long inventoryId, String targetUserId, List<String> scopes, String requesterId) {
        // 1. Verify the requester is the DB-level owner
        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new RuntimeException("Inventory not found"));

        if (!inventory.getUserId().equals(requesterId)) {
            throw new RuntimeException("Access Denied: Only the owner can share this inventory.");
        }

        // 2. Find the Keycloak Resource ID by name
        ResourceRepresentation resource = authzClient.protection().resource().findByName("Inventory-" + inventoryId);
        if (resource == null) {
            throw new RuntimeException("Keycloak resource not found for Inventory-" + inventoryId);
        }

        // 3. Get a fresh PAT using Client Credentials
        String pat = getFreshPAT();

        // 4. Create a UMA Policy for the target user on this resource via Protection API
        // POST /realms/{realm}/authz/protection/uma-policy/{resourceId}
        String policyUrl = keycloakIssuerUri + "/authz/protection/uma-policy/" + resource.getId();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(pat);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> policyBody = Map.of(
                "name", "Share-Inventory-" + inventoryId + "-to-" + targetUserId,
                "description", "Granted by owner via application",
                "users", List.of(targetUserId),
                "scopes", scopes
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(policyBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(policyUrl, entity, String.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Keycloak rejected policy creation: " + response.getBody());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to create permission policy in Keycloak: " + e.getMessage(), e);
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
     * Check permission for non-owners via Keycloak RPT evaluation.
     * Bypasses check for DB-level owners.
     */
    public void checkPermission(String resourceName, String scope, String accessToken, String userId) {
        Long inventoryId = Long.parseLong(resourceName.split("-")[1]);
        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new RuntimeException("Inventory not found"));

        // Owners always have full access — checked against our DB
        if (inventory.getUserId().equals(userId)) {
            return;
        }

        // For non-owners, ask Keycloak to evaluate their RPT
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
            if (e.getMessage() != null && e.getMessage().contains("Access Denied")) {
                throw e;
            }
            throw new RuntimeException("Authorization check failed: " + e.getMessage(), e);
        }
    }

    /**
     * Obtain a fresh Protection API Token (PAT) using Client Credentials.
     * This is the Service Account token with uma_protection role.
     */
    private String getFreshPAT() {
        String tokenUrl = keycloakIssuerUri + "/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "client_credentials");
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(form, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, entity, Map.class);
            if (response.getBody() == null || !response.getBody().containsKey("access_token")) {
                throw new RuntimeException("PAT token response missing access_token");
            }
            return (String) response.getBody().get("access_token");
        } catch (Exception e) {
            throw new RuntimeException("Failed to obtain PAT from Keycloak: " + e.getMessage(), e);
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
