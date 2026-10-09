package com.raymond.ecommerce.cart;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.raymond.ecommerce.cart.dto.AddCartItemRequest;
import com.raymond.ecommerce.cart.dto.CartItemResponse;
import com.raymond.ecommerce.cart.dto.CartResponse;
import com.raymond.ecommerce.cart.dto.UpdateCartItemRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/carts")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping
    public Cart createCart() {
        return cartService.createCart();
    }

    @PostMapping("/{cartId}/items")
    public CartItemResponse addItem(
            @PathVariable Long cartId,
            @Valid @RequestBody AddCartItemRequest request) {

        return cartService.addItem(cartId, request);
    }

    @PutMapping("/{cartId}/items/{itemId}")
    public CartItemResponse updateItem(
            @PathVariable Long cartId,
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateCartItemRequest request) {

        return cartService.updateItem(cartId, itemId, request);
    }

    @DeleteMapping("/{cartId}/items/{itemId}")
    public ResponseEntity<Void> removeItem(
            @PathVariable Long cartId,
            @PathVariable Long itemId) {

        cartService.removeItem(cartId, itemId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{cartId}")
    public CartResponse getCart(
            @PathVariable Long cartId) {

        return cartService.getCart(cartId);
    }
}