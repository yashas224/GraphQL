package com.graphql.trial.graphql_trial.controller;

import graphql.schema.DataFetchingEnvironment;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class LevelController {

  @QueryMapping
  public Object level1(DataFetchingEnvironment dataFetchingEnvironment) {
    System.out.println("DataFetchingEnvironment : ".concat(dataFetchingEnvironment.getDocument().toString()));
    System.out.println("Selection set : ".concat(String.valueOf(dataFetchingEnvironment.getSelectionSet().contains("level2"))));
    System.out.println("Selection set : ".concat(String.valueOf(dataFetchingEnvironment.getSelectionSet().contains("level2/level3"))));
    System.out.println("Selection set : ".concat(String.valueOf(dataFetchingEnvironment.getSelectionSet().contains("*/level3"))));
    System.out.println("Selection set : ".concat(String.valueOf(dataFetchingEnvironment.getSelectionSet().contains("level2/**/level5"))));

    return "level1 invocation";
  }
}
