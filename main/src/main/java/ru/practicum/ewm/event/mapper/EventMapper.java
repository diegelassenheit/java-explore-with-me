package ru.practicum.ewm.event.mapper;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.event.dto.EventFullDto;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

import ru.practicum.ewm.event.dto.EventNewDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.EventDtoUpdateAdmin;

import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.event.model.Event;


@Service
@RequiredArgsConstructor
public class EventMapper {

    private final ModelMapper modelMapper;
    private final CategoryRepository categoryRepository;

    @PostConstruct
    private void setupModelMapper() {
        Converter<Long, Category> longToCategoryConverter = mappingContext -> {
            if (mappingContext.getSource() == null) {
                return null;
            }

            if (!categoryRepository.existsById(mappingContext.getSource())) {
                throw new NotFoundException(String.format("CategoryId = %d wasn't found", mappingContext.getSource()));
            }

            return categoryRepository.getReferenceById(mappingContext.getSource());
        };

        modelMapper.createTypeMap(EventDtoUpdateAdmin.class, Event.class).addMappings(
                mapper -> mapper.using(longToCategoryConverter).map(
                        EventDtoUpdateAdmin::getCategory, Event::setCategory));

        modelMapper.createTypeMap(EventNewDto.class, Event.class).addMappings(
                mapper -> mapper.using(longToCategoryConverter).map(EventNewDto::getCategory, Event::setCategory));

    }

    public <T> Event toEventModel(T dto) {
        return modelMapper.map(dto, Event.class);
    }

    public EventShortDto toEventShortDto(Event model) {
        return modelMapper.map(model, EventShortDto.class);
    }

    public EventFullDto toEventFullDto(Event model) {
        return modelMapper.map(model, EventFullDto.class);
    }


    public List<EventShortDto> toEventShortDtoList(List<Event> models) {
        return models.stream().map(this::toEventShortDto).collect(Collectors.toList());
    }

    public List<EventFullDto> toEventFullDtoList(List<Event> models) {
        return models.stream().map(this::toEventFullDto).collect(Collectors.toList());
    }


}