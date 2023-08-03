package com.orderService.business.mappers;

import com.orderService.business.repository.model.OrderDAO;
import com.orderService.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    @Mapping(source = "orderTime", target = "orderTime", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(source = "orderItemDAOList", target = "orderItems")
    Order daoToOrder(OrderDAO ordersDAO);
     @Mapping(source = "orderItems", target = "orderItemDAOList")
    OrderDAO orderToDAO(Order orders);

  //  List<Order> orderDAOListToOrderList(List<OrderDAO> orderDAOList);

}
