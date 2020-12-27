package com.junsang.restAPI.events;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.net.URI;
//import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Controller
public class EventController {

    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;
    private final EventValidator eventValidator;

    public EventController(EventRepository eventRepository, ModelMapper modelMapper, EventValidator eventValidator) {
        this.eventRepository = eventRepository;
        this.modelMapper = modelMapper;
        this.eventValidator = eventValidator;
    }

    @PostMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, Errors errors) {

        /**
         * body 에 담아주기 위해 (Test 에서 ObjectMapper 의 경우, BeanSerializer 를 사용)
         * - Event 객체의 경우, Java Bean 스펙을 준수한 객체이므로 객체의 정보를 JSON 으로 변환 가능 (기본 BeanSerializer)
         * - Errors 객체의 경우, Java Bean 스펙을 준수하지 않으므로 JSON 으로 변환 불가능 (커스터마이징 Serializer 필요)
         */
        if (errors.hasErrors()) {
//            return ResponseEntity.badRequest().build();
            return ResponseEntity.badRequest().body(errors);
        }

        eventValidator.validate(eventDto, errors);
        if (errors.hasErrors()) {
//            return ResponseEntity.badRequest().build();
            return ResponseEntity.badRequest().body(errors);
        }

        // 파라미터로 받은 eventDto 를 Event 타입으로 바꿔야 eventRepository 사용가능하다.
        // Event event = Event.builder()
        //         .name(eventDto.getName)
        //         .description(eventDto.getDescription)
        //         .build();

        // 위와 같은 번거러운 작업보다 ModelMapper 라이브러리를 사용한다.
        Event event = modelMapper.map(eventDto, Event.class);

        //== [S] Service 객체 범위
        event.update();
        Event newEvent = this.eventRepository.save(event);
        //== [E] Service 객체 범위

        URI createdUri = linkTo(EventController.class).slash(newEvent.getId()).toUri();
        return ResponseEntity.created(createdUri).body(event);
    }
}
