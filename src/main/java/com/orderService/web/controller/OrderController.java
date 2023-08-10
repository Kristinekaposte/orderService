package com.orderService.web.controller;

import com.orderService.business.service.OrderService;
import com.orderService.model.Order;
import com.orderService.swagger.DescriptionVariables;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Api(tags = DescriptionVariables.ORDERS)
@Slf4j
@AllArgsConstructor
@RequestMapping("api/v1/orders")
@RestController
public class OrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping("/allOrders")
    @ApiOperation(value = "Finds all Order entries",
            notes = "Returns all Order entries from the database",
            response = Order.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The request has succeeded"),
            @ApiResponse(code = 500, message = "Server error")})
    public ResponseEntity<List<Order>> getAllOrderEntries() {
        List<Order> list = orderService.getAllOrders();
        if (list.isEmpty()) {
            log.info("Empty Order list found");
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
        }
        log.info("List size: {}", list.size());
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping("/getByOrderNumber/{orderNumber}")
    @ApiOperation(value = "Find a order by orderNumber",
            notes = "Returns a order entry with related order items based on the provided orderNumber",
            response = Order.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The request has succeeded"),
            @ApiResponse(code = 404, message = "The server has not found anything matching the Request-URI"),
            @ApiResponse(code = 500, message = "Server error")})

    public ResponseEntity<Order> getOrderByOrderNumber(@ApiParam(value = "orderNumber of the order entry", required = true)
                                                       @PathVariable("orderNumber") String orderNumber) {
        Optional<Order> orderOptional = orderService.findOrderByOrderNumber(orderNumber);
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            log.info("Found order with orderNumber {}: {}", orderNumber, order);
            return ResponseEntity.status(HttpStatus.OK).body(order);
        }
        log.warn("order not found with orderNumber: {}", orderNumber);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).header(
                "Message", "order not found with orderNumber: " + orderNumber).build();
    }

    @PostMapping("/placeOrder")
    @CircuitBreaker(name = "OrderService", fallbackMethod ="fallbackMethod" )
    @ApiOperation(value = "Place a new order",
            notes = "Creates and places a new order in the DB",
            response = Order.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The request has create successfully"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "The server has not found anything matching the Request-URI"),
            @ApiResponse(code = 500, message = "Server error")
    })
    public ResponseEntity<Order> placeOrder(@RequestBody @Valid Order order) {
        Order response = orderService.placeOrder(order);
        if (response != null) {
            log.info("Order placed successfully");
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }
            log.warn("Order placement failed");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).header(
                    "Message", "Order placement failed some products does not exist.").build();
    }

    public ResponseEntity<Order> fallbackMethod (Order order, RuntimeException runtimeException){
        log.warn("Something went wrong, FallBack was called " + runtimeException.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).header(
                "Message", "Order placement failed something went wrong. Please try again later.").build();
    }
}
