package com.ecommerce.service;

import com.ecommerce.dto.category.CategoryRequest;
import com.ecommerce.dto.category.CategoryResponse;
import com.ecommerce.entity.Category;
import com.ecommerce.exception.BadRequestException;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryResponse> findAll() {
        return categoryRepository.findAll().stream().map(this::toResponse).toList();
    }

    public CategoryResponse findById(Long id) {
        return categoryRepository.findById(id).map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id));
    }

    public CategoryResponse create(CategoryRequest req) {
        categoryRepository.findByNameIgnoreCase(req.getName()).ifPresent(c -> {
            throw new BadRequestException("Category already exists");
        });
        Category c = new Category();
        c.setName(req.getName());
        c.setDescription(req.getDescription());
        return toResponse(categoryRepository.save(c));
    }

    public CategoryResponse update(Long id, CategoryRequest req) {
        Category c = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id));
        c.setName(req.getName());
        c.setDescription(req.getDescription());
        return toResponse(categoryRepository.save(c));
    }

    public void delete(Long id) {
        Category c = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id));
        categoryRepository.delete(c);
    }

    public Category getEntity(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id));
    }

    private CategoryResponse toResponse(Category c) {
        CategoryResponse r = new CategoryResponse();
        r.setId(c.getId());
        r.setName(c.getName());
        r.setDescription(c.getDescription());
        return r;
    }
}
