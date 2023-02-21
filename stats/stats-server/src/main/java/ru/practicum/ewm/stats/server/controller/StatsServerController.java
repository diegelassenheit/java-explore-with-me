package ru.practicum.ewm.stats.server.controller;

import org.springframework.http.HttpStatus;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;

import ru.practicum.ewm.stats.dto.HitDtoRequest;
import ru.practicum.ewm.stats.dto.HitDtoResponse;
import ru.practicum.ewm.stats.server.service.StatsServerService;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.ewm.stats.server.utils.DateFormatConstant;

@Slf4j
@RequiredArgsConstructor
@RestController
public class StatsServerController {

    private final StatsServerService statsServerService;

    @PostMapping("/hit")
    public ResponseEntity<HitDtoRequest> createHit(@RequestBody HitDtoRequest dtoRequest) {
        log.info("Got POST createHit: dtoRequest={}", dtoRequest);
        return new ResponseEntity<>(statsServerService.post(dtoRequest), HttpStatus.CREATED);
    }

    @GetMapping("/stats")
    public ResponseEntity<List<HitDtoResponse>> getStats(@RequestParam @DateTimeFormat(pattern = DateFormatConstant.TIME_PATTERN) LocalDateTime start,
                                                         @RequestParam @DateTimeFormat(pattern = DateFormatConstant.TIME_PATTERN) LocalDateTime end,
                                                         @RequestParam(required = false) Optional<List<String>> uris,
                                                         @RequestParam(defaultValue = "false") boolean unique) {

        log.info("Got GET getStats: start={}, end={}, uris={}, unique={}", start, end, uris, unique);
        return uris.map(strings -> new ResponseEntity<>(
                statsServerService.getStats(start, end, strings, unique),
                        HttpStatus.OK))
                        .orElseGet(() -> new ResponseEntity<>(
                                statsServerService.getStats(start, end, unique), HttpStatus.OK));

    }

}