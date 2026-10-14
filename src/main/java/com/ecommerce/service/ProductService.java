package com.ecommerce.service;

import com.ecommerce.dto.product.ProductRequest;
import com.ecommerce.dto.product.ProductResponse;
import com.ecommerce.entity.Category;
import com.ecommerce.entity.Product;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;

    public ProductService(ProductRepository productRepository, CategoryService categoryService) {
        this.productRepository = productRepository;
        this.categoryService = categoryService;
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> search(String query, Long categoryId, BigDecimal minPrice, BigDecimal maxPrice, String sort, int page, int size) {
        Sort sortObj = Sort.by("createdAt").descending();
        if ("price_asc".equalsIgnoreCase(sort)) {
            sortObj = Sort.by("price").ascending();
        } else if ("price_desc".equalsIgnoreCase(sort)) {
            sortObj = Sort.by("price").descending();
        } else if ("rating".equalsIgnoreCase(sort)) {
            sortObj = Sort.by("rating").descending();
        } else if ("name".equalsIgnoreCase(sort)) {
            sortObj = Sort.by("name").ascending();
        }
        Pageable pageable = PageRequest.of(page, size, sortObj);
        return productRepository.search(query, categoryId, minPrice, maxPrice, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public ProductResponse findById(Long id) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
        return toResponse(p);
    }

    public ProductResponse create(ProductRequest req) {
        Product p = new Product();
        apply(p, req);
        return toResponse(productRepository.save(p));
    }

    public ProductResponse update(Long id, ProductRequest req) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
        apply(p, req);
        return toResponse(productRepository.save(p));
    }

    public void delete(Long id) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
        productRepository.delete(p);
    }

    public Product getEntity(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
    }

    public ProductResponse updateStock(Long id, int stock) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
        if (stock < 0) throw new IllegalArgumentException("Stock cannot be negative");
        p.setStock(stock);
        return toResponse(productRepository.save(p));
    }

    private void apply(Product p, ProductRequest req) {
        p.setName(req.getName());
        p.setDescription(req.getDescription());
        p.setPrice(req.getPrice());
        p.setImageUrl(req.getImageUrl());
        p.setStock(req.getStock() == null ? 0 : req.getStock());
        p.setFeatured(req.isFeatured());
        p.setRating(req.getRating());
        p.setReviewCount(req.getReviewCount() == null ? 0 : req.getReviewCount());
        if (req.getCategoryId() != null) {
            Category c = categoryService.getEntity(req.getCategoryId());
            p.setCategory(c);
        } else {
            p.setCategory(null);
        }
    }

    private ProductResponse toResponse(Product p) {
        ProductResponse r = new ProductResponse();
        r.setId(p.getId());
        r.setName(p.getName());
        r.setDescription(p.getDescription());
        r.setPrice(p.getPrice());
        r.setImageUrl(p.getImageUrl());
        r.setStock(p.getStock());
        r.setFeatured(p.isFeatured());
        r.setRating(p.getRating());
        r.setReviewCount(p.getReviewCount());
        if (p.getCategory() != null) {
            r.setCategoryId(p.getCategory().getId());
            r.setCategoryName(p.getCategory().getName());
        }
        return r;
    }
}
