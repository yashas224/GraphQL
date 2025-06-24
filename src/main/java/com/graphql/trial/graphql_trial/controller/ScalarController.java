package com.graphql.trial.graphql_trial.controller;

import com.graphql.trial.graphql_trial.dto.Product;
import com.graphql.trial.graphql_trial.dto.ScalarDto;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Controller
public class ScalarController {

  private static final List<ScalarDto> list = List.of(
     new ScalarDto(1000L, java.time.LocalDate.of(2023, 10, 1)),
     new ScalarDto(2000L, java.time.LocalDate.of(2023, 10, 2)),
     new ScalarDto(3000L, java.time.LocalDate.of(2023, 10, 3))
  );

  private static final List<Product> productList = Arrays.asList(
     new Product("TV", Map.of("height", "1cm", "weight", "10kg", "brand", "Apple")),
     new Product("Apple", Map.of("country", "India", "weight", "1kg"))
  );

  /**
   * This method returns a Flux of ScalarDto objects.
   *
   * @return Flux of ScalarDto
   */
  @QueryMapping
  public Flux<ScalarDto> getScalarTypes() {
    return Flux.fromIterable(list);
  }

  @QueryMapping
  public Flux<Product> getProducts() {
    return Flux.fromIterable(productList);
  }
}
