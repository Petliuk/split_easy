package com.example.split_easy.bot.callback.impl;

import com.example.split_easy.bot.SplitExpensesBot;
import com.example.split_easy.bot.callback.CallbackProcessor;
import com.example.split_easy.bot.command.Command;
import com.example.split_easy.bot.command.CommandFactory;
import com.example.split_easy.bot.message.MessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@RequiredArgsConstructor
public class CommandCallbackProcessor  implements CallbackProcessor{
    private final CommandFactory commandFactory;
    private final MessageSender messageSender;

    @Override
    public boolean supports(String callbackData) {
        return callbackData.startsWith("command_");
    }

    @Override
    public void process(Update update, SplitExpensesBot bot) throws TelegramApiException {
        String callbackData = update.getCallbackQuery().getData();
        String chatId = update.getCallbackQuery().getMessage().getChatId().toString();
        String commandText = callbackData.replace("command_", "");
        Command command = commandFactory.getCommand(commandText);
        if (command != null) {
            command.execute(update, bot);
        } else {
            messageSender.sendMessage(bot, chatId, "Невідома команда: " + commandText);
        }
    }
}
