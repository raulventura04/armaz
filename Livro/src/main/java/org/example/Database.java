package org.example;

import java.sql.*;

public class Database {
    private static final String URL = "jdbc:postgresql://localhost:5432/Literatura";
    private static final String USER = "postgres";
    private static final String PASSWORD = "123";

    // Estabelece a conexão com o banco de dados
    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Cria a tabela (se não existir)
    public static void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS livros (" +
                "id SERIAL PRIMARY KEY," +
                "title VARCHAR(255) NOT NULL," +
                "author VARCHAR(255) NOT NULL," +
                "language VARCHAR(50)," +
                "downloads INT," +
                "rating FLOAT," +
                "birth_year INT," +
                "death_year INT)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.execute();
            System.out.println("Tabela criada com sucesso.");
        } catch (SQLException e) {
            System.out.println("Erro ao criar tabela: " + e.getMessage());
        }
    }

    // Insere um novo livro com título, autor, idioma, downloads, data de nascimento e data de falecimento
    public static void insertBook(String title, String author, String language, int downloads, int birthYear, int deathYear) {
        String sql = "INSERT INTO livros(title, author, language, downloads, birth_year, death_year) VALUES(?, ?, ?, ?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, title);
            pstmt.setString(2, author);
            pstmt.setString(3, language);
            pstmt.setInt(4, downloads);
            pstmt.setInt(5, birthYear);
            pstmt.setInt(6, deathYear);
            pstmt.executeUpdate();
            System.out.println("Livro inserido com sucesso.");
        } catch (SQLException e) {
            System.out.println("Erro ao inserir livro: " + e.getMessage());
        }
    }

    // Lista todos os livros
    public static void listBooks(String language) {
        String sql = "SELECT id, title, author, language, downloads, birth_year, death_year FROM livros";

        if (language != null) {
            sql += " WHERE language = ?";
        }

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (language != null) {
                pstmt.setString(1, language);
            }

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id"));
                System.out.println("Título: " + rs.getString("title"));
                System.out.println("Autor: " + rs.getString("author"));
                System.out.println("Idioma: " + rs.getString("language"));
                System.out.println("Downloads: " + rs.getInt("downloads"));
                System.out.println("Data de nascimento: " + rs.getInt("birth_year"));
                System.out.println("Data de falecimento: " + rs.getInt("death_year"));
                System.out.println();
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar livros: " + e.getMessage());
        }
    }

    // Lista todos os autores registrados com ano de nascimento, ano de falecimento e livros associados
    public static void listAuthorsWithDetails() {
        String sql = "SELECT author, birth_year, death_year, ARRAY_AGG(title) AS livros " +
                "FROM livros " +
                "GROUP BY author, birth_year, death_year";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            System.out.println("Autores Registrados:");
            while (rs.next()) {
                System.out.println("Nome do Autor: " + rs.getString("author"));
                System.out.println("Ano de Nascimento: " + rs.getInt("birth_year"));
                System.out.println("Ano de Falecimento: " + rs.getInt("death_year"));
                System.out.println("Livros:");
                String[] livros = (String[]) rs.getArray("livros").getArray();
                for (String livro : livros) {
                    System.out.println("- " + livro);
                }
                System.out.println();
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar autores: " + e.getMessage());
        }
    }

    // Lista autores vivos em um determinado ano
    public static void listAuthorsAliveInYear(int year) {
        String sql = "SELECT DISTINCT author FROM livros WHERE (birth_year <= ? OR birth_year IS NULL) AND (death_year >= ? OR death_year IS NULL)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, year);
            pstmt.setInt(2, year);

            ResultSet rs = pstmt.executeQuery();

            System.out.println("Autores Vivos em " + year + ":");
            while (rs.next()) {
                System.out.println(rs.getString("author"));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar autores vivos: " + e.getMessage());
        }
    }


    // Classifica livros por classificação média
    public static void listBooksByAverageRating() {
        String sql = "SELECT title, author, rating FROM livros ORDER BY rating DESC";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            System.out.println("Livros classificados por classificação média:");
            while (rs.next()) {
                System.out.println("Título: " + rs.getString("title"));
                System.out.println("Autor: " + rs.getString("author"));
                System.out.println("Classificação: " + rs.getFloat("rating"));
                System.out.println();
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar livros por classificação média: " + e.getMessage());
        }
    }

    // Encontra livros similares
    public static void findSimilarBooks(String titlePart) {
        String sql = "SELECT title, author, language, downloads FROM livros WHERE title ILIKE ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + titlePart + "%");

            ResultSet rs = pstmt.executeQuery();

            System.out.println("Livros similares encontrados:");
            while (rs.next()) {
                System.out.println("Título: " + rs.getString("title"));
                System.out.println("Autor: " + rs.getString("author"));
                System.out.println("Idioma: " + rs.getString("language"));
                System.out.println("Downloads: " + rs.getInt("downloads"));
                System.out.println();
            }
        } catch (SQLException e) {
            System.out.println("Erro ao encontrar livros similares: " + e.getMessage());
        }
    }// Método main para testar a conexão e as operações

//    public static void main(String[] args) {
//        createTable();
//        insertBook("Example Book", "John Doe", "en", 1500, Date.valueOf("2000-01-01"), Date.valueOf("2020-01-01"));
//        listBooks(null); // Lista todos os livros
//        listAuthorsWithDetails();   // Lista todos os autores registrados
//        listAuthorsAliveInYear(2024); // Lista autores vivos em 2024
//        listBooksByAverageRating(); // Lista livros classificados por classificação média
//        findSimilarBooks("Example"); // Encontra livros similares
//    }
}
