package model;

import java.util.ArrayList;
import java.util.List;

public class Livro {

    private int idLivro;
    protected String titulo;
    protected Integer ano_publicacao;
    protected String isbn;
    protected Categoria categoria;
    protected Editora editora;
    protected List<Autor> autores = new ArrayList<>();

    public Livro() {
    }

    public Livro(String titulo, Integer ano_publicacao, String isbn, Categoria categoria, Editora editora) {
        this.titulo = titulo;
        this.ano_publicacao = ano_publicacao;
        this.isbn = isbn;
        this.categoria = categoria;
        this.editora = editora;
    }

    public int getIdLivro() {
        return idLivro;
    }

    public void setIdLivro(int idLivro) {
        this.idLivro = idLivro;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Integer getAno_publicacao() {
        return ano_publicacao;
    }

    public void setAno_publicacao(Integer ano_publicacao) {
        this.ano_publicacao = ano_publicacao;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Editora getEditora() {
        return editora;
    }

    public void setEditora(Editora editora) {
        this.editora = editora;
    }

    public List<Autor> getAutores() {
        return autores;
    }

    public void setAutores(List<Autor> autores) {
        this.autores = autores;
    }

    @Override
    public String toString() {
        return titulo;
    }
}
