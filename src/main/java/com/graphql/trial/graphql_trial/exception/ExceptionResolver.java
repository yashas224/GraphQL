package com.graphql.trial.graphql_trial.exception;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.graphql.execution.DataFetcherExceptionResolver;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

// universal Exception Handler
@Component
public class ExceptionResolver implements DataFetcherExceptionResolver {
  @Override
  public Mono<List<GraphQLError>> resolveException(Throwable exception, DataFetchingEnvironment environment) {
    List<GraphQLError> list = Arrays.asList(
       GraphqlErrorBuilder.newError(environment)
          .message(exception.getMessage())
          .errorType(ErrorType.INTERNAL_ERROR)
          .extensions(Map.of("CustomerID", Objects.requireNonNull(environment.getArgument("id")),
             "TimeStamp", java.time.OffsetDateTime.now().toString()))
          .build()
    );

    return Mono.just(list);
  }
}
