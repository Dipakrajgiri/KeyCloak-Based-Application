package com.sso.backend.service;

import com.sso.backend.dto.CategoryDTO;
import com.sso.backend.entity.Category;
import com.sso.backend.entity.Inventory;
import com.sso.backend.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final InventoryService inventoryService;

    public List<CategoryDTO.Response> getCategories(Long inventoryId) {
        inventoryService.getInventoryEntity(inventoryId);

        return categoryRepository.findByInventoryId(inventoryId).stream()
                .map(this::toResponse)
                .toList();
    }

    public CategoryDTO.Response getCategory(Long inventoryId, Long categoryId) {
        inventoryService.getInventoryEntity(inventoryId);

        Category category = categoryRepository.findByIdAndInventoryId(categoryId, inventoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        return toResponse(category);
    }

    public CategoryDTO.Response createCategory(Long inventoryId, CategoryDTO.CreateRequest request, String userId, String accessToken) {
        inventoryService.checkPermission("Inventory-" + inventoryId, "inventory:edit", accessToken, userId);
        Inventory inventory = inventoryService.getInventoryEntity(inventoryId);

        Category category = Category.builder()
                .name(request.name())
                .description(request.description())
                .inventory(inventory)
                .build();

        Category saved = categoryRepository.save(category);
        return toResponse(saved);
    }

    public CategoryDTO.Response updateCategory(Long inventoryId, Long categoryId,
                                                CategoryDTO.CreateRequest request, String userId, String accessToken) {
        inventoryService.checkPermission("Inventory-" + inventoryId, "inventory:edit", accessToken, userId);
        inventoryService.getInventoryEntity(inventoryId);

        Category category = categoryRepository.findByIdAndInventoryId(categoryId, inventoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        category.setName(request.name());
        category.setDescription(request.description());

        Category saved = categoryRepository.save(category);
        return toResponse(saved);
    }

    public void deleteCategory(Long inventoryId, Long categoryId, String userId, String accessToken) {
        inventoryService.checkPermission("Inventory-" + inventoryId, "inventory:delete", accessToken, userId);
        inventoryService.getInventoryEntity(inventoryId);

        Category category = categoryRepository.findByIdAndInventoryId(categoryId, inventoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        categoryRepository.delete(category);
    }

    public Category getCategoryEntity(Long inventoryId, Long categoryId) {
        inventoryService.getInventoryEntity(inventoryId);
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
