package com.example.split_easy.bot.handler;

import com.example.split_easy.bot.SplitExpensesBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface UpdateHandler {
    void handle(Update update, SplitExpensesBot bot) throws TelegramApiException;
}
