package com.ecommerce.repository;

import com.ecommerce.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("""
            SELECT p FROM Product p
            WHERE (:categoryId IS NULL OR p.category.id = :categoryId)
              AND (:query IS NULL OR :query = '' OR
                   LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR
                   LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%')))
            """)
    Page<Product> search(@Param("query") String query,
                       @Param("categoryId") Long categoryId,
                       Pageable pageable);

    Page<Product> findByFeaturedTrue(Pageable pageable);
}
