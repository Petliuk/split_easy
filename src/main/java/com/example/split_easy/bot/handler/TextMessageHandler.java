package com.example.split_easy.bot.handler;

import com.example.split_easy.bot.SplitExpensesBot;
import com.example.split_easy.bot.callback.impl.PaymentMethodCallbackProcessor;
import com.example.split_easy.bot.command.Command;
import com.example.split_easy.bot.command.CommandFactory;
import com.example.split_easy.bot.command.impl.AddFriendCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Component
@RequiredArgsConstructor
public class TextMessageHandler implements UpdateHandler{
    private final CommandFactory commandFactory;
    private final AddFriendCommand addFriendCommand;
    private final PaymentMethodCallbackProcessor paymentMethodCallbackProcessor;
    private UpdateHandler nextHandler;

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
            command.execute(update, bot);
            return;
        }

        // Обробка текстових повідомлень для додавання друга або номера картки
        if (addFriendCommand.isAwaitingFriendUniqueId(chatId)) {
            addFriendCommand.execute(update, bot);
            return;
        }

        paymentMethodCallbackProcessor.processTextMessage(update, bot);
    }

}
