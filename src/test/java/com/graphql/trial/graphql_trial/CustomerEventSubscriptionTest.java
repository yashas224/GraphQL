package com.graphql.trial.graphql_trial;

import com.graphql.trial.graphql_trial.dto.CustomerAction;
import com.graphql.trial.graphql_trial.dto.CustomerEventDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureHttpGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.graphql.test.tester.WebSocketGraphQlTester;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.test.StepVerifier;

import java.net.URI;
import java.util.UUID;

@Disabled
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureHttpGraphQlTester
@TestPropertySource(properties = "spring.profiles.active=test")
public class CustomerEventSubscriptionTest {

  @Autowired
  HttpGraphQlTester httpGraphQlTester;

  @Test
  void subscription() {
    URI url = URI.create("ws://localhost:8080/graphql");
    WebSocketClient client = new ReactorNettyWebSocketClient();

    WebSocketGraphQlTester tester = WebSocketGraphQlTester.builder(url, client)
       .headers((headers) -> headers.add("request-header-id", UUID.randomUUID().toString()))
       .build();

    // delete event trigger
    httpGraphQlTester
       .mutate().header("request-header-id", UUID.randomUUID().toString()).build()
       .documentName("customer-db-crud")
       .variable("id", 1)
       .operationName("deleteCustomerDB")
       .executeAndVerify();

    tester.documentName("suctomer-event-subscription")
       .executeSubscription()
       .toFlux("customerEvents", CustomerEventDto.class)
       .take(1)
       .as(StepVerifier::create)
       .consumeNextWith(customerEventDto -> Assertions.assertEquals(CustomerAction.DELETED, customerEventDto.getAction()))
       .verifyComplete();
  }
}
