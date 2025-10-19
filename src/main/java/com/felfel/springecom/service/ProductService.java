package com.felfel.springecom.service;

import com.felfel.springecom.entity.Product;
import com.felfel.springecom.repositry.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


@Service
/**
 * Service that encapsulates product retrieval, persistence and image-processing logic.
 */
public class ProductService {

    @Autowired
    private ProductRepo repo; // JPA repository for persistence operations

    /**
     * Retrieve all stored products.
     *
     * @return list of products
     */
    public List<Product> getAllProducts() {
        return repo.findAll(); // delegate to JPA repository findAll
    }

    /**
     * Get product by id.
     *
     * @param id product id
     * @return product entity or null if not found
     */
    public Product getProductById(int id) {
        return repo.findById(id).orElse(null); // optional -> entity or null
    }

    /**
     * Create or update a product and optionally persist image bytes if provided.
     *
     * @param product product entity to save
     * @param image   optional multipart file containing image data
     * @return saved product entity with image metadata populated
     * @throws IOException if reading image bytes fails
     */
    public Product addOrUpdateProduct(Product product, MultipartFile image) throws IOException {
        // If an image is supplied, extract name, content type and byte[] and set on product
        if (image != null && !image.isEmpty()) {
            product.setImageName(image.getOriginalFilename()); // original filename
            product.setImageType(image.getContentType());     // MIME type
            product.setImageData(image.getBytes());           // raw bytes
        }
        // Save the product (insert or update) and return persisted instance
        return repo.save(product);
    }

    /**
     * Delete product by id and return a status message.
     *
     * @param id product id to delete
     * @return "Success" when deleted, otherwise error message
     */
    public String deleteProductById(int id) {
        try {
            repo.deleteById(id); // attempt deletion
            return "Success";
        } catch (Exception e) {
            // Return failure string containing exception message for controller to surface
            return "Failed" + e.getMessage();
        }
    }

    /**
     * Search products by keyword.
     *
     * @param keyword substring to search for
     * @return matching products
     */
    public List<Product> searchProducts(String keyword) {
        return repo.searchProducts(keyword); // delegate to custom repo method
    }
}
