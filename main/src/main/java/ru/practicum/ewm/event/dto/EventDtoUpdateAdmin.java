package ru.practicum.ewm.event.dto;

import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;

import com.fasterxml.jackson.annotation.JsonFormat;
import ru.practicum.ewm.util.DateFormatConstant;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
public class EventDtoUpdateAdmin {

    @Size(min = 20, max = 2000)
    private String annotation;

    private Long category;

    @Size(min = 20, max = 7000)
    private String description;

    @JsonFormat(pattern = DateFormatConstant.TIME_PATTERN)
    private LocalDateTime eventDate;

    private Location location;

    private Boolean paid;

    private Boolean requestModeration;
    private Integer participantLimit;

    private EventStateAction stateAction;

    @Size(min = 3, max = 120)
    private String title;

}