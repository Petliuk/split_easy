package com.example.split_easy.bot.command.impl;

import com.example.split_easy.bot.command.Command;
import com.example.split_easy.bot.message.MessageSender;
import com.example.split_easy.bot.SplitExpensesBot;
import com.example.split_easy.entity.Status;
import com.example.split_easy.entity.User;
import com.example.split_easy.service.FriendService;
import com.example.split_easy.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ViewFriendsCommand implements Command {
    private final MessageSender messageSender;
    private final UserService userService;
    private final FriendService friendService;

    @Override
    public void execute(Update update, SplitExpensesBot bot) throws TelegramApiException {
        String chatId = update.getMessage().getChatId().toString();
        log.info("Обробка команди /view_friends для chatId: {}", chatId);

        User user = userService.findByChatId(chatId).orElse(null);
        if (user == null) {
            messageSender.sendMessage(bot, chatId, "Користувача не знайдено! Спробуйте /start.");
            return;
        }

        if (user.getStatus() != Status.APPROVED) {
            messageSender.sendMessage(bot, chatId, "Ви повинні бути підтверджені адміністратором, щоб переглядати друзів!");
            return;
        }

        List<User> friends = friendService.getFriends(chatId);
        if (friends.isEmpty()) {
            messageSender.sendMessage(bot, chatId, "У вас ще немає друзів.");
            return;
        }

        String friendsList = friends.stream()
                .map(User::getName)
                .collect(Collectors.joining("\n"));
        messageSender.sendMessage(bot, chatId, "Ваші друзі:\n" + friendsList);
    }
}