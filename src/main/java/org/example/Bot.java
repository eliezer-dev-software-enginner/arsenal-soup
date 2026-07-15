package org.example;

import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;


public class Bot implements LongPollingSingleThreadUpdateConsumer {
    private final TelegramClient telegramClient;

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

    public InlineKeyboardMarkup inlineButton(String url) {
        InlineKeyboardButton downloadButton = InlineKeyboardButton.builder()
                .text("⬇️ Ler notícia completa")
                .url(url)
                .build();

        var keyboardRow = new InlineKeyboardRow(
                List.of(downloadButton)
        );

        // 3. Cria o layout do teclado: uma linha com o botão
        return InlineKeyboardMarkup.builder()
                .keyboardRow(keyboardRow)
                .build();
    }

    public String createCaption(Content content){
        return "<b>\uD83D\uDCF6 " + content.titulo() + "</b>" + "\n\n" +
                content.resumo() + "\n\n" +

                // 9. Separador
                "------------------------------------------" + "\n\n";
    }
}