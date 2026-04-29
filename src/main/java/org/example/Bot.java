package org.example;

import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import static org.example.UiBuilder.createCaption;
import static org.example.UiBuilder.inlineButton;

public class Bot implements LongPollingSingleThreadUpdateConsumer {
    private final TelegramClient telegramClient;

    public Bot(){
        telegramClient = new OkHttpTelegramClient(System.getenv("BOT_TOKEN"));
    }

    public Bot(String token) {
        telegramClient = new OkHttpTelegramClient(token);
    }

    @Override
    public void consume(Update update) {
        // Lógica de consumo de updates (chat, etc.) permanece inalterada
    }

    public void sendMessageTo(Content content, String chat_id){
        InlineKeyboardMarkup keyboard = inlineButton(content.link());

        // 4. Monta a mensagem SendPhoto
        var msg = SendPhoto
                .builder()
                .chatId(chat_id)
                .photo(new InputFile(content.imagem()))
                .caption(createCaption(content))
                .parseMode("HTML") // **MUITO IMPORTANTE**: Informa ao Telegram para interpretar o 'caption' como HTML (para o negrito)
                .replyMarkup(keyboard) // Anexa o teclado inline à mensagem
                .build();
        try {
            // Executa o envio
            telegramClient.execute(msg);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}