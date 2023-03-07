package ru.practicum.ewm.compilation.service;

import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationDto;
import ru.practicum.ewm.compilation.mapper.CompilationMapper;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.compilation.model.QCompilation;
import ru.practicum.ewm.compilation.repository.CompilationRepository;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.util.EwmUtils;
import ru.practicum.ewm.util.Page;
import ru.practicum.ewm.util.QPredicates;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CompilationService {

    private final CompilationMapper compilationMapper;
    private final CompilationRepository compilationRepository;

    public CompilationDto createCompilation(NewCompilationDto dto) {
        Compilation compilation = compilationMapper.toModel(dto);

        return compilationMapper.toDto(
                compilationRepository.save(compilation)
        );
    }

    public void deleteCompilation(Long compId) {
        checkCompilation(compId);
        compilationRepository.deleteById(compId);
    }

    public CompilationDto updateCompilation(Long compId, UpdateCompilationDto dto) {
        checkCompilation(compId);

        Compilation compilationTarget = compilationRepository.getReferenceById(compId);
        Compilation src = compilationMapper.toModel(dto);

        EwmUtils.copyNotNullProperties(src, compilationTarget);

        return compilationMapper.toDto(
                compilationRepository.save(compilationTarget)
        );
    }

    @Transactional(readOnly = true)
    public List<CompilationDto> getCompilationList(Boolean pinned, Integer from, Integer size) {

        QPredicates predicates = QPredicates.builder().add(pinned, QCompilation.compilation.pinned::eq);

        Predicate predicate = predicates.buildAnd();
        Page page = new Page(from, size);

        return compilationMapper.toDtoList(
                (predicate == null)
                        ? compilationRepository.findAll(page).toList()
                        : compilationRepository.findAll(predicate, page).toList()
        );
    }

    @Transactional(readOnly = true)
    public CompilationDto getCompilation(Long compId) {
        checkCompilation(compId);
        return compilationMapper.toDto(
                compilationRepository.getReferenceById(compId)
        );
    }

    private void checkCompilation(Long compId) {
        if (!compilationRepository.existsById(compId)) {
            throw new NotFoundException("Compilation by id=" + compId + " was not found.");
        }
    }
}
