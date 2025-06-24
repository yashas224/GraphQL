package com.graphql.trial.graphql_trial.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "create")
public class Customer {
  @Id
  private Integer id;
  private String name;
  private String city;
  private int age;
}
