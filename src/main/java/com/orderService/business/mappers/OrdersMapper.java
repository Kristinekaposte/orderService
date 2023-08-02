package com.orderService.business.mappers;

import com.orderService.business.repository.model.OrdersDAO;
import com.orderService.model.Orders;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrdersMapper {

    OrdersDAO ordersToDAO(Orders orders);

    Orders daoToOrders (OrdersDAO ordersDAO);
}
