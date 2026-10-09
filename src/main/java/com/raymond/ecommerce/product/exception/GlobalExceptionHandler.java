package com.raymond.ecommerce.common.exception;

import com.raymond.ecommerce.cart.exception.CartItemNotFoundException;
import com.raymond.ecommerce.cart.exception.InsufficientStockException;
import com.raymond.ecommerce.product.exception.ProductNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(ProductNotFoundException.class)
        public ResponseEntity<Map<String, String>> handleProductNotFound(
                ProductNotFoundException exception) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                        "error", "Product Not Found",
                        "message", exception.getMessage()
                ));
        }

        @ExceptionHandler(CartItemNotFoundException.class)
        public ResponseEntity<Map<String, String>> handleCartItemNotFound(
                CartItemNotFoundException exception) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                        "error", "Cart Item Not Found",
                        "message", exception.getMessage()
                ));
        }

        @ExceptionHandler(InsufficientStockException.class)
        public ResponseEntity<Map<String, String>> handleInsufficientStock(
                InsufficientStockException exception) {

        return ResponseEntity
                .badRequest()
                .body(Map.of(
                        "error", "Insufficient Stock",
                        "message", exception.getMessage()
                ));
        }
    
}