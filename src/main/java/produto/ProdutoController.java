package produto;

import com.sun.net.httpserver.HttpExchange;
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
        try {
            Produto produto = RequestUtils.parseBody(exchange, Produto.class);
            ResponseUtils.create(exchange).withBody(produto).withStatusCode(201).send();
            ProdutoDAO produtoDAO = new ProdutoDAO();
            produtoDAO.save(produto);
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void findAllProducts(HttpExchange exchange) throws SQLException {
        try {
            Map<String, String> queryParameters = RouterUtils.getQueryParameters(exchange.getRequestURI().getQuery());
            Paginavel paginavel = Paginavel.fromParameters(queryParameters);
            Pagina<Produto> produtoPagina = new ProdutoDAO().findAll(paginavel);
            ResponseUtils.create(exchange).withBody(produtoPagina).send();
        } catch (IOException ex){
            throw new RuntimeException(ex);
        }
    }

}
