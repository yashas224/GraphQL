package com.graphql.trial.graphql_trial.config;

import com.graphql.trial.graphql_trial.datafetchers.CustomerOrderDataFetcher;
import com.graphql.trial.graphql_trial.dto.Book;
import com.graphql.trial.graphql_trial.dto.Electronics;
import com.graphql.trial.graphql_trial.dto.FruitDto;
import graphql.execution.preparsed.PreparsedDocumentEntry;
import graphql.execution.preparsed.PreparsedDocumentProvider;
import graphql.scalars.ExtendedScalars;
import graphql.schema.GraphQLScalarType;
import graphql.schema.TypeResolver;
import org.springframework.boot.autoconfigure.graphql.GraphQlSourceBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.ClassNameTypeResolver;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class GraphQlConfig {

  @Bean
  public RuntimeWiringConfigurer runtimeWiringConfigurer(CustomerOrderDataFetcher customerOrderDataFetcher) {

    // creating Alias Scalar for "Label" type to represent ExtendedScalars.Object
    GraphQLScalarType LabelScalarType = ExtendedScalars.newAliasedScalar("Label")
       .aliasedScalar(ExtendedScalars.Object).build();

    return t -> t
       .type("Item", builder -> builder.typeResolver(customFruitTypeResolver()))
       .type("Result", builder -> builder.typeResolver(customSearchTypeResolver()))
       .type("Query", builder -> builder.dataFetcher("customers", customerOrderDataFetcher))
       .scalar(ExtendedScalars.GraphQLLong)
       .scalar(ExtendedScalars.Date)
       .scalar(LabelScalarType);
  }

  public TypeResolver customFruitTypeResolver() {
    // Custom Type Resolver for Fruit --> FruitDto
    ClassNameTypeResolver typeResolver = new ClassNameTypeResolver();
    typeResolver.addMapping(FruitDto.class, "Fruit");
    return typeResolver;
  }

  public TypeResolver customSearchTypeResolver() {
    ClassNameTypeResolver typeResolver = new ClassNameTypeResolver();

    typeResolver.addMapping(FruitDto.class, "FruitSearchResult");
    typeResolver.addMapping(Electronics.class, "ElectronicsSearchResult");
    typeResolver.addMapping(Book.class, "BookSearchResult");
    return typeResolver;
  }

  // Query caching
  // to improve caching try to use variables so that we have a generic Document
  @Bean
  PreparsedDocumentProvider provider() {
    Map<String, PreparsedDocumentEntry> map = new ConcurrentHashMap<>();

    return (executionInput, parseAndValidateFunction) -> {
      // No caching for Subscription types
      if(executionInput.getQuery().contains("subscription")) {
        return CompletableFuture.completedFuture(parseAndValidateFunction.apply(executionInput));
      }

      //caching  for queries and mutation
      String query = executionInput.getQuery();
      System.out.println("Query Received: " + query);
      map.computeIfAbsent(query, key -> {
        System.out.println("Creating PreparsedDocumentEntry");
        PreparsedDocumentEntry preparsedDocumentEntry = parseAndValidateFunction.apply(executionInput);
        System.out.println("cached entry  " + preparsedDocumentEntry);
        return preparsedDocumentEntry;
      });
      return CompletableFuture.completedFuture(map.get(query));
    };
  }

  @Bean
  public GraphQlSourceBuilderCustomizer sourceBuilderCustomizer(PreparsedDocumentProvider provider) {
    return (builder) ->
       builder.configureGraphQl(graphQlBuilder ->
          graphQlBuilder.preparsedDocumentProvider(provider));
  }
}