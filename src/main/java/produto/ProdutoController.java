package produto;

import com.sun.net.httpserver.HttpExchange;
import infra.controlleradvide.GlobalExceptionHandler;
import shared.paginacao.Pagina;
import shared.paginacao.Paginavel;
import shared.utils.request.RequestUtils;
import shared.utils.response.ResponseUtils;
import shared.utils.router.RouterUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

public class ProdutoController {

    public void createProduto(HttpExchange exchange) {
        GlobalExceptionHandler.handle(exchange, () -> {
                Produto produto = RequestUtils.parseBody(exchange, Produto.class);
                ResponseUtils.create(exchange).withBody(produto).withStatusCode(201).send();
                ProdutoDAO produtoDAO = new ProdutoDAO();
                produtoDAO.save(produto);
        });
    }

    public void findAllProducts(HttpExchange exchange) throws SQLException {
        GlobalExceptionHandler.handle(exchange, () -> {
            Map<String, String> queryParameters = RouterUtils.getQueryParameters(exchange.getRequestURI().getQuery());
            Paginavel paginavel = Paginavel.fromParameters(queryParameters);
            Pagina<Produto> produtoPagina = new ProdutoDAO().findAll(paginavel);
            ResponseUtils.create(exchange).withBody(produtoPagina).withStatusCode(200).send();
        });
    }
    
    public void findProdutoById(HttpExchange exchange) {
        GlobalExceptionHandler.handle(exchange, () -> {
            Long id = RouterUtils.getPathId(exchange);
            ProdutoDAO produtoDAO = new ProdutoDAO();
            produtoDAO.findById(id).ifPresentOrElse(produto -> {
                try{
                    ResponseUtils.create(exchange).withBody(produto).withStatusCode(200).send();
                } catch (IOException e){
                    throw new RuntimeException(e);
                }
            }, () -> {
                try {
                    ResponseUtils.create(exchange).withStatusCode(404).send();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        });
    }

    public void updateProduto(HttpExchange exchange) {
        GlobalExceptionHandler.handle(exchange, () -> {
            Long id = RouterUtils.getPathId(exchange);
            Produto produtoDoRequest = RequestUtils.parseBody(exchange, Produto.class);
            produtoDoRequest.setId(id);

            ProdutoDAO produtoDAO = new ProdutoDAO();
            int linhasAfetadas = produtoDAO.update(produtoDoRequest);
            if (linhasAfetadas > 0) {
                ResponseUtils.create(exchange).withBody(produtoDoRequest).withStatusCode(200).send();
            } else {
                ResponseUtils.create(exchange).withStatusCode(204).send();
            }
        });
    }

    public void deleteProduto(HttpExchange exchange) {
        GlobalExceptionHandler.handle(exchange, () -> {
            Long id = RouterUtils.getPathId(exchange);
            ProdutoDAO produtoDAO = new ProdutoDAO();
            int linhasAfetadas = produtoDAO.delete(id);

            if (linhasAfetadas > 0) {
                ResponseUtils.create(exchange).withStatusCode(204).send();
            } else {
                ResponseUtils.create(exchange).withStatusCode(404).send();
            }
        });
    }

}
