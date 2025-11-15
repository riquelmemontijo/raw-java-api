package infra.controlleradvide;

@FunctionalInterface
public interface HandlerAction {
    void execute() throws Exception;
}
