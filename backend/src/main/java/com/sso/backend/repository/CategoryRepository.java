package com.sso.backend.repository;

import com.sso.backend.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Category Repository
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /** Find all categories in a specific inventory */
    List<Category> findByInventoryId(Long inventoryId);

    /** Find a specific category by ID within a specific inventory */
    Optional<Category> findByIdAndInventoryId(Long id, Long inventoryId);
}
