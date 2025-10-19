package com.felfel.springecom.entity.DTO;

import java.math.BigDecimal;

/**
 * DTO representing an item in an order request (client -> server).
 *
 * @param productId identifier of the product being ordered
 * @param quantity  quantity requested for that product
 */
public record OrderItemRequest(
     Long productId,
     Integer quantity) {}
