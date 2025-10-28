package produto;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

public class ProdutoController {

    private final Gson gson = new Gson();

    public void createProduto(HttpExchange exchange) throws IOException {

        try(InputStream inputStream = exchange.getRequestBody();
            ReadableByteChannel channel = Channels.newChannel(inputStream);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            ByteBuffer buffer = ByteBuffer.allocate(1024);

            while (channel.read(buffer) != -1) {
                buffer.flip();
                outputStream.write(buffer.array(), 0, buffer.limit());
                buffer.clear();
            }

            String json = outputStream.toString(StandardCharsets.UTF_8);

            Produto produto = gson.fromJson(json, Produto.class);

            ProdutoDAO produtoDAO = new ProdutoDAO();
            produtoDAO.save(produto);

            String response = gson.toJson(produto);
            byte[] jsonBytes = response.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(201, jsonBytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(jsonBytes);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
