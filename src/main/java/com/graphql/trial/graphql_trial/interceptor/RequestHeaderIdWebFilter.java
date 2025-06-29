package com.graphql.trial.graphql_trial.interceptor;

import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@Profile("!dev")
public class RequestHeaderIdWebFilter implements WebFilter {
  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    boolean isHeaderEmpty = exchange.getRequest().getHeaders().getOrEmpty("request-header-id").isEmpty();
    return isHeaderEmpty ? Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid request-header-id value")) : chain.filter(exchange);
  }
}

