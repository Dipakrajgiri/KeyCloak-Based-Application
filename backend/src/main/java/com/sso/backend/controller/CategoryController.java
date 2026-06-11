package com.sso.backend.controller;

import com.sso.backend.dto.CategoryDTO;
import com.sso.backend.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventories/{inventoryId}/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final OAuth2AuthorizedClientService authorizedClientService;

    private String getAccessToken(OAuth2AuthenticationToken auth) {
        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                auth.getAuthorizedClientRegistrationId(), auth.getName());
        if (client == null || client.getAccessToken() == null) {
            throw new RuntimeException("Unauthorized: No active access token found");
        }
        return client.getAccessToken().getTokenValue();
    }

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
            OAuth2AuthenticationToken auth) {
        String userId = jwt.getAttribute("sub");
        CategoryDTO.Response created = categoryService.createCategory(inventoryId, request, userId, getAccessToken(auth));
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTO.Response> update(
            @PathVariable Long inventoryId,
            @PathVariable Long id,
            @Valid @RequestBody CategoryDTO.CreateRequest request,
            @AuthenticationPrincipal OAuth2User jwt,
            OAuth2AuthenticationToken auth) {
        String userId = jwt.getAttribute("sub");
        return ResponseEntity.ok(categoryService.updateCategory(inventoryId, id, request, userId, getAccessToken(auth)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long inventoryId,
            @PathVariable Long id,
            @AuthenticationPrincipal OAuth2User jwt,
            OAuth2AuthenticationToken auth) {
        String userId = jwt.getAttribute("sub");
        categoryService.deleteCategory(inventoryId, id, userId, getAccessToken(auth));
        return ResponseEntity.noContent().build();
    }
}
