package com.example.split_easy.service.impl;

import com.example.split_easy.entity.Role;
import com.example.split_easy.entity.Status;
import com.example.split_easy.entity.User;
import com.example.split_easy.repository.UserRepository;
import com.example.split_easy.service.UserStatusService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class UserStatusServiceImpl implements UserStatusService {
    private final UserRepository userRepository;

    public UserStatusServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User approveUser(String uniqueId) {
        User user = userRepository.findByUniqueId(uniqueId)
                .orElseThrow(() -> new RuntimeException("Користувача не знайдено"));
        user.setStatus(Status.APPROVED);
        return userRepository.save(user);
    }

    @Override
    public User rejectUser(String uniqueId) {
        User user = userRepository.findByUniqueId(uniqueId)
                .orElseThrow(() -> new RuntimeException("Користувача не знайдено"));
        user.setStatus(Status.REJECTED);
        return userRepository.save(user);
    }

    @Override
    public User blockUser(String uniqueId) {
        User user = userRepository.findByUniqueId(uniqueId)
                .orElseThrow(() -> new RuntimeException("Користувача не знайдено"));
        user.setStatus(Status.BLOCKED);
        return userRepository.save(user);
    }

    @Override
    public List<User> getPendingUsers() {
        return userRepository.findByStatus(Status.PENDING);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public boolean isAdmin(String chatId) {
        return userRepository.findByChatId(chatId)
                .map(user -> Role.ADMIN.equals(user.getRole()))
                .orElse(false);
    }

}
