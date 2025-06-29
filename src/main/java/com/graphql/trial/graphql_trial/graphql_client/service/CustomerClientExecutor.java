package com.graphql.trial.graphql_trial.graphql_client.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.graphql.trial.graphql_trial.dto.CustomerDto;
import com.graphql.trial.graphql_trial.dto.DeleteStatus;
import com.graphql.trial.graphql_trial.graphql_client.client.CustomerGraphQLClient;
import com.graphql.trial.graphql_trial.graphql_client.client.SubscriptionClient;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.graphql.client.ClientGraphQlResponse;
import org.springframework.graphql.client.ClientResponseField;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class CustomerClientExecutor implements CommandLineRunner {
  ObjectMapper objectMapper;
  CustomerGraphQLClient customerGraphQLClient;
  SubscriptionClient subscriptionClient;

  @Override
  public void run(String... args) throws Exception {
    subscriptionClient
       .subscription()
       .doOnNext(o -> System.out.println("******* Subscription Event Received *******" + o))
       .subscribe();

    executeRawDocument()
       .then(getCustomerByID())
       .then(getCustomerByIdMultipleQueries())
       .then(getSearchUnionTypes())
       .then(customerDBCrud_getAll())
       .then(customerDBCrud_customerByIdDB())
       .then(customerDBCrud_createCustomerDB())
       .then(customerDBCrud_getAll())
       .then(customerDBCrud_updateCustomerDB())
       .then(customerDBCrud_getAll())
       .then(customerDBCrud_deleteCustomerDB())
       .then(customerDBCrud_getAll())
       .subscribe();
  }

  // Reactive Publishers are not executed unless they are subscribed
  private Mono<Void> executeRawDocument() {
    String document = """
       {
         customersDB{
           id
           name
           age
           city
         }
       }
       """;

    Mono<List<CustomerDto>> customersDBResponse = customerGraphQLClient
       .rawQuery(document)
       .map(clientGraphQlResponse -> {
         System.out.println("ResponseMap: " + clientGraphQlResponse.toMap());
         return clientGraphQlResponse.field("customersDB").toEntityList(CustomerDto.class);
       });

    return executor("Query - customersDB", customersDBResponse);
  }

  private Mono<Void> getCustomerByID() {
    return executor("Query - getCustomerById", customerGraphQLClient.getCustomerById(1));
  }

  private Mono<Void> getCustomerByIdMultipleQueries() {
    Mono<ClientGraphQlResponse> graphQlResponseMono = customerGraphQLClient
       .getCustomerByIdMultipleQueries(36)
       .doOnNext(clientGraphQlResponse -> {
         String fields = "first,second";
         for(String field : fields.split(",")) {
           ClientResponseField clientResponseField = clientGraphQlResponse.field(field);
           System.out.println(clientResponseField.getValue() != null ? clientResponseField.toEntity(CustomerDto.class) : clientResponseField.getErrors());
         }
       });
    return executor("getCustomerByIdMultipleQueries", graphQlResponseMono);
  }

  private Mono<Void> getSearchUnionTypes() {

    Mono<ClientGraphQlResponse> clientGraphQlResponseMono = customerGraphQLClient
       .getSearchUnionTypes()
       .doOnNext((response) -> System.out.println(response.toMap()));

    return executor("getSearchUnionTypes", clientGraphQlResponseMono);
  }

  private Mono<Void> customerDBCrud_getAll() {
    return executor("customerDBCrud_getAll", customerGraphQLClient.customerDBCrud("GetAllDB", Collections.EMPTY_MAP, new ParameterizedTypeReference<List<CustomerDto>>() {
    }));
  }

  private Mono<Void> customerDBCrud_customerByIdDB() {
    return executor("customerDBCrud_customerByIdDB", customerGraphQLClient.customerDBCrud("customerByIdDB", Map.of("id", 2), new ParameterizedTypeReference<CustomerDto>() {
    }));
  }

  private Mono<Void> customerDBCrud_createCustomerDB() {
    CustomerDto customerDto = new CustomerDto(null, "Test USer", "Bangalore", 30);
    return executor("customerDBCrud_createCustomerDB", customerGraphQLClient.customerDBCrud("createCustomerDB", Map.of("customer", customerDto), new ParameterizedTypeReference<CustomerDto>() {
    }));
  }

  private Mono<Void> customerDBCrud_updateCustomerDB() {
    CustomerDto customerDto = new CustomerDto(null, "Test USer Updated", "Bangalore-Rural", 35);
    return executor("customerDBCrud_updateCustomerDB", customerGraphQLClient.customerDBCrud("updateCustomerDB", Map.of("customer", customerDto, "id", 4), new ParameterizedTypeReference<CustomerDto>() {
    }));
  }

  private Mono<Void> customerDBCrud_deleteCustomerDB() {
    return executor("customerDBCrud_deleteCustomerDB", customerGraphQLClient.customerDBCrud("deleteCustomerDB", Map.of("id", 4), new ParameterizedTypeReference<>() {
    }));
  }

  private <T> Mono<Void> executor(String documentName, Mono<T> customerDtoMono) {
    return Mono.delay(Duration.ofSeconds(3))
       .doFirst(() -> System.out.println("Executing  Document -  ".concat(documentName)))
       .then(customerDtoMono)
       .doOnNext(System.out::println)
       .then();
  }
}
