package dao;

import database.Conexao;
import model.Endereco;
import model.Funcionario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class FuncionarioDAO {

    private final EnderecoDAO enderecoDAO = new EnderecoDAO();

    public void inserir(Funcionario funcionario) throws Exception {
        try (Connection conn = Conexao.getConnection()) {
            Integer idEndereco = salvarEndereco(conn, funcionario.getEndereco());

            String sql = "INSERT INTO funcionario (nome, cpf, telefone, email, id_endereco, cargo) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, funcionario.getNome());
                stmt.setString(2, funcionario.getCpf());
                stmt.setString(3, funcionario.getTelefone());
                stmt.setString(4, funcionario.getEmail());
                setNullableInt(stmt, 5, idEndereco);
                stmt.setString(6, funcionario.getCargo());
                stmt.executeUpdate();
            }
        }
    }

    public void atualizar(Funcionario funcionario) throws Exception {
        try (Connection conn = Conexao.getConnection()) {
            Integer idEndereco = salvarEndereco(conn, funcionario.getEndereco());

            String sql = "UPDATE funcionario SET nome = ?, cpf = ?, telefone = ?, email = ?, id_endereco = ?, cargo = ? WHERE id_funcionario = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, funcionario.getNome());
                stmt.setString(2, funcionario.getCpf());
                stmt.setString(3, funcionario.getTelefone());
                stmt.setString(4, funcionario.getEmail());
                setNullableInt(stmt, 5, idEndereco);
                stmt.setString(6, funcionario.getCargo());
                stmt.setInt(7, funcionario.getIdFuncionario());
                stmt.executeUpdate();
            }
        }
    }

    public void excluir(int id) throws Exception {
        if (possuiEmprestimoAberto(id)) {
            throw new IllegalStateException("Este funcionário possui empréstimo(s) em aberto vinculado(s) e não pode ser excluído.");
        }
        String sql = "DELETE FROM funcionario WHERE id_funcionario = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private boolean possuiEmprestimoAberto(int idFuncionario) throws Exception {
        String sql = "SELECT COUNT(*) FROM emprestimo WHERE id_funcionario = ? AND status = 'ATIVO'";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idFuncionario);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    public List<Funcionario> listarTodos() throws Exception {
        List<Funcionario> lista = new ArrayList<>();
        String sql = "SELECT f.*, en.* FROM funcionario f LEFT JOIN endereco en ON en.id_endereco = f.id_endereco ORDER BY f.nome";
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

    private Funcionario map(ResultSet rs) throws Exception {
        Funcionario funcionario = new Funcionario();
        funcionario.setIdFuncionario(rs.getInt("id_funcionario"));
        funcionario.setNome(rs.getString("nome"));
        funcionario.setCpf(rs.getString("cpf"));
        funcionario.setTelefone(rs.getString("telefone"));
        funcionario.setEmail(rs.getString("email"));
        funcionario.setCargo(rs.getString("cargo"));

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
            funcionario.setEndereco(endereco);
        }
        return funcionario;
    }
}
