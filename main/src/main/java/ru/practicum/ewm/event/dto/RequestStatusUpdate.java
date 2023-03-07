package ru.practicum.ewm.event.dto;

import ru.practicum.ewm.request.dto.RequestStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@Getter
@Setter
public class RequestStatusUpdate {

    @NotNull
    private List<Long> requestIds = new ArrayList<>();

    @NotNull
    private RequestStatus status;

}