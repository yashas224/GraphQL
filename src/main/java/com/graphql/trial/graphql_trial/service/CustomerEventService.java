package com.graphql.trial.graphql_trial.service;

import com.graphql.trial.graphql_trial.dto.CustomerEventDto;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Service
public class CustomerEventService {

  //In Reactor a sink is a class that allows safe manual triggering of signals in a standalone fashion,
  // creating a Publisher-like structure capable of dealing with multiple Subscriber (with the exception of unicast() flavors).
// https://projectreactor.io/docs/core/release/reference/coreFeatures/sinks.html#sinks-many-multicast-onbackpressurebufferargs

  private final Sinks.Many<CustomerEventDto> sink = Sinks.many()
     .multicast()
     .onBackpressureBuffer();
  private final Flux<CustomerEventDto> flux = sink.asFlux();

  public Flux<CustomerEventDto> subscribe() {
    return flux;
  }

  public void emitEvent(CustomerEventDto customerEventDto) {
    sink.tryEmitNext(customerEventDto);
  }
}
