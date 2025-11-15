package infra.controlleradvide;

public record ErrorResponse(int status, String title, String detail) {
}
