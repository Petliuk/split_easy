package com.example.split_easy.bot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Slf4j
@Component
@RequiredArgsConstructor
public class BotInitializer {
    private final SplitExpensesBot bot;

    /**
     * Registers the bot with the Telegram Bots API.
     *
     * @throws TelegramApiException If registration fails
     */
    @EventListener(ContextRefreshedEvent.class)
    public void init() throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        try {
            telegramBotsApi.registerBot(bot);
            log.info("Successfully registered bot: {}", bot.getBotUsername());
        } catch (TelegramApiException e) {
            log.error("Failed to register bot: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to register bot", e);
        }
    }
}
