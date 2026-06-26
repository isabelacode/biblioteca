package model;

public class Autor {

    private int idAutor;
    protected String nome;
    protected String pseudonimo;
    protected String nacionalidade;
    protected String email;
    protected String telefone;
    protected Endereco endereco;

    public Autor() {
    }

    public Autor(String nome, String pseudonimo, String nacionalidade, String email, String telefone, Endereco endereco) {
        this.nome = nome;
        this.pseudonimo = pseudonimo;
        this.nacionalidade = nacionalidade;
        this.email = email;
        this.telefone = telefone;
        this.endereco = endereco;
    }

    public int getIdAutor() {
        return idAutor;
    }

    public void setIdAutor(int idAutor) {
        this.idAutor = idAutor;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

    @Override
    public String toString() {
        return nome;
    }
}
