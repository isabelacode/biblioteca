package dao;

import database.Conexao;
import model.Autor;
import model.Categoria;
import model.Editora;
import model.Livro;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class LivroDAO {

    public void inserir(Livro livro) throws Exception {
        try (Connection conn = Conexao.getConnection()) {
            String sql = "INSERT INTO livro (titulo, ano_publicacao, isbn, id_categoria, id_editora) VALUES (?, ?, ?, ?, ?)";
            int idLivro;
            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                preencher(stmt, livro);
                stmt.executeUpdate();
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    rs.next();
                    idLivro = rs.getInt(1);
                }
            }
            livro.setIdLivro(idLivro);
            salvarAutores(conn, livro);
        }
    }

    public void atualizar(Livro livro) throws Exception {
        try (Connection conn = Conexao.getConnection()) {
            String sql = "UPDATE livro SET titulo = ?, ano_publicacao = ?, isbn = ?, id_categoria = ?, id_editora = ? WHERE id_livro = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                preencher(stmt, livro);
                stmt.setInt(6, livro.getIdLivro());
                stmt.executeUpdate();
            }
            try (PreparedStatement del = conn.prepareStatement("DELETE FROM livro_autor WHERE id_livro = ?")) {
                del.setInt(1, livro.getIdLivro());
                del.executeUpdate();
            }
            salvarAutores(conn, livro);
        }
    }

    public void excluir(int id) throws Exception {
        try (Connection conn = Conexao.getConnection()) {
            try (PreparedStatement del = conn.prepareStatement("DELETE FROM livro_autor WHERE id_livro = ?")) {
                del.setInt(1, id);
                del.executeUpdate();
            }
            try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM livro WHERE id_livro = ?")) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
            }
        }
    }

    public List<Livro> listarTodos() throws Exception {
        List<Livro> lista = new ArrayList<>();
        String sql = "SELECT l.*, c.nome_categoria AS categoria_nome, c.descricao AS categoria_descricao, " +
                "ed.nome_editora AS editora_nome FROM livro l " +
                "LEFT JOIN categoria c ON c.id_categoria = l.id_categoria " +
                "LEFT JOIN editora ed ON ed.id_editora = l.id_editora " +
                "ORDER BY l.titulo";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(map(rs));
            }
        }
        for (Livro livro : lista) {
            livro.setAutores(carregarAutores(livro.getIdLivro()));
        }
        return lista;
    }

    private void preencher(PreparedStatement stmt, Livro livro) throws Exception {
        stmt.setString(1, livro.getTitulo());
        if (livro.getAno_publicacao() != null) {
            stmt.setInt(2, livro.getAno_publicacao());
        } else {
            stmt.setNull(2, java.sql.Types.INTEGER);
        }
        stmt.setString(3, livro.getIsbn());
        if (livro.getCategoria() != null) {
            stmt.setInt(4, livro.getCategoria().getIdCategoria());
        } else {
            stmt.setNull(4, java.sql.Types.INTEGER);
        }
        if (livro.getEditora() != null) {
            stmt.setInt(5, livro.getEditora().getIdEditora());
        } else {
            stmt.setNull(5, java.sql.Types.INTEGER);
        }
    }

    private void salvarAutores(Connection conn, Livro livro) throws Exception {
        if (livro.getAutores() == null || livro.getAutores().isEmpty()) {
            return;
        }
        String sql = "INSERT INTO livro_autor (id_livro, id_autor) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (Autor autor : livro.getAutores()) {
                stmt.setInt(1, livro.getIdLivro());
                stmt.setInt(2, autor.getIdAutor());
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    private List<Autor> carregarAutores(int idLivro) throws Exception {
        List<Autor> autores = new ArrayList<>();
        String sql = "SELECT a.* FROM autor a INNER JOIN livro_autor la ON la.id_autor = a.id_autor WHERE la.id_livro = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idLivro);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Autor autor = new Autor();
                    autor.setIdAutor(rs.getInt("id_autor"));
                    autor.setNome(rs.getString("nome_autor"));
                    autor.setPseudonimo(rs.getString("pseudonimo"));
                    autor.setNacionalidade(rs.getString("nacionalidade"));
                    autores.add(autor);
                }
            }
        }
        return autores;
    }

    private Livro map(ResultSet rs) throws Exception {
        Livro livro = new Livro();
        livro.setIdLivro(rs.getInt("id_livro"));
        livro.setTitulo(rs.getString("titulo"));
        int ano = rs.getInt("ano_publicacao");
        livro.setAno_publicacao(rs.wasNull() ? null : ano);
        livro.setIsbn(rs.getString("isbn"));

        int idCategoria = rs.getInt("id_categoria");
        if (idCategoria > 0) {
            Categoria categoria = new Categoria();
            categoria.setIdCategoria(idCategoria);
            categoria.setNome(rs.getString("categoria_nome"));
            categoria.setDescricao(rs.getString("categoria_descricao"));
            livro.setCategoria(categoria);
        }

        int idEditora = rs.getInt("id_editora");
        if (idEditora > 0) {
            Editora editora = new Editora();
            editora.setIdEditora(idEditora);
            editora.setNome(rs.getString("editora_nome"));
            livro.setEditora(editora);
        }

        return livro;
    }
}
