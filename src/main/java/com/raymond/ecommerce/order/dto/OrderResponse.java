package com.raymond.ecommerce.order.dto;

import com.raymond.ecommerce.order.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderResponse {

    private Long id;
    private OrderStatus status;
    private List<OrderItemResponse> items;
    private BigDecimal total;
    private LocalDateTime createdAt;

    public OrderResponse(
            Long id,
            OrderStatus status,
            List<OrderItemResponse> items,
            BigDecimal total,
            LocalDateTime createdAt) {

        this.id = id;
        this.status = status;
        this.items = items;
        this.total = total;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public List<OrderItemResponse> getItems() {
        return items;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}