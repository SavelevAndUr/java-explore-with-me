package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.UserDto;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.UserMapper;
import ru.practicum.model.User;
import ru.practicum.repository.UserRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private static final String INCORRECT_DATA_INPUT_MSG = "Incorrect data input";
    private static final String INCORRECT_NAME_UNIQUE_REASON = "Field name must be unique";
    private static final String NOT_FOUND_USER_MSG = "User not found";
    private static final String NOT_FOUND_ID_REASON = "Incorrect Id";

    private final UserRepository userRepository;

    @Override
    public UserDto createUser(UserDto userDto) {
        checkUserName(userDto);
        User user = userRepository.save(UserMapper.toNewEntity(userDto));
        log.info("Created user {} and reverting to controller as dto", user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getUsers(List<Long> ids, PageRequest page) {
        List<User> users;
        if (Objects.isNull(ids)) {
            users = userRepository.findAllOrderById(page);
        } else {
            users = userRepository.findAllByIdIn(ids);
        }
        log.info("Found users {} and reverting to controller as dto", users);
        return UserMapper.toUsersDto(users);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        User user = findById(userId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_USER_MSG, NOT_FOUND_ID_REASON));
        userRepository.deleteUserById(userId);
        log.info("Deleted user {} ", user);
    }

    @Override
    public Optional<User> findById(Long userId) {
        return userRepository.findById(userId);
    }

    private void checkUserName(UserDto userDto) {
        Optional<User> category = userRepository.findByName(userDto.getName());
        if (category.isPresent()) {
            throw new ConflictException(INCORRECT_DATA_INPUT_MSG, INCORRECT_NAME_UNIQUE_REASON);
        }
    }

}