package ru.practicum.ewm.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.util.DateFormatConstant;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
public class RequestDto {

    @JsonFormat(pattern = DateFormatConstant.TIME_PATTERN)
    private LocalDateTime created;

    private Long event;

    private Long id;

    private Long requester;

    private RequestStatus status = RequestStatus.PENDING;
}