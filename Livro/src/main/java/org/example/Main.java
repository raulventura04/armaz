package org.example;

import java.util.Scanner;

import static org.example.Database.createTable;

public class Main {
    private static final ApiClient apiClient = new ApiClient();

    public static void main(String[] args) {
        createTable();
        Scanner scanner = new Scanner(System.in);
        String language = null; // Inicializa a variável language como null

        while (true) {
            System.out.println("Escolha uma opção:");
            System.out.println("1. Buscar livro pelo título");
            System.out.println("2. Listar todos os livros registrados");
            System.out.println("3. Listar todos os autores registrados");
            System.out.println("4. Listar autores vivos em determinado ano");
            System.out.println("5. Listar livros em um determinado idioma");
            System.out.println("6. Classificar livros por classificação média");
            System.out.println("7. Encontrar livros similares");
            System.out.println("8. Sair");

            int opcao = -1;
            if (scanner.hasNextInt()) {
                opcao = scanner.nextInt();
                scanner.nextLine(); // Consumir a nova linha
            } else {
                System.out.println("Opção inválida. Digite um número entre 1 e 8.");
                scanner.nextLine(); // Consumir a entrada inválida
                continue;
            }

            switch (opcao) {
                case 1:
                    System.out.println("Digite o título do livro:");
                    String query = scanner.nextLine();
                    try {
                        apiClient.fetchAndStoreBooks(query);
                    } catch (Exception e) {
                        System.out.println("Erro ao buscar livro: " + e.getMessage());
                    }
                    break;
                case 2:
                    Database.listBooks(language); // Lista todos os livros
                    break;
                case 3:
                    Database.listAuthorsWithDetails(); // Lista todos os autores registrados
                    break;
                case 4:
                    System.out.println("Digite o ano:");
                    if (scanner.hasNextInt()) {
                        int year = scanner.nextInt();
                        scanner.nextLine(); // Consumir a nova linha
                        Database.listAuthorsAliveInYear(year); // Lista autores vivos em determinado ano
                    } else {
                        System.out.println("Ano inválido. Tente novamente.");
                        scanner.nextLine(); // Consumir a entrada inválida
                    }
                    break;
                case 5:
                    System.out.println("Digite o idioma (ou deixe em branco para listar todos os idiomas):");
                    language = scanner.nextLine().trim();
                    if (language.isEmpty()) {
                        language = null;
                    }
                    Database.listBooks(language); // Lista livros em um determinado idioma
                    break;
                case 6:
                    Database.listBooksByAverageRating(); // Classifica livros por classificação média
                    break;
                case 7:
                    System.out.println("Digite parte do título do livro para encontrar similares:");
                    String similarTitle = scanner.nextLine();
                    Database.findSimilarBooks(similarTitle); // Encontra livros similares
                    break;
                case 8:
                    System.out.println("Saindo...");
                    scanner.close();
                    System.exit(0);
                    break;
                default:
                    System.out.println("Opção inválida. Digite um número entre 1 e 8.");
            }
        }
    }
}