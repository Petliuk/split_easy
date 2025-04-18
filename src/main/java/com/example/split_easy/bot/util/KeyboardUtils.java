package com.example.split_easy.bot.util;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import java.util.ArrayList;
import java.util.List;

@Component
public class KeyboardUtils {

    public record Button(String text, String callbackData) {}

    public InlineKeyboardMarkup createKeyboard(List<Button> buttons, int buttonsPerRow) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        for (int i = 0; i < buttons.size(); i += buttonsPerRow) {
            rows.add(buttons.subList(i, Math.min(i + buttonsPerRow, buttons.size()))
                    .stream()
                    .map(b -> InlineKeyboardButton.builder()
                            .text(b.text())
                            .callbackData(b.callbackData())
                            .build())
                    .toList());
        }

        markup.setKeyboard(rows);
        return markup;
    }

    public InlineKeyboardMarkup createPaymentMethodKeyboard() {
        List<Button> buttons = List.of(
                new Button("Готівка", "payment_method_CASH"),
                new Button("Картка", "payment_method_CARD")
        );
        return createKeyboard(buttons, 2);
    }

    public InlineKeyboardMarkup createUserManagementKeyboard(String userId) {
        List<Button> buttons = List.of(
                new Button("Підтвердити", "approve_" + userId),
                new Button("Відхилити", "reject_" + userId),
                new Button("Заблокувати", "block_" + userId)
        );
        return createKeyboard(buttons, 3);
    }
}
