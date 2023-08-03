package com.orderService.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@ApiModel(description = "Model of Orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class Order {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @ApiModelProperty(notes = "The unique ID of the order", example = "1")
    private Long id;

    @NotBlank(message = "Order Number cannot be blank or null")
    @Size(max = 40, message = "Order number length must not exceed 40 characters")
    @ApiModelProperty(notes = "The unique order number", example = "ORD12345") // THIS EXAMPLE MIGHT CHANGE!!!
    private String orderNumber;

    @NotNull(message = "Customer ID cannot be null")
    @ApiModelProperty(notes = "The customer ID", example = "1")
    private Long customerId;

    @NotNull(message = "Order time cannot be null")
    @ApiModelProperty(notes = "The order time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime orderTime;

    @NotNull(message = "Total price cannot be null")
    @Positive(message = "Total price must be a positive number")
    @ApiModelProperty(notes = "The total price of the order", example = "50.25")
    private Double totalPrice;

    private List<OrderItem> orderItems;
}
