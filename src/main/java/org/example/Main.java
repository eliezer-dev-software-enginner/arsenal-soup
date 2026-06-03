package org.example;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {

    static void main(String[] args) {
        if(args.length < 2) {
            IO.println("Missing runtime arguments! Aborting");
            return;
        }

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        // Agendar para executar a cada 3 horas
        scheduler.scheduleAtFixedRate(() -> {
            try {
                executarTarefa(args);
            } catch (Exception e) {
                System.err.println("Erro: " + e.getMessage());
                e.printStackTrace();
            }
        }, 0, 3, TimeUnit.HOURS);
    }

    private static void executarTarefa(String[] args) throws IOException, InterruptedException {

        Path dataDirectory = Path.of(System.getProperty("user.home")).resolve("arsenal-soup");
        if (!Files.exists(dataDirectory)) Files.createDirectory(dataDirectory);

        Path noticiasJson = dataDirectory.resolve("noticias.json");
        ObjectMapper mapper = new ObjectMapper();

        List<Content> noticiasList = new ArrayList<>();
        if (!Files.exists(noticiasJson)) {
            mapper.writeValue(noticiasJson.toFile(), noticiasList);
        } else {
            noticiasList = mapper.readValue(noticiasJson.toFile(), new TypeReference<>() {
            });
        }

        String url = "https://recomendacao.globo.com/v3/globocom/rec/g1-trendings?registerImpression=false&responseFormat=legacyPublishing&perPage=20";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonNode root = mapper.readTree(response.body());

        //JsonNode root = mapper.readTree(Path.of("C:\\Users\\3855-2278\\Documents\\outros\\dev\\java\\g1-soup\\response.json").toFile());

        List<Content> noticiasTemp = new ArrayList<>();

        for (JsonNode item : root) {
            JsonNode content = item.get("content");
            Content result = getResult(content);

            System.out.println("Título: " + result.titulo());
            System.out.println("Resumo: " + result.resumo());
            System.out.println("Link: " + result.link());
            System.out.println("Imagem: " + result.imagem());
            System.out.println("-----");

            // Verificar se já existe (forma mais eficiente)
            boolean existe = noticiasList.stream()
                    .anyMatch(noticia -> noticia.titulo().trim().equals(result.titulo().trim()));

            if (existe) {
                System.out.println("Notícia já presente no arquivo... Pulando....");
            } else {
                String testChannel = "-1003457993247";
                String chatIdTarget = args[0] == null ? testChannel : args[0];

                String g1 = "-1002403342784";
                new Bot(args[1]).sendMessageTo(result, chatIdTarget);
                noticiasTemp.add(result);
                System.out.println("Nova notícia adicionada: " + result.titulo());
                Thread.sleep(2000);
            }
        }

        noticiasList.addAll(noticiasTemp);
        mapper.writeValue(noticiasJson.toFile(), noticiasList);
    }

    private static Content getResult(JsonNode content) {
        String titulo = content.get("title").asText();
        String resumo = content.get("summary").asText();
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
