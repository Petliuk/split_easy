package com.example.split_easy.repository;

import com.example.split_easy.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.split_easy.entity.User;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUniqueId(String uniqueId);
    List<User> findByStatus(Status status);
    Optional<User> findByChatId(String chatId);
}
