package com.sso.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTOs (Data Transfer Objects) for Inventory
 *
 * WHY USE DTOs?
 * - We don't expose our Entity directly to the API
 * - DTOs let us control exactly what data goes in/out
 * - Request DTOs have validation annotations
 * - Response DTOs have only the fields we want to expose
 *
 * Java Records are perfect for DTOs:
 * - Immutable (can't change after creation)
 * - Auto-generates constructor, getters, equals, hashCode, toString
 * - Very concise syntax!
 */
public class InventoryDTO {

    /**
     * Request DTO — used when creating or updating an inventory
     * Validation annotations ensure the data is valid before it reaches our service
     */
    public record CreateRequest(
        @NotBlank(message = "Name is required")
        @Size(min = 2, max = 100, message = "Name must be 2-100 characters")
        String name,

        @Size(max = 500, message = "Description must be under 500 characters")
        String description
    ) {}

    /**
     * Response DTO — used when returning inventory data to the frontend
     */
    public record Response(
        Long id,
        String name,
        String description,
        String ownerId,
        int categoryCount,
        String createdAt,
        String updatedAt
    ) {}
}
