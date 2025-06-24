package com.graphql.trial.graphql_trial.controller;

import com.graphql.trial.graphql_trial.dto.Book;
import com.graphql.trial.graphql_trial.dto.Electronics;
import com.graphql.trial.graphql_trial.dto.FruitDto;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;

@Controller
public class ItemController {
  List<Object> list = Arrays.asList(
     new FruitDto("1", "Apple", 100, "2024-12-31"),
     new FruitDto("2", "Banana", 50, "2024-11-30"),
     new FruitDto("3", "Orange", 80, "2024-10-15"),
     new Electronics("4", "Smartphone", 500, "Samsung"),
     new Electronics("5", "Laptop", 1200, "Dell"),
     new Book("6", "Java Programming", 300, "John Doe"),
     new Book("7", "Spring Boot Guide", 350, "Jane Smith")
  );

  @QueryMapping
  public Flux<Object> getItems() {
    return Flux.fromIterable(list);
  }
}
