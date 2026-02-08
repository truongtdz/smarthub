package com.smarthub.smarthub.controller;

import com.smarthub.smarthub.domain.Order;
import com.smarthub.smarthub.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping()
    public ResponseEntity<String> createOrder(@RequestBody Order order) {
        return ResponseEntity.ok(orderService.createOrder(order));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getUserOrders(@PathVariable Long userId) {
        return ResponseEntity.ok(orderService.getUserOrders(userId));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderDetail(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        Map<String, Object> stats = orderService.getStatistics();
        return ResponseEntity.ok(stats);
    }

    @PostMapping("/update-status")
    public ResponseEntity<?> updateStatusPayment(
            @RequestParam("orderId") String orderId,
            @RequestParam("status") Integer status
    ) {
        orderService.updateStatusPayment(Long.parseLong(orderId), status);
        return ResponseEntity.ok("");
    }
}
