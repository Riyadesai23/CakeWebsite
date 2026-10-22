package com.example.ecommerce.repository;

import com.example.ecommerce.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    Optional<Product> findBySku(String sku);
    boolean existsBySku(String sku);
    long countByActive(boolean active);

    Page<Product> findByActiveTrue(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.active = true AND " +
           "(:categoryId IS NULL OR p.category.id = :categoryId) AND " +
           "(:brand IS NULL OR LOWER(p.brand) = LOWER(:brand)) AND " +
           "(:minPrice IS NULL OR p.finalPrice >= :minPrice) AND " +
           "(:maxPrice IS NULL OR p.finalPrice <= :maxPrice) AND " +
           "(:keyword IS NULL OR (LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(p.sku) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(p.brand) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))))")
    Page<Product> searchProductsPublic(@Param("keyword") String keyword,
                                      @Param("categoryId") Long categoryId,
                                      @Param("brand") String brand,
                                      @Param("minPrice") BigDecimal minPrice,
                                      @Param("maxPrice") BigDecimal maxPrice,
                                      Pageable pageable);

    @Query("SELECT p FROM Product p WHERE " +
           "(:active IS NULL OR p.active = :active) AND " +
           "(:categoryId IS NULL OR p.category.id = :categoryId) AND " +
           "(:keyword IS NULL OR (LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(p.sku) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(p.brand) LIKE LOWER(CONCAT('%', :keyword, '%'))))")
    Page<Product> searchProductsAdmin(@Param("keyword") String keyword,
                                     @Param("categoryId") Long categoryId,
                                     @Param("active") Boolean active,
                                     Pageable pageable);

    @Query("SELECT DISTINCT p.brand FROM Product p WHERE p.brand IS NOT NULL AND p.active = true ORDER BY p.brand")
    List<String> findAllActiveBrands();
}
