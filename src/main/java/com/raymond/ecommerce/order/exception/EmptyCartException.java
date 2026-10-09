package com.raymond.ecommerce.order.exception;

public class EmptyCartException extends RuntimeException {

    public EmptyCartException(Long cartId) {
        super("Cannot create order from empty cart: " + cartId);
    }
}