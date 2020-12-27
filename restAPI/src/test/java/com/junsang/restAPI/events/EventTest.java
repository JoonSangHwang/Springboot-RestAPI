package com.junsang.restAPI.events;

import junitparams.Parameters;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
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

    @ParameterizedTest(name = "{index} => basePrice={0}, maxPrice={1}, isFree={2}")
    @CsvSource({
            "0, 0, true",
            "100, 0, falae",
            "0, 1000, falae"
    })
    public void testFree(int basePrice, int maxPrice, boolean isFree) {

        /**
         * 1. base 와 max 둘 다 0 일 경우, 무료
         * 2. base > max 경우, 유료
         * 3. base < max 경우, 유료
         */

        // Given
        Event event = Event.builder()
                .basePrice(basePrice)
                .maxPrice(maxPrice)
                .build();

        // When
        event.update();

        // Then
        assertThat(event.isFree()).isEqualTo(isFree);
    }

    @ParameterizedTest
    @MethodSource("parametersForTestOffline")
    public void testOffline(String location, boolean isOffline) {

        /**
         * 1. 장소가 존재할 경우, 오프라인
         * 2. 장소가 존재하지 않을 경우, 온라인
         * 3. 장소가 존재하지 않을 경우, 온라인
         */

        // Given
        Event event = Event.builder()
                .location(location)
                .build();

        // When
        event.update();

        // Then
        assertThat(event.isOffline()).isEqualTo(isOffline);
    }

    private static Stream<Arguments> parametersForTestOffline() {
        return Stream.of(
                Arguments.of("강남역", true),
                Arguments.of(null, false),
                Arguments.of("", false)
        );
    }
}