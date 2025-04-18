package com.example.split_easy.bot.command.impl;

import com.example.split_easy.bot.SplitExpensesBot;
import com.example.split_easy.bot.command.Command;
import com.example.split_easy.bot.message.MessageSender;
import com.example.split_easy.bot.util.KeyboardUtils;
import com.example.split_easy.entity.Role;
import com.example.split_easy.entity.Status;
import com.example.split_easy.entity.User;
import com.example.split_easy.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Component
public class StartCommand implements Command{
    private final MessageSender messageSender;
    private final UserService userService;
    private final KeyboardUtils keyboardUtils;
    private final String adminChatId;

    public StartCommand(MessageSender messageSender, UserService userService, KeyboardUtils keyboardUtils,
                        @Value("${admin.chatId}") String adminChatId) {
        this.messageSender = messageSender;
        this.userService = userService;
        this.keyboardUtils = keyboardUtils;
        this.adminChatId = adminChatId;
    }

    @Override
    public void execute(Update update, SplitExpensesBot bot) throws TelegramApiException {
        String chatId = update.getMessage().getChatId().toString();
        String userName = update.getMessage().getChat().getFirstName();
        log.info("Processing /start command for chatId: {}", chatId);

        User user = userService.registerUser(chatId, userName);

        if (user.getRole() == Role.ADMIN) {
            messageSender.sendMessage(bot, chatId, "Вітаємо, адміне! Ви можете використовувати всі доступні команди.");
        } else if (user.getStatus() == Status.APPROVED) {
            if (user.getPaymentMethod() == null) {
                sendPaymentMethodOptions(bot, chatId);
            } else {
                messageSender.sendMessage(bot, chatId, "Вітаємо! Ви вже зареєстровані та можете використовувати бот.");
            }
        } else {
            messageSender.sendMessage(bot, chatId, "Вітаємо! Ваш запит на реєстрацію відправлено. Чекайте схвалення від адміністратора.");
            String adminMessage = String.format("Новий користувач подав заявку на реєстрацію:\n" +
                    "Ім'я: %s\nChat ID: %s\nУнікальний ID: %s", userName, chatId, user.getUniqueId());
            messageSender.sendMessage(bot, adminChatId, adminMessage);
        }
    }

    private void sendPaymentMethodOptions(SplitExpensesBot bot, String chatId) throws TelegramApiException {
        messageSender.sendMessage(bot, chatId, "Оберіть спосіб отримання грошей:", keyboardUtils.createPaymentMethodKeyboard());
    }
}
