package model;

import java.time.LocalDate;

public class EmprestimoLivro {

    private int idEmprestimoLivro;
    private Emprestimo emprestimo;
    private Exemplar exemplar;
    private LocalDate dataDevolucao;
    private Multa multa;

    public EmprestimoLivro() {
    }

    public EmprestimoLivro(Emprestimo emprestimo, Exemplar exemplar) {
        this.emprestimo = emprestimo;
        this.exemplar = exemplar;
    }

    public int getIdEmprestimoLivro() {
        return idEmprestimoLivro;
    }

    public void setIdEmprestimoLivro(int idEmprestimoLivro) {
        this.idEmprestimoLivro = idEmprestimoLivro;
    }

    public Emprestimo getEmprestimo() {
        return emprestimo;
    }

    public void setEmprestimo(Emprestimo emprestimo) {
        this.emprestimo = emprestimo;
    }

    public Exemplar getExemplar() {
        return exemplar;
    }

    public void setExemplar(Exemplar exemplar) {
        this.exemplar = exemplar;
    }

    public LocalDate getDataDevolucao() {
        return dataDevolucao;
    }

    public void setDataDevolucao(LocalDate dataDevolucao) {
        this.dataDevolucao = dataDevolucao;
    }

    public Multa getMulta() {
        return multa;
    }

    public void setMulta(Multa multa) {
        this.multa = multa;
    }
}
