package com.example.split_easy.bot;

import com.example.split_easy.bot.handler.UpdateHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Slf4j
@Component
@RequiredArgsConstructor
public class BotInitializer {
    private final SplitExpensesBot bot;
    private final UpdateHandler updateHandler;

    @EventListener(ContextRefreshedEvent.class)
    public void init() throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        try {
            telegramBotsApi.registerBot(new TelegramLongPollingBot() {
                @Override
                public String getBotUsername() {
                    return bot.getBotUsername();
                }

                @Override
                public String getBotToken() {
                    return bot.getBotToken();
                }

                @Override
                public void onUpdateReceived(Update update) {
                    try {
                        updateHandler.handle(update, bot);
                    } catch (TelegramApiException e) {
                        log.error("Error processing update: {}", e.getMessage(), e);
                    }
                }
            });
            log.info("Successfully registered bot: {}", bot.getBotUsername());
        } catch (TelegramApiException e) {
            log.error("Error occurred while registering bot: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to register bot", e);
        }
    }
}
