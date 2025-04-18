package com.example.split_easy.service;

import com.example.split_easy.entity.User;
import java.util.List;

public interface FriendService {
    void addFriend(String chatId, String friendUniqueId);
    List<User> getFriends(String chatId);
}
