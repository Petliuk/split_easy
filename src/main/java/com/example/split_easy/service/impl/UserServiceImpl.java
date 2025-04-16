package com.example.split_easy.service.impl;

import com.example.split_easy.entity.*;
import com.example.split_easy.repository.UserRepository;
import com.example.split_easy.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.Random;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final String adminChatId;

    public UserServiceImpl(UserRepository userRepository,
                           @Value("${admin.chatId}") String adminChatId) {
        this.userRepository = userRepository;
        this.adminChatId = adminChatId;
    }

    @Override
    public User registerUser(String chatId, String name) {
        Optional<User> existingUser = userRepository.findByChatId(chatId);
        if (existingUser.isPresent()) {
            return existingUser.get();
        }

        String uniqueId = generateUniqueId();
        Role role = chatId.equals(adminChatId) ? Role.ADMIN : Role.USER;
        User user = User.builder()
                .chatId(chatId)
                .uniqueId(uniqueId)
                .name(name)
                .role(role)
                .status(Status.PENDING)
                .build();
        return userRepository.save(user);
    }

    @Override
    public User updatePaymentMethod(String chatId, PaymentMethod paymentMethod) {
        User user = userRepository.findByChatId(chatId)
                .orElseThrow(() -> new RuntimeException("Користувача не знайдено"));
        user.setPaymentMethod(paymentMethod);
        return userRepository.save(user);
    }

    @Override
    public User updateCardNumber(String chatId, String cardNumber) {
        User user = userRepository.findByChatId(chatId)
                .orElseThrow(() -> new RuntimeException("Користувача не знайдено"));
        user.setCardNumber(cardNumber);
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findByChatId(String chatId) {
        return userRepository.findByChatId(chatId);
    }

    @Override
    public Optional<User> findByUniqueId(String uniqueId) {
        return userRepository.findByUniqueId(uniqueId);
    }

    private String generateUniqueId() {
        Random random = new Random();
        String uniqueId;
        do {
            uniqueId = String.format("%010d", random.nextInt(1000000000));
        } while (userRepository.findByUniqueId(uniqueId).isPresent());
        return uniqueId;
    }
}
