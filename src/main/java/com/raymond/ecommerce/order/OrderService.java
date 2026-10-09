package com.raymond.ecommerce.order;

import com.raymond.ecommerce.cart.Cart;
import com.raymond.ecommerce.cart.CartItem;
import com.raymond.ecommerce.cart.CartRepository;
import com.raymond.ecommerce.cart.exception.CartNotFoundException;
import com.raymond.ecommerce.cart.exception.InsufficientStockException;
import com.raymond.ecommerce.order.dto.OrderItemResponse;
import com.raymond.ecommerce.order.dto.OrderResponse;
import com.raymond.ecommerce.order.exception.EmptyCartException;
import com.raymond.ecommerce.order.exception.InvalidOrderStatusException;
import com.raymond.ecommerce.order.exception.OrderNotFoundException;
import com.raymond.ecommerce.product.Product;
import com.raymond.ecommerce.product.ProductRepository;
import com.raymond.ecommerce.product.exception.ProductNotFoundException;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    public OrderService(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            CartRepository cartRepository,
            ProductRepository productRepository) {

        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public OrderResponse createOrderFromCart(Long cartId) {

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new CartNotFoundException(cartId));

        if (cart.getItems().isEmpty()) {
            throw new EmptyCartException(cartId);
        }

        Order order = new Order();
        order.setStatus(OrderStatus.PENDING);
        order.setTotal(BigDecimal.ZERO);
        order.setCreatedAt(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);

        List<OrderItem> orderItems = new ArrayList<>();
        List<OrderItemResponse> itemResponses = new ArrayList<>();

        for (CartItem cartItem : cart.getItems()) {

            OrderItem orderItem = toOrderItem(cartItem, savedOrder);
            OrderItem savedItem = orderItemRepository.save(orderItem);

            deductStock(cartItem);

            orderItems.add(savedItem);
            itemResponses.add(toOrderItemResponse(savedItem));
        }

        BigDecimal total = calculateOrderTotal(orderItems);

        savedOrder.setTotal(total);
        savedOrder = orderRepository.save(savedOrder);

        return new OrderResponse(
                savedOrder.getId(),
                savedOrder.getStatus(),
                itemResponses,
                savedOrder.getTotal(),
                savedOrder.getCreatedAt()
        );
    }

    @Transactional
    public void updateOrderStatus(Long orderId, OrderStatus newStatus) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if (!order.canTransitionTo(newStatus)) {
            throw new InvalidOrderStatusException(
                    order.getStatus(),
                    newStatus
            );
        }

        order.setStatus(newStatus);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        return toOrderResponse(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {

        return orderRepository.findAll()
                .stream()
                .map(this::toOrderResponse)
                .toList();
    }

    private OrderItem toOrderItem(CartItem cartItem, Order order) {

        OrderItem orderItem = new OrderItem();
    
        orderItem.setOrder(order);
        orderItem.setProduct(cartItem.getProduct());
        orderItem.setProductName(cartItem.getProduct().getName());
        orderItem.setUnitPrice(cartItem.getProduct().getPrice());
        orderItem.setQuantity(cartItem.getQuantity());
    
        return orderItem;
    }

    private OrderItemResponse toOrderItemResponse(OrderItem orderItem) {

        return new OrderItemResponse(
                orderItem.getId(),
                orderItem.getProduct().getId(),
                orderItem.getProductName(),
                orderItem.getUnitPrice(),
                orderItem.getQuantity()
        );
    }

    private OrderResponse toOrderResponse(Order order) {

        List<OrderItemResponse> items = order.getItems()
                .stream()
                .map(this::toOrderItemResponse)
                .toList();
    
        return new OrderResponse(
                order.getId(),
                order.getStatus(),
                items,
                order.getTotal(),
                order.getCreatedAt()
        );
    }

    private BigDecimal calculateOrderTotal(List<OrderItem> orderItems) {

        return orderItems.stream()
                .map(item ->
                        item.getUnitPrice()
                                .multiply(BigDecimal.valueOf(item.getQuantity()))
                )
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void deductStock(CartItem cartItem) {

        Long productId = cartItem.getProduct().getId();

        Product product = productRepository.findByIdForUpdate(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        int requestedQuantity = cartItem.getQuantity();
        int availableStock = product.getStock();

        if (requestedQuantity > availableStock) {
            throw new InsufficientStockException(
                    productId,
                    requestedQuantity,
                    availableStock
            );
        }
    
        product.setStock(availableStock - requestedQuantity);

        productRepository.save(product);
    }
}