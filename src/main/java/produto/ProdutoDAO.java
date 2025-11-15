package produto;

import database.DatabaseConnectionFactory;
import shared.paginacao.Pagina;
import shared.paginacao.Paginavel;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

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

    public Pagina<Produto> findAll(Paginavel paginavel){
        String sql = "SELECT id, nome, preco FROM produto " +
                     "ORDER BY id " +
                     "LIMIT ? OFFSET ?";


        Set<Produto> produtos = new LinkedHashSet<>();

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, paginavel.getTamanhoDaPagina());
            statement.setInt(2, paginavel.getNumeroPagina() * paginavel.getTamanhoDaPagina() - paginavel.getTamanhoDaPagina());

            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                Long id = resultSet.getLong("id");
                String nome = resultSet.getString("nome");
                BigDecimal preco = resultSet.getBigDecimal("preco");
                produtos.add(new Produto(id, nome, preco));
            }

            return new Pagina<>(produtos, paginavel, countAllProdutos());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Produto> findById(Long id){
        String sql = "SELECT id, nome, preco FROM produto WHERE id = ?";
        Produto produto = null;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String nome = resultSet.getString("nome");
                BigDecimal preco = resultSet.getBigDecimal("preco");
                produto = new Produto(id, nome, preco);
            }
            return Optional.ofNullable(produto);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int update(Produto produto) {
        String sql = "UPDATE produto SET nome = ?, preco = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, produto.getNome());
            statement.setBigDecimal(2, produto.getPreco());
            statement.setLong(3, produto.getId());
            return statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int delete(Long id) {
        String sql = "DELETE FROM produto WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            return statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Integer countAllProdutos(){
        String sql = "SELECT COUNT(*) FROM produto";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
