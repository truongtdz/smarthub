package com.smarthub.smarthub.service;

import com.smarthub.smarthub.config.exception.AppException;
import com.smarthub.smarthub.domain.Order;
import com.smarthub.smarthub.domain.OrderItem;
import com.smarthub.smarthub.domain.Product;
import com.smarthub.smarthub.domain.Users;
import com.smarthub.smarthub.repository.OrderRepository;
import com.smarthub.smarthub.repository.ProductRepository;
import com.smarthub.smarthub.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createOrder(Order order) {
        double total = 0;

        for (OrderItem item : order.getOrderItems()) {
            Product product = productRepository.findById(item.getProduct().getId())
                    .orElseThrow(() -> new AppException("Không tìm thấy sản phẩm"));

            item.setPrice(product.getPrice());
            item.setOrder(order);
            total += product.getPrice() * item.getQuantity();

            if (product.getStock() - item.getQuantity() < 0) {
                throw new AppException("Số lượng sản phẩm trong kho không đủ");
            }
            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);
        }

        order.setTotalAmount(total);
        order.setCreatedAt(LocalDateTime.now());
        orderRepository.save(order);
    }

    public List<Order> getUserOrders(Long userId) {
        Optional<Users> users = userRepository.findById(userId);

        if (users.isPresent()) {
            if ("ADMIN".equals(users.get().getRole())) {
                return orderRepository.findAll().stream()
                        .sorted(Comparator.comparing(Order::getCreatedAt).reversed())
                        .toList();
            }
        }

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
