package com.graphql.trial.graphql_trial.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class CustomerAgeException extends RuntimeException {
  int age;
  String message;

  public CustomerAgeException(String message, int age) {
    super(message);
    this.message = message;
    this.age = age;
  }
}
