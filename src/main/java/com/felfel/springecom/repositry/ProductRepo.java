package com.felfel.springecom.repositry;
import com.felfel.springecom.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
/**
 * Repository for Product entities with a custom search query across multiple text fields.
 */
public interface ProductRepo extends JpaRepository<Product, Integer> {
    @Query("SELECT p FROM Product p " +
            "WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))" +
            "OR LOWER(p.category) LIKE LOWER(CONCAT('%', :keyword, '%'))" +
            "OR LOWER(p.brand) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    /**
     * Search products by keyword in name, description, category or brand (case-insensitive).
     *
     * @param keyword search substring
     * @return list of matching products
     */
    List<Product> searchProducts(String keyword);
}
