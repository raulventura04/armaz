package org.example;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient.Redirect;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Date;

public class ApiClient {
    private static final String API_URL = "https://gutendex.com/books";
    private Date parseDateString(String dateString) {
        try {
            return Date.valueOf(dateString);
        } catch (IllegalArgumentException e) {
            System.out.println("Erro ao converter string de data: " + dateString);
            return null;
        }
    }
    public void fetchAndStoreBooks(String query) throws Exception {
        JsonNode books = fetchBooks(query);
        if (books.has("results")) { // Verifica se a chave "results" está presente
            for (JsonNode book : books.get("results")) {
                System.out.println("Book JSON: " + book.toString()); // Imprimir o JSON do livro para depuração
                String title = book.get("title").asText();
                JsonNode authorNode = book.get("authors").get(0);
                String author = authorNode.get("name").asText();
                String language = book.get("languages").get(0).asText();
                int downloads = book.get("formats").size();
                JsonNode formats = book.get("formats");
                int birthYear = authorNode.get("birth_year").asInt();
                int deathYear = authorNode.get("death_year").asInt();
                System.out.println("Título: " + title);
                System.out.println("Autor: " + author);
                System.out.println("Idioma: " + language);
                System.out.println("Downloads: " + downloads);
                System.out.println("Ano de nascimento: " + birthYear);
                System.out.println("Ano de falecimento: " + deathYear);
                Database.insertBook(title, author, language, downloads, birthYear, deathYear);
            }
            System.out.println("Livros inseridos com sucesso no banco de dados.");
        } else {
            System.out.println("A resposta da API não contém a chave 'results'.");
        }
    }


    JsonNode fetchBooks(String query) throws Exception {
        // Substitui os espaços por "+"
        String formattedQuery = query.replace(" ", "+");
        String fullUrl = API_URL + "?search=" + formattedQuery;

        // Imprime a URL completa para verificação
        System.out.println("Request URL: " + fullUrl);

        HttpClient client = HttpClient.newBuilder()
                .followRedirects(Redirect.NORMAL)  // Seguir redirecionamentos
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(fullUrl))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Verifica se a resposta foi bem-sucedida
        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to fetch books: " + response.statusCode());
        }

        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(response.body());
    }
}
