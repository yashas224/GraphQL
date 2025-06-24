package com.graphql.trial.graphql_trial.service;

import com.graphql.trial.graphql_trial.dto.CustomerAction;
import com.graphql.trial.graphql_trial.dto.CustomerDto;
import com.graphql.trial.graphql_trial.dto.CustomerEventDto;
import com.graphql.trial.graphql_trial.dto.DeleteStatus;
import com.graphql.trial.graphql_trial.entity.Customer;
import com.graphql.trial.graphql_trial.exception.CustomerAgeException;
import com.graphql.trial.graphql_trial.repository.CustomerRepository;
import com.graphql.trial.graphql_trial.util.Utils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class CustomerService {
  Utils utils;
  CustomerRepository customerRepository;
  CustomerEventService customerEventService;

  private final Flux<Customer> flux = Flux.just(Customer.create(1, "John Doe", "New York", 30),
     Customer.create(2, "Jane Smith", "Los Angeles", 25),
     Customer.create(3, "Alice Johnson", "Chicago", 55),
     Customer.create(4, "Bob Brown", "Houston", 35),
     Customer.create(5, "Charlie Davis", "Phoenix", 60));

  public Flux<Customer> getAll() {
    return flux;
  }

  public Flux<Customer> getAllWithDelay() {
    return flux.delayElements(Duration.ofMillis(1000)).doOnNext(c -> utils.log("customer", c.getId().toString()));
  }

  public Mono<Customer> getCustomerById(int id) {
    return flux.filter(customer -> customer.getId() == id).next();
  }

  public Flux<Customer> getCustomerByName(String name) {
    return flux.filter(customer -> customer.getName().equalsIgnoreCase(name));
  }

  // controller methods / handler functions at CustomerDBController
  public Flux<CustomerDto> getAllCustomersFromDB() {
    return customerRepository.findAll()
       .map(utils::toCustomerDto);
  }

  public Mono<CustomerDto> getCustomerByIdFromDB(int id) {
    return customerRepository.findById(id).switchIfEmpty(Mono.error(new NoSuchElementException("No customer found!!"))).map(utils::toCustomerDto);
  }

  public Mono<CustomerDto> createCustomerInDB(CustomerDto customerDto) {
    return Mono.just(customerDto)
       .filter(c -> c.getAge() > 18)
       .flatMap(customerDto1 -> customerRepository.save(utils.toCustomer(customerDto)))
       .map(utils::toCustomerDto)
       .switchIfEmpty(Mono.error(new CustomerAgeException("Customer Age less than 18", customerDto.getAge())))
       .doOnNext(c -> emitCustomerEvent(c.getId(), CustomerAction.CREATED));
  }

  private void emitCustomerEvent(int id, CustomerAction action) {
    customerEventService.emitEvent(CustomerEventDto.createCustomerEventDto(id, action));
  }

  public Mono<CustomerDto> updateCustomerInDB(int id, CustomerDto customerDto) {
    return customerRepository.findById(id)
       .map(c -> {
         Customer customer = utils.toCustomer(customerDto);
         customer.setId(id);
         return customer;
       })
       .flatMap(customerRepository::save)
       .map(utils::toCustomerDto)
       .doOnNext(c -> emitCustomerEvent(c.getId(), CustomerAction.UPDATED));
  }

  public Mono<DeleteStatus> deleteCustomerDB(int id) {
    return customerRepository.deleteById(id)
       .thenReturn(DeleteStatus.SUCCESS)
       .onErrorReturn(DeleteStatus.FAILURE)
       .doOnNext(c -> emitCustomerEvent(id, CustomerAction.DELETED));
  }
}
