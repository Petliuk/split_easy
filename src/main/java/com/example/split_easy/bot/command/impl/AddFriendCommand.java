package com.example.split_easy.bot.command.impl;

import com.example.split_easy.bot.SplitExpensesBot;
import com.example.split_easy.bot.command.Command;
import com.example.split_easy.bot.message.MessageSender;
import com.example.split_easy.entity.Status;
import com.example.split_easy.entity.User;
import com.example.split_easy.service.FriendService;
import com.example.split_easy.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AddFriendCommand implements Command {
    private final MessageSender messageSender;
    private final UserService userService;
    private final FriendService friendService;

    private final Map<String, Boolean> awaitingFriendUniqueId = new HashMap<>();

    @Override
    public void execute(Update update, SplitExpensesBot bot) throws TelegramApiException {
        String chatId = update.getMessage().getChatId().toString();
        String messageText = update.getMessage().getText();
        log.info("Обробка команди /add_friend для chatId: {}", chatId);

        User user = userService.findByChatId(chatId).orElse(null);
        if (user == null) {
            messageSender.sendMessage(bot, chatId, "Користувача не знайдено! Спробуйте /start.");
            return;
        }

        if (user.getStatus() != Status.APPROVED) {
            messageSender.sendMessage(bot, chatId, "Ви повинні бути підтверджені адміністратором, щоб додавати друзів!");
            return;
        }

        if (messageText.equals("/add_friend")) {
            awaitingFriendUniqueId.put(chatId, true);
            messageSender.sendMessage(bot, chatId, "Будь ласка, введіть uniqueId вашого друга:");
        } else if (awaitingFriendUniqueId.getOrDefault(chatId, false)) {
            User friend = userService.findByUniqueId(messageText).orElse(null);
            if (friend == null) {
                messageSender.sendMessage(bot, chatId, "Друга з таким uniqueId не знайдено.");
            } else {
                friendService.addFriend(chatId, messageText);
                messageSender.sendMessage(bot, chatId, "Користувача " + friend.getName() + " додано в друзі!");
            }
            awaitingFriendUniqueId.remove(chatId);
        }
    }

    public boolean isAwaitingFriendUniqueId(String chatId) {
        return awaitingFriendUniqueId.getOrDefault(chatId, false);
    }
}