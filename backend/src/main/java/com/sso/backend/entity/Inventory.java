package com.sso.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Inventory Entity — Represents a user's inventory collection
 *
 * JPA ANNOTATIONS EXPLAINED:
 * @Entity       — Marks this class as a database table
 * @Table        — Customizes the table name
 * @Id           — Primary key
 * @GeneratedValue — Auto-generate the ID (database handles it)
 * @Column       — Customizes the column (nullable, unique, etc.)
 * @OneToMany    — One inventory has many categories
 *
 * RELATIONSHIP:
 *   User (Keycloak) → has many → Inventory → has many → Category → has many → Item
 *
 * NOTE: We don't have a User entity because users are managed by Keycloak.
 *       We just store the userId (from Keycloak's JWT "sub" claim) as a String.
 */
@Entity
@Table(name = "inventories")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    /**
     * userId comes from Keycloak JWT token's "sub" (subject) claim.
     * This links the inventory to the authenticated user WITHOUT
     * needing a User table — Keycloak manages users for us!
     */
    @Column(nullable = false)
    private String userId;

    /**
     * One Inventory has Many Categories
     * - mappedBy = "inventory" → the Category entity owns the relationship
     * - cascade = ALL → when we delete an inventory, delete its categories too
     * - orphanRemoval = true → if a category is removed from the list, delete it from DB
     */
    @OneToMany(mappedBy = "inventory", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Category> categories = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
