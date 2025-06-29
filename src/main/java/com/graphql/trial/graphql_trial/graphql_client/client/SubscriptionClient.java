package com.graphql.trial.graphql_trial.graphql_client.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.graphql.client.WebSocketGraphQlClient;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Service
public class SubscriptionClient {
  private final WebSocketGraphQlClient webSocketGraphQlClient;

  public SubscriptionClient(@Value("${customer-subscription-url}") String customerServiceUrl) {
    WebSocketClient client = new ReactorNettyWebSocketClient();
    webSocketGraphQlClient = WebSocketGraphQlClient.builder(customerServiceUrl, client)
       .header("request-header-id", String.valueOf(UUID.randomUUID()))
       .build();
  }

  public Flux<Object> subscription() {

    String SubscriptionDocument = """
       subscription{
          customerEvents{
            id
            action
          }
       }
       """;

    Flux<Object> customerEvents = webSocketGraphQlClient.document(SubscriptionDocument)
       .retrieveSubscription("customerEvents")
       .toEntity(Object.class);

    return customerEvents;
  }
}
