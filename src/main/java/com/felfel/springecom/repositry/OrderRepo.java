package com.felfel.springecom.repositry;

import com.felfel.springecom.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
/**
 * JPA repository for Order entities providing CRUD operations and custom lookups.
 */
public interface OrderRepo extends JpaRepository<Order, Long> {
    /**
     * Find an Order by its external orderId string.
     *
     * @param orderId external identifier of the order
     * @return Optional containing Order if found
     */
    Optional<Order> findByOrderId(String orderId);
}
