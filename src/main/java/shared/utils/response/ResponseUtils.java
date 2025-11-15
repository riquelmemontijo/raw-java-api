package shared.utils.response;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ResponseUtils {

    private static final Gson GSON = new Gson();

    public static JsonResponseBuilder create(HttpExchange exchange) {
        return new JsonResponseBuilder(exchange);
    }

    public static class JsonResponseBuilder {

        private final HttpExchange exchange;
        private int statusCode = 200;
        private Object body;
        private final Map<String, String> headers = new HashMap<>();

        private JsonResponseBuilder(HttpExchange exchange) {
            this.exchange = exchange;
            this.headers.put("Content-Type", "application/json; charset=utf-8");
        }

        public JsonResponseBuilder withStatusCode(int statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        public JsonResponseBuilder withBody(Object body) {
            this.body = body;
            return this;
        }

        public JsonResponseBuilder withHeader(String key, String value) {
            this.headers.put(key, value);
            return this;
        }

        public void send() throws IOException {
            byte[] jsonBytes = new byte[0];
            if (body != null) {
                String json = GSON.toJson(body);
                jsonBytes = json.getBytes(StandardCharsets.UTF_8);
            }

            headers.forEach((key, value) -> exchange.getResponseHeaders().set(key, value));

            long responseLength = jsonBytes.length > 0 ? jsonBytes.length : -1;
            exchange.sendResponseHeaders(statusCode, responseLength);

            if (responseLength > 0) {
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(jsonBytes);
                }
            } else {
                exchange.getResponseBody().close();
            }
        }
    }
}
