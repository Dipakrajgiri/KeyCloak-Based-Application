package com.sso.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Item Entity — Individual items within a category
 *
 * This is the leaf entity in our hierarchy:
 *   User → Inventory → Category → Item
 *
 * Each item has:
 * - name: what the item is called
 * - description: optional details
 * - quantity: how many we have
 * - price: optional price per unit
 */
@Entity
@Table(name = "items")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    @Builder.Default
    private Integer quantity = 0;

    @Builder.Default
    private Double price = 0.0;

    /**
     * Many Items belong to One Category
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @JsonIgnore
    private Category category;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
