package com.graphql.trial.graphql_trial.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScalarDto {
  long distance;
  LocalDate date;
}
