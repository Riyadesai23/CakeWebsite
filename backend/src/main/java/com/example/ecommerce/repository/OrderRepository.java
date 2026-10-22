package com.example.ecommerce.repository;

import com.example.ecommerce.entity.Order;
import com.example.ecommerce.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    Optional<Order> findByIdAndUserId(Long id, Long userId);

    Optional<Order> findByOrderNumber(String orderNumber);

    Page<Order> findAllByOrderByCreatedAtDesc(Pageable pageable);

    List<Order> findTop5ByOrderByCreatedAtDesc();

    long countByOrderStatus(OrderStatus orderStatus);

    @Query("SELECT COALESCE(SUM(o.finalAmount), 0) FROM Order o WHERE o.orderStatus <> 'CANCELLED'")
    BigDecimal calculateTotalSales();

    @Query("SELECT o FROM Order o WHERE " +
           "(:status IS NULL OR o.orderStatus = :status) AND " +
           "(:keyword IS NULL OR (LOWER(o.orderNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(o.recipientName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(o.contactPhone) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(o.user.email) LIKE LOWER(CONCAT('%', :keyword, '%'))))")
    Page<Order> searchOrdersAdmin(@Param("keyword") String keyword,
                                 @Param("status") OrderStatus status,
                                 Pageable pageable);
}
