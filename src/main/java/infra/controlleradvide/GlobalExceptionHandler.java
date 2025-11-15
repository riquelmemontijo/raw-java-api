package infra.controlleradvide;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import shared.utils.response.ResponseUtils;

import java.io.IOException;
import java.sql.SQLException;

public class GlobalExceptionHandler {

    public static void handle(HttpExchange exchange, HandlerAction action) {
        try {
            action.execute();
        } catch (NumberFormatException | JsonSyntaxException e) {
            sendErrorResponse(exchange, 400, "Requisição inválida: " + e.getMessage());
        } catch (SQLException e) {
            sendErrorResponse(exchange, 500, "Erro interno do servidor ao acessar o banco de dados.");
        } catch (IOException e) {
            sendErrorResponse(exchange, 500, "Erro interno do servidor durante o processamento da requisição.");
        } catch (Exception e) {
            sendErrorResponse(exchange, 500, "Ocorreu um erro inesperado.");
        }
    }

    private static void sendErrorResponse(HttpExchange exchange, int statusCode, String message) {
        try {
            var errorResponse = new ErrorResponse(statusCode, "Error", message);
            ResponseUtils.create(exchange)
                    .withStatusCode(statusCode)
                    .withBody(errorResponse)
                    .send();
        } catch (IOException _) {
        }
    }

}
