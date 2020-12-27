package com.junsang.restAPI.events;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import org.hibernate.event.spi.EventSource;
import org.springframework.hateoas.RepresentationModel;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class EventResource2 extends RepresentationModel {

    @JsonUnwrapped
    private Event event;

    public EventResource2(Event event) {
        this.event = event;
        add(linkTo(EventController.class).slash(event.getId()).withSelfRel());
    }

    public Event getEvent() {
        return event;
    }
}
