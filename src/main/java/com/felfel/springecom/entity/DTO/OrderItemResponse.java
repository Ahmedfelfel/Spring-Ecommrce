package com.felfel.springecom.entity.DTO;

import java.math.BigDecimal;

/**
 * Immutable DTO representing an item in an order returned to clients.
 *
 * @param productName user friendly name of the product
 * @param quantity    quantity ordered for this item
 * @param totalPrice  total price for this line (product price * quantity)
 */
public record OrderItemResponse(
     String productName,
     Integer quantity,
     BigDecimal totalPrice) {}
