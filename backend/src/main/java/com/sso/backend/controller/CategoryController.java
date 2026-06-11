package com.sso.backend.controller;

import com.sso.backend.dto.CategoryDTO;
import com.sso.backend.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventories/{inventoryId}/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryDTO.Response>> getAll(
            @PathVariable Long inventoryId) {
        return ResponseEntity.ok(categoryService.getCategories(inventoryId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO.Response> getOne(
            @PathVariable Long inventoryId,
            @PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategory(inventoryId, id));
    }

    @PostMapping
    public ResponseEntity<CategoryDTO.Response> create(
            @PathVariable Long inventoryId,
            @Valid @RequestBody CategoryDTO.CreateRequest request,
            @AuthenticationPrincipal OAuth2User jwt,
            @RegisteredOAuth2AuthorizedClient("keycloak") OAuth2AuthorizedClient client) {
        String userId = jwt.getAttribute("sub");
        CategoryDTO.Response created = categoryService.createCategory(inventoryId, request, userId, client.getAccessToken().getTokenValue());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTO.Response> update(
            @PathVariable Long inventoryId,
            @PathVariable Long id,
            @Valid @RequestBody CategoryDTO.CreateRequest request,
            @AuthenticationPrincipal OAuth2User jwt,
            @RegisteredOAuth2AuthorizedClient("keycloak") OAuth2AuthorizedClient client) {
        String userId = jwt.getAttribute("sub");
        return ResponseEntity.ok(categoryService.updateCategory(inventoryId, id, request, userId, client.getAccessToken().getTokenValue()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long inventoryId,
            @PathVariable Long id,
            @AuthenticationPrincipal OAuth2User jwt,
            @RegisteredOAuth2AuthorizedClient("keycloak") OAuth2AuthorizedClient client) {
        String userId = jwt.getAttribute("sub");
        categoryService.deleteCategory(inventoryId, id, userId, client.getAccessToken().getTokenValue());
        return ResponseEntity.noContent().build();
    }
}
