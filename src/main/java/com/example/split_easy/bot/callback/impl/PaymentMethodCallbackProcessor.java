package com.example.split_easy.bot.callback.impl;

import com.example.split_easy.bot.callback.CallbackProcessor;
import com.example.split_easy.bot.message.MessageSender;
import com.example.split_easy.bot.SplitExpensesBot;
import com.example.split_easy.entity.PaymentMethod;
import com.example.split_easy.entity.Status;
import com.example.split_easy.entity.User;
import com.example.split_easy.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class PaymentMethodCallbackProcessor implements CallbackProcessor {
    private final UserService userService;
    private final MessageSender messageSender;

    private final Map<String, Boolean> awaitingCardNumber = new HashMap<>();

    @Override
    public boolean supports(String callbackData) {
        return callbackData.startsWith("payment_method_");
    }

    @Override
    public void process(Update update, SplitExpensesBot bot) throws TelegramApiException {
        String callbackData = update.getCallbackQuery().getData();
        String chatId = update.getCallbackQuery().getMessage().getChatId().toString();

        User user = userService.findByChatId(chatId).orElse(null);
        if (user == null) {
            messageSender.sendMessage(bot, chatId, "Користувача не знайдено!");
            return;
        }

        if (user.getStatus() != Status.APPROVED) {
            messageSender.sendMessage(bot, chatId, "Ви повинні бути підтверджені адміністратором!");
            return;
        }

        String method = callbackData.replace("payment_method_", "");
        if ("CASH".equals(method)) {
            userService.updatePaymentMethod(chatId, PaymentMethod.CASH);
            messageSender.sendMessage(bot, chatId, "Ви обрали готівку як спосіб оплати.");
            awaitingCardNumber.remove(chatId);
        } else if ("CARD".equals(method)) {
            userService.updatePaymentMethod(chatId, PaymentMethod.CARD);
            awaitingCardNumber.put(chatId, true);
            messageSender.sendMessage(bot, chatId, "Будь ласка, надішліть номер вашої картки.");
        }
    }

    public void processTextMessage(Update update, SplitExpensesBot bot) throws TelegramApiException {
        String chatId = update.getMessage().getChatId().toString();
        String cardNumber = update.getMessage().getText();

        User user = userService.findByChatId(chatId).orElse(null);
        if (user == null) {
            messageSender.sendMessage(bot, chatId, "Користувача не знайдено!");
            return;
        }

        if (user.getStatus() != Status.APPROVED) {
            messageSender.sendMessage(bot, chatId, "Ви повинні бути підтверджені адміністратором!");
            return;
        }

        if (awaitingCardNumber.getOrDefault(chatId, false)) {
            userService.updateCardNumber(chatId, cardNumber);
            messageSender.sendMessage(bot, chatId, "Номер картки збережено!");
            awaitingCardNumber.remove(chatId);
        }
    }
}