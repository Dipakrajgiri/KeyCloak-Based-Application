package com.sso.backend.controller;

import com.sso.backend.dto.ItemDTO;
import com.sso.backend.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Item Controller — Nested under inventories and categories
 *
 * REST PATTERN (deeply nested):
 * GET    /api/inventories/{invId}/categories/{catId}/items         → List items
 * GET    /api/inventories/{invId}/categories/{catId}/items/{id}    → Get one
 * POST   /api/inventories/{invId}/categories/{catId}/items         → Create
 * PUT    /api/inventories/{invId}/categories/{catId}/items/{id}    → Update
 * DELETE /api/inventories/{invId}/categories/{catId}/items/{id}    → Delete
 */
@RestController
@RequestMapping("/api/inventories/{inventoryId}/categories/{categoryId}/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public ResponseEntity<List<ItemDTO.Response>> getAll(
            @PathVariable Long inventoryId,
            @PathVariable Long categoryId,
            @AuthenticationPrincipal OAuth2User jwt) {
        return ResponseEntity.ok(itemService.getItems(inventoryId, categoryId, jwt.getAttribute("sub")));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemDTO.Response> getOne(
            @PathVariable Long inventoryId,
            @PathVariable Long categoryId,
            @PathVariable Long id,
            @AuthenticationPrincipal OAuth2User jwt) {
        return ResponseEntity.ok(itemService.getItem(inventoryId, categoryId, id, jwt.getAttribute("sub")));
    }

    @PostMapping
    public ResponseEntity<ItemDTO.Response> create(
            @PathVariable Long inventoryId,
            @PathVariable Long categoryId,
            @Valid @RequestBody ItemDTO.CreateRequest request,
            @AuthenticationPrincipal OAuth2User jwt) {
        ItemDTO.Response created = itemService.createItem(inventoryId, categoryId, request, jwt.getAttribute("sub"));
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ItemDTO.Response> update(
            @PathVariable Long inventoryId,
            @PathVariable Long categoryId,
            @PathVariable Long id,
            @Valid @RequestBody ItemDTO.CreateRequest request,
            @AuthenticationPrincipal OAuth2User jwt) {
        return ResponseEntity.ok(itemService.updateItem(inventoryId, categoryId, id, request, jwt.getAttribute("sub")));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long inventoryId,
            @PathVariable Long categoryId,
            @PathVariable Long id,
            @AuthenticationPrincipal OAuth2User jwt) {
        itemService.deleteItem(inventoryId, categoryId, id, jwt.getAttribute("sub"));
        return ResponseEntity.noContent().build();
    }
}
