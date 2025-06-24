package com.graphql.trial.graphql_trial.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerEventDto {

  int id;
  CustomerAction action;

  public static CustomerEventDto createCustomerEventDto(int id, CustomerAction action) {
    return CustomerEventDto.builder().id(id).action(action).build();
  }
}
