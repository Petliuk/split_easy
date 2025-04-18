package com.example.split_easy.service.impl;

import com.example.split_easy.entity.Friend;
import com.example.split_easy.entity.User;
import com.example.split_easy.repository.FriendRepository;
import com.example.split_easy.repository.UserRepository;
import com.example.split_easy.service.FriendService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FriendServiceImpl implements FriendService {
    private final UserRepository userRepository;
    private final FriendRepository friendRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public FriendServiceImpl(UserRepository userRepository, FriendRepository friendRepository) {
        this.userRepository = userRepository;
        this.friendRepository = friendRepository;
    }

    @Override
    @Transactional
    public void addFriend(String chatId, String friendUniqueId) {
        User user = userRepository.findByChatId(chatId)
                .orElseThrow(() -> new RuntimeException("Користувача не знайдено"));
        User friend = userRepository.findByUniqueId(friendUniqueId)
                .orElseThrow(() -> new RuntimeException("Друга з таким uniqueId не знайдено"));

        if (user.getUniqueId().equals(friendUniqueId)) {
            throw new RuntimeException("Ви не можете додати себе в друзі!");
        }

        if (friendRepository.existsByUserIdAndFriendId(user.getId(), friend.getId())) {
            throw new RuntimeException("Цей користувач уже є вашим другом!");
        }

        Friend friendship = Friend.builder()
                .user(user)
                .friend(friend)
                .createdAt(LocalDateTime.now())
                .build();

        Friend reverseFriendship = Friend.builder()
                .user(friend)
                .friend(user)
                .createdAt(LocalDateTime.now())
                .build();

        friendRepository.save(friendship);
        friendRepository.save(reverseFriendship);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getFriends(String chatId) {
        User user = userRepository.findByChatId(chatId)
                .orElseThrow(() -> new RuntimeException("Користувача не знайдено"));
        return entityManager.createQuery(
                        "SELECT f.friend FROM Friend f WHERE f.user = :user", User.class)
                .setParameter("user", user)
                .getResultList();
    }
}
