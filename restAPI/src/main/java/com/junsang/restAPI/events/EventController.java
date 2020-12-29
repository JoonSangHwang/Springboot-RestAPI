package com.junsang.restAPI.events;

import com.junsang.restAPI.common.ErrorResource;
import com.junsang.restAPI.index.IndexController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;
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

    /**
     * 이벤트 전문 생성
     *
     * @param eventDto
     * @param errors
     */
    @PostMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, Errors errors) {

        /**
         * body 에 담아주기 위해 (Test 에서 ObjectMapper 의 경우, BeanSerializer 를 사용)
         * - Event 객체의 경우, Java Bean 스펙을 준수한 객체이므로 객체의 정보를 JSON 으로 변환 가능 (기본 BeanSerializer)
         * - Errors 객체의 경우, Java Bean 스펙을 준수하지 않으므로 JSON 으로 변환 불가능 (커스터마이징 Serializer 필요)
         */
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(ErrorResource.modelOf(errors));
        }

        eventValidator.validate(eventDto, errors);
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(ErrorResource.modelOf(errors));
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

        WebMvcLinkBuilder selfLinkBuilder = linkTo(EventController.class).slash(newEvent.getId());
        URI createdUri = selfLinkBuilder.toUri();
        EventResource eventResource = new EventResource(event);
//        EventResource2 eventResource = new EventResource2(event);
        eventResource.add(linkTo(EventController.class).withRel("query-events"));
        eventResource.add(selfLinkBuilder.withRel("update-event"));
        eventResource.add(Link.of("/docs/index.html#resources-events-create").withRel("profile"));
        return ResponseEntity.created(createdUri).body(eventResource);
    }


    /**
     * 이벤트 List 조회 API
     *
     * @param pageable  페이징 관련 파라미터 사용하기 위함 (page, size, sort 등)
     * @param assembler 페이지를 리소스로 바꿔 링크 정보 추출하기 위함
     */
    @GetMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<?> queryEvents(Pageable pageable, PagedResourcesAssembler<Event> assembler) {
        Page<Event> page = this.eventRepository.findAll(pageable);

        // Repo 에서 받아온 페이지를 리소스로 변경 후 링크 추출 (각 이벤트 마다 self 링크 포함)
//        PagedModel<EntityModel<Event>> pageResource = assembler.toModel(page, e -> new EventResource(e));
        PagedModel<EntityModel<Event>> pageResource = assembler.toModel(page, new RepresentationModelAssembler<Event, EntityModel<Event>>() {
            @Override
            public EntityModel<Event> toModel(Event entity) {
                return new EventResource(entity);
            }
        });

        // 프로필 링크
        pageResource.add(Link.of("/docs/index.html#resources-events-list").withRel("profile"));

        return ResponseEntity.ok(pageResource);
    }


    /**
     * 이벤트 Detail 조회 API
     *
     * @param id
     */
    @GetMapping(value = "/api/events/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity getEvent(@PathVariable Integer id) {
        Optional<Event> optionalEvent = this.eventRepository.findById(id);

        // 빈 객체
        if (!optionalEvent.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        // 반환
        Event event = optionalEvent.get();

        // 프로필 링크
        EventResource eventResource = new EventResource(event);
        eventResource.add(Link.of("/docs/index.html#resources-events-get").withRel("profile"));

        return ResponseEntity.ok(eventResource);
    }


    /**
     *
     * 이벤트 수정 API
     *
     * @param id
     * @param eventDto
     * @param errors
     */
    @PutMapping(value = "/api/events/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity updateEvent(@PathVariable Integer id,
                                      @RequestBody @Valid EventDto eventDto,
                                      Errors errors) {

        // 조회
        Optional<Event> optionalEvent = this.eventRepository.findById(id);
        if (!optionalEvent.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(ErrorResource.modelOf(errors));
        }

        this.eventValidator.validate(eventDto, errors);
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(ErrorResource.modelOf(errors));
        }

        Event existingEvent = optionalEvent.get();

        // 파라미터로 받은 eventDto 를 Event 타입으로 바꿔야 eventRepository 사용가능하다.
        this.modelMapper.map(eventDto, existingEvent);

        // 수정(저장)
        Event savedEvent = this.eventRepository.save(existingEvent);

        // 프로필 링크
        EventResource eventResource = new EventResource(savedEvent);
        eventResource.add(Link.of("/docs/index.html#resources-events-update").withRel("profile"));

        return ResponseEntity.ok(eventResource);
    }

}
