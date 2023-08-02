package com.orderService.business.repository.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "orders")
public class OrdersDAO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_number", nullable = false, length = 40, unique = true)
    private String orderNumber;

    @Column (name = "customer_id", nullable = false)
    private Long customerId;

    @Column (name = "order_date" , nullable = false)
    private LocalDateTime orderTime;

    @Column (name = "total_price", nullable = false)
    private Double totalPrice;
}
