package com.felfel.springecom.entity.DTO;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO returned to clients representing an order.
 *
 * @param orderId      external unique identifier for the order
 * @param customerName name of the customer who placed the order
 * @param email        customer contact email
 * @param status       current status of the order (e.g., CREATED, SHIPPED)
 * @param orderDate    date when the order was placed
 * @param items        list of item DTOs included in the order
 */
public record OrderResponse (
    String orderId,
    String customerName,
    String email,
    String status,
    LocalDate orderDate,
    List<OrderItemResponse> items) { }
