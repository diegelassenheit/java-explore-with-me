package ru.practicum.ewm.user.service;

import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.dto.UserDtoNew;
import ru.practicum.ewm.user.dto.UserDtoUpdate;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.model.QUser;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;
import ru.practicum.ewm.util.Page;
import ru.practicum.ewm.util.QPredicates;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public List<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {
        QPredicates predicates = QPredicates.builder().add(ids, QUser.user.id::in);

        Page page = new Page(from, size);
        Predicate predicate = predicates.buildAnd();

        List<User> users = predicate == null ? userRepository.findAll(page).toList() :
                userRepository.findAll(predicate, page).toList();

        return users.stream().map(userMapper::toUserDto).collect(Collectors.toList());
    }

    public UserDto createUser(UserDtoNew dto) {
        User entity = userMapper.toModel(dto);

        return userMapper.toUserDto(userRepository.save(entity));
    }


    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("User with userId = %d not found", userId));
        }

        userRepository.deleteById(userId);
    }

    public User getUserOrThrow(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("User with userId = %d was not found.", userId));
        }

        return userRepository.getReferenceById(userId);
    }

    public UserDto update(Long userId, UserDtoUpdate dto) {
        User user = getUserOrThrow(userId);
        modelMapper.map(dto, user);

        return modelMapper.map(userRepository.save(user), UserDto.class);

    }
}