package com.raymond.ecommerce.order;

import com.raymond.ecommerce.order.dto.OrderResponse;
import com.raymond.ecommerce.order.dto.UpdateOrderStatusRequest;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/from-cart/{cartId}")
    public OrderResponse createOrderFromCart(
            @PathVariable Long cartId) {

        return orderService.createOrderFromCart(cartId);
    }

    @PatchMapping("/{orderId}/status")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateOrderStatus(
            @PathVariable Long orderId,
            @Valid @RequestBody UpdateOrderStatusRequest request) {

        orderService.updateOrderStatus(orderId, request.status());
    }

    @GetMapping("/{orderId}")
    public OrderResponse getOrderById(@PathVariable Long orderId) {
        return orderService.getOrderById(orderId);
    }

    @GetMapping
    public List<OrderResponse> getAllOrders() {
        return orderService.getAllOrders();
    }
}