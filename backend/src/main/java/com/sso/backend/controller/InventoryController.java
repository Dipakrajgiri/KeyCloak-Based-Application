package com.sso.backend.controller;

import com.sso.backend.dto.InventoryDTO;
import com.sso.backend.service.InventoryService;
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
@RequestMapping("/api/inventories")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    /** GET /api/inventories — List all inventories (Global Read allowed by UMA) */
    @GetMapping
    public ResponseEntity<List<InventoryDTO.Response>> getAll() {
        return ResponseEntity.ok(inventoryService.getAllInventories());
    }

    /** GET /api/inventories/{id} — Get a specific inventory */
    @GetMapping("/{id}")
    public ResponseEntity<InventoryDTO.Response> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(inventoryService.getInventory(id));
    }

    /** POST /api/inventories — Create a new inventory */
    @PostMapping
    public ResponseEntity<InventoryDTO.Response> create(
            @Valid @RequestBody InventoryDTO.CreateRequest request,
            @AuthenticationPrincipal OAuth2User jwt,
            @RegisteredOAuth2AuthorizedClient("keycloak") OAuth2AuthorizedClient client) {
        String userId = jwt.getAttribute("sub");
        // We pass the user's access token so the Service can register the UMA resource on their behalf if needed.
        InventoryDTO.Response created = inventoryService.createInventory(request, userId, client.getAccessToken().getTokenValue());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /** PUT /api/inventories/{id} — Update an inventory */
    @PutMapping("/{id}")
    public ResponseEntity<InventoryDTO.Response> update(
            @PathVariable Long id,
            @Valid @RequestBody InventoryDTO.CreateRequest request,
            @AuthenticationPrincipal OAuth2User jwt,
            @RegisteredOAuth2AuthorizedClient("keycloak") OAuth2AuthorizedClient client) {
        String userId = jwt.getAttribute("sub");
        return ResponseEntity.ok(inventoryService.updateInventory(id, request, userId, client.getAccessToken().getTokenValue()));
    }

    /** DELETE /api/inventories/{id} — Delete an inventory */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal OAuth2User jwt,
            @RegisteredOAuth2AuthorizedClient("keycloak") OAuth2AuthorizedClient client) {
        String userId = jwt.getAttribute("sub");
        inventoryService.deleteInventory(id, userId, client.getAccessToken().getTokenValue());
        return ResponseEntity.noContent().build();
    }
}
