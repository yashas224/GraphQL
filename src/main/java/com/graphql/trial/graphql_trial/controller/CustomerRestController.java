package com.graphql.trial.graphql_trial.controller;

import com.graphql.trial.graphql_trial.dto.CustomerCompleteResponse;
import com.graphql.trial.graphql_trial.service.CustomerOrderService;
import com.graphql.trial.graphql_trial.service.CustomerService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/rest-api")
@AllArgsConstructor
public class CustomerRestController {

  CustomerService customerService;
  CustomerOrderService customerOrderService;

  //  Compare REST API and GraphQL
  @GetMapping("/customers")
  public Flux<CustomerCompleteResponse> getAllCustomers() {
    return customerService.getAllWithDelay()
       .flatMap(customer ->
          customerOrderService.orderByCustomerIdDelay(customer.getId())
             .collectList()
             .map(listObj -> CustomerCompleteResponse.createObj(customer, listObj))
       );
  }
}
