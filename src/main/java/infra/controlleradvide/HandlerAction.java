package infra.controlleradvide;

import com.sun.net.httpserver.HttpExchange;

@FunctionalInterface
public interface HandlerAction {
    void execute() throws Exception;
}
