package com.smarthub.smarthub.repository;

import com.smarthub.smarthub.domain.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o")
    Double getTotalRevenue();

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE CAST(o.createdAt AS date) = CURRENT_DATE")
    Double getTodayRevenue();

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE YEAR(o.createdAt) = YEAR(CURRENT_DATE) AND MONTH(o.createdAt) = MONTH(CURRENT_DATE)")
    Double getMonthRevenue();

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE YEAR(o.createdAt) = YEAR(CURRENT_DATE) AND MONTH(o.createdAt) = MONTH(CURRENT_DATE) - 1")
    Double getLastMonthRevenue();

    @Query("SELECT COUNT(o) FROM Order o")
    Long getTotalOrders();

    @Query("SELECT CONCAT(MONTH(o.createdAt), '/', YEAR(o.createdAt)) as month, " +
            "SUM(o.totalAmount) as revenue, COUNT(o) as orders " +
            "FROM Order o WHERE YEAR(o.createdAt) = YEAR(CURRENT_DATE) " +
            "GROUP BY MONTH(o.createdAt), YEAR(o.createdAt) " +
            "ORDER BY MONTH(o.createdAt)")
    List<Object[]> getMonthlyRevenue();

    @Query("SELECT p.id, p.name, SUM(oi.quantity), SUM(oi.price * oi.quantity) " +
            "FROM OrderItem oi JOIN oi.product p " +
            "GROUP BY p.id, p.name " +
            "ORDER BY SUM(oi.quantity) DESC")
    List<Object[]> getTopProducts(Pageable pageable);

}
