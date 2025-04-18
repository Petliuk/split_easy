package com.example.split_easy.bot.callback.impl;

import com.example.split_easy.bot.callback.CallbackProcessor;
import com.example.split_easy.bot.message.MessageSender;
import com.example.split_easy.bot.SplitExpensesBot;
import com.example.split_easy.entity.User;
import com.example.split_easy.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@RequiredArgsConstructor
public class BlockCallbackProcessor implements CallbackProcessor {

    private final UserStatusService userStatusService;
    private final MessageSender messageSender;

    @Override
    public boolean supports(String callbackData) {
        return callbackData.startsWith("block_");
    }

    @Override
    public void process(Update update, SplitExpensesBot bot) throws TelegramApiException {
        String callbackData = update.getCallbackQuery().getData();
        String chatId = update.getCallbackQuery().getMessage().getChatId().toString();

        if (!userStatusService.isAdmin(chatId)) {
            messageSender.sendMessage(bot, chatId, "Ви не маєте прав для виконання цього!");
            return;
        }

        String uniqueId = callbackData.replace("block_", "");
        User user = userStatusService.blockUser(uniqueId);
        messageSender.sendMessage(bot, chatId, "Користувача " + user.getName() + " заблоковано!");
        messageSender.sendMessage(bot, user.getChatId(), "Ваш доступ до бота заблоковано.");
    }
}