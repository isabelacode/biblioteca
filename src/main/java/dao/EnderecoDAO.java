package dao;

import database.Conexao;
import model.Endereco;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class EnderecoDAO {

    public int inserir(Connection conn, Endereco endereco) throws Exception {
        String sql = "INSERT INTO endereco (cep, numero, estado, cidade, bairro, rua, complemento) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, endereco.getCep());
            stmt.setString(2, endereco.getNumero());
            stmt.setString(3, endereco.getEstado());
            stmt.setString(4, endereco.getCidade());
            stmt.setString(5, endereco.getBairro());
            stmt.setString(6, endereco.getRua());
            stmt.setString(7, endereco.getComplemento());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new Exception("Não foi possível obter o id do endereço inserido.");
    }

    public void atualizar(Connection conn, Endereco endereco) throws Exception {
        String sql = "UPDATE endereco SET cep = ?, numero = ?, estado = ?, cidade = ?, bairro = ?, rua = ?, complemento = ? WHERE id_endereco = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, endereco.getCep());
            stmt.setString(2, endereco.getNumero());
            stmt.setString(3, endereco.getEstado());
            stmt.setString(4, endereco.getCidade());
            stmt.setString(5, endereco.getBairro());
            stmt.setString(6, endereco.getRua());
            stmt.setString(7, endereco.getComplemento());
            stmt.setInt(8, endereco.getIdEndereco());
            stmt.executeUpdate();
        }
    }

    public Endereco buscarPorId(Connection conn, int id) throws Exception {
        String sql = "SELECT * FROM endereco WHERE id_endereco = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    public List<Endereco> listarTodos() throws Exception {
        List<Endereco> lista = new ArrayList<>();
        String sql = "SELECT * FROM endereco ORDER BY id_endereco";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(map(rs));
            }
        }
        return lista;
    }

    public void excluir(int id) throws Exception {
        String sql = "DELETE FROM endereco WHERE id_endereco = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private Endereco map(ResultSet rs) throws Exception {
        Endereco endereco = new Endereco();
        endereco.setIdEndereco(rs.getInt("id_endereco"));
        endereco.setCep(rs.getString("cep"));
        endereco.setNumero(rs.getString("numero"));
        endereco.setEstado(rs.getString("estado"));
        endereco.setCidade(rs.getString("cidade"));
        endereco.setBairro(rs.getString("bairro"));
        endereco.setRua(rs.getString("rua"));
        endereco.setComplemento(rs.getString("complemento"));
        return endereco;
    }
}
