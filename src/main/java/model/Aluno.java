package model;

public class Aluno extends Pessoa {

    private String cpf;

    public Aluno() {
    }

    public Aluno(String nome, String cpf, String telefone, String email, Endereco endereco) {
        super(nome, telefone, email, endereco);
        this.cpf = cpf;
    }

    public int getIdAluno() {
        return id;
    }

    public void setIdAluno(int idAluno) {
        this.id = idAluno;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    @Override
    public String toString() {
        return nome;
    }
}
