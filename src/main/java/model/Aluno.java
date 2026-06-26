package model;

public class Aluno extends Pessoa {

    private int idAluno;

    public Aluno() {
    }

    public Aluno(String nome, String cpf, String telefone, String email, Endereco endereco) {
        super(nome, cpf, telefone, email, endereco);
    }

    public int getIdAluno() {
        return idAluno;
    }

    public void setIdAluno(int idAluno) {
        this.idAluno = idAluno;
    }

    @Override
    public String toString() {
        return nome;
    }
}
