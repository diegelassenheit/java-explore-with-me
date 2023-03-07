package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.user.dto.UserDtoShort;
import ru.practicum.ewm.util.DateFormatConstant;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
public class EventFullDto {
    private String annotation;

    private CategoryDto category;

    private Long confirmedRequests = 0L;

    @JsonFormat(pattern = DateFormatConstant.TIME_PATTERN)
    private LocalDateTime createdOn;

    private String description;

    @JsonFormat(pattern = DateFormatConstant.TIME_PATTERN)
    private LocalDateTime eventDate;

    private Long id;

    private UserDtoShort initiator;

    private Location location;

    private Integer participantLimit = 0;    // TODO: убрать?
    private Boolean paid = true;


    @JsonFormat(pattern = DateFormatConstant.TIME_PATTERN)
    private LocalDateTime publishedOn;

    private Boolean requestModeration = true;

    private EventState state;

    private String title;

    private Long views;

}