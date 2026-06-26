package model;

public class Funcionario extends Pessoa {

    private int idFuncionario;
    protected String cargo;

    public Funcionario() {
    }

    public Funcionario(String nome, String cpf, String telefone, String email, Endereco endereco, String cargo) {
        super(nome, cpf, telefone, email, endereco);
        this.cargo = cargo;
    }

    public int getIdFuncionario() {
        return idFuncionario;
    }

    public void setIdFuncionario(int idFuncionario) {
        this.idFuncionario = idFuncionario;
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
