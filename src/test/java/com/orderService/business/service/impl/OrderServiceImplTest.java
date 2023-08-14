package com.orderService.business.service.impl;

import com.orderService.business.mappers.OrderItemMapper;
import com.orderService.business.mappers.OrderMapper;
import com.orderService.business.repository.OrderRepository;
import com.orderService.business.repository.model.OrderDAO;
import com.orderService.business.repository.model.OrderItemDAO;
import com.orderService.model.Order;
import com.orderService.model.OrderItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderMapper orderMapper;
    @Mock
    private OrderItemMapper orderItemMapper;
    @InjectMocks
    private OrderServiceImpl orderService;

    private List<OrderItemDAO> orderItemDAOList;
    private OrderDAO orderDAO;
    private OrderItemDAO orderItemDAO;
    private List<OrderDAO> orderDAOList;
    //___________________order___________________
    private List<OrderItem> orderItemList;
    private Order order;
    private OrderItem orderItem;

    @BeforeEach
    public void init() {
        orderItemDAO = createOrderItemDAO(orderDAO);
        orderItemDAOList = createOrderItemDAOList(orderItemDAO);
        orderDAO = createOrderDAO(orderItemDAOList);
        orderDAOList =createOrderDAOList(orderDAO);
        //____________________________________________
        orderItem = createOrderItem(order);
        orderItemList = createOrderItemList(orderItem);
        order = createOrder(orderItemList);

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


    private OrderDAO createOrderDAO(List<OrderItemDAO> orderItemDAOList) {
        return new OrderDAO(1L, "ORD-12345678", 1L, LocalDateTime.now(), 20.00, orderItemDAOList);
    }

    private OrderItemDAO createOrderItemDAO(OrderDAO orderDAO) {
        return new OrderItemDAO(1L,orderDAO,2L,10.00,2);
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
    // ORDER

    private Order createOrder(List<OrderItem> orderItemList) {
        return new Order(1L, "ORD-12345678", 1L, LocalDateTime.now(), 20.00, orderItemList);
    }

    private OrderItem createOrderItem(Order order) {
        return new OrderItem(1L,1L,2L,10.00,2);
    }

    private List<OrderItem> createOrderItemList(OrderItem orderItem) {
        List<OrderItem> list = new ArrayList<>();
        list.add(orderItem);
        return list;
    }

}
