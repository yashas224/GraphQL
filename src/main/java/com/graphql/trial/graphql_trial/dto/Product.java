package com.graphql.trial.graphql_trial.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {
  String name;
  Map<String, String> label;
}
