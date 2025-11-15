import com.sun.net.httpserver.HttpServer;
import database.DataBaseInitializerConfig;
import router.MasterRouter;
import router.anotacoes.ApiApplication;

import java.io.IOException;
import java.net.InetSocketAddress;

@ApiApplication
public class Main {

    static void main() throws IOException, InterruptedException {
        var databaseInitializer = new DataBaseInitializerConfig();
        databaseInitializer.initializeDatabase();

        Thread.sleep(3000);

        var server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/", new MasterRouter());
        server.setExecutor(null);
        server.start();
        System.out.println("Servidor rodando na porta 8080");
    }

}
