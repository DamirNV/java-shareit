package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto create(UserDto userDto) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new DuplicateEmailException("Пользователь с email " + userDto.getEmail() + " уже существует");
        }
        User user = UserMapper.toUser(userDto);
        user = userRepository.save(user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto update(Long id, UserDto userDto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));

        if (userDto.getName() != null) {
            existingUser.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            if (!existingUser.getEmail().equals(userDto.getEmail())
                    && userRepository.existsByEmail(userDto.getEmail())) {
                throw new DuplicateEmailException("Пользователь с email " + userDto.getEmail() + " уже существует");
            }
            existingUser.setEmail(userDto.getEmail());
        }

        return UserMapper.toUserDto(userRepository.save(existingUser));
    }

    @Override
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
        userRepository.deleteById(id);
    }
}