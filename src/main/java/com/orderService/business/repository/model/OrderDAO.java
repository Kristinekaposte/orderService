package com.orderService.business.repository.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "orders")
public class OrderDAO {
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

    @OneToMany(mappedBy = "orderDAO", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<OrderItemDAO> orderItemDAOList;
}
