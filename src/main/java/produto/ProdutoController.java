package produto;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class ProdutoController {

    public void handleGetProdutos(HttpExchange exchange) throws IOException {
        // A lógica que estava no Main.java agora vive aqui.
        List<Produto> produtos = List.of(
                new Produto(1L, "Notebook Gamer", new BigDecimal("7500.00")),
                new Produto(2L, "Mouse Vertical", new BigDecimal("250.50"))
        );

        String jsonResponse = produtos.stream()
                .map(p -> String.format("{\"id\":%d,\"nome\":\"%s\",\"preco\":%s}",
                        p.getId(), p.getNome(), p.getPreco().toPlainString()))
                .collect(Collectors.joining(",", "[", "]"));

        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, jsonResponse.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(jsonResponse.getBytes());
        }
    }

}
