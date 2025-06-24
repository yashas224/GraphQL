package com.graphql.trial.graphql_trial.datafetchers;

import com.graphql.trial.graphql_trial.dto.CustomerCompleteResponse;
import com.graphql.trial.graphql_trial.service.CustomerOrderService;
import com.graphql.trial.graphql_trial.service.CustomerService;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Service
@AllArgsConstructor
public class CustomerOrderDataFetcher implements DataFetcher<Flux<CustomerCompleteResponse>> {

  CustomerService customerService;
  CustomerOrderService customerOrderService;

  private Flux<CustomerCompleteResponse> customerCompleteResponseFlux(DataFetchingEnvironment dataFetchingEnvironment) {
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

  @Override
  public Flux<CustomerCompleteResponse> get(DataFetchingEnvironment environment) throws Exception {
    System.out.println("Fetching data from custom DatFetcher : CustomerOrderDataFetcher");
    return customerCompleteResponseFlux(environment);
  }
}
