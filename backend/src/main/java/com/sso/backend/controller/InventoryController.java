package com.sso.backend.controller;

import com.sso.backend.dto.InventoryDTO;
import com.sso.backend.service.InventoryService;
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
@RequestMapping("/api/inventories")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;
    private final OAuth2AuthorizedClientService authorizedClientService;

    private String getAccessToken(OAuth2AuthenticationToken auth) {
        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                auth.getAuthorizedClientRegistrationId(), auth.getName());
        if (client == null || client.getAccessToken() == null) {
            throw new RuntimeException("Unauthorized: No active access token found");
        }
        return client.getAccessToken().getTokenValue();
    }

    /** GET /api/inventories — List all inventories */
    @GetMapping
    public ResponseEntity<List<InventoryDTO.Response>> getAll() {
        return ResponseEntity.ok(inventoryService.getAllInventories());
    }

    /** GET /api/inventories/{id} — Get a specific inventory */
    @GetMapping("/{id}")
    public ResponseEntity<InventoryDTO.Response> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(inventoryService.getInventory(id));
    }

    /** POST /api/inventories — Create a new inventory (Client owns resource in Keycloak) */
    @PostMapping
    public ResponseEntity<InventoryDTO.Response> create(
            @Valid @RequestBody InventoryDTO.CreateRequest request,
            @AuthenticationPrincipal OAuth2User jwt) {
        String userId = jwt.getAttribute("sub");
        InventoryDTO.Response created = inventoryService.createInventory(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /** PUT /api/inventories/{id} — Update an inventory */
    @PutMapping("/{id}")
    public ResponseEntity<InventoryDTO.Response> update(
            @PathVariable Long id,
            @Valid @RequestBody InventoryDTO.CreateRequest request,
            @AuthenticationPrincipal OAuth2User jwt,
            OAuth2AuthenticationToken auth) {
        String userId = jwt.getAttribute("sub");
        return ResponseEntity.ok(inventoryService.updateInventory(id, request, userId, getAccessToken(auth)));
    }

    /** DELETE /api/inventories/{id} — Delete an inventory */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal OAuth2User jwt,
            OAuth2AuthenticationToken auth) {
        String userId = jwt.getAttribute("sub");
        inventoryService.deleteInventory(id, userId, getAccessToken(auth));
        return ResponseEntity.noContent().build();
    }

    /**
     * POST /api/inventories/{id}/share — Share inventory with another user.
     */
    @PostMapping("/{id}/share")
    public ResponseEntity<Void> share(
            @PathVariable Long id,
            @RequestBody InventoryDTO.ShareRequest request,
            @AuthenticationPrincipal OAuth2User jwt) {
        String requesterId = jwt.getAttribute("sub");
        inventoryService.shareInventory(id, request.targetUserId(), request.scopes(), requesterId);
        return ResponseEntity.ok().build();
    }
}
