package com.orderService.business.repository;

import com.orderService.business.repository.model.OrdersDAO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdersRepository extends JpaRepository<Long, OrdersDAO> {
}
