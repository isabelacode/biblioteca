package dao;

import database.Conexao;
import model.Aluno;
import model.Emprestimo;
import model.EmprestimoLivro;
import model.Exemplar;
import model.Funcionario;
import model.Livro;
import model.Multa;
import model.Usuario;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmprestimoLivroDAO {

    private static final BigDecimal MULTA_POR_DIA = new BigDecimal("1.00");

    private final ExemplarDAO exemplarDAO = new ExemplarDAO();
    private final MultaDAO multaDAO = new MultaDAO();

    public void inserir(Connection conn, int idEmprestimo, int idExemplar) throws Exception {
        String sql = "INSERT INTO emprestimo_livro (id_emprestimo, id_exemplar) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idEmprestimo);
            stmt.setInt(2, idExemplar);
            stmt.executeUpdate();
        }
        exemplarDAO.atualizarStatus(conn, idExemplar, "EMPRESTADO");
    }

    public void registrarDevolucao(int idEmprestimoLivro) throws Exception {
        try (Connection conn = Conexao.getConnection()) {
            int idExemplar;
            int idEmprestimo;
            LocalDate dataPrevista;
            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT el.id_exemplar, el.id_emprestimo, e.data_devolucao_prevista " +
                            "FROM emprestimo_livro el INNER JOIN emprestimo e ON e.id_emprestimo = el.id_emprestimo " +
                            "WHERE el.id_emprestimo_livro = ?")) {
                stmt.setInt(1, idEmprestimoLivro);
                try (ResultSet rs = stmt.executeQuery()) {
                    rs.next();
                    idExemplar = rs.getInt("id_exemplar");
                    idEmprestimo = rs.getInt("id_emprestimo");
                    dataPrevista = rs.getDate("data_devolucao_prevista").toLocalDate();
                }
            }

            LocalDate hoje = LocalDate.now();
            try (PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE emprestimo_livro SET data_devolucao = ? WHERE id_emprestimo_livro = ?")) {
                stmt.setDate(1, Date.valueOf(hoje));
                stmt.setInt(2, idEmprestimoLivro);
                stmt.executeUpdate();
            }

            exemplarDAO.atualizarStatus(conn, idExemplar, "DISPONIVEL");

            if (hoje.isAfter(dataPrevista)) {
                long diasAtraso = java.time.temporal.ChronoUnit.DAYS.between(dataPrevista, hoje);
                BigDecimal valor = MULTA_POR_DIA.multiply(BigDecimal.valueOf(diasAtraso));
                multaDAO.inserir(conn, idEmprestimoLivro, valor);
            }

            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT COUNT(*) FROM emprestimo_livro WHERE id_emprestimo = ? AND data_devolucao IS NULL")) {
                stmt.setInt(1, idEmprestimo);
                try (ResultSet rs = stmt.executeQuery()) {
                    rs.next();
                    if (rs.getInt(1) == 0) {
                        try (PreparedStatement upd = conn.prepareStatement(
                                "UPDATE emprestimo SET status = 'DEVOLVIDO' WHERE id_emprestimo = ?")) {
                            upd.setInt(1, idEmprestimo);
                            upd.executeUpdate();
                        }
                    }
                }
            }
        }
    }

    public List<EmprestimoLivro> listarTodos() throws Exception {
        List<EmprestimoLivro> lista = new ArrayList<>();
        String sql = "SELECT el.*, " +
                "e.data_emprestimo, e.data_devolucao_prevista, e.status AS emprestimo_status, " +
                "a.id_aluno, a.nome AS aluno_nome, " +
                "f.id_funcionario, f.nome AS funcionario_nome, " +
                "u.id_usuario, u.nome_usuario, " +
                "ex.status AS exemplar_status, l.id_livro, l.titulo, " +
                "m.id_multa, m.valor, m.data_pagamento " +
                "FROM emprestimo_livro el " +
                "INNER JOIN emprestimo e ON e.id_emprestimo = el.id_emprestimo " +
                "INNER JOIN aluno a ON a.id_aluno = e.id_aluno " +
                "LEFT JOIN funcionario f ON f.id_funcionario = e.id_funcionario " +
                "INNER JOIN usuario u ON u.id_usuario = e.id_usuario " +
                "INNER JOIN exemplar ex ON ex.id_exemplar = el.id_exemplar " +
                "INNER JOIN livro l ON l.id_livro = ex.id_livro " +
                "LEFT JOIN multa m ON m.id_emprestimo_livro = el.id_emprestimo_livro " +
                "ORDER BY e.data_emprestimo DESC, el.id_emprestimo_livro DESC";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(map(rs));
            }
        }
        return lista;
    }

    private EmprestimoLivro map(ResultSet rs) throws Exception {
        EmprestimoLivro item = new EmprestimoLivro();
        item.setIdEmprestimoLivro(rs.getInt("id_emprestimo_livro"));

        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setIdEmprestimo(rs.getInt("id_emprestimo"));
        emprestimo.setDataEmprestimo(rs.getDate("data_emprestimo").toLocalDate());
        emprestimo.setDataDevolucaoPrevista(rs.getDate("data_devolucao_prevista").toLocalDate());
        emprestimo.setStatus(rs.getString("emprestimo_status"));

        Aluno aluno = new Aluno();
        aluno.setIdAluno(rs.getInt("id_aluno"));
        aluno.setNome(rs.getString("aluno_nome"));
        emprestimo.setAluno(aluno);

        int idFuncionario = rs.getInt("id_funcionario");
        if (!rs.wasNull()) {
            Funcionario funcionario = new Funcionario();
            funcionario.setIdFuncionario(idFuncionario);
            funcionario.setNome(rs.getString("funcionario_nome"));
            emprestimo.setFuncionario(funcionario);
        }

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(rs.getInt("id_usuario"));
        usuario.setNomeUsuario(rs.getString("nome_usuario"));
        emprestimo.setUsuario(usuario);

        item.setEmprestimo(emprestimo);

        Livro livro = new Livro();
        livro.setIdLivro(rs.getInt("id_livro"));
        livro.setTitulo(rs.getString("titulo"));

        Exemplar exemplar = new Exemplar();
        exemplar.setIdExemplar(rs.getInt("id_exemplar"));
        exemplar.setStatus(rs.getString("exemplar_status"));
        exemplar.setLivro(livro);
        item.setExemplar(exemplar);

        Date dataDevolucao = rs.getDate("data_devolucao");
        if (dataDevolucao != null) {
            item.setDataDevolucao(dataDevolucao.toLocalDate());
        }

        int idMulta = rs.getInt("id_multa");
        if (!rs.wasNull()) {
            Multa multa = new Multa();
            multa.setIdMulta(idMulta);
            multa.setValor(rs.getBigDecimal("valor"));
            Date dataPagamento = rs.getDate("data_pagamento");
            if (dataPagamento != null) {
                multa.setDataPagamento(dataPagamento.toLocalDate());
            }
            item.setMulta(multa);
        }

        return item;
    }
}
