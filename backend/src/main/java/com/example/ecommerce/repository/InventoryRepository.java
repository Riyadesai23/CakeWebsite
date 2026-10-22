package com.example.ecommerce.repository;

import com.example.ecommerce.entity.Inventory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findByProductId(Long productId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Inventory i WHERE i.product.id = :productId")
    Optional<Inventory> findByProductIdWithLock(@Param("productId") Long productId);

    @Query("SELECT i FROM Inventory i WHERE i.availableQuantity <= i.minimumStockLevel")
    List<Inventory> findLowStockInventory();

    @Query("SELECT COUNT(i) FROM Inventory i WHERE i.availableQuantity <= i.minimumStockLevel")
    long countLowStockInventory();

    @Query("SELECT i FROM Inventory i WHERE " +
           "(:keyword IS NULL OR (LOWER(i.product.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(i.product.sku) LIKE LOWER(CONCAT('%', :keyword, '%')))) AND " +
           "(:lowStockOnly = false OR i.availableQuantity <= i.minimumStockLevel)")
    Page<Inventory> searchInventory(@Param("keyword") String keyword,
                                   @Param("lowStockOnly") boolean lowStockOnly,
                                   Pageable pageable);
}
