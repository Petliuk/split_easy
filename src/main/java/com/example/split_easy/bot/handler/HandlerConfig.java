package com.example.split_easy.bot.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HandlerConfig {
    private final TextMessageHandler textMessageHandler;
    private final CallbackQueryHandler callbackQueryHandler;

    @Bean
    public UpdateHandler updateHandler() {
        textMessageHandler.setNextHandler(callbackQueryHandler);
        return textMessageHandler;
    }
}
