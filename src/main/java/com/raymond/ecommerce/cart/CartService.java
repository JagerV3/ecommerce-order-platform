package com.raymond.ecommerce.cart;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

import com.raymond.ecommerce.product.ProductRepository;
import com.raymond.ecommerce.product.Product;
import com.raymond.ecommerce.product.exception.ProductNotFoundException;
import com.raymond.ecommerce.cart.dto.AddCartItemRequest;
import com.raymond.ecommerce.cart.dto.CartItemResponse;
import com.raymond.ecommerce.cart.dto.CartResponse;
import com.raymond.ecommerce.cart.dto.UpdateCartItemRequest;
import com.raymond.ecommerce.cart.exception.CartItemNotFoundException;
import com.raymond.ecommerce.cart.exception.CartNotFoundException;
import com.raymond.ecommerce.cart.exception.InsufficientStockException;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public CartService(
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            ProductRepository productRepository) {

        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    public Cart createCart() {
        Cart cart = new Cart();

        return cartRepository.save(cart);
    }

    public CartItemResponse addItem(Long cartId, AddCartItemRequest request) {

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() ->
                        new CartNotFoundException(cartId));
    
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() ->
                        new ProductNotFoundException(request.getProductId()));

        Optional<CartItem> existingItem = cartItemRepository.findByCartIdAndProductId(
                cartId,
                request.getProductId()
        );
    
        int newQuantity;

        if (existingItem.isPresent()) {
            newQuantity = existingItem.get().getQuantity() + request.getQuantity();
        } else {
            newQuantity = request.getQuantity();
        }

        if (newQuantity > product.getStock()) {
            throw new InsufficientStockException(
                    product.getId(),
                    newQuantity,
                    product.getStock()
            );
        }

        CartItem cartItem;

        if (existingItem.isPresent()) {
            cartItem = existingItem.get();
        } else {
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
        }
        cartItem.setQuantity(newQuantity);

        CartItem savedItem = cartItemRepository.save(cartItem);
    
        return new CartItemResponse(
            savedItem.getId(),
            savedItem.getProduct().getId(),
            savedItem.getProduct().getName(),
            savedItem.getProduct().getPrice(),
            savedItem.getQuantity()
        );
    }

    public CartItemResponse updateItem(
            Long cartId,
            Long itemId,
            UpdateCartItemRequest request) {

        CartItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() ->
                        new CartItemNotFoundException(itemId));

        if (!cartItem.getCart().getId().equals(cartId)) {
            throw new RuntimeException("Cart item does not belong to this cart");
        }

        cartItem.setQuantity(request.getQuantity());

        CartItem updatedItem = cartItemRepository.save(cartItem);

        if (request.getQuantity() > cartItem.getProduct().getStock()) {
            throw new InsufficientStockException(
                    cartItem.getProduct().getId(),
                    request.getQuantity(),
                    cartItem.getProduct().getStock()
            );
        }

        return new CartItemResponse(
                updatedItem.getId(),
                updatedItem.getProduct().getId(),
                updatedItem.getProduct().getName(),
                updatedItem.getProduct().getPrice(),
                updatedItem.getQuantity()
        );
    }

    public void removeItem(Long cartId, Long itemId) {

        CartItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() ->
                        new CartItemNotFoundException(cartId));
    
        if (!cartItem.getCart().getId().equals(cartId)) {
            throw new RuntimeException("Cart item does not belong to this cart");
        }
    
        cartItemRepository.delete(cartItem);
    }

    public CartResponse getCart(Long cartId) {

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() ->
                        new CartNotFoundException(cartId));
    
        List<CartItemResponse> items = cart.getItems()
                .stream()
                .map(item -> new CartItemResponse(
                        item.getId(),
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getProduct().getPrice(),
                        item.getQuantity()
                ))
                .toList();
    
        BigDecimal total = cart.getItems()
                .stream()
                .map(item ->
                        item.getProduct()
                                .getPrice()
                                .multiply(BigDecimal.valueOf(item.getQuantity()))
                )
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    
        return new CartResponse(
                cart.getId(),
                items,
                total
        );
    }
}