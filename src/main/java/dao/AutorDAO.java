package dao;

import database.Conexao;
import model.Autor;
import model.Endereco;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class AutorDAO {

    private final EnderecoDAO enderecoDAO = new EnderecoDAO();

    public void inserir(Autor autor) throws Exception {
        try (Connection conn = Conexao.getConnection()) {
            Integer idEndereco = salvarEndereco(conn, autor.getEndereco());

            String sql = "INSERT INTO autor (nome_autor, pseudonimo, nacionalidade, email, telefone, id_endereco) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, autor.getNome());
                stmt.setString(2, autor.getPseudonimo());
                stmt.setString(3, autor.getNacionalidade());
                stmt.setString(4, autor.getEmail());
                stmt.setString(5, autor.getTelefone());
                setNullableInt(stmt, 6, idEndereco);
                stmt.executeUpdate();
            }
        }
    }

    public void atualizar(Autor autor) throws Exception {
        try (Connection conn = Conexao.getConnection()) {
            Integer idEndereco = salvarEndereco(conn, autor.getEndereco());

            String sql = "UPDATE autor SET nome_autor = ?, pseudonimo = ?, nacionalidade = ?, email = ?, telefone = ?, id_endereco = ? WHERE id_autor = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, autor.getNome());
                stmt.setString(2, autor.getPseudonimo());
                stmt.setString(3, autor.getNacionalidade());
                stmt.setString(4, autor.getEmail());
                stmt.setString(5, autor.getTelefone());
                setNullableInt(stmt, 6, idEndereco);
                stmt.setInt(7, autor.getIdAutor());
                stmt.executeUpdate();
            }
        }
    }

    public void excluir(int id) throws Exception {
        String sql = "DELETE FROM autor WHERE id_autor = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public List<Autor> listarTodos() throws Exception {
        List<Autor> lista = new ArrayList<>();
        String sql = "SELECT a.*, en.* FROM autor a LEFT JOIN endereco en ON en.id_endereco = a.id_endereco ORDER BY a.nome_autor";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(map(rs));
            }
        }
        return lista;
    }

    private Integer salvarEndereco(Connection conn, Endereco endereco) throws Exception {
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

    private Autor map(ResultSet rs) throws Exception {
        Autor autor = new Autor();
        autor.setIdAutor(rs.getInt("id_autor"));
        autor.setNome(rs.getString("nome_autor"));
        autor.setPseudonimo(rs.getString("pseudonimo"));
        autor.setNacionalidade(rs.getString("nacionalidade"));
        autor.setEmail(rs.getString("email"));
        autor.setTelefone(rs.getString("telefone"));

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
            autor.setEndereco(endereco);
        }
        return autor;
    }
}
