package com.orderService.business.service.impl;

import com.orderService.business.mappers.OrderMapper;
import com.orderService.business.repository.OrderRepository;
import com.orderService.business.repository.model.OrderDAO;
import com.orderService.business.service.OrderService;
import com.orderService.client.Client;
import com.orderService.model.Order;
import com.orderService.model.OrderItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    private Client client;

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
        ResponseEntity<Object> customerResponse = client.checkCustomerExistence(order.getCustomerId());
        if (customerResponse.getStatusCode() == HttpStatus.OK) {
            List<Long> productIds = getProductIds(order);
            ResponseEntity<Map<Long, Double>> productInfoResponse = client.getProductInfo(productIds);
            if (productInfoResponse.getStatusCode() == HttpStatus.OK) {
                Map<Long, Double> productInfo = productInfoResponse.getBody();
                if (productInfo.keySet().containsAll(productIds)) {
                    OrderDAO orderDAO = createOrderDAO(order, productInfo);
                    setOrderDAOForOrderItems(orderDAO, productInfo);
                    calculateOrderTotalPrice(order, orderDAO, productInfo);
                    Order savedOrder = saveOrder(orderDAO);
                    log.info("Order placed" + savedOrder);
                    return savedOrder;
                } else {
                    List<Long> existingProductList = new ArrayList<>(productInfo.keySet());
                    handleMissingProducts(productIds, existingProductList);
                }
            }
        }
        log.info("Order not placed");
        return null;
    }


    /**
     * Extracts the productIds from the order's orderItems.
     *
     * @param order The order from which to extract product IDs.
     * @return List of productIds.
     */
    public List<Long> getProductIds(Order order) {
        return order.getOrderItems().stream()
                .map(OrderItem::getProductId)
                .collect(Collectors.toList());
    }

    /**
     * Creates an OrderDAO object from the given Order.
     *
     * @param order The Order object to be converted to OrderDAO.
     * @return The created OrderDAO object.
     */

    public OrderDAO createOrderDAO(Order order, Map<Long, Double> productInfo) {
        OrderDAO orderDAO = orderMapper.orderToDAO(order);
        setOrderDAOForOrderItems(orderDAO, productInfo);
        return orderDAO;
    }

    /**
     * Sets the OrderDAO for each OrderItemDAO in the list.
     * This method ensures that each OrderItemDAO in the list of the current OrderDAO knows which order it belongs to.
     *
     * @param orderDAO The OrderDAO object to which to set OrderItemDAOs.
     */

    public void setOrderDAOForOrderItems(OrderDAO orderDAO, Map<Long, Double> productInfo) {
        if (orderDAO.getOrderItemDAOList() != null) {
            orderDAO.getOrderItemDAOList().forEach(item -> {
                item.setOrderDAO(orderDAO);
                item.setItemPrice(productInfo.get(item.getProductId()));
            });
        }
    }

    /**
     * Saves the OrderDAO object and maps it back to the Order model.
     *
     * @param orderDAO The OrderDAO object to be saved.
     * @return The Order object after saving.
     */
    public Order saveOrder(OrderDAO orderDAO) {
        return orderMapper.daoToOrder(orderRepository.save(orderDAO));
    }

    /**
     * Handles not existing products by log displaying a warning message after existing products are removed.
     *
     * @param productIds       The list of productIds that were not found.
     * @param existingProducts The list of existing productIds.
     */
    public void handleMissingProducts(List<Long> productIds, List<Long> existingProducts) {
        List<Long> missingProducts = new ArrayList<>(productIds);
        missingProducts.removeAll(existingProducts);
        log.warn("COULD NOT PLACE ORDER, SOME PRODUCTS ID'S DON'T EXIST: {}", missingProducts);
    }

    /**
     * Autogenerates an OrderNumber for the Order which is to be placed.
     *
     * @param order Order object for which to generate the OrderNumber.
     */
    public void autogenerateOrderNumber(Order order) {
        String orderNumber = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        order.setOrderNumber(orderNumber);
    }

    /**
     * Autogenerates the order time for the Order which is to be placed.
     *
     * @param order The Order object for which to set the OrderTime.
     */
    public void autogenerateOrderTime(Order order) {
        order.setOrderTime(LocalDateTime.now());
    }

    /**
     * Calculates the total price of the provided Order based on product price from Product service,
     * and sets the total price in the OrderDAO.
     *
     * @param order    Order object for which to calculate the total price.
     * @param orderDAO OrderDAO in which to set the calculated total price.
     */

    public void calculateOrderTotalPrice(Order order, OrderDAO orderDAO, Map<Long, Double> productInfo) {
        double totalPrice = order.getOrderItems().stream()
                .mapToDouble(item -> productInfo.get(item.getProductId()) * item.getQuantity())
                .sum();
        orderDAO.setTotalPrice(totalPrice);
    }
}
