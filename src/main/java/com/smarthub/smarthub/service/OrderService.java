package com.smarthub.smarthub.service;

import com.smarthub.smarthub.domain.Order;
import com.smarthub.smarthub.domain.OrderItem;
import com.smarthub.smarthub.domain.Product;
import com.smarthub.smarthub.repository.OrderRepository;
import com.smarthub.smarthub.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public void createOrder(Order order) {
        double total = 0;

        for (OrderItem item : order.getOrderItems()) {
            Product product = productRepository.findById(item.getProduct().getId()).get();

            item.setPrice(product.getPrice());
            item.setOrder(order);
            total += product.getPrice() * item.getQuantity();

            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);
        }

        order.setTotalAmount(total);
        orderRepository.save(order);
    }

    public List<Order> getUserOrders(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId).orElse(null);
    }

    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();

        // Revenue stats
        Double totalRevenue = orderRepository.getTotalRevenue();
        Double todayRevenue = orderRepository.getTodayRevenue();
        Double monthRevenue = orderRepository.getMonthRevenue();
        Double lastMonthRevenue = orderRepository.getLastMonthRevenue();
        Double growth = lastMonthRevenue > 0
                ? ((monthRevenue - lastMonthRevenue) / lastMonthRevenue) * 100
                : 0.0;

        Map<String, Object> revenueStats = new HashMap<>();
        revenueStats.put("totalRevenue", totalRevenue);
        revenueStats.put("todayRevenue", todayRevenue);
        revenueStats.put("monthRevenue", monthRevenue);
        revenueStats.put("growth", growth);
        stats.put("revenueStats", revenueStats);

        // Order stats - chỉ tổng đơn hàng
        Map<String, Object> orderStats = new HashMap<>();
        orderStats.put("totalOrders", orderRepository.getTotalOrders());
        stats.put("orderStats", orderStats);

        // Monthly revenue
        List<Map<String, Object>> monthlyRevenue = new ArrayList<>();
        List<Object[]> monthlyData = orderRepository.getMonthlyRevenue();
        for (Object[] row : monthlyData) {
            Map<String, Object> item = new HashMap<>();
            item.put("month", row[0]);
            item.put("revenue", row[1]);
            item.put("orders", row[2]);
            monthlyRevenue.add(item);
        }
        stats.put("monthlyRevenue", monthlyRevenue);

        // Top products
        List<Map<String, Object>> topProducts = new ArrayList<>();
        List<Object[]> productData = orderRepository.getTopProducts(PageRequest.of(0, 5));
        for (Object[] row : productData) {
            Map<String, Object> item = new HashMap<>();
            item.put("productId", row[0]);
            item.put("productName", row[1]);
            item.put("totalSold", row[2]);
            item.put("revenue", row[3]);
            topProducts.add(item);
        }
        stats.put("topProducts", topProducts);

        return stats;
    }
}
