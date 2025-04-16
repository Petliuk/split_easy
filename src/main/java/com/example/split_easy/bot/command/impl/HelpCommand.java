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
public class HelpCommand implements Command {
    private final MessageSender messageSender;
    private final UserService userService;
    private final BotCommandMenuService botCommandMenuService;

    @Override
    public void execute(Update update, SplitExpensesBot bot) throws TelegramApiException {
        String chatId = update.getMessage().getChatId().toString();
        log.info("Processing /help command for chatId: {}", chatId);

        User user = userService.findByChatId(chatId).orElse(null);
        if (user == null) {
            messageSender.sendMessage(bot, chatId, "Користувача не знайдено. Спробуйте надіслати /start.");
            return;
        }

        String helpMessage = buildHelpMessage(user);
        messageSender.sendMessage(bot, chatId, helpMessage);
    }

    private String buildHelpMessage(User user) {
        String uniqueIdMessage = String.format("Ваш uniqueId: %s\n\n", user.getUniqueId());
        StringBuilder commands = new StringBuilder("Доступні команди:\n");
        botCommandMenuService.getHelpCommandsForUser(user)
                .forEach(command -> commands.append(command).append("\n"));
        return uniqueIdMessage + commands.toString();
    }
}
