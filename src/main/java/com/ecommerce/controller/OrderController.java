package com.ecommerce.controller;

import com.ecommerce.dto.order.CheckoutRequest;
import com.ecommerce.dto.order.OrderResponse;
import com.ecommerce.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    private String email(UserDetails ud) { return ud.getUsername(); }
    private boolean isAdmin(UserDetails ud) {
        return ud.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    @PostMapping("/checkout")
    public ResponseEntity<OrderResponse> checkout(@AuthenticationPrincipal UserDetails ud, @Valid @RequestBody CheckoutRequest req) {
        return ResponseEntity.ok(orderService.checkout(email(ud), req));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> myOrders(@AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(orderService.myOrders(email(ud)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> get(@AuthenticationPrincipal UserDetails ud, @PathVariable Long id) {
        return ResponseEntity.ok(orderService.findById(email(ud), id, isAdmin(ud)));
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<OrderResponse>> all(@RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(orderService.allOrders(page, size));
    }

    @PutMapping("/admin/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderResponse> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(orderService.updateStatus(id, body.get("status")));
    }
}
