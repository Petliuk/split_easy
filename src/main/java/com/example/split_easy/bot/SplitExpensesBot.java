package com.example.split_easy.bot;

import com.example.split_easy.config.BotConfig;
import com.example.split_easy.entity.User;
import com.example.split_easy.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import jakarta.annotation.PostConstruct;

import java.io.Serializable;

@Slf4j
@Component
@RequiredArgsConstructor
public class SplitExpensesBot extends TelegramLongPollingBot {
    private final BotConfig config;
    private final BotCommandMenuService botCommandMenuService;
    private final UserService userService;

    @PostConstruct
    public void init() {
        try {
            setCommands(botCommandMenuService.getCommandsForUser(null));
        } catch (TelegramApiException e) {
            log.error("Error initializing bot commands: {}", e.getMessage(), e);
        }
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
    }

    public <T extends Serializable, Method extends BotApiMethod<T>> T execute(Method method) throws TelegramApiException {
        return super.execute(method);
    }

    public void setCommands(SetMyCommands setMyCommands) throws TelegramApiException {
        try {
            execute(setMyCommands);
            log.info("Command menu set successfully");
        } catch (TelegramApiException e) {
            log.error("Error setting command menu: {}", e.getMessage(), e);
            throw e;
        }
    }

    public void updateCommandMenuForUser(String chatId) throws TelegramApiException {
        User user = userService.findByChatId(chatId).orElse(null);
        setCommands(botCommandMenuService.getCommandsForUser(user));
    }
}