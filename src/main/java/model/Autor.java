package model;

public class Autor extends Pessoa {

    private String pseudonimo;
    private String nacionalidade;

    public Autor() {
    }

    public Autor(String nome, String pseudonimo, String nacionalidade, String email, String telefone, Endereco endereco) {
        super(nome, telefone, email, endereco);
        this.pseudonimo = pseudonimo;
        this.nacionalidade = nacionalidade;
    }

    public int getIdAutor() {
        return id;
    }

    public void setIdAutor(int idAutor) {
        this.id = idAutor;
    }

    public String getPseudonimo() {
        return pseudonimo;
    }

    public void setPseudonimo(String pseudonimo) {
        this.pseudonimo = pseudonimo;
    }

    public String getNacionalidade() {
        return nacionalidade;
    }

    public void setNacionalidade(String nacionalidade) {
        this.nacionalidade = nacionalidade;
    }

    @Override
    public String toString() {
        return nome;
    }
}
