package com.example.split_easy.bot.callback;

import com.example.split_easy.bot.SplitExpensesBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
public interface CallbackProcessor {
    boolean supports(String callbackData);
    void process(Update update, SplitExpensesBot bot) throws TelegramApiException;
}
