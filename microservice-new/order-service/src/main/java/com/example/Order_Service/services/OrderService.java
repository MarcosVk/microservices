package com.example.Order_Service.services;

import com.example.Order_Service.dto.InventoryResponse;
import com.example.Order_Service.dto.OrderLineItemsDto;
import com.example.Order_Service.dto.OrderRequest;
import com.example.Order_Service.model.OrderLineItems;
import com.example.Order_Service.repository.OrderRespository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.Order_Service.model.Order;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    private final OrderRespository orderRespository;
    private final WebClient webClient;

    public void placeOrder(OrderRequest orderRequest){

        Order order=new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        List<OrderLineItems> orderLineItemsList=orderRequest.getOrderLineItemsDtoList()
                .stream().map(this::mapToDto).toList();

        order.setOrderLineItemsList(orderLineItemsList);

        List<String> skuCode=order.getOrderLineItemsList().stream()
                .map(OrderLineItems::getSkuCode).toList();

        InventoryResponse[] inventoryResponsesArray=webClient.get().uri("http://localhost:8082/api/inventory",uriBuilder->
                uriBuilder.queryParam("skuCode",skuCode).build())
                        .retrieve()
                        .bodyToMono(InventoryResponse[].class)
                        .block();

        Boolean allProductsIsInStock=Arrays.stream(inventoryResponsesArray)
                .allMatch(InventoryResponse::getIsInStock);

        if(allProductsIsInStock){
            orderRespository.save(order);
        }
        else{
            throw new IllegalArgumentException("Product is not in stock, please try again later");
        }
    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems=new OrderLineItems();

        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());

        return orderLineItems;
    }

}
