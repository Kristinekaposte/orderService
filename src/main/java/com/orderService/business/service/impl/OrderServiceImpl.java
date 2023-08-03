package com.orderService.business.service.impl;

import com.orderService.business.mappers.OrderMapper;
import com.orderService.business.repository.OrderRepository;
import com.orderService.business.repository.model.OrderDAO;
import com.orderService.business.service.OrderService;
import com.orderService.model.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderMapper orderMapper;

//    @Override
//    public List<Order> getAllOrders() {
//        List<OrderDAO> orderDAOList = orderRepository.findAll();
//        return orderMapper.orderDAOListToOrderList(orderDAOList);
//    }

    @Override
    public List<Order> getAllOrders() {
        List<Order> list = orderRepository.findAll()
                .stream()
                .map(orderMapper::daoToOrder)
                .collect(Collectors.toList());
        log.info("Size of the Order list: {}", list.size());
        return list;
    }


}
