package com.raymond.ecommerce.order;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "order")
    private List<OrderItem> items = new ArrayList<>();

    public Order() {
    }

    public Long getId() {
        return id;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean canTransitionTo(OrderStatus newStatus) {

        return switch (this.status) {
            case PENDING ->
                    newStatus == OrderStatus.PAID ||
                    newStatus == OrderStatus.CANCELLED;
    
            case PAID ->
                    newStatus == OrderStatus.PROCESSING ||
                    newStatus == OrderStatus.CANCELLED;
    
            case PROCESSING ->
                    newStatus == OrderStatus.SHIPPED;
    
            case SHIPPED ->
                    newStatus == OrderStatus.COMPLETED;
    
            case COMPLETED, CANCELLED -> false;
        };
    }
}