package com.sso.backend.repository;

import com.sso.backend.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Item Repository
 */
@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    /** Find all items in a specific category */
    List<Item> findByCategoryId(Long categoryId);

    /** Find a specific item by ID within a specific category */
    Optional<Item> findByIdAndCategoryId(Long id, Long categoryId);
}
