package com.raymond.ecommerce.cart;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
        Optional<CartItem> findByCartIdAndProductId(
            Long cartId,
            Long productId
    );
}