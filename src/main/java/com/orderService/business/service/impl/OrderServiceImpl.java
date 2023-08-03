package com.orderService.business.service.impl;

import com.orderService.business.mappers.OrderItemMapper;
import com.orderService.business.mappers.OrderMapper;
import com.orderService.business.repository.OrderItemRepository;
import com.orderService.business.repository.OrderRepository;
import com.orderService.business.repository.model.OrderDAO;
import com.orderService.business.repository.model.OrderItemDAO;
import com.orderService.business.service.OrderService;
import com.orderService.model.Order;
import com.orderService.model.OrderItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
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
    public void placeOrder(Order order) {
        autogenerateOrderNumber(order);
        autogenerateOrderTime(order);

        // Map the Order object to OrderDAO using the OrderMapper
        OrderDAO orderDAO = orderMapper.orderToDAO(order);

        // Set the OrderDAO for each OrderItemDAO in the list
        if (orderDAO.getOrderItemDAOList() != null) {
            orderDAO.getOrderItemDAOList().forEach(item -> item.setOrderDAO(orderDAO));
        }

        // Calculate the total price based on the order items
        calculateOrderTotalPrice(order, orderDAO);


        // Check if the customer exists in the customer service
        Boolean result = webClient.get()
                .uri("http://localhost:5050/api/v1/customer/isCustomerExistingById/{id}", order.getCustomerId())
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();

        //price and product id check should be taken from productsService

        //OPTIONAL:maybe  can send back a information to products service to decrease quantity of that product which was bough here
        //that would require another check in products table isProductAvailable where quantity>0
        if (Boolean.TRUE.equals(result)) {
            log.info("Placing a new order: {}", order);
            orderRepository.save(orderDAO);
        } else {
            log.warn("COULD NOT PLACE ORDER, CUSTOMER NOT FOUND");
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
