package com.raymond.ecommerce.cart.dto;

import java.math.BigDecimal;
import java.util.List;

public class CartResponse {

    private Long id;
    private List<CartItemResponse> items;
    private BigDecimal total;

    public CartResponse(
            Long id,
            List<CartItemResponse> items,
            BigDecimal total) {

        this.id = id;
        this.items = items;
        this.total = total;
    }

    public Long getId() {
        return id;
    }

    public List<CartItemResponse> getItems() {
        return items;
    }

    public BigDecimal getTotal() {
        return total;
    }
}