package com.graphql.trial.graphql_trial.graphql_client.client;

import com.graphql.trial.graphql_trial.dto.CustomerDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.graphql.client.ClientGraphQlResponse;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;
import java.util.UUID;

// Client class used to interact with Graph QL end point
@Service
public class CustomerGraphQLClient {

  private final HttpGraphQlClient httpGraphQlClient;

  public CustomerGraphQLClient(@Value("${customer-service-url}") String customerServiceUrl) {
    httpGraphQlClient = HttpGraphQlClient.builder()
       .url(customerServiceUrl)
       .header("request-header-id", String.valueOf(UUID.randomUUID()))
       .build();
  }

  public Mono<ClientGraphQlResponse> rawQuery(String document) {
    return httpGraphQlClient
       .document(document)
       .execute();
  }

  // executing document - customer-by-id.graphql
  public Mono<CustomerDto> getCustomerById(Integer id) {
    return httpGraphQlClient
       .documentName("customer-by-id")
       .variable("id", id)
       .retrieve("customerByIdDB")
       .toEntity(CustomerDto.class);
  }

  // executing document - customer-by-id-multiple-queries.graphql
  public Mono<ClientGraphQlResponse> getCustomerByIdMultipleQueries(Integer id) {
    return httpGraphQlClient
       .documentName("customer-by-id-multiple-queries")
       .variable("id", id)
       .execute();
  }

  // executing document - customer-by-id-multiple-queries.graphql
  public Mono<ClientGraphQlResponse> getSearchUnionTypes() {
    return httpGraphQlClient
       .documentName("search-union-types")
       .execute();
  }

  public <T> Mono<T> customerDBCrud(String operationName, Map<String, Object> variables, ParameterizedTypeReference<T> typeReference) {
    return httpGraphQlClient.documentName("customer-db-crud")
       .operationName(operationName)
       .variables(variables)
       .retrieve("response")
       .toEntity(typeReference);
  }
}

