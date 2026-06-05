package com.sso.backend.service;

import com.sso.backend.dto.CategoryDTO;
import com.sso.backend.entity.Category;
import com.sso.backend.entity.Inventory;
import com.sso.backend.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Category Service — handles category CRUD within an inventory
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final InventoryService inventoryService;

    /**
     * Get all categories in an inventory
     * First verifies the inventory belongs to the user (security!)
     */
    public List<CategoryDTO.Response> getCategories(Long inventoryId, String userId) {
        // This throws if the inventory doesn't belong to the user
        inventoryService.getInventoryEntity(inventoryId, userId);

        return categoryRepository.findByInventoryId(inventoryId).stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Get a single category
     */
    public CategoryDTO.Response getCategory(Long inventoryId, Long categoryId, String userId) {
        inventoryService.getInventoryEntity(inventoryId, userId);

        Category category = categoryRepository.findByIdAndInventoryId(categoryId, inventoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        return toResponse(category);
    }

    /**
     * Create a new category within an inventory
     */
    public CategoryDTO.Response createCategory(Long inventoryId, CategoryDTO.CreateRequest request, String userId) {
        Inventory inventory = inventoryService.getInventoryEntity(inventoryId, userId);

        Category category = Category.builder()
                .name(request.name())
                .description(request.description())
                .inventory(inventory)
                .build();

        Category saved = categoryRepository.save(category);
        return toResponse(saved);
    }

    /**
     * Update a category
     */
    public CategoryDTO.Response updateCategory(Long inventoryId, Long categoryId,
                                                CategoryDTO.CreateRequest request, String userId) {
        inventoryService.getInventoryEntity(inventoryId, userId);

        Category category = categoryRepository.findByIdAndInventoryId(categoryId, inventoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        category.setName(request.name());
        category.setDescription(request.description());

        Category saved = categoryRepository.save(category);
        return toResponse(saved);
    }

    /**
     * Delete a category (cascade deletes all items within it)
     */
    public void deleteCategory(Long inventoryId, Long categoryId, String userId) {
        inventoryService.getInventoryEntity(inventoryId, userId);

        Category category = categoryRepository.findByIdAndInventoryId(categoryId, inventoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        categoryRepository.delete(category);
    }

    /**
     * Helper: Get the raw entity (used by ItemService)
     */
    public Category getCategoryEntity(Long inventoryId, Long categoryId, String userId) {
        inventoryService.getInventoryEntity(inventoryId, userId);
        return categoryRepository.findByIdAndInventoryId(categoryId, inventoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }

    private CategoryDTO.Response toResponse(Category category) {
        return new CategoryDTO.Response(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getInventory().getId(),
                category.getItems() != null ? category.getItems().size() : 0,
                category.getCreatedAt() != null ? category.getCreatedAt().toString() : null,
                category.getUpdatedAt() != null ? category.getUpdatedAt().toString() : null
        );
    }
}
