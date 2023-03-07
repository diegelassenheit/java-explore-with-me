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
public class EventShortDto {

    private String annotation;

    private CategoryDto category;

    private Long confirmedRequests = 0L;

    @JsonFormat(pattern = DateFormatConstant.TIME_PATTERN)
    private LocalDateTime eventDate;

    private Long id;

    private UserDtoShort initiator;

    private Boolean paid = true;

    private String title;

    private Long views;
}