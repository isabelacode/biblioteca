package model;

import java.time.LocalDate;

public class Exemplar {

    private int idExemplar;
    private Livro livro;
    private String status;
    private LocalDate dataEntrada;

    public Exemplar() {
    }

    public Exemplar(Livro livro, String status) {
        this.livro = livro;
        this.status = status;
    }

    public int getIdExemplar() {
        return idExemplar;
    }

    public void setIdExemplar(int idExemplar) {
        this.idExemplar = idExemplar;
    }

    public LocalDate getDataEntrada() {
        return dataEntrada;
    }

    public void setDataEntrada(LocalDate dataEntrada) {
        this.dataEntrada = dataEntrada;
    }

    public Livro getLivro() {
        return livro;
    }

    public void setLivro(Livro livro) {
        this.livro = livro;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return (livro != null ? livro.getTitulo() : "?") + " (#" + idExemplar + ")";
    }
}
