package com.orderService.business.repository;

import com.orderService.business.repository.model.OrderDAO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderDAO, Long> {
}
