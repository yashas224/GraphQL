package com.graphql.trial.graphql_trial.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "create")
public class CustomerOrderObject {

  private UUID id;
  private String description;
}
