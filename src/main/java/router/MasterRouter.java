package router;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class MasterRouter implements HttpHandler {

    private final List<Route> routes;

    public MasterRouter() {
        AnnotationScanner scanner = new AnnotationScanner();
        this.routes = scanner.scan();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        for (Route route : routes) {
            if (route.matches(method, path)) {
                route.handler().accept(exchange);
                return;
            }
        }

        String response = "404 - Rota nao encontrada";
        exchange.sendResponseHeaders(404, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}