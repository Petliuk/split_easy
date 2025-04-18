package com.example.split_easy.bot.util;

import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

import java.util.Arrays;
import java.util.List;

public class BotCommands {
    public static final List<BotCommand> BASIC_COMMANDS = Arrays.asList(
            new BotCommand("/start", "Почати роботу з ботом"),
            new BotCommand("/help", "Показати інструкцію з використання бота"),
            new BotCommand("/command", "Показати список доступних команд")
    );

    public static final List<BotCommand> APPROVED_USER_COMMANDS = Arrays.asList(
            new BotCommand("/add_friend", "Додати друга"),
            new BotCommand("/view_friends", "Переглянути список друзів"),
            new BotCommand("/create_group", "Створити групу"),
            new BotCommand("/view_groups", "Переглянути список груп"),
            new BotCommand("/set_payment_method", "Змінити спосіб оплати")
    );

    public static final List<BotCommand> ADMIN_COMMANDS = Arrays.asList(
            new BotCommand("/users", "Переглянути список користувачів"),
            new BotCommand("/pending", "Переглянути користувачів, які очікують схвалення")
    );

    public static final String[] ALLOWED_COMMANDS_FOR_UNAPPROVED = {"/start"};

    private BotCommands() {
    }

}
