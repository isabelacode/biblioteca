package dao;

import database.Conexao;
import model.Usuario;

import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;

public class UsuarioDAO {

    public void inserir(Usuario usuario, String senhaPlana) throws Exception {
        String sql = "INSERT INTO usuario (nome_usuario, login, senha_hash) VALUES (?, ?, ?)";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, usuario.getNomeUsuario());
            stmt.setString(2, usuario.getLogin());
            stmt.setString(3, hash(senhaPlana));
            stmt.executeUpdate();
        }
    }

    public void atualizar(Usuario usuario, String novaSenhaOuNull) throws Exception {
        String sql = novaSenhaOuNull == null || novaSenhaOuNull.isBlank()
                ? "UPDATE usuario SET nome_usuario = ?, login = ? WHERE id_usuario = ?"
                : "UPDATE usuario SET nome_usuario = ?, login = ?, senha_hash = ? WHERE id_usuario = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, usuario.getNomeUsuario());
            stmt.setString(2, usuario.getLogin());
            if (novaSenhaOuNull == null || novaSenhaOuNull.isBlank()) {
                stmt.setInt(3, usuario.getIdUsuario());
            } else {
                stmt.setString(3, hash(novaSenhaOuNull));
                stmt.setInt(4, usuario.getIdUsuario());
            }
            stmt.executeUpdate();
        }
    }

    public void excluir(int id) throws Exception {
        String sql = "DELETE FROM usuario WHERE id_usuario = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public Usuario autenticar(String login, String senhaPlana) throws Exception {
        String sql = "SELECT * FROM usuario WHERE login = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, login);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next() && hash(senhaPlana).equals(rs.getString("senha_hash"))) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    public List<Usuario> listarTodos() throws Exception {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuario ORDER BY nome_usuario";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(map(rs));
            }
        }
        return lista;
    }

    private String hash(String senhaPlana) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = digest.digest(senhaPlana.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        return HexFormat.of().formatHex(bytes);
    }

    private Usuario map(ResultSet rs) throws Exception {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(rs.getInt("id_usuario"));
        usuario.setNomeUsuario(rs.getString("nome_usuario"));
        usuario.setLogin(rs.getString("login"));
        usuario.setSenhaHash(rs.getString("senha_hash"));
        return usuario;
    }
}
