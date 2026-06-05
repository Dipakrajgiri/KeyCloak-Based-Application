package com.sso.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTOs for Category operations
 */
public class CategoryDTO {

    public record CreateRequest(
        @NotBlank(message = "Category name is required")
        @Size(min = 2, max = 100, message = "Name must be 2-100 characters")
        String name,

        @Size(max = 500, message = "Description must be under 500 characters")
        String description
    ) {}

    public record Response(
        Long id,
        String name,
        String description,
        Long inventoryId,
        int itemCount,
        String createdAt,
        String updatedAt
    ) {}
}
