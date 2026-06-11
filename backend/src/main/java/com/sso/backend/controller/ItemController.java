package com.sso.backend.controller;

import com.sso.backend.dto.ItemDTO;
import com.sso.backend.service.ItemService;
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
@RequestMapping("/api/inventories/{inventoryId}/categories/{categoryId}/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public ResponseEntity<List<ItemDTO.Response>> getAll(
            @PathVariable Long inventoryId,
            @PathVariable Long categoryId) {
        return ResponseEntity.ok(itemService.getItems(inventoryId, categoryId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemDTO.Response> getOne(
            @PathVariable Long inventoryId,
            @PathVariable Long categoryId,
            @PathVariable Long id) {
        return ResponseEntity.ok(itemService.getItem(inventoryId, categoryId, id));
    }

    @PostMapping
    public ResponseEntity<ItemDTO.Response> create(
            @PathVariable Long inventoryId,
            @PathVariable Long categoryId,
            @Valid @RequestBody ItemDTO.CreateRequest request,
            @AuthenticationPrincipal OAuth2User jwt,
            @RegisteredOAuth2AuthorizedClient("keycloak") OAuth2AuthorizedClient client) {
        String userId = jwt.getAttribute("sub");
        ItemDTO.Response created = itemService.createItem(inventoryId, categoryId, request, userId, client.getAccessToken().getTokenValue());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ItemDTO.Response> update(
            @PathVariable Long inventoryId,
            @PathVariable Long categoryId,
            @PathVariable Long id,
            @Valid @RequestBody ItemDTO.CreateRequest request,
            @AuthenticationPrincipal OAuth2User jwt,
            @RegisteredOAuth2AuthorizedClient("keycloak") OAuth2AuthorizedClient client) {
        String userId = jwt.getAttribute("sub");
        return ResponseEntity.ok(itemService.updateItem(inventoryId, categoryId, id, request, userId, client.getAccessToken().getTokenValue()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long inventoryId,
            @PathVariable Long categoryId,
            @PathVariable Long id,
            @AuthenticationPrincipal OAuth2User jwt,
            @RegisteredOAuth2AuthorizedClient("keycloak") OAuth2AuthorizedClient client) {
        String userId = jwt.getAttribute("sub");
        itemService.deleteItem(inventoryId, categoryId, id, userId, client.getAccessToken().getTokenValue());
        return ResponseEntity.noContent().build();
    }
}
