package com.example.split_easy.bot.handler;

import com.example.split_easy.bot.SplitExpensesBot;
import com.example.split_easy.bot.command.Command;
import com.example.split_easy.bot.command.CommandFactory;
import com.example.split_easy.bot.message.MessageSender;
import com.example.split_easy.entity.PaymentMethod;
import com.example.split_easy.entity.User;
import com.example.split_easy.service.FriendService;
import com.example.split_easy.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommandHandler implements UpdateHandler{
    private final CommandFactory commandFactory;
    private final UserService userService;
    private final FriendService friendService;
    private final MessageSender messageSender;
    private UpdateHandler nextHandler;

    private final Map<String, Boolean> awaitingFriendUniqueId = new HashMap<>();

    public void setNextHandler(UpdateHandler nextHandler) {
        this.nextHandler = nextHandler;
    }

    @Override
    public void handle(Update update, SplitExpensesBot bot) throws TelegramApiException {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            log.info("Оновлення не містить текстового повідомлення, передача наступному обробнику");
            if (nextHandler != null) {
                nextHandler.handle(update, bot);
            }
            return;
        }

        String messageText = update.getMessage().getText();
        String chatId = update.getMessage().getChatId().toString();
        log.info("Обробка текстового повідомлення: {} від chatId: {}", messageText, chatId);

        Command command = commandFactory.getCommand(messageText);
        if (command != null) {
            awaitingFriendUniqueId.remove(chatId);
            if (messageText.equals("/add_friend")) {
                awaitingFriendUniqueId.put(chatId, true);
            }
            command.execute(update, bot);
            return;
        }

        User user = userService.findByChatId(chatId).orElse(null);
        if (user == null) {
            log.warn("Користувача не знайдено для chatId: {}", chatId);
            messageSender.sendMessage(bot, chatId, "Користувача не знайдено. Спробуйте ще раз.");
            return;
        }

        if (awaitingFriendUniqueId.getOrDefault(chatId, false)) {
            User friend = userService.findByUniqueId(messageText).orElse(null);
            if (friend == null) {
                messageSender.sendMessage(bot, chatId, "Друга з таким uniqueId не знайдено.");
            } else {
                friendService.addFriend(chatId, messageText);
                messageSender.sendMessage(bot, chatId, "Користувача " + friend.getName() + " додано в друзі!");
            }
            awaitingFriendUniqueId.remove(chatId);
            return;
        }

        if (user.getPaymentMethod() == PaymentMethod.CARD && user.getCardNumber() == null) {
            log.info("Збереження номера картки: {} для chatId: {}", messageText, chatId);
            userService.updateCardNumber(chatId, messageText);
            messageSender.sendMessage(bot, chatId, "Номер картки збережено!");
            return;
        }

        log.info("Команду не знайдено для повідомлення: {}, передача наступному обробнику", messageText);
        if (nextHandler != null) {
            nextHandler.handle(update, bot);
        }
    }
}
