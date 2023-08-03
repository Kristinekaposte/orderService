package com.orderService.business.mappers;

import com.orderService.business.repository.model.OrderItemDAO;
import com.orderService.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    @Mapping(source = "orderId", target = "orderDAOId")
    OrderItemDAO orderItemToDAO(OrderItem orderItem);

    @Mapping(source = "orderDAOId", target = "orderId")
    OrderItem daoToOrderItem(OrderItemDAO orderItemDAO);

}
