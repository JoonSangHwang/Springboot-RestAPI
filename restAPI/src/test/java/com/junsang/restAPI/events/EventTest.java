package com.junsang.restAPI.events;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EventTest {

    @Test
    public void builder() {
        Event event = Event.builder()
                .name("Rest API")
                .description("인프런 스프링 REST API 개발 강의")
                .build();
        assertThat(event).isNotNull();
    }

    @Test
    public void javaBean() {
        // Given
        String name         = "Event";
        String description  = "Spring";

        // When
        Event event = new Event();
        event.setName(name);
        event.setDescription(description);

        // Then
        assertThat(event.getName()).isEqualTo(name);
        assertThat(event.getDescription()).isEqualTo(description);
    }

    @Test
    public void testFree() {

        /* 1. base 와 max 둘 다 0 일 경우, 무료 */

        // Given
        Event event = Event.builder()
                .basePrice(0)
                .maxPrice(0)
                .build();

        // When
        event.update();

        // Then
        assertThat(event.isFree()).isTrue();


        /* 2. base > max 경우, 유료 */

        // Given
        event = Event.builder()
                .basePrice(100)
                .maxPrice(0)
                .build();

        // When
        event.update();

        // Then
        assertThat(event.isFree()).isFalse();


        /* 2. base < max 경우, 유료 */

        // Given
        event = Event.builder()
                .basePrice(0)
                .maxPrice(100)
                .build();

        // When
        event.update();

        // Then
        assertThat(event.isFree()).isFalse();
    }

    @Test
    public void testOffline() {

        /* 1. 장소가 존재할 경우, 오프라인 */

        // Given
        Event event = Event.builder()
                .location("감남역 네이버 D2 스타텁 팩토리")
                .build();

        // When
        event.update();

        // Then
        assertThat(event.isOffline()).isTrue();


        /* 2. 장소가 존재하지 않을 경우, 온라인 */

        // Given
        event = Event.builder()
                .build();

        // When
        event.update();

        // Then
        assertThat(event.isOffline()).isFalse();
    }
}