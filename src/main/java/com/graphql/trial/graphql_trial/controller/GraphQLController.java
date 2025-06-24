package com.graphql.trial.graphql_trial.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Controller
public class GraphQLController {

  @Value("${spring.application.name}")
  private String serviceName;

  @QueryMapping(name = "testHealth")
  public Mono<String> AppHealth() {
    return Mono.just(serviceName.concat(" is healthy")).delayElement(Duration.ofSeconds(10));
  }

  @QueryMapping(name = "hi")
  public Mono<String> sayHi(@Argument(name = "name") String PersonName) {
    String message = "Hello from ".concat(serviceName).concat(" to ").concat(PersonName);
    return Mono.fromSupplier(() -> message).delayElement(Duration.ofSeconds(2));
  }

  @QueryMapping
  public Mono<Integer> random() {
    return Mono.just((int) (Math.random() * 100)).delayElement(Duration.ofSeconds(10));
  }
}
