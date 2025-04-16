package com.example.split_easy.bot.command.impl;

import com.example.split_easy.bot.command.Command;
import com.example.split_easy.bot.message.MessageSender;
import com.example.split_easy.bot.SplitExpensesBot;
import com.example.split_easy.bot.util.KeyboardUtils;
import com.example.split_easy.entity.User;
import com.example.split_easy.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UsersCommand implements Command {
    private final MessageSender messageSender;
    private final UserStatusService userStatusService;
    private final KeyboardUtils keyboardUtils;

    @Override
    public void execute(Update update, SplitExpensesBot bot) throws TelegramApiException {
        String chatId = update.getMessage().getChatId().toString();

        if (!userStatusService.isAdmin(chatId)) {
            messageSender.sendMessage(bot, chatId, "Ця команда доступна тільки адміністратору!");
            return;
        }

        List<User> users = userStatusService.getAllUsers();
        if (users.isEmpty()) {
            messageSender.sendMessage(bot, chatId, "У боті ще немає користувачів.");
            return;
        }

        for (User user : users) {
            String userInfo = String.format("Користувач: %s\nChat ID: %s\nУнікальний ID: %s\nСтатус: %s",
                    user.getName(), user.getChatId(), user.getUniqueId(), user.getStatus());

            messageSender.sendMessage(bot, chatId, userInfo, keyboardUtils.createUserManagementKeyboard(user.getUniqueId()));
        }
    }
}