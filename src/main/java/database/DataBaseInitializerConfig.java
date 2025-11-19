package database;

import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DataBaseInitializerConfig {
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(DataBaseInitializerConfig.class);
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
            LOGGER.info("Tabela 'produto' verificada/criada com sucesso.");
        } catch (SQLException e) {
            LOGGER.error("Erro ao verificar/criar a tabela 'produto': {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

}
