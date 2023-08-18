package com.orderService.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

@Component
public class Client {

    @Autowired
    private  WebClient webClient;


    /**
     * Checks if a customer with the given ID exists from CustomerService endpoint.
     *
     * @param customerId The customer id to check.
     * @return ResponseEntity containing info about if customer was found by id.
     */
    public ResponseEntity<Object> checkCustomerExistence(Long customerId) {
        return webClient.get()
                .uri("http://localhost:5050/api/v1/customer/getById/{id}", customerId)
                .retrieve()
                .toEntity(Object.class)
                .block();
    }

    /**
     * Checks the existence of the productIds in the products service.
     *
     * @param productIds The list of productIds to check.
     * @return A ResponseEntity containing the list of existing productIds.
     */
    public ResponseEntity<Map<Long, Double>> getProductInfo(List<Long> productIds) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString("http://localhost:5051/api/v1/products/getProductInfo")
                .queryParam("productIds", productIds);

        return webClient.get()
                .uri(uriBuilder.toUriString())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<Map<Long, Double>>() {
                })
                .block();
    }

}
