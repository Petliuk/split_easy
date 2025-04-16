package com.example.split_easy.service;

import com.example.split_easy.entity.User;
import java.util.List;

public interface UserStatusService {
    User approveUser(String uniqueId);
    User rejectUser(String uniqueId);
    User blockUser(String uniqueId);
    List<User> getPendingUsers();
    List<User> getAllUsers();
    boolean isAdmin(String chatId);
}
