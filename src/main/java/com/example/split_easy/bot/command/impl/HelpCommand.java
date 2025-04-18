package com.example.split_easy.bot.command.impl;

import com.example.split_easy.bot.SplitExpensesBot;
import com.example.split_easy.bot.command.Command;
import com.example.split_easy.bot.message.MessageSender;
import com.example.split_easy.entity.Role;
import com.example.split_easy.entity.Status;
import com.example.split_easy.entity.User;
import com.example.split_easy.service.UserService;
import com.vdurmont.emoji.EmojiManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Component
@RequiredArgsConstructor
public class HelpCommand implements Command {
    private final MessageSender messageSender;
    private final UserService userService;

    @Override
    public void execute(Update update, SplitExpensesBot bot) throws TelegramApiException {
        String chatId = update.getMessage().getChatId().toString();
        log.info("Processing /help command for chatId: {}", chatId);

        User user = userService.findByChatId(chatId).orElse(null);
        if (user == null) {
            messageSender.sendMessage(bot, chatId, "Користувача не знайдено. Спробуйте надіслати /start.");
            return;
        }

        String helpMessage = buildHelpMessage(user);
        messageSender.sendMessage(bot, chatId, helpMessage);
    }

    private String buildHelpMessage(User user) {
        StringBuilder helpMessage = new StringBuilder();
        helpMessage.append(EmojiManager.getForAlias("open_book").getUnicode())
                .append(" **Довідка SplitEasyBot** ")
                .append(EmojiManager.getForAlias("open_book").getUnicode())
                .append("\n\n");

        if (user.getStatus() != Status.APPROVED) {
            helpMessage.append(EmojiManager.getForAlias("warning").getUnicode())
                    .append(" **Ваш профіль ще не підтверджено адміністратором**\n")
                    .append("Щоб почати користуватися ботом, виконайте наступні кроки:\n")
                    .append(EmojiManager.getForAlias("one").getUnicode())
                    .append(" Надішліть команду **/start**, щоб зареєструватися в системі.\n")
                    .append(EmojiManager.getForAlias("two").getUnicode())
                    .append(" Дочекайтеся, поки адміністратор підтвердить ваш запит. Ви отримаєте сповіщення.\n")
                    .append(EmojiManager.getForAlias("three").getUnicode())
                    .append(" Після підтвердження вам стануть доступні всі функції бота, такі як додавання друзів, створення груп і керування витратами.\n\n")
                    .append(EmojiManager.getForAlias("id").getUnicode())
                    .append(" **Ваш uniqueId**: ").append(user.getUniqueId()).append("\n")
                    .append("Цей унікальний ідентифікатор потрібен для додавання вас у друзі іншими користувачами. Поділіться ним із тими, з ким плануєте розділяти витрати.\n\n")
                    .append(EmojiManager.getForAlias("bulb").getUnicode())
                    .append(" **Порада**: Якщо підтвердження затримується, зверніться до адміністратора.\n");
            return helpMessage.toString();
        }

        helpMessage.append("Ласкаво просимо до **SplitEasyBot**! ")
                .append(EmojiManager.getForAlias("moneybag").getUnicode())
                .append("\nЦей бот допомагає легко розділяти витрати з друзями, створювати групи для спільних платежів і керувати фінансами.\n\n")
                .append(EmojiManager.getForAlias("id").getUnicode())
                .append(" **Ваш uniqueId**: ").append(user.getUniqueId()).append("\n")
                .append("Використовуйте цей ідентифікатор, щоб додавати друзів або ділитися ним із іншими для спільних витрат.\n\n")
                .append(EmojiManager.getForAlias("gear").getUnicode())
                .append(" **Як користуватися ботом**:\n")
                .append(EmojiManager.getForAlias("rocket").getUnicode())
                .append(" **/start** — Зареєструйтеся в системі або перевірте статус вашого профілю. Ця команда ініціалізує ваш обліковий запис.\n")
                .append(EmojiManager.getForAlias("busts_in_silhouette").getUnicode())
                .append(" **/add_friend** — Додайте друга, ввівши його uniqueId. Після цього ви зможете створювати спільні групи для витрат.\n")
                .append(EmojiManager.getForAlias("mag").getUnicode())
                .append(" **/view_friends** — Перегляньте список ваших друзів, доданих у системі, щоб перевірити, з ким ви можете ділити витрати.\n")
                .append(EmojiManager.getForAlias("busts_in_silhouette").getUnicode())
                .append(" **/create_group** — Створіть групу для спільних витрат, наприклад, для вечірки чи поїздки. Ви зможете додати до групи друзів.\n")
                .append(EmojiManager.getForAlias("mag_right").getUnicode())
                .append(" **/view_groups** — Перегляньте список ваших груп, щоб перевірити їхній статус або керувати учасниками.\n")
                .append(EmojiManager.getForAlias("credit_card").getUnicode())
                .append(" **/set_payment_method** — Виберіть спосіб оплати (картка чи готівка). Якщо обрано картку, введіть її номер для зручного розрахунку.\n")
                .append(EmojiManager.getForAlias("question").getUnicode())
                .append(" **/command** — Отримайте повний список доступних команд із короткими описами для швидкого ознайомлення.\n\n");

        if (user.getRole() == Role.ADMIN) {
            helpMessage.append(EmojiManager.getForAlias("cop").getUnicode())
                    .append(" **Адмін-панель**:\n")
                    .append("Ви маєте доступ до спеціальних команд для керування користувачами:\n")
                    .append(EmojiManager.getForAlias("bust_in_silhouette").getUnicode())
                    .append(" **/users** — Перегляньте список усіх зареєстрованих користувачів, їхні статуси та унікальні ідентифікатори.\n")
                    .append(EmojiManager.getForAlias("hourglass").getUnicode())
                    .append(" **/pending** — Ознайомтеся зі списком користувачів, які очікують підтвердження, щоб активувати їхні профілі.\n")
                    .append(EmojiManager.getForAlias("white_check_mark").getUnicode())
                    .append(" **/approve_<uniqueId>** — Підтвердьте користувача, ввівши його uniqueId. Наприклад, /approve_123456789 активує профіль.\n\n");
        }

        helpMessage.append(EmojiManager.getForAlias("bulb").getUnicode())
                .append(" **Порада**: Якщо у вас виникли запитання або проблеми, зверніться до адміністратора або повторно надішліть **/help** для перегляду цієї довідки.\n");

        return helpMessage.toString();
    }
}
