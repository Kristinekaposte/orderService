package com.orderService.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderService.business.service.OrderService;
import com.orderService.model.Order;
import com.orderService.model.OrderItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerTest {
    @MockBean
    private OrderService orderService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    public static final String URL = "/api/v1/orders";
    public static final String URL1 = URL + "/allOrders";
    public static final String URL2 = URL + "/getByOrderNumber";
    public static final String URL3 = URL + "/placeOrder";
    private List<Order> orderListResponse;
    private Order orderResponse;
    private OrderItem orderItem;
    private List<OrderItem> orderItemList;
    private Order orderRequest;
    private OrderItem orderItemRequest;
    private List<OrderItem> orderItemListRequest;
    private Order orderRequestEmptyOrderItemsList;


    @BeforeEach
    public void init() {
        orderItem = createOrderItem();
        orderItemList = createOrderItemList(orderItem);
        orderResponse = createOrderResponse(orderItemList);
        orderListResponse = createOrderList(orderResponse);
        orderItemRequest = createOrderItemRequest();
        orderItemListRequest = createOrderItemListRequest(orderItemRequest);
        orderRequest = createOrderRequest(orderItemListRequest);
        orderRequestEmptyOrderItemsList = createOrderRequestEmptyOrderItemsList();
    }

    @Test
    void getAllOrders_Successful() throws Exception {
        when(orderService.getAllOrders()).thenReturn(orderListResponse);
        mockMvc.perform(get(URL1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].orderNumber", is("ORD12345678")))
                .andExpect(jsonPath("$[0].customerId", is(1)))
                .andExpect(jsonPath("$[0].orderTime", is(formatLocalDateTime(orderResponse.getOrderTime()))))
                .andExpect(jsonPath("$[0].totalPrice", is(50.25)))
                .andExpect(jsonPath("$[0].orderItems", hasSize(1)));
        verify(orderService, times(1)).getAllOrders();
    }

    @Test
    void getOrderByOrderNumber_OrderNumberExists_Successful() throws Exception {
        when(orderService.findOrderByOrderNumber("ORD12345678")).thenReturn(Optional.of(orderResponse));
        mockMvc.perform(get(URL2 + "/{orderNumber}", "ORD12345678"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.orderNumber", is("ORD12345678")))
                .andExpect(jsonPath("$.customerId", is(1)))
                .andExpect(jsonPath("$.orderTime", is(formatLocalDateTime(orderResponse.getOrderTime()))))
                .andExpect(jsonPath("$.totalPrice", is(50.25)))
                .andExpect(jsonPath("$.orderItems", hasSize(1)));
        verify(orderService, times(1)).findOrderByOrderNumber("ORD12345678");
    }

    @Test
    void getAllOrders_WhenListEmpty_Successful() throws Exception {
        when(orderService.getAllOrders()).thenReturn(Collections.emptyList());
        mockMvc.perform(get(URL1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
        verify(orderService, times(1)).getAllOrders();
    }

    @Test
    void getOrderByOrderNumber_OrderNumberDoesNotExist_UnSuccessful() throws Exception {
        String orderNumber = "ORD12345";
        when(orderService.findOrderByOrderNumber(orderNumber)).thenReturn(Optional.empty());
        mockMvc.perform(get(URL2 + "/{orderNumber}", orderNumber))
                .andExpect(status().isNotFound())
                .andExpect(header().string("Message", "order not found with orderNumber: " + orderNumber));
        verify(orderService, times(1)).findOrderByOrderNumber("ORD12345");
    }

    @Test
    void placeOrder_Successful() throws Exception {
        when(orderService.placeOrder(any())).thenReturn(orderRequest);
        mockMvc.perform(post(URL3)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customerId").value(orderRequest.getCustomerId().intValue()))
                .andExpect(jsonPath("$.orderItems", hasSize(orderRequest.getOrderItems().size())));
        verify(orderService, times(1)).placeOrder(orderRequest);
    }

    @Test
    void placeOrder_WhenSomeProductsDoNotExist_UnSuccessful() throws Exception {
        when(orderService.placeOrder(orderRequestEmptyOrderItemsList)).thenReturn(null);
        mockMvc.perform(post(URL3)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequestEmptyOrderItemsList)))
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Message", "Order placement failed some products does not exist."))
                .andReturn();
        verify(orderService, times(1)).placeOrder(orderRequestEmptyOrderItemsList);
    }

    @Test
    void placeOrder_WhenExceptionOccurs_FallbackMethodIsCalled_UnSuccessful() throws Exception {
        when(orderService.placeOrder(any(Order.class))).thenThrow(new RuntimeException("Something went wrong"));
        mockMvc.perform(post(URL3)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(header().string("Message", "Order placement failed something went wrong. Please try again later."))
                .andReturn();
    }

    private Order createOrderResponse(List<OrderItem> orderItemList) {
        return new Order(1L, "ORD12345678", 1L, LocalDateTime.of(2000, 10, 20, 14, 44, 55), 50.25, orderItemList);
    }

    private List<Order> createOrderList(Order order) {
        List<Order> list = new ArrayList<>();
        list.add(order);
        return list;
    }

    private OrderItem createOrderItem() {
        return new OrderItem(1L, 1L, 2L, 10.00, 2);
    }

    private List<OrderItem> createOrderItemList(OrderItem orderItem) {
        List<OrderItem> list = new ArrayList<>();
        list.add(orderItem);
        return list;
    }

    private Order createOrderRequest(List<OrderItem> orderItemsRequest) {
        return new Order(null, null, 1L, null, null, orderItemsRequest);
    }

    private Order createOrderRequestEmptyOrderItemsList() {
        return new Order(null, null, 1L, null, null, new ArrayList<>());
    }

    private OrderItem createOrderItemRequest() {
        return new OrderItem(null, null, 2L, null, 2);
    }

    private List<OrderItem> createOrderItemListRequest(OrderItem orderItemRequest) {
        List<OrderItem> orderItemsRequest = new ArrayList<>();
        orderItemsRequest.add(orderItemRequest);
        return orderItemsRequest;
    }

    private String formatLocalDateTime(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
