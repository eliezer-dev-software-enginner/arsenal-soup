package org.example;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ArgsParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    static HttpClient client = HttpClient.newHttpClient();
    static Bot bot;
    static ObjectMapper mapper = new ObjectMapper();

    static List<Content> noticiasTemp = new ArrayList<>();
    static String url = "https://recomendacao.globo.com/v3/globocom/rec/g1-trendings?registerImpression=false&responseFormat=legacyPublishing&perPage=20";

    public static void main(String[] args) throws Exception {
        Map<String, String> arguments = ArgsParser.parse(args);

        String targetChannel = ArgsParser.require(arguments, "targetChannel");
        String botToken = ArgsParser.require(arguments, "botToken");

        bot = new Bot(botToken);

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        // Agendar para executar a cada 3 horas
        scheduler.scheduleAtFixedRate(() -> {
            try {
                executarTarefa(targetChannel);
            } catch (Exception e) {
                log.error("Erro ao executar a tarefa de publicação.", e);
            }
        }, 0, 3, TimeUnit.HOURS);

        log.info("Bot iniciado! Publicando notícias do G1 a cada 3 horas. Pressione Ctrl+C para parar.");
        Thread.currentThread().join();
    }

    private static void executarTarefa(String targetChannel) throws IOException, InterruptedException {
        Path dataDirectory = Path.of(System.getProperty("user.home")).resolve("arsenal-soup");
        if (!Files.exists(dataDirectory)) Files.createDirectory(dataDirectory);

        Path noticiasJson = dataDirectory.resolve("noticias.json");

        List<Content> noticiasList = new ArrayList<>();
        if (!Files.exists(noticiasJson)) {
            mapper.writeValue(noticiasJson.toFile(), noticiasList);
        } else {
            noticiasList = mapper.readValue(noticiasJson.toFile(), new TypeReference<>() {
            });
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonNode root = mapper.readTree(response.body());

        //JsonNode root = mapper.readTree(Path.of("C:\\Users\\3855-2278\\Documents\\outros\\dev\\java\\g1-soup\\response.json").toFile());

        for (JsonNode item : root) {
            JsonNode content = item.get("content");
            Content result = getResult(content);

            log.debug("Título: {}", result.titulo());
            log.debug("Resumo: {}", result.resumo());
            log.debug("Link: {}", result.link());
            log.debug("Imagem: {}", result.imagem());

            // Verificar se já existe (forma mais eficiente)
            boolean existe = noticiasList.stream()
                    .anyMatch(noticia -> noticia.titulo().trim().equals(result.titulo().trim()));

            if (existe) {
                log.debug("Notícia já presente no arquivo... Pulando {}", result.titulo());
            } else {
                bot.sendMessageTo(result, targetChannel);
                noticiasTemp.add(result);
                log.info("Nova notícia publicada: {}", result.titulo());
                Thread.sleep(2000);
            }
        }

        noticiasList.addAll(noticiasTemp);
        mapper.writeValue(noticiasJson.toFile(), noticiasList);
    }

    private static Content getResult(JsonNode content) {
        String titulo = content.get("title").asText().trim();
        String resumo = content.get("summary").asText().trim();
        String link = content.get("url").asText();
        String imagem = content
                .get("image")
                .get("sizes")
                .get("L")
                .get("url")
                .asText();
        return new Content(titulo, resumo, link, imagem);
    }

}