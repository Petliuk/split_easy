package com.example.split_easy.bot;

import com.example.split_easy.bot.handler.TextMessageHandler;
import com.example.split_easy.config.BotConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.commands.DeleteMyCommands;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import jakarta.annotation.PostConstruct;

import java.io.Serializable;

@Slf4j
@Component
@RequiredArgsConstructor
public class SplitExpensesBot extends TelegramLongPollingBot {
    private final BotConfig config;
    private final BotCommandMenuService botCommandMenuService;
    private final TextMessageHandler textMessageHandler;

    @Value("${spring.profiles.active:prod}")
    private String activeProfile;

    /**
     * Initializes bot commands by clearing existing commands and setting new ones.
     * Skips initialization in development profile.
     */
    @PostConstruct
    public void init() {
        if ("dev".equals(activeProfile)) {
            log.info("Skipping command initialization in dev profile");
            return;
        }
        try {
            execute(new DeleteMyCommands());
            setCommands(botCommandMenuService.getAllCommands());
        } catch (TelegramApiException | InterruptedException e) {
            log.error("Failed to initialize bot commands: {}", e.getMessage(), e);
        }
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    // TODO: Replace with constructor-based token passing after upgrading to telegrambots 6.x
    @Override
    public String getBotToken() {
        return config.getToken();
    }

    /**
     * Processes incoming updates by delegating to the TextMessageHandler.
     *
     * @param update The incoming Telegram update
     */
    @Override
    public void onUpdateReceived(Update update) {
        try {
            textMessageHandler.handle(update, this);
        } catch (TelegramApiException e) {
            log.error("Failed to process update: {}", e.getMessage(), e);
        }
    }

    /**
     * Executes a Telegram API method with retry logic for rate limit errors (429).
     *
     * @param method The Telegram API method to execute
     * @param <T>    The type of the result
     * @param <M>    The type of the method
     * @return The result of the API method
     * @throws TelegramApiException If the execution fails
     */
    public <T extends Serializable, M extends BotApiMethod<T>> T execute(M method) throws TelegramApiException {
        log.debug("Executing Telegram API method: {}", method.getMethod());
        try {
            return super.execute(method);
        } catch (TelegramApiRequestException e) {
            if (e.getErrorCode() == 429) {
                int retryAfter = extractRetryAfter(e.getMessage());
                log.warn("Rate limit exceeded. Retry after {} seconds", retryAfter);
                throw e;
            }
            log.error("Failed to execute API method {}: {}", method.getMethod(), e.getMessage());
            throw e;
        }
    }

    /**
     * Sets the bot's command menu with retry logic for rate limit errors.
     *
     * @param setMyCommands The command menu to set
     * @throws TelegramApiException If the operation fails
     * @throws InterruptedException If the retry is interrupted
     */
    public void setCommands(SetMyCommands setMyCommands) throws TelegramApiException, InterruptedException {
        try {
            execute(setMyCommands);
            log.info("Bot command menu set successfully");
        } catch (TelegramApiRequestException e) {
            if (e.getErrorCode() == 429) {
                int retryAfter = extractRetryAfter(e.getMessage());
                log.warn("Rate limit exceeded during command initialization. Retrying after {} seconds", retryAfter);
                Thread.sleep(retryAfter * 1000L);
                execute(setMyCommands);
                log.info("Bot command menu set successfully after retry");
            } else {
                log.error("Failed to set command menu: {}", e.getMessage(), e);
                throw e;
            }
        }
    }

    /**
     * Extracts the retry-after duration from a Telegram API error message.
     *
     * @param message The error message
     * @return The retry-after duration in seconds, or 30 if parsing fails
     */
    private int extractRetryAfter(String message) {
        try {
            String[] parts = message.split("retry after ");
            return Integer.parseInt(parts[1].trim());
        } catch (Exception e) {
            return 30;
        }
    }
}