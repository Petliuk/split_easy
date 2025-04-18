package com.example.split_easy.bot;

import com.example.split_easy.bot.util.BotCommands;
import com.example.split_easy.entity.Role;
import com.example.split_easy.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class BotCommandMenuService {

    public SetMyCommands getAllCommands() {
        List<BotCommand> commands = new ArrayList<>();
        commands.addAll(BotCommands.BASIC_COMMANDS);
        commands.addAll(BotCommands.APPROVED_USER_COMMANDS);

        SetMyCommands setMyCommands = new SetMyCommands();
        setMyCommands.setCommands(commands);
        return setMyCommands;
    }

    public List<String> getCommandsListForUser(User user) {
        List<String> commands = new ArrayList<>();
        // Додаємо BASIC_COMMANDS і APPROVED_USER_COMMANDS
        BotCommands.BASIC_COMMANDS.forEach(
                cmd -> commands.add(cmd.getCommand() + " - " + cmd.getDescription()));
        BotCommands.APPROVED_USER_COMMANDS.forEach(
                cmd -> commands.add(cmd.getCommand() + " - " + cmd.getDescription()));

        // Додаємо ADMIN_COMMANDS для адміністраторів
        if (user != null && user.getRole() == Role.ADMIN) {
            BotCommands.ADMIN_COMMANDS.forEach(
                    cmd -> commands.add(cmd.getCommand() + " - " + cmd.getDescription()));
        }

        return commands;
    }
}

