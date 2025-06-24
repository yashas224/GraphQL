package com.graphql.trial.graphql_trial.util;

import com.graphql.trial.graphql_trial.dto.CustomerAction;
import com.graphql.trial.graphql_trial.dto.CustomerDto;
import com.graphql.trial.graphql_trial.dto.CustomerEventDto;
import com.graphql.trial.graphql_trial.entity.Customer;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class Utils {

  private static final ModelMapper modelMapper = new ModelMapper();

  public void log(String... str) {
    System.out.println("Thread :" + Thread.currentThread().getName() + "--" + str[0] + " :" + str[1] + " emitted at " + LocalDateTime.now());
  }

  public Customer toCustomer(CustomerDto customerDto) {
    return modelMapper.map(customerDto, Customer.class);
  }

  public CustomerDto toCustomerDto(Customer customer) {
    return modelMapper.map(customer, CustomerDto.class);
  }

}
