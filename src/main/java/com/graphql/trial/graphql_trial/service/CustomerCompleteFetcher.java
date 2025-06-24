package com.graphql.trial.graphql_trial.service;

import com.graphql.trial.graphql_trial.dto.CustomerCompleteResponse;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Service
@AllArgsConstructor
public class CustomerCompleteFetcher {

  CustomerService customerService;
  CustomerOrderService customerOrderService;

  public Flux<CustomerCompleteResponse> customerCompleteResponseFlux(DataFetchingEnvironment dataFetchingEnvironment) {
    System.out.println("Fetching from CustomerCompleteFetcher");
    var isOrderSelected = dataFetchingEnvironment.getSelectionSet().contains("orders");
    return customerService.getAllWithDelay()
       .map(c -> CustomerCompleteResponse.createObj(c, Collections.emptyList()))
       .transform(customerCompleteResponseFlux ->
          isOrderSelected ? customerCompleteResponseFlux.flatMapSequential(this::getOrders)
             : customerCompleteResponseFlux
       );
  }

  private Mono<CustomerCompleteResponse> getOrders(CustomerCompleteResponse customerCompleteResponse) {
    return customerOrderService.orderByCustomerIdDelay(customerCompleteResponse.getId())
       .collectList()
       .doOnNext(customerCompleteResponse::setOrders)
       .thenReturn(customerCompleteResponse);
  }
}
