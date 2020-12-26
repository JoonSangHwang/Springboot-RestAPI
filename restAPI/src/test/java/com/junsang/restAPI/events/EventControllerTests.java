package com.junsang.restAPI.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class EventControllerTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    /**
     *** EventRepository Bean 이 없다는 Exception 발생 ***
     * JpaRepository 를 상속 받는 Interface 만 있으면 자동으로 Bean 이 만들어지는데, Why ?
     * @WebMvcTest 는 Slash 테스트라 웹과 관련된 빈만 주입되고(@Controller, @ControllerAdvice 등)
     * @Service / @Repository 같은 @Component는 주입되지 않는다고 한다.
     * @Repository 빈이 주입되지 않아, 컨트롤러 생성에 실패하여 발생한 에러였다.
     * 그러하여 Repository 를 Mocking
     */
//    @MockBean
//    EventRepository eventRepository;

    @Test
    public void createEvent() throws Exception {
        EventDto event = EventDto.builder()
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2018, 11, 23, 14, 21))
                .closeEnrollmentDateTime(LocalDateTime.of(2018, 11, 24, 14, 21))
                .beginEventDateTime(LocalDateTime.of(2018, 11, 25, 14, 21))
                .endEventDateTime(LocalDateTime.of(2018, 11, 26, 14, 21))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역 D2 스타텁 팩토리")
                .build();

        mockMvc.perform(post("/api/events/")                // Request
                    .contentType(MediaType.APPLICATION_JSON_VALUE)    // Header의 Content-Type
                    .accept(MediaTypes.HAL_JSON_VALUE)                // 요구 Content-Type
                    .content(objectMapper.writeValueAsString(event))
                )
                .andDo(print())                                      // 응답과 요청 출력
                .andExpect(status().isCreated())                     // 201 상태 검증
                .andExpect(jsonPath("id").exists())         // ID 값이 존재 검증
                .andExpect(header().exists(HttpHeaders.LOCATION))    // Location 존재 검증
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("free").value(Matchers.not(true)))
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
        ;

    }

    @Test
    public void createEvent_Bad_Request() throws Exception {
        Event event = Event.builder()
                .id(100)
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2018, 11, 23, 14, 21))
                .closeEnrollmentDateTime(LocalDateTime.of(2018, 11, 24, 14, 21))
                .beginEventDateTime(LocalDateTime.of(2018, 11, 25, 14, 21))
                .endEventDateTime(LocalDateTime.of(2018, 11, 26, 14, 21))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역 D2 스타텁 팩토리")
                .free(true)
                .offline(false)
                .eventStatus(EventStatus.PUBLISHED)
                .build();


        /* ======================================================================== */
        /* Mock 객체이기에 save 등을 하더라도 무조건 NullPoint 가 일어남. ==> stubbing 필요 */
        /* ======================================================================== */
        // event.setId(10);
        // Mockito.when(eventRepository.save(event)).thenReturn(event);    // save 시, event 를 리턴하라
        // ==> Mocking 시, Dto 로 변환 된 객체와 요청 한 객체와 같지 않아 NPE 발생. 하지 않기로함
        /* ===================================================================== */


        mockMvc.perform(post("/api/events/")            // Request
                .contentType(MediaType.APPLICATION_JSON_VALUE)    // Header의 Content-Type
                .accept(MediaTypes.HAL_JSON_VALUE)                // 요구 Content-Type
                .content(objectMapper.writeValueAsString(event))
        )
                .andDo(print())                                      // 응답과 요청 출력
                .andExpect(status().isBadRequest())                  // 400 상태 검증
        ;

    }
}
