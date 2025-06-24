package com.graphql.trial.graphql_trial.controller;

import com.graphql.trial.graphql_trial.dto.CustomerEventDto;
import com.graphql.trial.graphql_trial.service.CustomerEventService;
import com.graphql.trial.graphql_trial.service.CustomerService;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

@Controller
@AllArgsConstructor
public class CustomerEventController {
  private CustomerEventService customerEventService;
  private CustomerService customerService;

  @SubscriptionMapping
  public Flux<CustomerEventDto> customerEvents() {
    return customerEventService.subscribe();
  }
}
