package com.felfel.springecom.service;

import com.felfel.springecom.entity.DTO.OrderItemRequest;
import com.felfel.springecom.entity.DTO.OrderItemResponse;
import com.felfel.springecom.entity.DTO.OrderRequest;
import com.felfel.springecom.entity.DTO.OrderResponse;
import com.felfel.springecom.entity.Order;
import com.felfel.springecom.entity.OrderItem;
import com.felfel.springecom.entity.Product;
import com.felfel.springecom.repositry.OrderRepo;
import com.felfel.springecom.repositry.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service responsible for order-related business logic.
 *
 * <p>This service exposes operations to place orders and to fetch all orders.
 * It coordinates with {@link ProductRepo} to validate and update product stock
 * and with {@link OrderRepo} to persist {@link Order} entities.</p>
 *
 * <p>Implementation details:
 * - Generates a short unique order id prefixed with "ORD".
 * - Sets order metadata (customer name, email, date, status).
 * - Validates product existence and adjusts stock quantity.
 * - Builds {@link OrderItem} entries with computed total price.
 * - Persists the order and returns DTO responses.</p>
 *
 * Thread-safety: This class is a Spring singleton service. Concurrent requests
 * that modify the same product stock rely on the underlying repository / DB
 * transactional behavior to remain consistent; consider adding transactions
 * and optimistic/pessimistic locking if concurrent stock updates are possible.
 */
@Service
public class OrderService {

    // Injected repository to read/update Product entities.
    @Autowired
    private ProductRepo productRepo;

    // Injected repository to persist/read Order entities.
    @Autowired
    private OrderRepo orderRepo;

    /**
     * Place an order based on the provided request.
     *
     * <p>The method performs the following steps:
     * <ol>
     *   <li>Create a new {@link Order} entity and populate basic metadata.</li>
     *   <li>Iterate over each {@link OrderItemRequest} from the {@code orderRequest}:</li>
     *   <ul>
     *     <li>Load the referenced {@link Product} from {@link ProductRepo}.</li>
     *     <li>Decrease the product stock by the requested quantity and update availability flag.</li>
     *     <li>Persist the updated {@link Product}.</li>
     *     <li>Create an {@link OrderItem} with computed total price (price * quantity) and link it to the order.</li>
     *   </ul>
     *   <li>Persist the {@link Order} and convert the saved entity to an {@link OrderResponse} DTO.</li>
     * </ol>
     *
     * <p>Errors:
     * - If a product referenced by an item is not found a {@link RuntimeException} is thrown.
     *   Consider replacing with a more specific checked or custom exception for production code.</p>
     *
     * @param orderRequest the incoming order request DTO containing customer info and item list; must not be {@code null}
     * @return an {@link OrderResponse} DTO representing the saved order and its items
     * @see OrderRequest
     * @see OrderResponse
     */
    public OrderResponse placeOrder(OrderRequest orderRequest) {
        // Create a new Order entity instance to populate and persist.
        Order order = new Order();

        // Generate a short unique identifier for the order:
        // prefix "ORD" + first 8 chars of a UUID in uppercase.
        order.setOrderId("ORD" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        // Set customer name from the request DTO.
        order.setCustomerName(orderRequest.customerName());

        // Set customer email from the request DTO.
        order.setEmail(orderRequest.email());

        // Set the order creation date/time to now.
        order.setOrderDate(LocalDateTime.now());

        // Initial order status for newly placed orders.
        order.setStatus("PLACED");

        // Prepare a list to hold OrderItem entities for this order.
        List<OrderItem> orderItems = new ArrayList<>();

        // Iterate through each requested item to validate, compute and create OrderItem entities.
        for (OrderItemRequest itemRequest : orderRequest.items()) {
            // Find the product by id; convert long to int if necessary.
            // If not found, throw a RuntimeException (consider using a custom exception).
            Product product = productRepo.findById(Math.toIntExact(itemRequest.productId()))
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            // Decrease product stock by the requested quantity.
            // Note: this updates the in-memory product object; call save() to persist.
            product.setStockQuantity(product.getStockQuantity() - itemRequest.quantity());

            // If stock is zero or negative after subtraction, mark product as unavailable.
            if (product.getStockQuantity() <= 0) {
                product.setProductAvailable(false);
            }

            // Persist updated product stock/availability back to the repository.
            productRepo.save(product);

            // Build the OrderItem entity with references to product, order and computed total price.
            OrderItem orderItem = OrderItem.builder()
                    .product(product) // reference to product entity
                    .quantity(itemRequest.quantity()) // ordered quantity
                    .totalPrice(product.getPrice().multiply(BigDecimal.valueOf(itemRequest.quantity()))) // price * qty
                    .order(order) // back-reference to parent order
                    .build();

            // Add the created order item to the list for this order.
            orderItems.add(orderItem);
        }

        // Attach the list of created OrderItem entities to the Order entity.
        order.setItems(orderItems);

        // Persist the order (and, depending on cascade configuration, its items) and obtain the saved instance.
        Order savedOrder = orderRepo.save(order);

        // Convert saved order items into response DTOs for API return.
        List<OrderItemResponse> itemResponses = new ArrayList<>();
        for (OrderItem item : savedOrder.getItems()) {
            // Build a response DTO for each order item containing human-friendly fields.
            OrderItemResponse itemResponse = new OrderItemResponse(
                    item.getProduct().getName(), // product name
                    item.getQuantity(), // quantity ordered
                    item.getTotalPrice() // total price for this item
            );
            // Add to response list.
            itemResponses.add(itemResponse);
        }

        // Build the final OrderResponse DTO using saved order metadata and the item responses.
        OrderResponse orderResponse = new OrderResponse(
                savedOrder.getOrderId(), // generated order id
                savedOrder.getCustomerName(), // customer name
                savedOrder.getEmail(), // customer email
                savedOrder.getStatus(), // status (e.g., PLACED)
                savedOrder.getOrderDate().toLocalDate(), // order date only (no time)
                itemResponses // list of item response DTOs
        );

        // Return the response DTO to the caller.
        return orderResponse;
    }

    /**
     * Retrieve all orders and convert them to {@link OrderResponse} DTOs.
     *
     * <p>This method:
     * <ol>
     *   <li>Loads all {@link Order} entities from the {@link OrderRepo}.</li>
     *   <li>Converts each order and its items into {@link OrderResponse} and {@link OrderItemResponse} DTOs.</li>
     * </ol>
     *
     * <p>Note: For large datasets, consider adding pagination instead of returning all orders at once.</p>
     *
     * @return a list of {@link OrderResponse} DTOs representing persisted orders; never {@code null}
     */
    public List<OrderResponse> getAllOrderResponses() {
        // Fetch all orders from the repository.
        List<Order> orders = orderRepo.findAll();

        // Prepare the list that will hold the converted order responses.
        List<OrderResponse> orderResponses = new ArrayList<>();

        // Iterate over each persisted Order entity.
        for (Order order : orders) {
            // Convert order items into DTOs for the response.
            List<OrderItemResponse> itemResponses = new ArrayList<>();
            for (OrderItem item : order.getItems()) {
                // Create a DTO for each order item.
                OrderItemResponse itemResponse = new OrderItemResponse(
                        item.getProduct().getName(), // product name
                        item.getQuantity(), // quantity ordered
                        item.getTotalPrice() // total price for this item
                );
                // Add to the item's response list.
                itemResponses.add(itemResponse);
            }

            // Create the OrderResponse DTO for the current order.
            OrderResponse orderResponse = new OrderResponse(
                    order.getOrderId(), // order id
                    order.getCustomerName(), // customer name
                    order.getEmail(), // customer email
                    order.getStatus(), // current order status
                    order.getOrderDate().toLocalDate(), // order date (date-only)
                    itemResponses // converted item responses
            );

            // Add the converted order response to the result list.
            orderResponses.add(orderResponse);
        }

        // Return the list of converted order responses.
        return orderResponses;
    }
}