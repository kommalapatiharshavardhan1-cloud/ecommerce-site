package com.ecommerce.service;

import com.ecommerce.dto.order.CheckoutRequest;
import com.ecommerce.dto.order.OrderItemResponse;
import com.ecommerce.dto.order.OrderResponse;
import com.ecommerce.entity.CartItem;
import com.ecommerce.entity.Order;
import com.ecommerce.entity.OrderItem;
import com.ecommerce.entity.OrderStatus;
import com.ecommerce.entity.User;
import com.ecommerce.exception.BadRequestException;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.repository.CartItemRepository;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductService productService;

    public OrderService(OrderRepository orderRepository, CartItemRepository cartItemRepository,
                        UserRepository userRepository, ProductService productService) {
        this.orderRepository = orderRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.productService = productService;
    }

    private User currentUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Transactional
    public OrderResponse checkout(String email, CheckoutRequest req) {
        User user = currentUser(email);
        List<CartItem> cartItems = cartItemRepository.findByUser(user);
        if (cartItems.isEmpty()) {
            throw new BadRequestException("Cart is empty");
        }

        for (CartItem ci : cartItems) {
            if (ci.getProduct().getStock() < ci.getQuantity()) {
                throw new BadRequestException("Insufficient stock for " + ci.getProduct().getName());
            }
        }

        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(req.getShippingAddress());
        order.setPhone(req.getPhone());
        order.setStatus(OrderStatus.PENDING);

        BigDecimal subtotal = BigDecimal.ZERO;
        for (CartItem ci : cartItems) {
            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setProduct(ci.getProduct());
            oi.setQuantity(ci.getQuantity());
            oi.setPrice(ci.getProduct().getPrice());
            order.getItems().add(oi);
            subtotal = subtotal.add(ci.getProduct().getPrice().multiply(BigDecimal.valueOf(ci.getQuantity())));

            int newStock = ci.getProduct().getStock() - ci.getQuantity();
            productService.updateStock(ci.getProduct().getId(), newStock);
        }

        // Shipping policy: free above Rs 499, otherwise a nominal shipping charge.
        BigDecimal shipping = subtotal.compareTo(new BigDecimal("499")) >= 0
                ? BigDecimal.ZERO
                : new BigDecimal("49.00");
        BigDecimal total = subtotal.add(shipping);

        order.setTotalAmount(total);
        Order saved = orderRepository.save(order);

        cartItemRepository.deleteByUser(user);

        return toResponse(saved);
    }

    public List<OrderResponse> myOrders(String email) {
        User user = currentUser(email);
        return orderRepository.findByUserOrderByCreatedAtDesc(user).stream()
                .map(this::toResponse).toList();
    }

    public OrderResponse findById(String email, Long id, boolean admin) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + id));
        if (!admin && !order.getUser().getEmail().equals(email)) {
            throw new BadRequestException("Not your order");
        }
        return toResponse(order);
    }

    public Page<OrderResponse> allOrders(int page, int size) {
        return orderRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(page, size))
                .map(this::toResponse);
    }

    @Transactional
    public OrderResponse updateStatus(Long id, String status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + id));
        order.setStatus(OrderStatus.valueOf(status.toUpperCase()));
        return toResponse(orderRepository.save(order));
    }

    private OrderResponse toResponse(Order o) {
        OrderResponse r = new OrderResponse();
        r.setId(o.getId());
        r.setUserId(o.getUser().getId());
        r.setUserEmail(o.getUser().getEmail());
        r.setStatus(o.getStatus().name());
        r.setTotalAmount(o.getTotalAmount());
        r.setShippingAddress(o.getShippingAddress());
        r.setPhone(o.getPhone());
        r.setItems(o.getItems().stream().map(i -> {
            OrderItemResponse ir = new OrderItemResponse();
            ir.setId(i.getId());
            ir.setProductId(i.getProduct().getId());
            ir.setProductName(i.getProduct().getName());
            ir.setQuantity(i.getQuantity());
            ir.setPrice(i.getPrice());
            ir.setSubtotal(i.getPrice().multiply(java.math.BigDecimal.valueOf(i.getQuantity())));
            return ir;
        }).toList());
        r.setCreatedAt(o.getCreatedAt());
        r.setUpdatedAt(o.getUpdatedAt());
        return r;
    }
}
