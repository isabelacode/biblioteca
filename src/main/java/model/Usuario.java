package model;

public class Usuario {

    private int idUsuario;
    private String nomeUsuario;
    private String login;
    private String senhaHash;

    public Usuario() {
    }

    public Usuario(String nomeUsuario, String login, String senhaHash) {
        this.nomeUsuario = nomeUsuario;
        this.login = login;
        this.senhaHash = senhaHash;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public void setSenhaHash(String senhaHash) {
        this.senhaHash = senhaHash;
    }

    @Override
    public String toString() {
        return nomeUsuario;
    }
}
