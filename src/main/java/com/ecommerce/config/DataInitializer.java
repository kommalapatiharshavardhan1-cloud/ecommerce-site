package com.ecommerce.config;

import com.ecommerce.entity.Category;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.Role;
import com.ecommerce.entity.User;
import com.ecommerce.repository.CategoryRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedData(UserRepository userRepository,
                            CategoryRepository categoryRepository,
                            ProductRepository productRepository,
                            PasswordEncoder passwordEncoder,
                            @Value("${app.admin.email}") String adminEmail,
                            @Value("${app.admin.password}") String adminPassword,
                            @Value("${app.admin.name}") String adminName) {
        return args -> {
            if (!userRepository.existsByEmail(adminEmail)) {
                User admin = new User();
                admin.setFullName(adminName);
                admin.setEmail(adminEmail);
                admin.setPassword(passwordEncoder.encode(adminPassword));
                admin.setRole(Role.ADMIN);
                userRepository.save(admin);
            }

            if (categoryRepository.count() == 0) {
                Category electronics = cat(categoryRepository, "Electronics", "Gadgets and devices");
                Category fashion = cat(categoryRepository, "Fashion", "Clothing and accessories");
                Category home = cat(categoryRepository, "Home & Kitchen", "Home essentials");
                Category books = cat(categoryRepository, "Books", "Books and stationery");

                seedProduct(productRepository, "Wireless Headphones", "Premium noise-cancelling headphones", 79.99, "/images/product-placeholder.svg", 25, electronics, true);
                seedProduct(productRepository, "Smart Watch", "Fitness tracker with heart rate monitor", 129.99, "/images/product-placeholder.svg", 15, electronics, true);
                seedProduct(productRepository, "Bluetooth Speaker", "Portable waterproof speaker", 49.99, "/images/product-placeholder.svg", 40, electronics, false);
                seedProduct(productRepository, "Laptop Backpack", "Durable water-resistant backpack", 39.99, "/images/product-placeholder.svg", 30, fashion, false);
                seedProduct(productRepository, "Running Shoes", "Lightweight running shoes", 69.99, "/images/product-placeholder.svg", 20, fashion, true);
                seedProduct(productRepository, "Cotton T-Shirt", "Comfortable cotton tee", 19.99, "/images/product-placeholder.svg", 50, fashion, false);
                seedProduct(productRepository, "Coffee Maker", "Programmable coffee machine", 89.99, "/images/product-placeholder.svg", 12, home, true);
                seedProduct(productRepository, "Air Fryer", "Healthy cooking air fryer", 99.99, "/images/product-placeholder.svg", 18, home, false);
                seedProduct(productRepository, "Cookware Set", "Non-stick 10-piece set", 74.99, "/images/product-placeholder.svg", 14, home, false);
                seedProduct(productRepository, "Java Programming Book", "Learn Java from scratch", 29.99, "/images/product-placeholder.svg", 35, books, false);
                seedProduct(productRepository, "Spring Boot in Action", "Master Spring Boot", 34.99, "/images/product-placeholder.svg", 28, books, false);
            }
        };
    }

    private Category cat(CategoryRepository repo, String name, String desc) {
        Category c = new Category();
        c.setName(name);
        c.setDescription(desc);
        return repo.save(c);
    }

    private void seedProduct(ProductRepository repo, String name, String desc, double price,
                             String img, int stock, Category cat, boolean featured) {
        Product p = new Product();
        p.setName(name);
        p.setDescription(desc);
        p.setPrice(java.math.BigDecimal.valueOf(price));
        p.setImageUrl(img);
        p.setStock(stock);
        p.setCategory(cat);
        p.setFeatured(featured);
        repo.save(p);
    }
}
