package com.sso.backend.repository;

import com.sso.backend.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Inventory Repository — Database access layer
 *
 * WHAT IS A REPOSITORY?
 * - It's the layer that talks to the database
 * - Spring Data JPA auto-implements these methods for you!
 * - Just define the method name, and Spring generates the SQL query
 *
 * HOW METHOD NAMING WORKS:
 * - findByUserId → SELECT * FROM inventories WHERE user_id = ?
 * - findByIdAndUserId → SELECT * FROM inventories WHERE id = ? AND user_id = ?
 *
 * JpaRepository<Inventory, Long>:
 * - Inventory = the entity type
 * - Long = the type of the primary key (id)
 *
 * Built-in methods from JpaRepository:
 * - save(entity) → INSERT or UPDATE
 * - findById(id) → SELECT by primary key
 * - findAll() → SELECT all
 * - deleteById(id) → DELETE by primary key
 * - count() → COUNT all records
 */
@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    /**
     * Find all inventories belonging to a specific user
     * Spring auto-generates: SELECT * FROM inventories WHERE user_id = ?
     */
    List<Inventory> findByUserId(String userId);

    /**
     * Find a specific inventory by ID, but only if it belongs to the given user
     * This is important for security — users should only access their own data!
     */
    Optional<Inventory> findByIdAndUserId(Long id, String userId);
}
