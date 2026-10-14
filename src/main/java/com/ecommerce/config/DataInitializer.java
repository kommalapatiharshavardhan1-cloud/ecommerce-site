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

            Category mobiles = getOrCreateCategory(categoryRepository, "Mobiles and Smartphones", "Mobile phones and smart devices");
            Category laptops = getOrCreateCategory(categoryRepository, "Laptops and Computers", "Desktop and laptop computers");
            Category audio = getOrCreateCategory(categoryRepository, "Headphones and Speakers", "Audio devices and speakers");
            Category wearables = getOrCreateCategory(categoryRepository, "Smartwatches and Accessories", "Wearables and accessories");
            Category mens = getOrCreateCategory(categoryRepository, "Men's Clothing", "Men's fashion and apparel");
            Category womens = getOrCreateCategory(categoryRepository, "Women's Clothing", "Women's fashion and apparel");
            Category shoes = getOrCreateCategory(categoryRepository, "Shoes and Sneakers", "Footwear for every occasion");
            Category bags = getOrCreateCategory(categoryRepository, "Bags and Backpacks", "Travel, school, office bags");
            Category home = getOrCreateCategory(categoryRepository, "Home and Kitchen", "Home essentials and kitchen appliances");
            Category books = getOrCreateCategory(categoryRepository, "Books and Stationery", "Books, guides and stationery");

            java.util.Map<String, Category> correctCategory = new java.util.HashMap<>();

            // original products
            correctCategory.put("Wireless Headphones", audio);
            correctCategory.put("Smart Watch", wearables);
            correctCategory.put("Bluetooth Speaker", audio);
            correctCategory.put("Laptop Backpack", bags);
            correctCategory.put("Running Shoes", shoes);
            correctCategory.put("Cotton T-Shirt", mens);
            correctCategory.put("Coffee Maker", home);
            correctCategory.put("Air Fryer", home);
            correctCategory.put("Cookware Set", home);
            correctCategory.put("Java Programming Book", books);
            correctCategory.put("Spring Boot in Action", books);

            // mobile products
            correctCategory.put("Redmi Note 13 5G", mobiles);
            correctCategory.put("Samsung Galaxy A15", mobiles);
            correctCategory.put("realme Narzo 60", mobiles);
            correctCategory.put("iPhone 13 (Refurbished)", mobiles);

            // laptop products
            correctCategory.put("HP Pavilion 15 Laptop", laptops);
            correctCategory.put("Lenovo IdeaPad Slim 3", laptops);
            correctCategory.put("ASUS Vivobook 15", laptops);
            correctCategory.put("Apple MacBook Air M1", laptops);
            correctCategory.put("Dell Inspiron 15", laptops);

            // audio products
            correctCategory.put("Sony WH-CH520 Headphones", audio);
            correctCategory.put("boAt Rockerz 450", audio);
            correctCategory.put("JBL Go 3 Speaker", audio);

            // wearables / accessories
            correctCategory.put("Noise ColorFit Pro 4", wearables);
            correctCategory.put("boAt Wave Flex Connect", wearables);
            correctCategory.put("Fire-Boltt Ninja 3", wearables);
            correctCategory.put("Apple Watch SE", wearables);
            correctCategory.put("Anker Power Bank 10000mAh", wearables);

            // men fashion products
            correctCategory.put("Men's Casual Shirt", mens);
            correctCategory.put("Denim Jacket", mens);
            correctCategory.put("Men's Chinos", mens);
            correctCategory.put("Men's Polo T-Shirt", mens);
            correctCategory.put("Running Hoodie", mens);

            // women fashion products
            correctCategory.put("Women's Kurta Set", womens);
            correctCategory.put("Floral Summer Dress", womens);
            correctCategory.put("Women's Leggings", womens);
            correctCategory.put("Women's Hoodie", womens);
            correctCategory.put("Silk Saree", womens);

            // footwear
            correctCategory.put("Nike Air Max", shoes);
            correctCategory.put("Adidas Ultraboost", shoes);
            correctCategory.put("Puma Smash V2", shoes);
            correctCategory.put("Bata Formal Shoes", shoes);
            correctCategory.put("Comfort Sandals", shoes);

            // bags
            correctCategory.put("Travel Duffel Bag", bags);
            correctCategory.put("Office Laptop Bag", bags);
            correctCategory.put("Leather Handbag", bags);
            correctCategory.put("School Backpack", bags);

            // home / kitchen
            correctCategory.put("Electric Kettle", home);
            correctCategory.put("Mixer Grinder", home);
            correctCategory.put("Induction Cooktop", home);
            correctCategory.put("Dinner Set", home);

            // books
            correctCategory.put("Data Structures Book", books);
            correctCategory.put("Python Crash Course", books);
            correctCategory.put("Java Programming Guide", books);

            // migrate existing products to clearer category names
            for (Product existing : productRepository.findAll()) {
                Category desired = correctCategory.get(existing.getName());
                if (desired != null && (existing.getCategory() == null || !desired.getId().equals(existing.getCategory().getId()))) {
                    existing.setCategory(desired);
                    productRepository.save(existing);
                }
            }

            // original products
            seedIfMissing(productRepository, "Wireless Headphones", "Premium noise-cancelling headphones with 30-hour battery", 79.99, "/images/products/product-1.svg", 25, audio, true);
            seedIfMissing(productRepository, "Smart Watch", "Fitness tracker with heart rate monitor and water resistance", 129.99, "/images/products/product-2.svg", 15, wearables, true);
            seedIfMissing(productRepository, "Bluetooth Speaker", "Portable waterproof speaker with deep bass", 49.99, "/images/products/product-3.svg", 40, audio, false);
            seedIfMissing(productRepository, "Laptop Backpack", "Durable water-resistant backpack with laptop compartment", 39.99, "/images/products/product-4.svg", 30, bags, false);
            seedIfMissing(productRepository, "Running Shoes", "Lightweight running shoes with cushioned sole", 69.99, "/images/products/product-5.svg", 20, shoes, true);
            seedIfMissing(productRepository, "Cotton T-Shirt", "Comfortable cotton tee, available in multiple sizes", 19.99, "/images/products/product-6.svg", 50, mens, false);
            seedIfMissing(productRepository, "Coffee Maker", "Programmable coffee machine with auto shut-off", 89.99, "/images/products/product-7.svg", 12, home, true);
            seedIfMissing(productRepository, "Air Fryer", "Healthy cooking air fryer with digital controls", 99.99, "/images/products/product-8.svg", 18, home, false);
            seedIfMissing(productRepository, "Cookware Set", "Non-stick 10-piece cookware set", 74.99, "/images/products/product-9.svg", 14, home, false);
            seedIfMissing(productRepository, "Java Programming Book", "Learn Java from beginner to advanced", 29.99, "/images/products/product-10.svg", 35, books, false);
            seedIfMissing(productRepository, "Spring Boot in Action", "Build production-grade Spring Boot applications", 34.99, "/images/products/product-11.svg", 28, books, false);

            // mobiles
            seedIfMissing(productRepository, "Redmi Note 13 5G", "5G smartphone with 120Hz AMOLED display", 14999.00, "/images/products/product-12.svg", 30, mobiles, true);
            seedIfMissing(productRepository, "Samsung Galaxy A15", "4G smartphone with sAMOLED display and long battery", 15999.00, "/images/products/product-13.svg", 24, mobiles, false);
            seedIfMissing(productRepository, "realme Narzo 60", "Stylish 5G phone with 90Hz display", 12999.00, "/images/products/product-14.svg", 22, mobiles, false);
            seedIfMissing(productRepository, "iPhone 13 (Refurbished)", "Certified refurbished Apple iPhone 13", 32999.00, "/images/products/product-15.svg", 8, mobiles, true);

            // laptops
            seedIfMissing(productRepository, "HP Pavilion 15 Laptop", "Everyday laptop with 12th Gen Intel Core i5", 64999.00, "/images/products/product-16.svg", 10, laptops, false);
            seedIfMissing(productRepository, "Lenovo IdeaPad Slim 3", "Lightweight laptop for work and study", 49999.00, "/images/products/product-17.svg", 12, laptops, false);
            seedIfMissing(productRepository, "ASUS Vivobook 15", "Thin and light laptop with Full HD display", 52999.00, "/images/products/product-18.svg", 11, laptops, false);
            seedIfMissing(productRepository, "Apple MacBook Air M1", "Apple MacBook Air with M1 chip and Retina display", 84999.00, "/images/products/product-19.svg", 6, laptops, true);
            seedIfMissing(productRepository, "Dell Inspiron 15", "Reliable laptop for home and office use", 56999.00, "/images/products/product-20.svg", 9, laptops, false);

            // audio
            seedIfMissing(productRepository, "Sony WH-CH520 Headphones", "Wireless on-ear headphones with 50-hour battery", 4999.00, "/images/products/product-21.svg", 26, audio, true);
            seedIfMissing(productRepository, "boAt Rockerz 450", "Wireless Bluetooth headset with deep bass", 1799.00, "/images/products/product-22.svg", 48, audio, false);
            seedIfMissing(productRepository, "JBL Go 3 Speaker", "Mini portable waterproof Bluetooth speaker", 3399.00, "/images/products/product-23.svg", 34, audio, false);

            // wearables / accessories
            seedIfMissing(productRepository, "Noise ColorFit Pro 4", "Smartwatch with AMOLED display and health tracking", 3499.00, "/images/products/product-24.svg", 35, wearables, false);
            seedIfMissing(productRepository, "boAt Wave Flex Connect", "Smartwatch with Bluetooth calling", 2499.00, "/images/products/product-25.svg", 40, wearables, false);
            seedIfMissing(productRepository, "Fire-Boltt Ninja 3", "Fitness smartwatch with SpO2 and heart rate monitor", 1599.00, "/images/products/product-26.svg", 55, wearables, false);
            seedIfMissing(productRepository, "Apple Watch SE", "Apple Watch SE (2nd Generation) GPS", 29900.00, "/images/products/product-27.svg", 7, wearables, true);
            seedIfMissing(productRepository, "Anker Power Bank 10000mAh", "Fast-charging portable power bank", 2199.00, "/images/products/product-28.svg", 60, wearables, false);

            // men's clothing
            seedIfMissing(productRepository, "Men's Casual Shirt", "Breathable cotton casual shirt for everyday wear", 899.00, "/images/products/product-29.svg", 40, mens, false);
            seedIfMissing(productRepository, "Denim Jacket", "Stylish denim jacket for men", 1899.00, "/images/products/product-30.svg", 22, mens, false);
            seedIfMissing(productRepository, "Men's Chinos", "Comfort-fit chino trousers", 1499.00, "/images/products/product-31.svg", 31, mens, false);
            seedIfMissing(productRepository, "Men's Polo T-Shirt", "Classic polo t-shirt with breathable fabric", 799.00, "/images/products/product-32.svg", 60, mens, false);
            seedIfMissing(productRepository, "Running Hoodie", "Warm running hoodie with zip pockets", 1299.00, "/images/products/product-33.svg", 28, mens, false);

            // women's clothing
            seedIfMissing(productRepository, "Women's Kurta Set", "Elegant cotton kurta set with dupatta", 1499.00, "/images/products/product-34.svg", 24, womens, false);
            seedIfMissing(productRepository, "Floral Summer Dress", "Lightweight floral dress for summer", 1299.00, "/images/products/product-35.svg", 27, womens, false);
            seedIfMissing(productRepository, "Women's Leggings", "High-waist stretch leggings", 699.00, "/images/products/product-36.svg", 50, womens, false);
            seedIfMissing(productRepository, "Women's Hoodie", "Comfortable fleece hoodie", 1399.00, "/images/products/product-37.svg", 33, womens, false);
            seedIfMissing(productRepository, "Silk Saree", "Traditional silk saree with rich work", 2499.00, "/images/products/product-38.svg", 18, womens, false);

            // shoes
            seedIfMissing(productRepository, "Nike Air Max", "Premium sports shoes with air cushioning", 8999.00, "/images/products/product-39.svg", 14, shoes, true);
            seedIfMissing(productRepository, "Adidas Ultraboost", "Running shoes with responsive cushioning", 9999.00, "/images/products/product-40.svg", 11, shoes, false);
            seedIfMissing(productRepository, "Puma Smash V2", "Casual lifestyle shoes", 4499.00, "/images/products/product-41.svg", 20, shoes, false);
            seedIfMissing(productRepository, "Bata Formal Shoes", "Classic formal shoes for office wear", 2499.00, "/images/products/product-42.svg", 26, shoes, false);
            seedIfMissing(productRepository, "Comfort Sandals", "Open sandals for everyday comfort", 999.00, "/images/products/product-43.svg", 38, shoes, false);

            // bags
            seedIfMissing(productRepository, "Travel Duffel Bag", "Spacious travel duffel bag with wheels", 1799.00, "/images/products/product-44.svg", 18, bags, false);
            seedIfMissing(productRepository, "Office Laptop Bag", "Professional laptop shoulder bag", 1299.00, "/images/products/product-45.svg", 25, bags, false);
            seedIfMissing(productRepository, "Leather Handbag", "Stylish leather handbag for women", 2499.00, "/images/products/product-46.svg", 14, bags, false);
            seedIfMissing(productRepository, "School Backpack", "Durable backpack for school and travel", 899.00, "/images/products/product-47.svg", 42, bags, false);

            // home / kitchen
            seedIfMissing(productRepository, "Electric Kettle", "1.5L stainless steel electric kettle", 1499.00, "/images/products/product-48.svg", 29, home, false);
            seedIfMissing(productRepository, "Mixer Grinder", "3-jar mixer grinder for kitchen use", 2999.00, "/images/products/product-49.svg", 16, home, false);
            seedIfMissing(productRepository, "Induction Cooktop", "Energy efficient induction cooktop", 1999.00, "/images/products/product-50.svg", 19, home, false);
            seedIfMissing(productRepository, "Dinner Set", "27-piece ceramic dinner set", 1299.00, "/images/products/product-51.svg", 22, home, false);

            // books
            seedIfMissing(productRepository, "Data Structures Book", "Reference book for algorithms and data structures", 599.00, "/images/products/product-52.svg", 44, books, false);
            seedIfMissing(productRepository, "Python Crash Course", "Hands-on Python programming book", 799.00, "/images/products/product-53.svg", 31, books, false);
            seedIfMissing(productRepository, "Java Programming Guide", "Comprehensive Java programming guide", 899.00, "/images/products/product-54.svg", 37, books, false);

            // remove old generic categories when their products were remapped
            java.util.Set<String> oldCategoryNames = new java.util.HashSet<>(java.util.Arrays.asList(
                    "Electronics", "Fashion", "Home & Kitchen", "Books"
            ));
            java.util.Set<Long> oldCategoryIds = new java.util.HashSet<>();
            for (Category c : categoryRepository.findAll()) {
                if (oldCategoryNames.contains(c.getName())) {
                    oldCategoryIds.add(c.getId());
                }
            }
            for (Product existing : productRepository.findAll()) {
                if (existing.getCategory() != null && oldCategoryIds.contains(existing.getCategory().getId())) {
                    Category desired = correctCategory.get(existing.getName());
                    if (desired == null) {
                        desired = books;
                    }
                    existing.setCategory(desired);
                    productRepository.save(existing);
                }
            }
            for (Category c : new java.util.ArrayList<>(categoryRepository.findAll())) {
                if (oldCategoryIds.contains(c.getId())) {
                    categoryRepository.delete(c);
                }
            }
        };
    }

    private Category getOrCreateCategory(CategoryRepository repo, String name, String desc) {
        return repo.findByNameIgnoreCase(name)
                .orElseGet(() -> {
                    Category c = new Category();
                    c.setName(name);
                    c.setDescription(desc);
                    return repo.save(c);
                });
    }

    private void seedIfMissing(ProductRepository repo, String name, String desc, double price, String img, int stock, Category cat, boolean featured) {
        if (repo.findByName(name).isPresent()) {
            return;
        }
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
