package dao;

import database.Conexao;
import model.Emprestimo;
import model.Exemplar;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

public class EmprestimoDAO {

    private final EmprestimoLivroDAO emprestimoLivroDAO = new EmprestimoLivroDAO();

    public void inserir(Emprestimo emprestimo, List<Exemplar> exemplares) throws Exception {
        try (Connection conn = Conexao.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int idEmprestimo;
                String sql = "INSERT INTO emprestimo (id_usuario, id_aluno, id_funcionario, data_emprestimo, data_devolucao_prevista, status) " +
                        "VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setInt(1, emprestimo.getUsuario().getIdUsuario());
                    stmt.setInt(2, emprestimo.getAluno().getIdAluno());
                    if (emprestimo.getFuncionario() != null) {
                        stmt.setInt(3, emprestimo.getFuncionario().getIdFuncionario());
                    } else {
                        stmt.setNull(3, java.sql.Types.INTEGER);
                    }
                    stmt.setDate(4, Date.valueOf(emprestimo.getDataEmprestimo()));
                    stmt.setDate(5, Date.valueOf(emprestimo.getDataDevolucaoPrevista()));
                    stmt.setString(6, emprestimo.getStatus());
                    stmt.executeUpdate();
                    try (ResultSet rs = stmt.getGeneratedKeys()) {
                        rs.next();
                        idEmprestimo = rs.getInt(1);
                    }
                }

                for (Exemplar exemplar : exemplares) {
                    emprestimoLivroDAO.inserir(conn, idEmprestimo, exemplar.getIdExemplar());
                }

                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        }
    }

    public void excluir(int idEmprestimo) throws Exception {
        try (Connection conn = Conexao.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement stmt = conn.prepareStatement(
                        "DELETE m FROM multa m INNER JOIN emprestimo_livro el ON el.id_emprestimo_livro = m.id_emprestimo_livro " +
                                "WHERE el.id_emprestimo = ?")) {
                    stmt.setInt(1, idEmprestimo);
                    stmt.executeUpdate();
                }
                try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM emprestimo_livro WHERE id_emprestimo = ?")) {
                    stmt.setInt(1, idEmprestimo);
                    stmt.executeUpdate();
                }
                try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM emprestimo WHERE id_emprestimo = ?")) {
                    stmt.setInt(1, idEmprestimo);
                    stmt.executeUpdate();
                }
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        }
    }
}
