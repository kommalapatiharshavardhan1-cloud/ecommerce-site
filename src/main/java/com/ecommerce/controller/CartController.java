package com.ecommerce.controller;

import com.ecommerce.dto.cart.CartItemRequest;
import com.ecommerce.dto.cart.CartItemResponse;
import com.ecommerce.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    private String email(UserDetails ud) { return ud.getUsername(); }

    @GetMapping
    public ResponseEntity<List<CartItemResponse>> get(@AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(cartService.getCart(email(ud)));
    }

    @PostMapping("/items")
    public ResponseEntity<CartItemResponse> add(@AuthenticationPrincipal UserDetails ud, @Valid @RequestBody CartItemRequest req) {
        return ResponseEntity.ok(cartService.addItem(email(ud), req));
    }

    @PutMapping("/items/{id}")
    public ResponseEntity<CartItemResponse> update(@AuthenticationPrincipal UserDetails ud, @PathVariable Long id, @RequestParam int quantity) {
        return ResponseEntity.ok(cartService.updateQuantity(email(ud), id, quantity));
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<Void> remove(@AuthenticationPrincipal UserDetails ud, @PathVariable Long id) {
        cartService.removeItem(email(ud), id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> clear(@AuthenticationPrincipal UserDetails ud) {
        cartService.clear(email(ud));
        return ResponseEntity.noContent().build();
    }
}
