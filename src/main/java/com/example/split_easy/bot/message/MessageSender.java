package com.example.split_easy.bot.message;

import com.example.split_easy.bot.SplitExpensesBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Component
public class MessageSender {
    public void sendMessage(SplitExpensesBot bot, String chatId, String text) throws TelegramApiException {
        SendMessage message = new SendMessage(chatId, text);
        bot.execute(message);
    }

    public void sendMessage(SplitExpensesBot bot, String chatId, String text, InlineKeyboardMarkup markup) throws TelegramApiException {
        SendMessage message = new SendMessage(chatId, text);
        message.setReplyMarkup(markup);
        bot.execute(message);
    }
}
