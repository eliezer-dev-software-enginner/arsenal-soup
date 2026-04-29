package org.example;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.List;

public class UiBuilder {

    public static InlineKeyboardMarkup inlineButton(String url) {
        // 2. Cria o botão inline "Download"
        // IMPORTANTE: Substitua "https://your.download.link" pela URL real do seu conteúdo.
        InlineKeyboardButton downloadButton = InlineKeyboardButton.builder()
                .text("⬇️ Ler notícia completa")
                .url(url) // URL para o download
                .build();

        var keyboardRow = new InlineKeyboardRow(
                List.of(downloadButton)
        );

        // 3. Cria o layout do teclado: uma linha com o botão
        InlineKeyboardMarkup keyboard = InlineKeyboardMarkup.builder()
                .keyboardRow(keyboardRow)
                .build();
        return keyboard;
    }

    public static String createCaption(Content content){
        StringBuilder sb = new StringBuilder();

        sb.append("<b>\uD83D\uDCF6 ").append(content.titulo()).append("</b>").append("\n\n");

        sb.append(content.resumo()).append("\n\n");

        // 9. Separador
        sb.append("------------------------------------------").append("\n\n");

        return sb.toString();
    }
}