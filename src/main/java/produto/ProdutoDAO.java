package produto;

import database.DatabaseConnectionFactory;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class ProdutoDAO {

    private final Connection connection;

    public ProdutoDAO() throws SQLException {
        this.connection = DatabaseConnectionFactory.getConnection();
    }

    public void save(Produto produto){
        String sql = "INSERT INTO produto (nome, preco) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, produto.getNome());
            statement.setBigDecimal(2, produto.getPreco());
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Set<Produto> findAll(){
        String sql = "SELECT nome, preco FROM produto";
        Set<Produto> produtos = new HashSet<>();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.execute();
            ResultSet resultSet = statement.getResultSet();
            while(statement.getResultSet().next()){
                String nome = resultSet.getString("nome");
                BigDecimal preco = BigDecimal.valueOf(resultSet.getDouble("preco"));
                produtos.add(new Produto(null, nome, preco));
            }
            return produtos;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
