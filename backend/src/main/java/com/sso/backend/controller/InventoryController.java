package com.sso.backend.controller;

import com.sso.backend.dto.InventoryDTO;
import com.sso.backend.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Inventory Controller — REST API endpoints for inventories
 *
 * REST API PATTERN:
 * GET    /api/inventories       → List all (for current user)
 * GET    /api/inventories/{id}  → Get one
 * POST   /api/inventories       → Create
 * PUT    /api/inventories/{id}  → Update
 * DELETE /api/inventories/{id}  → Delete
 *
 * AUTHENTICATION:
 * Every endpoint receives the JWT token via @AuthenticationPrincipal
 * We extract the user ID from jwt.getSubject() (the "sub" claim)
 * This ensures users can only access their own inventories
 *
 * @Valid — triggers validation on the request body (checks @NotBlank, @Size, etc.)
 * @RequestBody — deserializes the JSON request body into a Java object
 * @PathVariable — extracts the {id} from the URL path
 */
@RestController
@RequestMapping("/api/inventories")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    /** GET /api/inventories — List all inventories for the logged-in user */
    @GetMapping
    public ResponseEntity<List<InventoryDTO.Response>> getAll(@AuthenticationPrincipal OAuth2User jwt) {
        String userId = jwt.getAttribute("sub");
        return ResponseEntity.ok(inventoryService.getUserInventories(userId));
    }

    /** GET /api/inventories/{id} — Get a specific inventory */
    @GetMapping("/{id}")
    public ResponseEntity<InventoryDTO.Response> getOne(
            @PathVariable Long id,
            @AuthenticationPrincipal OAuth2User jwt) {
        String userId = jwt.getAttribute("sub");
        return ResponseEntity.ok(inventoryService.getInventory(id, userId));
    }

    /** POST /api/inventories — Create a new inventory */
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
            @AuthenticationPrincipal OAuth2User jwt) {
        String userId = jwt.getAttribute("sub");
        return ResponseEntity.ok(inventoryService.updateInventory(id, request, userId));
    }

    /** DELETE /api/inventories/{id} — Delete an inventory */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal OAuth2User jwt) {
        String userId = jwt.getAttribute("sub");
        inventoryService.deleteInventory(id, userId);
        return ResponseEntity.noContent().build();
    }
}
