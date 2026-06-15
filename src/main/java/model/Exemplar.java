package model;

public class Exemplar {

    private int idExemplar;
    private String status;

    public Exemplar(int idExemplar, String status) {
        this.idExemplar = idExemplar;
        this.status = status;
    }

    public int getIdExemplar() {
        return idExemplar;
    }

    public String getStatus() {
        return status;
    }
}