package com.felfel.springecom.entity.DTO;

import java.util.List;

/**
 * DTO representing an order request coming from client.
 *
 * @param customerName name of the customer placing the order
 * @param email        contact email of the customer
 * @param items        list of requested items and quantities
 */
public record OrderRequest(
      String customerName,
      String email,
     List<OrderItemRequest> items) {}
