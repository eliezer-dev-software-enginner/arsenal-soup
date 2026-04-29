package org.example;

/*
  System.out.println("Título: " + result.titulo());
            System.out.println("Resumo: " + result.resumo());
            System.out.println("Link: " + result.link());
            System.out.println("Imagem: " + result.imagem());
 */
public record Content(
        String titulo,  String resumo, String link, String imagem
) {
}
