package com.example.ecommerce.repository;

import com.example.ecommerce.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByActiveTrueOrderByNameAsc();
    Optional<Category> findByCode(String code);
    boolean existsByName(String name);
    boolean existsByCode(String code);
    long countByActive(boolean active);
}
