package router;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import produto.ProdutoController;

import java.io.IOException;
import java.io.OutputStream;

public class MasterRouter implements HttpHandler {

    private final ProdutoController produtoController = new ProdutoController();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        if (path.equals("/produtos") && method.equals("GET")) {
            produtoController.handleGetProdutos(exchange);

        // No futuro, você adicionaria novas rotas aqui:
        //} else if (path.equals("/clientes") && method.equals("GET")) {
        //    clienteController.handleGetClientes(exchange);

        } else {
            // Se nenhuma rota corresponder, retorna 404 Not Found
            String response = "404 - Rota nao encontrada";
            exchange.sendResponseHeaders(404, response.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }
}