package ru.practicum.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.dto.UserDto;
import ru.practicum.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    UserDto createUser(UserDto userDto);

    List<UserDto> getUsers(List<Integer> ids, PageRequest page);

    void deleteUser(Integer userId);

    Optional<User> findById(Integer userId);
}