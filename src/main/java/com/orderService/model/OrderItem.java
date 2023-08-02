package com.orderService.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@ApiModel(description = "Model of Order Item")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class OrderItem {

    @NotNull(message = "Order ID cannot be null")
    @ApiModelProperty(notes = "The unique ID of the order", example = "1")
    private Long orderId;

    @NotNull(message = "Product ID cannot be null")
    @ApiModelProperty(notes = "The unique ID of the product", example = "1001")
    private Long productId;

    @NotNull(message = "Item price cannot be null")
    @Positive(message = "Item price must be a positive number")
    @ApiModelProperty(notes = "The price of the item", example = "12.50")
    private Double itemPrice;

    @NotNull(message = "Quantity cannot be null")
    @Positive(message = "Quantity must be a positive number")
    @ApiModelProperty(notes = "The quantity of the item", example = "3")
    private Integer quantity;
}
