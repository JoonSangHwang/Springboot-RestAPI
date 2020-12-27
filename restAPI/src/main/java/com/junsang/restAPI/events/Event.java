package com.junsang.restAPI.events;

import java.time.LocalDateTime;

import lombok.*;

import javax.persistence.*;

@Getter @Setter
@EqualsAndHashCode(of = "id")
@Builder @NoArgsConstructor @AllArgsConstructor
@Entity
public class Event {

    @Id @GeneratedValue
    private Integer id;
    private String name;
    private String description;
    private LocalDateTime beginEnrollmentDateTime;
    private LocalDateTime closeEnrollmentDateTime;
    private LocalDateTime beginEventDateTime;
    private LocalDateTime endEventDateTime;
    private String location; // (optional) 이게 없으면 온라인 모임
    private int basePrice; // (optional)
    private int maxPrice; // (optional)
    private int limitOfEnrollment;
    private boolean offline;
    private boolean free;
    @Enumerated(EnumType.STRING)
    private EventStatus eventStatus = EventStatus.DRAFT;

    public void update() {
        // Update free (무료/유료)
        if (this.basePrice == 0 && this.maxPrice == 0)
            this.free = true;       // 무료
        else
            this.free = false;      // 유료

        // Update offline (온라인/오프라인)
        if (this.location == null || this.location.trim().isEmpty())
            this.offline = false;
        else
            this.offline = true;

    }
}
