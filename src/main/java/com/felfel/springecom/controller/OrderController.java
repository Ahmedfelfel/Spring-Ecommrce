package com.felfel.springecom.controller;

import com.felfel.springecom.entity.DTO.OrderRequest;
import com.felfel.springecom.entity.DTO.OrderResponse;
import com.felfel.springecom.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api")
/**
 * Controller that exposes REST endpoints for placing and retrieving orders.
 *
 * Responsibilities:
 * - Accept order placement requests and delegate to OrderService.
 * - Return HTTP responses with appropriate status codes and bodies.
 */
public class OrderController {
    @Autowired
    private OrderService orderService; // injected service that handles order business logic

    /**
     * Place a new order.
     *
     * @param orderRequest DTO containing customer and item details for the new order
     * @return ResponseEntity containing OrderResponse and HTTP status CREATED if successful
     */
    @PostMapping("/orders/place")
    public ResponseEntity<OrderResponse> placeOrder(@RequestBody OrderRequest orderRequest) {
        // Delegate the creation to the service layer and get a response DTO
        OrderResponse orderResponse = orderService.placeOrder(orderRequest);
        // Return the created order with 201 status
        return new ResponseEntity<>(orderResponse, HttpStatus.CREATED);
    }

    /**
     * Retrieve all orders.
     *
     * @return ResponseEntity containing a list of OrderResponse and HTTP status OK
     */
    @GetMapping("orders")
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        // Fetch list of order response DTOs from service
        List<OrderResponse> orders = orderService.getAllOrderResponses();
        // Return list with 200 status
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }
}
