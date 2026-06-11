package com.sso.backend.service;

import com.sso.backend.dto.ItemDTO;
import com.sso.backend.entity.Category;
import com.sso.backend.entity.Item;
import com.sso.backend.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemService {

    private final ItemRepository itemRepository;
    private final CategoryService categoryService;
    private final InventoryService inventoryService; // To check permissions

    public List<ItemDTO.Response> getItems(Long inventoryId, Long categoryId) {
        categoryService.getCategoryEntity(inventoryId, categoryId);

        return itemRepository.findByCategoryId(categoryId).stream()
                .map(this::toResponse)
                .toList();
    }

    public ItemDTO.Response getItem(Long inventoryId, Long categoryId, Long itemId) {
        categoryService.getCategoryEntity(inventoryId, categoryId);

        Item item = itemRepository.findByIdAndCategoryId(itemId, categoryId)
                .orElseThrow(() -> new RuntimeException("Item not found"));
        return toResponse(item);
    }

    public ItemDTO.Response createItem(Long inventoryId, Long categoryId,
                                        ItemDTO.CreateRequest request, String userId, String accessToken) {
        inventoryService.checkPermission("Inventory-" + inventoryId, "inventory:edit", accessToken, userId);
        Category category = categoryService.getCategoryEntity(inventoryId, categoryId);

        Item item = Item.builder()
                .name(request.name())
                .description(request.description())
                .quantity(request.quantity())
                .price(request.price() != null ? request.price() : 0.0)
                .category(category)
                .build();

        Item saved = itemRepository.save(item);
        return toResponse(saved);
    }

    public ItemDTO.Response updateItem(Long inventoryId, Long categoryId, Long itemId,
                                        ItemDTO.CreateRequest request, String userId, String accessToken) {
        inventoryService.checkPermission("Inventory-" + inventoryId, "inventory:edit", accessToken, userId);
        categoryService.getCategoryEntity(inventoryId, categoryId);

        Item item = itemRepository.findByIdAndCategoryId(itemId, categoryId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        item.setName(request.name());
        item.setDescription(request.description());
        item.setQuantity(request.quantity());
        item.setPrice(request.price() != null ? request.price() : 0.0);

        Item saved = itemRepository.save(item);
        return toResponse(saved);
    }

    public void deleteItem(Long inventoryId, Long categoryId, Long itemId, String userId, String accessToken) {
        inventoryService.checkPermission("Inventory-" + inventoryId, "inventory:delete", accessToken, userId);
        categoryService.getCategoryEntity(inventoryId, categoryId);

        Item item = itemRepository.findByIdAndCategoryId(itemId, categoryId)
                .orElseThrow(() -> new RuntimeException("Item not found"));
        itemRepository.delete(item);
    }

    private ItemDTO.Response toResponse(Item item) {
        return new ItemDTO.Response(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getQuantity(),
                item.getPrice(),
                item.getCategory().getId(),
                item.getCreatedAt() != null ? item.getCreatedAt().toString() : null,
                item.getUpdatedAt() != null ? item.getUpdatedAt().toString() : null
        );
    }
}
