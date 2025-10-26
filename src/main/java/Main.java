import com.sun.net.httpserver.HttpServer;

void main() throws IOException {
    var server = HttpServer.create(new InetSocketAddress(8080), 0);
    server.createContext("/produtos");
    server.setExecutor(null);
    server.start();
    System.out.println("Servidor rodando na porta 8080");
}
