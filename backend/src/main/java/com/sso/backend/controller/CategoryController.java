package com.sso.backend.controller;

import com.sso.backend.dto.CategoryDTO;
import com.sso.backend.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Category Controller — Nested under inventories
 *
 * REST PATTERN (nested resources):
 * GET    /api/inventories/{inventoryId}/categories           → List categories
 * GET    /api/inventories/{inventoryId}/categories/{id}      → Get one
 * POST   /api/inventories/{inventoryId}/categories           → Create
 * PUT    /api/inventories/{inventoryId}/categories/{id}      → Update
 * DELETE /api/inventories/{inventoryId}/categories/{id}      → Delete
 */
@RestController
@RequestMapping("/api/inventories/{inventoryId}/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryDTO.Response>> getAll(
            @PathVariable Long inventoryId,
            @AuthenticationPrincipal OAuth2User jwt) {
        return ResponseEntity.ok(categoryService.getCategories(inventoryId, jwt.getAttribute("sub")));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO.Response> getOne(
            @PathVariable Long inventoryId,
            @PathVariable Long id,
            @AuthenticationPrincipal OAuth2User jwt) {
        return ResponseEntity.ok(categoryService.getCategory(inventoryId, id, jwt.getAttribute("sub")));
    }

    @PostMapping
    public ResponseEntity<CategoryDTO.Response> create(
            @PathVariable Long inventoryId,
            @Valid @RequestBody CategoryDTO.CreateRequest request,
            @AuthenticationPrincipal OAuth2User jwt) {
        CategoryDTO.Response created = categoryService.createCategory(inventoryId, request, jwt.getAttribute("sub"));
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTO.Response> update(
            @PathVariable Long inventoryId,
            @PathVariable Long id,
            @Valid @RequestBody CategoryDTO.CreateRequest request,
            @AuthenticationPrincipal OAuth2User jwt) {
        return ResponseEntity.ok(categoryService.updateCategory(inventoryId, id, request, jwt.getAttribute("sub")));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long inventoryId,
            @PathVariable Long id,
            @AuthenticationPrincipal OAuth2User jwt) {
        categoryService.deleteCategory(inventoryId, id, jwt.getAttribute("sub"));
        return ResponseEntity.noContent().build();
    }
}
