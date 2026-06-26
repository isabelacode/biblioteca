package dao;

import database.Conexao;
import model.Multa;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class MultaDAO {

    public void inserir(Connection conn, int idEmprestimoLivro, BigDecimal valor) throws Exception {
        String sql = "INSERT INTO multa (id_emprestimo_livro, valor) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idEmprestimoLivro);
            stmt.setBigDecimal(2, valor);
            stmt.executeUpdate();
        }
    }

    public Multa buscarPorEmprestimoLivro(int idEmprestimoLivro) throws Exception {
        String sql = "SELECT * FROM multa WHERE id_emprestimo_livro = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idEmprestimoLivro);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    public void marcarComoPago(int idMulta) throws Exception {
        String sql = "UPDATE multa SET data_pagamento = ? WHERE id_multa = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(java.time.LocalDate.now()));
            stmt.setInt(2, idMulta);
            stmt.executeUpdate();
        }
    }

    private Multa map(ResultSet rs) throws Exception {
        Multa multa = new Multa();
        multa.setIdMulta(rs.getInt("id_multa"));
        multa.setValor(rs.getBigDecimal("valor"));
        Date dataPagamento = rs.getDate("data_pagamento");
        if (dataPagamento != null) {
            multa.setDataPagamento(dataPagamento.toLocalDate());
        }
        return multa;
    }
}
