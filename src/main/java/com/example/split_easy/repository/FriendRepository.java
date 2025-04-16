package com.example.split_easy.repository;

import com.example.split_easy.entity.Friend;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendRepository extends JpaRepository<Friend, Long>{
    boolean existsByUserIdAndFriendId(Long userId, Long friendId);
}
