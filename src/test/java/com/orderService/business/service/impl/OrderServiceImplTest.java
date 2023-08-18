package com.orderService.business.service.impl;

import com.orderService.business.mappers.OrderItemMapper;
import com.orderService.business.mappers.OrderMapper;
import com.orderService.business.repository.OrderRepository;
import com.orderService.business.repository.model.OrderDAO;
import com.orderService.business.repository.model.OrderItemDAO;
import com.orderService.client.Client;
import com.orderService.model.Order;
import com.orderService.model.OrderItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderMapper orderMapper;
    @Mock
    private OrderItemMapper orderItemMapper;
    @Mock
    private Client client;
    @InjectMocks
    private OrderServiceImpl orderService;
    private OrderDAO orderDAO;
    private OrderItemDAO orderItemDAO;
    private List<OrderItemDAO> orderItemDAOList;
    private List<OrderDAO> orderDAOList;
    private OrderItem orderItem;
    private List<OrderItem> orderItemList;
    private Order order;


    @BeforeEach
    public void init() {
        orderDAO = createOrderDAO(orderItemDAOList);
        orderItemDAO = createOrderItemDAO(orderDAO);
        orderItemDAOList = createOrderItemDAOList(orderItemDAO);
        orderDAOList = createOrderDAOList(orderDAO);
        orderItem = createOrderItem();
        orderItemList = createOrderItemList(orderItem);
        order = createOrder(orderItemList);

    }

    private OrderDAO createOrderDAO(List<OrderItemDAO> orderItemDAOList) {
        return new OrderDAO(1L, "ORD-12345678", 1L, LocalDateTime.now(), 20.00, orderItemDAOList);
    }

    private OrderItemDAO createOrderItemDAO(OrderDAO orderDAO) {
        return new OrderItemDAO(1L, orderDAO, 2L, 10.00, 2);
    }

    private List<OrderItemDAO> createOrderItemDAOList(OrderItemDAO orderItemDAO) {
        List<OrderItemDAO> list = new ArrayList<>();
        list.add(orderItemDAO);
        return list;
    }

    private List<OrderDAO> createOrderDAOList(OrderDAO orderDAO) {
        List<OrderDAO> list = new ArrayList<>();
        list.add(orderDAO);
        list.add(orderDAO);
        return list;
    }

    private Order createOrder(List<OrderItem> orderItemList) {
        return new Order(1L, "ORD-12345678", 1L, LocalDateTime.now(), 20.00, orderItemList);
    }

    private OrderItem createOrderItem() {
        return new OrderItem(1L, 1L, 2L, 10.00, 2);
    }

    private List<OrderItem> createOrderItemList(OrderItem orderItem) {
        List<OrderItem> list = new ArrayList<>();
        list.add(orderItem);
        return list;
    }

    @Test
    void placeOrderWhenCustomerDoesNotExist() {
        ResponseEntity<Object> customerResponse = ResponseEntity.notFound().build();
        when(client.checkCustomerExistence(anyLong())).thenReturn(customerResponse);
        Order result = orderService.placeOrder(order);
        assertNull(result);
        verify(client, times(1)).checkCustomerExistence(anyLong());
    }

    @Test
    void testGetAllOrderEntries_Successful() {
        when(orderRepository.findAll()).thenReturn(orderDAOList);
        when(orderMapper.daoToOrder(orderDAO)).thenReturn(order);
        List<Order> list = orderService.getAllOrders();
        assertEquals(2, list.size());
        assertEquals(order.getId(), list.get(0).getId());
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void testGetAllOrders_ListEmpty_Successful() {
        when(orderRepository.findAll()).thenReturn(Collections.emptyList());
        List<Order> result = orderService.getAllOrders();
        verify(orderRepository, times(1)).findAll();
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindOrderByOrderNumber_Successful() {
        when(orderRepository.findByOrderNumber(anyString())).thenReturn(Optional.of(orderDAO));
        when(orderMapper.daoToOrder(orderDAO)).thenReturn(order);
        Optional<Order> actualResult = orderService.findOrderByOrderNumber("ORD-12345678");
        assertTrue(actualResult.isPresent());
        assertEquals(order, actualResult.get());
        verify(orderRepository, times(1)).findByOrderNumber("ORD-12345678");
        verify(orderMapper, times(1)).daoToOrder(orderDAO);
    }

    @Test
    void testFindOrderByOrderNumber_NonExistingOrderNumber_Failed() {
        when(orderRepository.findByOrderNumber(anyString())).thenReturn(Optional.empty());
        Optional<Order> result = orderService.findOrderByOrderNumber("ORD-number12");
        assertFalse(result.isPresent());
        verify(orderRepository, times(1)).findByOrderNumber(anyString());
    }
}