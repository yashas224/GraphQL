package com.graphql.trial.graphql_trial.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {

  String id;
  String description;
  int price;
  String author;
}
