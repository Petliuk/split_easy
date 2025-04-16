package com.example.split_easy.bot.command;

import com.example.split_easy.bot.SplitExpensesBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface Command {
    void execute(Update update, SplitExpensesBot bot) throws TelegramApiException;
}
