package router;

import com.sun.net.httpserver.HttpExchange;

import java.util.function.Consumer;
import java.util.regex.Pattern;

public record Route(String method, Pattern pathPattern, Consumer<HttpExchange> handler) {
    public boolean matches(String requestMethod, String requestPath) {
        return method.equals(requestMethod) && pathPattern.matcher(requestPath).matches();
    }
}
