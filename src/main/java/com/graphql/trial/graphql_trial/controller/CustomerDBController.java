package com.graphql.trial.graphql_trial.controller;

import com.graphql.trial.graphql_trial.dto.CustomerDto;
import com.graphql.trial.graphql_trial.dto.DeleteStatus;
import com.graphql.trial.graphql_trial.exception.CustomerAgeException;
import com.graphql.trial.graphql_trial.service.CustomerService;
import graphql.GraphQLError;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.*;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
@AllArgsConstructor
public class CustomerDBController {

  CustomerService customerService;

  @GraphQlExceptionHandler
  public GraphQLError handleCustomerAgeException(CustomerAgeException ex) {
    return GraphQLError.newError().errorType(ErrorType.BAD_REQUEST).message(ex.getMessage()).build();
  }

  @QueryMapping
  public Flux<CustomerDto> customersDB(@ContextValue(required = false, name = "request-header-id") String headerValue) {
    headerValue = ObjectUtils.isEmpty(headerValue) ? "" : headerValue;
    System.out.println("Header Value Received by GraphQL controller method :".concat(headerValue));
    return customerService.getAllCustomersFromDB();
  }

  @QueryMapping
  public Mono<CustomerDto> customerByIdDB(@Argument int id) {
    return customerService.getCustomerByIdFromDB(id);
  }

  @MutationMapping
  public Mono<CustomerDto> createCustomerDB(@Argument(name = "customer") CustomerDto customerDto) {
    return customerService.createCustomerInDB(customerDto);
  }

  @MutationMapping
  public Mono<CustomerDto> updateCustomerDB(@Argument(name = "customer") CustomerDto dto, @Argument int id) {
    return customerService.updateCustomerInDB(id, dto);
  }

  @MutationMapping
  public Mono<DeleteStatus> deleteCustomerDB(@Argument int id) {
    return customerService.deleteCustomerDB(id);
  }
}
