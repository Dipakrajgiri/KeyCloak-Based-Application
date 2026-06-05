package com.sso.backend.dto;

import jakarta.validation.constraints.*;

/**
 * DTOs for Item operations
 */
public class ItemDTO {

    public record CreateRequest(
        @NotBlank(message = "Item name is required")
        @Size(min = 1, max = 100, message = "Name must be 1-100 characters")
        String name,

        @Size(max = 500, message = "Description must be under 500 characters")
        String description,

        @NotNull(message = "Quantity is required")
        @Min(value = 0, message = "Quantity cannot be negative")
        Integer quantity,

        @Min(value = 0, message = "Price cannot be negative")
        Double price
    ) {}

    public record Response(
        Long id,
        String name,
        String description,
        Integer quantity,
        Double price,
        Long categoryId,
        String createdAt,
        String updatedAt
    ) {}
}
