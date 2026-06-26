package model;

public class Editora {

    private int idEditora;
    protected String nome;
    protected String cnpj;
    protected String email;
    protected String telefone;
    protected String nacionalidade;
    protected String endereco_web;
    protected Endereco endereco;

    public Editora() {
    }

    public Editora(String nome, String cnpj, String email, String telefone, String nacionalidade, String endereco_web, Endereco endereco) {
        this.nome = nome;
        this.cnpj = cnpj;
        this.email = email;
        this.telefone = telefone;
        this.nacionalidade = nacionalidade;
        this.endereco_web = endereco_web;
        this.endereco = endereco;
    }

    public int getIdEditora() {
        return idEditora;
    }

    public void setIdEditora(int idEditora) {
        this.idEditora = idEditora;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
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

    public String getNacionalidade() {
        return nacionalidade;
    }

    public void setNacionalidade(String nacionalidade) {
        this.nacionalidade = nacionalidade;
    }

    public String getEndereco_web() {
        return endereco_web;
    }

    public void setEndereco_web(String endereco_web) {
        this.endereco_web = endereco_web;
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
