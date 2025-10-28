import com.sun.net.httpserver.HttpServer;
import database.DataBaseInitializerConfig;
import router.MasterRouter;

void main() throws IOException {

    var databaseInitializer = new DataBaseInitializerConfig();
    databaseInitializer.initializeDatabase();

    var server = HttpServer.create(new InetSocketAddress(8080), 0);
    server.createContext("/", new MasterRouter());
    server.setExecutor(null);
    server.start();
    System.out.println("Servidor rodando na porta 8080");
}
