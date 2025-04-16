package com.example.split_easy.service;

import com.example.split_easy.entity.PaymentMethod;
import com.example.split_easy.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    User registerUser(String chatId, String name);
    User updatePaymentMethod(String chatId, PaymentMethod paymentMethod);
    User updateCardNumber(String chatId, String cardNumber);
    Optional<User> findByChatId(String chatId);
    Optional<User> findByUniqueId(String uniqueId);

}
