package com.graphql.trial.graphql_trial.controller;

import com.graphql.trial.graphql_trial.dto.AgeFilter;
import com.graphql.trial.graphql_trial.dto.CustomerCompleteResponse;
import com.graphql.trial.graphql_trial.entity.Customer;
import com.graphql.trial.graphql_trial.entity.CustomerOrderObject;
import com.graphql.trial.graphql_trial.service.CustomerCompleteFetcher;
import com.graphql.trial.graphql_trial.service.CustomerOrderService;
import com.graphql.trial.graphql_trial.service.CustomerService;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.DataFetchingFieldSelectionSet;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.*;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@Controller
@AllArgsConstructor
public class CustomerController {

  private CustomerService customerService;
  private CustomerOrderService customerOrderService;
  private CustomerCompleteFetcher customerCompleteFetcher;

/*
  @QueryMapping
  public Flux<Customer> customers(DataFetchingFieldSelectionSet dataFetchingFieldSelectionSet) {
    System.out.println("Customer selection set : " + dataFetchingFieldSelectionSet.getFields());
    return customerService.getAll();
  }
*/

//  @QueryMapping
//  public Flux<Customer> customers(DataFetchingEnvironment dataFetchingEnvironment) {
//    System.out.println("Customer Document  : " + dataFetchingEnvironment.getDocument());
//    System.out.println("Customer Operation Executed : " + dataFetchingEnvironment.getOperationDefinition());
//    return customerService.getAll();
//  }

  // start  Compare REST API and GraphQL
//  @QueryMapping
//  public Flux<Customer> customers(DataFetchingEnvironment dataFetchingEnvironment) {
//    System.out.println("Customer Document  : " + dataFetchingEnvironment.getDocument());
//    return customerService.getAllWithDelay();
//  }
//
//  // individual order is requested after  all  the  customers are emitted.
//  @SchemaMapping
//  public Flux<CustomerOrderObject> orders(Customer customer) {
//    System.out.println("Thread :" + Thread.currentThread().getName() + "--" + "Retrieving Order details  for customer" + customer.getId() + " emitted at " + LocalDateTime.now() + customer.getId().toString());
//    return customerOrderService.orderByCustomerIdDelay(customer.getId());
//  }
  //  end Compare REST API and GraphQL

  // setting order details by ourselves in the CustomerCompleteResponse  and not by having a separate handler function to fetch order like above
//  @QueryMapping
//  public Flux<CustomerCompleteResponse> customers(DataFetchingEnvironment dataFetchingEnvironment) {
//    System.out.println("Customer Document  : " + dataFetchingEnvironment.getDocument());
//    return customerCompleteFetcher.customerCompleteResponseFlux(dataFetchingEnvironment);
//  }

  @QueryMapping
  public Mono<Customer> customerById(@Argument int id) {
    return customerService.getCustomerById(id);
  }

  @QueryMapping
  public Flux<Customer> customerByName(@Argument String name) {
    return customerService.getCustomerByName(name);
  }

  @QueryMapping
  public Flux<Customer> customersInAgeRange(@Arguments Map<String, Integer> map) {
    int min = map.get("min");
    int max = map.get("max");
    return customerService.getAll().filter(customer -> {
      int age = customer.getAge();
      return age >= min && age <= max;
    });
  }

  @QueryMapping
  public Flux<Customer> customersInAgeRangeType(@Argument(name = "filter") @Valid AgeFilter ageFilter) {
    return customerService.getAll().filter(customer -> {
      int age = customer.getAge();
      return age >= ageFilter.min() && age <= ageFilter.max();
    });
  }

  // N+1 problem introduced
  // each  customer invokes  this method/resolver Function when "orders" field of "Customer Type"  is requested
  @SchemaMapping(field = "ordersLimit")
  public Flux<CustomerOrderObject> customerOrders(Customer customer, @Argument int limit, DataFetchingFieldSelectionSet dataFetchingFieldSelectionSet) {
    System.out.println("ordersLimit selection set : " + dataFetchingFieldSelectionSet.getFields());
    System.out.println("Retrieving Order details for customer Id ".concat(customer.getId().toString()));
    return customerOrderService.orderByCustomerId(customer.getId()).take(limit);
  }



/*
//DataFetchingEnvironment  provides entire env for the parent field
  @SchemaMapping(field = "ordersLimit")
  public Flux<CustomerOrderObject> customerOrders(Customer customer, @Argument int limit, DataFetchingEnvironment dataFetchingEnvironment) {
    System.out.println("ordersLimit Document : " + dataFetchingEnvironment.getDocument());
    System.out.println("Retrieving Order details for customer Id ".concat(customer.getId().toString()));
    return customerOrderService.orderByCustomerId(customer.getId()).take(limit);
  }*/

  // Option 1
  // returning List of results having same size and sequence as input List of Objects
//  @BatchMapping(field = "orders")
//  public Flux<List<CustomerOrderObject>> customerOrdersBatching(List<Customer> customerList) {
//    System.out.println("Retrieving Order details for customers ".concat(customerList.toString()));
//    return customerOrderService.getByCustomerIds(customerList.stream().map(Customer::getId).toList());
//  }

//   size issue in customerList and returned Flux<List<CustomerOrderObject>>
/*  @BatchMapping(field = "orders")
  public Flux<List<CustomerOrderObject>> customerOrdersBatching(List<Customer> customerList) {
    System.out.println("Retrieving Order details for customers ".concat(customerList.toString()));
    return customerOrderService.getByCustomerIdsSizeIssue(customerList.stream().map(Customer::getId).toList());
  }*/

//  // order issue
//  @BatchMapping(field = "orders")
//  public Flux<List<CustomerOrderObject>> customerOrdersBatching(List<Customer> customerList) {
//    System.out.println("Retrieving Order details for customers ".concat(customerList.toString()));
//    return customerOrderService.getByCustomerIdsOrderIssue(customerList.stream().map(Customer::getId).toList());
//  }

  // option 2
//  returning Mono of Map
//
//  @BatchMapping(field = "orders")
//  public Mono<Map<Customer, List<CustomerOrderObject>>> customerOrdersBatchingMap(List<Customer> customerList) {
//    System.out.println("Retrieving Order details for customers- customerOrdersBatchingMap ".concat(customerList.toString()));
//    return customerOrderService.fetchDataAsMap(customerList);
//  }
}
