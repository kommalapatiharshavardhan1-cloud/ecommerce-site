package com.ecommerce.repository;

import com.ecommerce.entity.Category;
import com.ecommerce.entity.Product;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("""
            SELECT p FROM Product p
            WHERE (:categoryId IS NULL OR p.category.id = :categoryId)
              AND (:query IS NULL OR :query = '' OR
                   LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR
                   LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%')))
              AND (:minPrice IS NULL OR p.price >= :minPrice)
              AND (:maxPrice IS NULL OR p.price <= :maxPrice)
            """)
    Page<Product> search(@Param("query") String query,
                       @Param("categoryId") Long categoryId,
                       @Param("minPrice") BigDecimal minPrice,
                       @Param("maxPrice") BigDecimal maxPrice,
                       Pageable pageable);
    Page<Product> findByFeaturedTrue(Pageable pageable);

    Optional<Product> findByName(String name);

    long countByCategory(Category category);
}
