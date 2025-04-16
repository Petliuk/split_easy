package com.example.split_easy.bot.command.impl;

import com.example.split_easy.bot.command.Command;
import com.example.split_easy.bot.message.MessageSender;
import com.example.split_easy.bot.SplitExpensesBot;
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
public class PendingCommand implements Command {
    private final MessageSender messageSender;
    private final UserStatusService userStatusService;

    @Override
    public void execute(Update update, SplitExpensesBot bot) throws TelegramApiException {
        String chatId = update.getMessage().getChatId().toString();
        log.info("Обробка /очікування команди для chatId: {}", chatId);

        if (!userStatusService.isAdmin(chatId)) {
            messageSender.sendMessage(bot, chatId, "Ви не адмін!");
            return;
        }

        List<User> pendingUsers = userStatusService.getPendingUsers();
        if (pendingUsers.isEmpty()) {
            messageSender.sendMessage(bot, chatId, "Немає незавершених запитів.");
            return;
        }

        StringBuilder response = new StringBuilder("Користувачі в очікуванні:");
        for (User user : pendingUsers) {
            response.append(user.getName())
                    .append(" (ID: ").append(user.getUniqueId()).append(")\n")
                    .append("/approve_").append(user.getUniqueId()).append("\n");
        }
        messageSender.sendMessage(bot, chatId, response.toString());
    }
}