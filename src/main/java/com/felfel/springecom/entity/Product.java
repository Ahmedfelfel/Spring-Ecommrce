package com.felfel.springecom.entity; // package for entity classes

import jakarta.persistence.*; // JPA annotations used for entity mapping
import lombok.AllArgsConstructor; // Lombok: all-args constructor
import lombok.Data; // Lombok: getters/setters and other boilerplate
import lombok.NoArgsConstructor; // Lombok: no-args constructor
import java.math.BigDecimal; // precise monetary type for price
import java.util.Date; // legacy date type for release date

/**
 * Domain entity representing a sellable product.
 *
 * <p>Fields:
 * - id: primary key.
 * - name, description, brand: textual metadata.
 * - price: product price represented with BigDecimal.
 * - category, releaseDate: classification and release information.
 * - productAvailable, stockQuantity: availability and inventory count.
 * - imageName, imageType, imageData: optional image metadata and binary data stored as a LOB.
 */
@Entity // mark class as JPA entity
@Data // generate getters/setters, equals, hashCode, toString
@NoArgsConstructor // generate no-args constructor
@AllArgsConstructor // generate all-args constructor
public class Product {
    @Id // primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto-generated id
    private int id; // numeric identifier used by the application and DB

    private String name; // human-readable product name

    private String description; // longer description of the product

    private String brand; // brand/manufacturer name

    private BigDecimal price; // unit price, BigDecimal to avoid float rounding errors

    private String category; // product category for filtering/search

    private Date releaseDate; // date the product was released/published

    private boolean productAvailable; // flag indicating whether product is available for sale

    private int stockQuantity; // current inventory count

    private String imageName; // original filename of uploaded image

    private String imageType; // MIME type of the image (e.g., "image/png")

    @Lob // store large object (binary) in DB
    private byte[] imageData; // raw image bytes

    public Product(int id) { // convenience constructor that sets only the id
        this.id = id; // assign provided id value
    }
}
