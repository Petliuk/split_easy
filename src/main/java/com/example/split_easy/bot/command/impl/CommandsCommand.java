package com.example.split_easy.bot.command.impl;
import com.example.split_easy.bot.BotCommandMenuService;

import com.example.split_easy.bot.SplitExpensesBot;
import com.example.split_easy.bot.command.Command;
import com.example.split_easy.bot.message.MessageSender;
import com.example.split_easy.entity.User;
import com.example.split_easy.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommandsCommand implements Command {
    private final MessageSender messageSender;
    private final UserService userService;
    private final BotCommandMenuService botCommandMenuService;

    @Override
    public void execute(Update update, SplitExpensesBot bot) throws TelegramApiException {
        String chatId = update.getMessage().getChatId().toString();
        log.info("Processing /команди command for chatId: {}", chatId);

        User user = userService.findByChatId(chatId).orElse(null);
        if (user == null) {
            messageSender.sendMessage(bot, chatId, "Користувача не знайдено. Спробуйте надіслати /start.");
            return;
        }

        String commandsMessage = buildCommandsMessage(user);
        messageSender.sendMessage(bot, chatId, commandsMessage);
    }

    private String buildCommandsMessage(User user) {
        StringBuilder commandsMessage = new StringBuilder("📋 **Список доступних команд**:\n\n");
        botCommandMenuService.getCommandsListForUser(user)
                .forEach(command -> commandsMessage.append(command).append("\n"));
        return commandsMessage.toString();
    }
}
