package model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Multa {

    private int idMulta;
    private EmprestimoLivro emprestimoLivro;
    private BigDecimal valor;
    private LocalDate dataPagamento;

    public Multa() {
    }

    public Multa(EmprestimoLivro emprestimoLivro, BigDecimal valor) {
        this.emprestimoLivro = emprestimoLivro;
        this.valor = valor;
    }

    public int getIdMulta() {
        return idMulta;
    }

    public void setIdMulta(int idMulta) {
        this.idMulta = idMulta;
    }

    public EmprestimoLivro getEmprestimoLivro() {
        return emprestimoLivro;
    }

    public void setEmprestimoLivro(EmprestimoLivro emprestimoLivro) {
        this.emprestimoLivro = emprestimoLivro;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public LocalDate getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(LocalDate dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public boolean isPaga() {
        return dataPagamento != null;
    }
}
