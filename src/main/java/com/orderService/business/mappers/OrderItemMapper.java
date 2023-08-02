package com.orderService.business.mappers;

import com.orderService.business.repository.model.OrderItemDAO;
import com.orderService.model.OrderItem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {
    OrderItemDAO orderItemToDAO(OrderItem orderItem);

    OrderItem daoToOrderItem(OrderItemDAO orderItemDAO);
}
