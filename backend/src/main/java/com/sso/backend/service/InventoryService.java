package com.sso.backend.service;

import com.sso.backend.dto.InventoryDTO;
import com.sso.backend.entity.Inventory;
import com.sso.backend.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Inventory Service — Business Logic Layer
 *
 * WHY A SERVICE LAYER?
 * - Controller handles HTTP requests/responses
 * - Service handles BUSINESS LOGIC
 * - Repository handles DATABASE operations
 *
 * This separation (Controller → Service → Repository) is called the "3-tier architecture"
 * and is the standard pattern in Spring Boot applications.
 *
 * @Service — Marks this as a Spring-managed service bean
 * @RequiredArgsConstructor — Lombok generates a constructor with all final fields
 * @Transactional — Wraps methods in a database transaction
 */
@Service
@RequiredArgsConstructor
@Transactional
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    /**
     * Get all inventories for the authenticated user
     * We filter by userId to ensure users only see their own data
     */
    public List<InventoryDTO.Response> getUserInventories(String userId) {
        return inventoryRepository.findByUserId(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Get a single inventory by ID (only if it belongs to the user)
     */
    public InventoryDTO.Response getInventory(Long id, String userId) {
        Inventory inventory = inventoryRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Inventory not found or access denied"));
        return toResponse(inventory);
    }

    /**
     * Create a new inventory for the user
     */
    public InventoryDTO.Response createInventory(InventoryDTO.CreateRequest request, String userId) {
        Inventory inventory = Inventory.builder()
                .name(request.name())
                .description(request.description())
                .userId(userId)
                .build();

        Inventory saved = inventoryRepository.save(inventory);
        return toResponse(saved);
    }

    /**
     * Update an existing inventory (only if it belongs to the user)
     */
    public InventoryDTO.Response updateInventory(Long id, InventoryDTO.CreateRequest request, String userId) {
        Inventory inventory = inventoryRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Inventory not found or access denied"));

        inventory.setName(request.name());
        inventory.setDescription(request.description());

        Inventory saved = inventoryRepository.save(inventory);
        return toResponse(saved);
    }

    /**
     * Delete an inventory (only if it belongs to the user)
     * Cascade delete will also remove all categories and items within it
     */
    public void deleteInventory(Long id, String userId) {
        Inventory inventory = inventoryRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Inventory not found or access denied"));
        inventoryRepository.delete(inventory);
    }

    /**
     * Helper: Get the raw entity (used by CategoryService to verify ownership)
     */
    public Inventory getInventoryEntity(Long id, String userId) {
        return inventoryRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Inventory not found or access denied"));
    }

    /**
     * Convert Entity → Response DTO
     * We do this to control what data gets sent to the frontend
     */
    private InventoryDTO.Response toResponse(Inventory inventory) {
        return new InventoryDTO.Response(
                inventory.getId(),
                inventory.getName(),
                inventory.getDescription(),
                inventory.getCategories() != null ? inventory.getCategories().size() : 0,
                inventory.getCreatedAt() != null ? inventory.getCreatedAt().toString() : null,
                inventory.getUpdatedAt() != null ? inventory.getUpdatedAt().toString() : null
        );
    }
}
