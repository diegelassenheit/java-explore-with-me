package ru.practicum.ewm.event.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonFormat;
import ru.practicum.ewm.util.DateFormatConstant;

import java.time.LocalDateTime;
import javax.validation.constraints.Size;


@Setter
@Getter
@NoArgsConstructor
public class EventDtoUpdateUser {

    @Size(min = 20, max = 2000)
    private String annotation;

    private Long category;

    @Size(min = 20, max = 7000)
    private String description;

    @JsonFormat(pattern = DateFormatConstant.TIME_PATTERN)
    private LocalDateTime eventDate;

    private Location location;

    private Integer participantLimit;
    private Boolean paid;

    private Boolean requestModeration;

    private EventStateAction stateAction;

    @Size(min = 3, max = 120)
    private String title;

}