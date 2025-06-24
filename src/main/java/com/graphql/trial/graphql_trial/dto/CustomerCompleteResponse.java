package com.graphql.trial.graphql_trial.dto;

import com.graphql.trial.graphql_trial.entity.Customer;
import com.graphql.trial.graphql_trial.entity.CustomerOrderObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor(staticName = "create")
@NoArgsConstructor
public class CustomerCompleteResponse extends Customer {

  private List<CustomerOrderObject> orders;

  public static CustomerCompleteResponse createObj(Customer customer, List<CustomerOrderObject> list) {
    System.out.println("Thread :" + Thread.currentThread().getName() + "--" + "ObjectCreation for customer " + customer.getId() + " :" + " emitted at " + LocalDateTime.now());
    ModelMapper modelMapper = new ModelMapper();
    CustomerCompleteResponse response = modelMapper.map(customer, CustomerCompleteResponse.class);
    response.setOrders(list);
    return response;
  }
}
