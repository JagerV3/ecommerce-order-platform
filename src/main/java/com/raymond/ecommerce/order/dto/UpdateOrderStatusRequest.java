package com.raymond.ecommerce.order.dto;

import com.raymond.ecommerce.order.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusRequest(
        @NotNull OrderStatus status
) {}