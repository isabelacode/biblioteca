package dao;

import database.Conexao;
import model.Exemplar;
import model.Livro;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ExemplarDAO {

    public void inserir(Exemplar exemplar) throws Exception {
        String sql = "INSERT INTO exemplar (id_livro, status) VALUES (?, ?)";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, exemplar.getLivro().getIdLivro());
            stmt.setString(2, exemplar.getStatus());
            stmt.executeUpdate();
        }
    }

    public void atualizar(Exemplar exemplar) throws Exception {
        String sql = "UPDATE exemplar SET id_livro = ?, status = ? WHERE id_exemplar = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, exemplar.getLivro().getIdLivro());
            stmt.setString(2, exemplar.getStatus());
            stmt.setInt(3, exemplar.getIdExemplar());
            stmt.executeUpdate();
        }
    }

    public void atualizarStatus(Connection conn, int idExemplar, String status) throws Exception {
        String sql = "UPDATE exemplar SET status = ? WHERE id_exemplar = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, idExemplar);
            stmt.executeUpdate();
        }
    }

    public void excluir(int id) throws Exception {
        String sql = "DELETE FROM exemplar WHERE id_exemplar = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public List<Exemplar> listarTodos() throws Exception {
        List<Exemplar> lista = new ArrayList<>();
        String sql = "SELECT e.*, l.titulo FROM exemplar e INNER JOIN livro l ON l.id_livro = e.id_livro ORDER BY l.titulo, e.id_exemplar";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(map(rs));
            }
        }
        return lista;
    }

    public List<Exemplar> listarDisponiveisPorLivro(int idLivro) throws Exception {
        List<Exemplar> lista = new ArrayList<>();
        String sql = "SELECT e.*, l.titulo FROM exemplar e INNER JOIN livro l ON l.id_livro = e.id_livro " +
                "WHERE e.id_livro = ? AND e.status = 'DISPONIVEL'";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idLivro);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(map(rs));
                }
            }
        }
        return lista;
    }

    private Exemplar map(ResultSet rs) throws Exception {
        Exemplar exemplar = new Exemplar();
        exemplar.setIdExemplar(rs.getInt("id_exemplar"));
        exemplar.setStatus(rs.getString("status"));

        Livro livro = new Livro();
        livro.setIdLivro(rs.getInt("id_livro"));
        livro.setTitulo(rs.getString("titulo"));
        exemplar.setLivro(livro);
        java.sql.Date dataEntrada = rs.getDate("data_entrada");
        if (dataEntrada != null) {
            exemplar.setDataEntrada(dataEntrada.toLocalDate());
        }

        return exemplar;
    }
}
