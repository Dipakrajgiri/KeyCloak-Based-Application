package com.sso.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Category Entity — Groups items within an inventory
 *
 * RELATIONSHIP:
 *   Inventory (parent) → Category (child) → Item (grandchild)
 *
 * @ManyToOne — Many categories belong to one inventory
 * @JoinColumn — Specifies the foreign key column name in the DB
 * @JsonIgnore — Prevents infinite recursion when serializing to JSON
 *               (Inventory → Categories → Inventory → Categories → ...)
 */
@Entity
@Table(name = "categories")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    /**
     * Many Categories belong to One Inventory
     * @JsonIgnore prevents circular reference in JSON output
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_id", nullable = false)
    @JsonIgnore
    private Inventory inventory;

    /**
     * One Category has Many Items
     */
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Item> items = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
