package com.graphql.trial.graphql_trial.repository;

import com.graphql.trial.graphql_trial.entity.Customer;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface CustomerRepository extends ReactiveCrudRepository<Customer, Integer> {
}
