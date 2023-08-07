package com.orderService.business.service.impl;

import com.orderService.business.mappers.OrderMapper;
import com.orderService.business.repository.OrderRepository;
import com.orderService.business.repository.model.OrderDAO;
import com.orderService.business.service.OrderService;
import com.orderService.model.Order;
import com.orderService.model.OrderItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private WebClient webClient;

    @Override
    public List<Order> getAllOrders() {
        List<Order> list = orderRepository.findAll()
                .stream()
                .map(orderMapper::daoToOrder)
                .collect(Collectors.toList());
        log.info("Size of the Order list: {}", list.size());
        return list;
    }

    @Override
    public Optional<Order> findOrderByOrderNumber(String orderNumber) {
            Optional<OrderDAO> orderDAO = orderRepository.findByOrderNumber(orderNumber);
            if (!orderDAO.isPresent()) {
                log.info("Order with orderNumber {} does not exist.", orderNumber);
                return Optional.empty();
            }
            log.info("Order with orderNumber {} found.", orderNumber);
            return orderDAO.map(orderMapper::daoToOrder);
        }

    @Override
    @Transactional
    public Order placeOrder(Order order) {
        autogenerateOrderNumber(order);
        autogenerateOrderTime(order);
        OrderDAO orderDAO = orderMapper.orderToDAO(order);
        if (orderDAO.getOrderItemDAOList() != null) {
            orderDAO.getOrderItemDAOList().forEach(item -> item.setOrderDAO(orderDAO));
        }
       // List<Long> productsList = order.getOrderItems().stream().map(OrderItem::getProductId).collect(Collectors.toList()); // should make smth to call products
        calculateOrderTotalPrice(order, orderDAO);
        ResponseEntity<Object> customerResponse = webClient.get()
                .uri("http://localhost:5050/api/v1/customer/getById/{id}", order.getCustomerId())
                .retrieve()
                .toEntity(Object.class)
                .block();
        if (customerResponse.getStatusCode() == HttpStatus.OK) {
            return orderMapper.daoToOrder(orderRepository.save(orderDAO));
        } else {
            log.warn("COULD NOT PLACE ORDER, CUSTOMER NOT FOUND");
            return null;
        }
    }

    private void autogenerateOrderNumber(Order order) {
        String orderNumber =  "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        order.setOrderNumber(orderNumber);
    }
    private void autogenerateOrderTime(Order order){
        order.setOrderTime(LocalDateTime.now());
    }

    private void calculateOrderTotalPrice(Order order, OrderDAO orderDAO){
        double totalPrice = order.getOrderItems().stream()
                .mapToDouble(item -> item.getItemPrice() * item.getQuantity())
                .sum();
        orderDAO.setTotalPrice(totalPrice);
    }
}
