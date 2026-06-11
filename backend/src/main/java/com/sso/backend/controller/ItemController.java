package com.sso.backend.controller;

import com.sso.backend.dto.ItemDTO;
import com.sso.backend.service.ItemService;
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
@RequestMapping("/api/inventories/{inventoryId}/categories/{categoryId}/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
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
            OAuth2AuthenticationToken auth) {
        String userId = jwt.getAttribute("sub");
        ItemDTO.Response created = itemService.createItem(inventoryId, categoryId, request, userId, getAccessToken(auth));
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ItemDTO.Response> update(
            @PathVariable Long inventoryId,
            @PathVariable Long categoryId,
            @PathVariable Long id,
            @Valid @RequestBody ItemDTO.CreateRequest request,
            @AuthenticationPrincipal OAuth2User jwt,
            OAuth2AuthenticationToken auth) {
        String userId = jwt.getAttribute("sub");
        return ResponseEntity.ok(itemService.updateItem(inventoryId, categoryId, id, request, userId, getAccessToken(auth)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long inventoryId,
            @PathVariable Long categoryId,
            @PathVariable Long id,
            @AuthenticationPrincipal OAuth2User jwt,
            OAuth2AuthenticationToken auth) {
        String userId = jwt.getAttribute("sub");
        itemService.deleteItem(inventoryId, categoryId, id, userId, getAccessToken(auth));
        return ResponseEntity.noContent().build();
    }
}
