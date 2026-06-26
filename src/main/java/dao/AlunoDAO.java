package dao;

import database.Conexao;
import model.Aluno;
import model.Endereco;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class AlunoDAO {

    private final EnderecoDAO enderecoDAO = new EnderecoDAO();

    public void inserir(Aluno aluno) throws Exception {
        try (Connection conn = Conexao.getConnection()) {
            Integer idEndereco = salvarEndereco(conn, aluno.getEndereco());

            String sql = "INSERT INTO aluno (nome, cpf, telefone, email, id_endereco) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, aluno.getNome());
                stmt.setString(2, aluno.getCpf());
                stmt.setString(3, aluno.getTelefone());
                stmt.setString(4, aluno.getEmail());
                setNullableInt(stmt, 5, idEndereco);
                stmt.executeUpdate();
            }
        }
    }

    public void atualizar(Aluno aluno) throws Exception {
        try (Connection conn = Conexao.getConnection()) {
            Integer idEndereco = salvarEndereco(conn, aluno.getEndereco());

            String sql = "UPDATE aluno SET nome = ?, cpf = ?, telefone = ?, email = ?, id_endereco = ? WHERE id_aluno = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, aluno.getNome());
                stmt.setString(2, aluno.getCpf());
                stmt.setString(3, aluno.getTelefone());
                stmt.setString(4, aluno.getEmail());
                setNullableInt(stmt, 5, idEndereco);
                stmt.setInt(6, aluno.getIdAluno());
                stmt.executeUpdate();
            }
        }
    }

    public void excluir(int id) throws Exception {
        if (possuiEmprestimoAberto(id)) {
            throw new IllegalStateException("Este aluno possui empréstimo(s) em aberto e não pode ser excluído.");
        }
        String sql = "DELETE FROM aluno WHERE id_aluno = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private boolean possuiEmprestimoAberto(int idAluno) throws Exception {
        String sql = "SELECT COUNT(*) FROM emprestimo WHERE id_aluno = ? AND status = 'ATIVO'";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idAluno);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    public List<Aluno> listarTodos() throws Exception {
        List<Aluno> lista = new ArrayList<>();
        String sql = "SELECT a.*, en.* FROM aluno a LEFT JOIN endereco en ON en.id_endereco = a.id_endereco ORDER BY a.nome";
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

    private Aluno map(ResultSet rs) throws Exception {
        Aluno aluno = new Aluno();
        aluno.setIdAluno(rs.getInt("id_aluno"));
        aluno.setNome(rs.getString("nome"));
        aluno.setCpf(rs.getString("cpf"));
        aluno.setTelefone(rs.getString("telefone"));
        aluno.setEmail(rs.getString("email"));

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
            aluno.setEndereco(endereco);
        }
        return aluno;
    }
}
