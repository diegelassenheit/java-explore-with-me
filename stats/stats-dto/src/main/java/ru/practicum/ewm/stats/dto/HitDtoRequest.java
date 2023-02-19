package ru.practicum.ewm.stats.dto;

import lombok.*;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HitDtoRequest {
    @NonNull
    private String app;
    @NonNull
    private String uri;
    @NonNull
    private String ip;

    // тут тоже хочется заменить формат на константу, но если ее из utils использовать тут, то надо прописывать
    // server в pom.xml, тогда maven ругается на циклическую зависимость. Доп. требований нет,
    // душные crud-api ТЗ меня достали, так что пока оставил так, потом по следующим частям диплома посмотрю,
    // что будет вырисовываться)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

}