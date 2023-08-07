package com.orderService.business.mappers;

import com.orderService.business.repository.model.OrderDAO;
import com.orderService.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = OrderItemMapper.class)
public interface OrderMapper {
    @Mapping(source = "orderTime", target = "orderTime", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(source = "orderItems", target = "orderItemDAOList")
    OrderDAO orderToDAO(Order order);
    @Mapping(source = "orderItemDAOList", target = "orderItems")
    Order daoToOrder(OrderDAO orderDAO);
}
