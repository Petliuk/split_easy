package com.example.split_easy.bot.command;

import com.example.split_easy.bot.command.impl.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CommandFactory {
    private final StartCommand startCommand;
    private final PendingCommand pendingCommand;
    private final ApproveCommand approveCommand;
    private final HelpCommand helpCommand;
    private final UsersCommand usersCommand;
    private final AddFriendCommand addFriendCommand;
    private final ViewFriendsCommand viewFriendsCommand;
    private final CommandsCommand commandsCommand;

    private final Map<String, Command> commandMap = new HashMap<>();

    @PostConstruct
    public void init() {
        commandMap.put("/start", startCommand);
        commandMap.put("/pending", pendingCommand);
        commandMap.put("/approve", approveCommand);
        commandMap.put("/users", usersCommand);
        commandMap.put("/add_friend", addFriendCommand);
        commandMap.put("/view_friends", viewFriendsCommand);
        commandMap.put("/command", commandsCommand);
        commandMap.put("/help", helpCommand);
    }

    public Command getCommand(String commandText) {
        if (commandText.startsWith("/approve_")) {
            return approveCommand;
        }
        return commandMap.get(commandText);
    }
}
