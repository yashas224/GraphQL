package com.graphql.trial.graphql_trial.interceptor;

import org.springframework.context.annotation.Profile;
import org.springframework.graphql.server.WebGraphQlInterceptor;
import org.springframework.graphql.server.WebGraphQlRequest;
import org.springframework.graphql.server.WebGraphQlResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@Component
@Profile("!dev")
class RequestHeaderInterceptor implements WebGraphQlInterceptor {

  @Override
  public Mono<WebGraphQlResponse> intercept(WebGraphQlRequest request, Chain chain) {
    List<String> valueList = request.getHeaders().getOrEmpty("request-header-id");
    String value = valueList.isEmpty() ? "" : valueList.get(0);
    request.configureExecutionInput((executionInput, builder) ->
       builder.graphQLContext(Collections.singletonMap("request-header-id", value)).build());
    return chain.next(request);
  }
}