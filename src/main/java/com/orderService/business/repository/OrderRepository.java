package com.orderService.business.repository;

import com.orderService.business.repository.model.OrderDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<OrderDAO, Long> {

    Optional<OrderDAO> findByOrderNumber(String orderNumber);
}
