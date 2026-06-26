package dao;

import database.Conexao;
import model.Editora;
import model.Endereco;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class EditoraDAO {

    private final EnderecoDAO enderecoDAO = new EnderecoDAO();

    public void inserir(Editora editora) throws Exception {
        try (Connection conn = Conexao.getConnection()) {
            Integer idEndereco = salvarEndereco(conn, editora);

            String sql = "INSERT INTO editora (nome_editora, cnpj, email, telefone, nacionalidade, endereco_web, id_endereco) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, editora.getNome());
                stmt.setString(2, editora.getCnpj());
                stmt.setString(3, editora.getEmail());
                stmt.setString(4, editora.getTelefone());
                stmt.setString(5, editora.getNacionalidade());
                stmt.setString(6, editora.getEndereco_web());
                setNullableInt(stmt, 7, idEndereco);
                stmt.executeUpdate();
            }
        }
    }

    public void atualizar(Editora editora) throws Exception {
        try (Connection conn = Conexao.getConnection()) {
            Integer idEndereco = salvarEndereco(conn, editora);

            String sql = "UPDATE editora SET nome_editora = ?, cnpj = ?, email = ?, telefone = ?, nacionalidade = ?, endereco_web = ?, id_endereco = ? WHERE id_editora = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, editora.getNome());
                stmt.setString(2, editora.getCnpj());
                stmt.setString(3, editora.getEmail());
                stmt.setString(4, editora.getTelefone());
                stmt.setString(5, editora.getNacionalidade());
                stmt.setString(6, editora.getEndereco_web());
                setNullableInt(stmt, 7, idEndereco);
                stmt.setInt(8, editora.getIdEditora());
                stmt.executeUpdate();
            }
        }
    }

    private Integer salvarEndereco(Connection conn, Editora editora) throws Exception {
        Endereco endereco = editora.getEndereco();
        if (endereco == null) {
            return null;
        }
        if (endereco.getIdEndereco() > 0) {
            enderecoDAO.atualizar(conn, endereco);
            return endereco.getIdEndereco();
        }
        int id = enderecoDAO.inserir(conn, endereco);
        endereco.setIdEndereco(id);
        return id;
    }

    private void setNullableInt(PreparedStatement stmt, int index, Integer value) throws Exception {
        if (value != null) {
            stmt.setInt(index, value);
        } else {
            stmt.setNull(index, java.sql.Types.INTEGER);
        }
    }

    public void excluir(int id) throws Exception {
        String sql = "DELETE FROM editora WHERE id_editora = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public List<Editora> listarTodos() throws Exception {
        List<Editora> lista = new ArrayList<>();
        String sql = "SELECT e.*, en.* FROM editora e LEFT JOIN endereco en ON en.id_endereco = e.id_endereco ORDER BY e.nome_editora";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(map(rs));
            }
        }
        return lista;
    }

    private Editora map(ResultSet rs) throws Exception {
        Editora editora = new Editora();
        editora.setIdEditora(rs.getInt("id_editora"));
        editora.setNome(rs.getString("nome_editora"));
        editora.setCnpj(rs.getString("cnpj"));
        editora.setEmail(rs.getString("email"));
        editora.setTelefone(rs.getString("telefone"));
        editora.setNacionalidade(rs.getString("nacionalidade"));
        editora.setEndereco_web(rs.getString("endereco_web"));

        int idEndereco = rs.getInt("id_endereco");
        if (idEndereco > 0) {
            Endereco endereco = new Endereco();
            endereco.setIdEndereco(idEndereco);
            endereco.setCep(rs.getString("cep"));
            endereco.setNumero(rs.getString("numero"));
            endereco.setEstado(rs.getString("estado"));
            endereco.setCidade(rs.getString("cidade"));
            endereco.setBairro(rs.getString("bairro"));
            endereco.setRua(rs.getString("rua"));
            endereco.setComplemento(rs.getString("complemento"));
            editora.setEndereco(endereco);
        }
        return editora;
    }
}
