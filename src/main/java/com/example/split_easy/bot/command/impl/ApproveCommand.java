package com.example.split_easy.bot.command.impl;

import com.example.split_easy.bot.SplitExpensesBot;
import com.example.split_easy.bot.command.Command;
import com.example.split_easy.bot.message.MessageSender;
import com.example.split_easy.entity.User;
import com.example.split_easy.service.UserService;
import com.example.split_easy.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApproveCommand implements Command {
    private final MessageSender messageSender;
    private final UserStatusService userStatusService;
    private final UserService userService;

    @Override
    public void execute(Update update, SplitExpensesBot bot) throws TelegramApiException {
        String chatId = update.getMessage().getChatId().toString();
        log.info("Processing /approve command for chatId: {}", chatId);

        User user = userService.findByChatId(chatId).orElse(null);
        if (user == null) {
            messageSender.sendMessage(bot, chatId, "Користувача не знайдено! Спробуйте /start.");
            return;
        }

        if (!userStatusService.isAdmin(chatId)) {
            messageSender.sendMessage(bot, chatId, "You are not an admin!");
            return;
        }

        messageSender.sendMessage(bot, chatId, "Enter the unique ID of the user to approve:");
    }

}
