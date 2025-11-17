package ru.akarpo.openprofile.is_openprofile.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.akarpo.openprofile.is_openprofile.domain.User;
import ru.akarpo.openprofile.is_openprofile.dto.UserDTO;
import ru.akarpo.openprofile.is_openprofile.exception.ResourceNotFoundException;
import ru.akarpo.openprofile.is_openprofile.mapper.UserMapper;
import ru.akarpo.openprofile.is_openprofile.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserDTO> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .toList();
    }

    public UserDTO findByIdOrThrow(UUID id) {
        return userRepository.findById(id)
                .map(userMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    public Optional<UserDTO> findById(UUID id) {
        return userRepository.findById(id)
                .map(userMapper::toDto);
    }

    public UserDTO findByEmailOrThrow(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }

    public Optional<UserDTO> findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::toDto);
    }

    public UserDTO save(UserDTO userDTO) {
        User user = userMapper.toEntity(userDTO);
        User saved = userRepository.save(user);
        return userMapper.toDto(saved);
    }

    public void deleteById(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", "id", id);
        }
        userRepository.deleteById(id);
    }
}