package model;

public class Funcionario extends Pessoa {

    private String cpf;
    protected String cargo;

    public Funcionario() {
    }

    public Funcionario(String nome, String cpf, String telefone, String email, Endereco endereco, String cargo) {
        super(nome, telefone, email, endereco);
        this.cpf = cpf;
        this.cargo = cargo;
    }

    public int getIdFuncionario() {
        return id;
    }

    public void setIdFuncionario(int idFuncionario) {
        this.id = idFuncionario;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    @Override
    public String toString() {
        return nome;
    }
}
