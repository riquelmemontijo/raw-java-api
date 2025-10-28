package database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DataBaseInitializerConfig {

    public void initializeDatabase() {
        criaTabelaProduto();
    }

    private void criaTabelaProduto(){
        var scriptCriacao = """
                            CREATE TABLE IF NOT EXISTS produto (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                nome VARCHAR(255) NOT NULL,
                                preco DECIMAL(10, 2) NOT NULL
                            );
                            """;

        try (Connection connection = DatabaseConnectionFactory.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(scriptCriacao);
            System.out.println("Tabela 'produto' verificada/criada com sucesso.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
