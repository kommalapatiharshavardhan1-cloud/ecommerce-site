package com.ecommerce.service;

import com.ecommerce.dto.cart.CartItemRequest;
import com.ecommerce.dto.cart.CartItemResponse;
import com.ecommerce.entity.CartItem;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.User;
import com.ecommerce.exception.BadRequestException;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.repository.CartItemRepository;
import com.ecommerce.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductService productService;

    public CartService(CartItemRepository cartItemRepository, UserRepository userRepository, ProductService productService) {
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.productService = productService;
    }

    private User currentUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public List<CartItemResponse> getCart(String email) {
        User user = currentUser(email);
        return cartItemRepository.findByUser(user).stream().map(this::toResponse).toList();
    }

    @Transactional
    public CartItemResponse addItem(String email, CartItemRequest req) {
        User user = currentUser(email);
        Product product = productService.getEntity(req.getProductId());
        if (product.getStock() < req.getQuantity()) {
            throw new BadRequestException("Insufficient stock. Available: " + product.getStock());
        }
        CartItem existing = cartItemRepository.findByUserAndProduct(user, product).orElse(null);
        if (existing != null) {
            int newQty = existing.getQuantity() + req.getQuantity();
            if (product.getStock() < newQty) {
                throw new BadRequestException("Insufficient stock. Available: " + product.getStock());
            }
            existing.setQuantity(newQty);
            return toResponse(cartItemRepository.save(existing));
        }
        CartItem item = new CartItem();
        item.setUser(user);
        item.setProduct(product);
        item.setQuantity(req.getQuantity());
        return toResponse(cartItemRepository.save(item));
    }

    @Transactional
    public CartItemResponse updateQuantity(String email, Long itemId, int quantity) {
        User user = currentUser(email);
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));
        if (!item.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("Not your cart item");
        }
        if (quantity <= 0) {
            cartItemRepository.delete(item);
            CartItemResponse r = new CartItemResponse();
            r.setId(itemId);
            r.setQuantity(0);
            return r;
        }
        if (item.getProduct().getStock() < quantity) {
            throw new BadRequestException("Insufficient stock. Available: " + item.getProduct().getStock());
        }
        item.setQuantity(quantity);
        return toResponse(cartItemRepository.save(item));
    }

    @Transactional
    public void removeItem(String email, Long itemId) {
        User user = currentUser(email);
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));
        if (!item.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("Not your cart item");
        }
        cartItemRepository.delete(item);
    }

    @Transactional
    public void clear(String email) {
        User user = currentUser(email);
        cartItemRepository.deleteByUser(user);
    }

    private CartItemResponse toResponse(CartItem c) {
        CartItemResponse r = new CartItemResponse();
        r.setId(c.getId());
        r.setProductId(c.getProduct().getId());
        r.setProductName(c.getProduct().getName());
        r.setImageUrl(c.getProduct().getImageUrl());
        r.setPrice(c.getProduct().getPrice());
        r.setQuantity(c.getQuantity());
        r.setSubtotal(c.getProduct().getPrice().multiply(BigDecimal.valueOf(c.getQuantity())));
        return r;
    }
}
