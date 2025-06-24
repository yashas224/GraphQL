package com.graphql.trial.graphql_trial.service;

import com.graphql.trial.graphql_trial.entity.Customer;
import com.graphql.trial.graphql_trial.entity.CustomerOrderObject;
import com.graphql.trial.graphql_trial.util.Utils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.time.Duration;
import java.util.*;

@Service
@AllArgsConstructor
public class CustomerOrderService {
  Utils utils;
  private static final Map<Integer, List<CustomerOrderObject>> map = Map.of(
     1, List.of(CustomerOrderObject.create(UUID.randomUUID(), "product-1"),
        CustomerOrderObject.create(UUID.randomUUID(), "product-2"),
        CustomerOrderObject.create(UUID.randomUUID(), "product-9")
     ),
     2, List.of(CustomerOrderObject.create(UUID.randomUUID(), "product-3"),
        CustomerOrderObject.create(UUID.randomUUID(), "product-4"),
        CustomerOrderObject.create(UUID.randomUUID(), "product-10")
     )
  );

  public Flux<CustomerOrderObject> orderByCustomerId(int id) {
    return Flux.fromIterable(map.getOrDefault(id, Collections.emptyList()));
  }

  public Flux<CustomerOrderObject> orderByCustomerIdDelay(int id) {
    return Flux.fromIterable(map.getOrDefault(id, Collections.emptyList())).delayElements(Duration.ofSeconds(1)).doOnNext(o -> utils.log("order", o.getId().toString()));
  }

  public Flux<List<CustomerOrderObject>> getByCustomerIds(List<Integer> idList) {
    return Flux.fromIterable(idList)
       .map(id -> map.getOrDefault(id, Collections.emptyList()));
  }

  /*
     Size mismatch
     DataLoaderAssertionException: The size of the promised values MUST be the same size as the key list
     @BatchMapping needs the returned List result size and sequence order same as input List
     flatMap doesn't publish empty Mono when Customer Order is not found
     -  getCustomerOrder can return an empty
  */
  public Flux<List<CustomerOrderObject>> getByCustomerIdsSizeIssue(List<Integer> idList) {
    return Flux.fromIterable(idList)
       .flatMap(this::getCustomerOrder);
  }

  public Mono<List<CustomerOrderObject>> getCustomerOrder(int id) {
    return Mono.justOrEmpty(map.get(id));
  }

  /* order issue
  flatMap is an async operation and Doesn't guarantee ordering.
  To simulate that a random delay is induced in getCustomerOrderrderIssue()
    */
  public Flux<List<CustomerOrderObject>> getByCustomerIdsOrderIssue(List<Integer> idList) {
    return Flux.fromIterable(idList)
       .flatMap(id -> getCustomerOrderrderIssue(id).defaultIfEmpty(Collections.emptyList()));
  }

  public Mono<List<CustomerOrderObject>> getCustomerOrderrderIssue(int id) {
    return Mono.justOrEmpty(map.get(id)).delayElement(Duration.ofMillis(new Random().nextLong(1000)));
  }

  public Mono<Map<Customer, List<CustomerOrderObject>>> fetchDataAsMap(List<Customer> customerList) {
    return Flux.fromIterable(customerList)
       .flatMap(
          customer -> Mono.just(Tuples.of(customer, map.getOrDefault(customer.getId(), Collections.emptyList())))
       )
       .collectMap(Tuple2::getT1, Tuple2::getT2);
  }
}
