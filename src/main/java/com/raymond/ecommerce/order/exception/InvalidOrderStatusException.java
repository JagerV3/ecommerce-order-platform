package com.raymond.ecommerce.order.exception;

import com.raymond.ecommerce.order.OrderStatus;

public class InvalidOrderStatusException extends RuntimeException {

    public InvalidOrderStatusException(
            OrderStatus currentStatus,
            OrderStatus newStatus) {

        super("Cannot change order status from "
                + currentStatus + " to " + newStatus);
    }
}