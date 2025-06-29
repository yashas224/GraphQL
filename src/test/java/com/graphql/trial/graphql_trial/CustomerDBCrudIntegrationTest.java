package com.graphql.trial.graphql_trial;

import com.graphql.trial.graphql_trial.dto.CustomerDto;
import com.graphql.trial.graphql_trial.dto.DeleteStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureHttpGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.util.UUID;

import static org.springframework.graphql.execution.ErrorType.BAD_REQUEST;
import static org.springframework.graphql.execution.ErrorType.INTERNAL_ERROR;

@SpringBootTest()
@AutoConfigureHttpGraphQlTester
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource(properties = "spring.profiles.active=test")
class CustomerDBCrudIntegrationTest {

  @Autowired
  HttpGraphQlTester httpGraphQlTester;

  @Test
  void allCustomerDBRawDocument() {
    String document = """
       {
         customersDB{
           id
           name
           age
           city
         }
       }
       """;
    httpGraphQlTester
       .mutate().header("request-header-id", UUID.randomUUID().toString()).build()
       .document(document)
       .execute()
       .path("customersDB").entityList(Object.class).hasSizeGreaterThan(2)
       .path("customersDB.[0].name").entity(String.class).isEqualTo("Alice");
  }

  @Test
  void allCustomerDBv1() {
    httpGraphQlTester
       .mutate().header("request-header-id", UUID.randomUUID().toString()).build()
       .documentName("customer-db-crud")
       .operationName("GetAllDB").execute()
       .path("response").entityList(Object.class).hasSize(3)
       .path("response.[0].name").entity(String.class).isEqualTo("Alice");
  }

  @Test
  void customerByIdDB() {
    httpGraphQlTester
       .mutate().header("request-header-id", UUID.randomUUID().toString()).build()
       .documentName("customer-db-crud")
       .operationName("customerByIdDB")
       .variable("id", 2)
       .execute()
       .path("response").entity(CustomerDto.class).matches(customerDto -> customerDto.getId() == 2)
       .path("response.name").entity(String.class).isEqualTo("Bob");
  }

  @Test
  void createCustomerDB() {
    CustomerDto sampleCustomer = new CustomerDto(null, "Sample Name", "Sample City", 25);
    httpGraphQlTester
       .mutate().header("request-header-id", UUID.randomUUID().toString()).build()
       .documentName("customer-db-crud")
       .variable("customer", sampleCustomer)
       .operationName("createCustomerDB")
       .execute()
       .path("response").entity(CustomerDto.class).matches(customerDto -> customerDto.getId() == 4)
       .path("response.name").entity(String.class).isEqualTo(sampleCustomer.getName());
  }

  @Test
  void updateCustomerDB() {
    CustomerDto updatedSampleCustomer = new CustomerDto(null, "Changed  Name", "Sample New city ", 45);
    httpGraphQlTester
       .mutate().header("request-header-id", UUID.randomUUID().toString()).build()
       .documentName("customer-db-crud")
       .variable("id", 2)
       .variable("customer", updatedSampleCustomer)
       .operationName("updateCustomerDB")
       .execute()
       .path("response").entity(CustomerDto.class).matches(customerDto -> customerDto.getId() == 2)
       .path("response.name").entity(String.class).isEqualTo(updatedSampleCustomer.getName())
       .path("response").entity(Object.class).satisfies(System.out::println);
  }

  @Test
  void deleteCustomerDB() {
    httpGraphQlTester
       .mutate().header("request-header-id", UUID.randomUUID().toString()).build()
       .documentName("customer-db-crud")
       .variable("id", 3)
       .operationName("deleteCustomerDB")
       .execute()
       .path("response").entity(DeleteStatus.class).isEqualTo(DeleteStatus.SUCCESS)
       .path("response").entity(Object.class).satisfies(System.out::println);
  }

  @Test
  void customerByIdDBException() {
    httpGraphQlTester
       .mutate().header("request-header-id", UUID.randomUUID().toString()).build()
       .documentName("customer-db-crud")
       .operationName("customerByIdDB")
       .variable("id", 20)
       .execute()
       .errors()
       .expect(responseError -> responseError.getMessage().matches("No customer found!!"))
       .expect(responseError -> responseError.getErrorType().equals(INTERNAL_ERROR));
  }

  @Test
  void createCustomerDBAgeException() {
    CustomerDto sampleCustomer = new CustomerDto(null, "Sample Name", "Sample City", 10);
    httpGraphQlTester
       .mutate().header("request-header-id", UUID.randomUUID().toString()).build()
       .documentName("customer-db-crud")
       .variable("customer", sampleCustomer)
       .operationName("createCustomerDB")
       .execute()
       .errors()
       .expect(responseError -> responseError.getMessage().matches("Customer Age less than 18"))
       .expect(responseError -> responseError.getErrorType().equals(BAD_REQUEST));
  }
}
