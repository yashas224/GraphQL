package com.graphql.trial.graphql_trial.dto;

import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.Range;

public record AgeFilter(@Positive @Range(min = 20, max = 30) int min, @Positive @Range(min = 40, max = 70) int max) {
}
