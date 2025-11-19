package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;

public class DatabaseConnectionFactory {

    public static Connection getConnection() throws SQLException {
        // Lê as variáveis de ambiente passadas pelo Docker Compose.
        // orElse() fornece um valor padrão para desenvolvimento local fora do Docker.
        String url = Optional.ofNullable(System.getenv("DB_URL"))
                .orElse("jdbc:mysql://localhost:3306/rawdb");

        String user = Optional.ofNullable(System.getenv("DB_USER"))
                .orElse("user");

        String password = Optional.ofNullable(System.getenv("DB_PASSWORD"))
                .orElse("password");

        return DriverManager.getConnection(url, user, password);
    }

}
