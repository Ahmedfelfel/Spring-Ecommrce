package com.felfel.springecom.controller;

import com.felfel.springecom.entity.Product;
import com.felfel.springecom.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
/**
 * REST controller exposing product-related endpoints (CRUD, search, images).
 */
public class ProductController {

    @Autowired
    private ProductService productService; // service managing product persistence and image handling

    /**
     * Get all products.
     *
     * @return 200 OK with list of products
     */
    @GetMapping("/products")
    public ResponseEntity<List<Product>> getProducts() {
        System.out.println("getting products"); // simple trace for debug
        return new ResponseEntity<>(productService.getAllProducts(), HttpStatus.OK);
    }

    /**
     * Get product by id.
     *
     * @param id product identifier
     * @return 200 OK with product or 404 if not found
     */
    @GetMapping("/product/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable int id) {
        // Retrieve product or null if absent
        Product product = productService.getProductById(id);
        if (product == null) {
            // Return 404 when product is not found
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Return the found product with 200 OK
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    /**
     * Get raw image bytes for a product.
     *
     * @param id product identifier
     * @return 200 OK with image bytes or 404 if not present
     */
    @GetMapping("/product/{id}/image")
    public ResponseEntity<?> getImageByProductId(@PathVariable int id) {
        System.out.println("getting image for each product");
        Product product = productService.getProductById(id); // fetch product to access image data
        if (product.getImageData() == null) {
            // No image stored for this product
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Return image bytes with 200
        return new ResponseEntity<>(product.getImageData(), HttpStatus.OK);
    }

    /**
     * Create a new product with optional image.
     *
     * @param product   product entity payload (form part)
     * @param imageFile optional multipart image file
     * @return 201 CREATED with created product or 500 on failure
     */
    @PostMapping("/product")
    public ResponseEntity<?> addProduct(@RequestPart Product product, @RequestPart MultipartFile imageFile) {
        Product createdProduct = null;
        try {
            // Delegate creation to service which handles image bytes
            createdProduct = productService.addOrUpdateProduct(product, imageFile);
            return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
        } catch (Exception e) {
            // Return server error with message on exception
            return new ResponseEntity<>("Failed to create product: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    /**
     * Update existing product by id with optional new image.
     *
     * @param id        product identifier
     * @param product   updated product entity payload (form part)
     * @param imageFile optional multipart image file
     * @return 200 OK with updated product or 500 on failure
     */
    @PutMapping("/product/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable int id, @RequestPart Product product, @RequestPart(required = false) MultipartFile imageFile) {
        Product updatedProduct = null;
        try {
            // Delegate creation to service which handles image bytes
            updatedProduct = productService.addOrUpdateProduct(product, imageFile);
            return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
        } catch (Exception e) {
            // Return server error with message on exception
            return new ResponseEntity<>("Failed to create product: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    /**
     * Delete product by id.
     *
     * @param id product identifier
     * @return 200 OK on success or 500 on failure
     */
    @DeleteMapping("/product/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable int id) {
        String response = productService.deleteProductById(id);
        if (response.equals("Success")) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    /**
     * Search products by keyword.
     *
     * @param keyword search term
     * @return 200 OK with list of matching products
     */
    @GetMapping("/products/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String keyword) {
        List<Product> results = productService.searchProducts(keyword);
        return new ResponseEntity<>(results, HttpStatus.OK);
    }

}


