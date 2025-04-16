package com.example.split_easy.bot;

import com.example.split_easy.entity.Role;
import com.example.split_easy.entity.Status;
import com.example.split_easy.entity.User;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class BotCommandMenuService {
    private static final List<BotCommand> BASIC_COMMANDS = Arrays.asList(
            new BotCommand("/start", "Почати роботу з ботом"),
            new BotCommand("/help", "Показати цей список команд")
    );

    private static final List<BotCommand> USER_COMMANDS = Arrays.asList(
            new BotCommand("/add_friend", "Додати друга"),
            new BotCommand("/view_friends", "Переглянути список друзів"),
            new BotCommand("/create_group", "Створити групу"),
            new BotCommand("/view_groups", "Переглянути список груп"),
            new BotCommand("/set_payment_method", "Змінити спосіб оплати")
    );

    private static final List<BotCommand> ADMIN_COMMANDS = Arrays.asList(
            new BotCommand("/users", "Переглянути список користувачів"),
            new BotCommand("/pending", "Переглянути користувачів, які очікують схвалення")
    );

    public SetMyCommands getCommandsForUser(User user) {
        List<BotCommand> commands = new ArrayList<>(BASIC_COMMANDS);

        if (user != null && user.getStatus() == Status.APPROVED && user.getRole() == Role.USER) {
            commands.addAll(USER_COMMANDS);
        } else if (user != null && user.getRole() == Role.ADMIN) {
            commands.addAll(USER_COMMANDS);
            commands.addAll(ADMIN_COMMANDS);
        }

        SetMyCommands setMyCommands = new SetMyCommands();
        setMyCommands.setCommands(commands);
        return setMyCommands;
    }

    public List<String> getHelpCommandsForUser(User user) {
        List<String> commands = new ArrayList<>();
        BASIC_COMMANDS.forEach(cmd -> commands.add(cmd.getCommand() + " - " + cmd.getDescription()));

        if (user != null && user.getStatus() == Status.APPROVED && user.getRole() == Role.USER) {
            USER_COMMANDS.forEach(cmd -> commands.add(cmd.getCommand() + " - " + cmd.getDescription()));
        } else if (user != null && user.getRole() == Role.ADMIN) {
            commands.add("");
            commands.add("Команди адміністратора:");
            USER_COMMANDS.forEach(cmd -> commands.add(cmd.getCommand() + " - " + cmd.getDescription()));
            ADMIN_COMMANDS.forEach(cmd -> commands.add(cmd.getCommand() + " - " + cmd.getDescription()));
        }

        return commands;
    }
}

