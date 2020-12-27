package com.junsang.restAPI.events;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;


public class EventResource extends EntityModel<Event> {

    public EventResource(Event event, Link... links) {
        super(event,  links);
        add(linkTo(EventController.class).slash(event.getId()).withSelfRel());
    }
}
