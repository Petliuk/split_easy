package com.example.split_easy.bot.handler;

import com.example.split_easy.bot.SplitExpensesBot;
import com.example.split_easy.bot.callback.CallbackProcessor;
import com.example.split_easy.bot.message.MessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CallbackQueryHandler implements UpdateHandler {
    private final MessageSender messageSender;
    private final List<CallbackProcessor> processors;

    @Override
    public void handle(Update update, SplitExpensesBot bot) throws TelegramApiException {
        if (!update.hasCallbackQuery()) {
            log.info("Update does not contain a callback query");
            String chatId = update.getMessage() != null ? update.getMessage().getChatId().toString() : null;
            if (chatId != null) {
                messageSender.sendMessage(bot, chatId, "Unknown action. Use /help for more info.");
            } else {
                log.warn("Cannot send message: chatId is null for update without callback query");
            }
            return;
        }

        String callbackData = update.getCallbackQuery().getData();
        String chatId = update.getCallbackQuery().getMessage().getChatId().toString();
        log.info("Processing callback query: {} from chatId: {}", callbackData, chatId);

        for (CallbackProcessor processor : processors) {
            if (processor.supports(callbackData)) {
                processor.process(update, bot);
                return;
            }
        }

        messageSender.sendMessage(bot, chatId, "Невідома дія: " + callbackData);
    }
}