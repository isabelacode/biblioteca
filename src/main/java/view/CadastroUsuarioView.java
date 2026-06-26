package view;

import app.Sessao;
import dao.UsuarioDAO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import model.Usuario;

import java.util.function.Consumer;

public class CadastroUsuarioView extends VBox {

    private final UsuarioDAO dao = new UsuarioDAO();

    private final TextField campoNome = new TextField();
    private final TextField campoLogin = new TextField();
    private final PasswordField campoSenha = new PasswordField();
    private final PasswordField campoConfirmarSenha = new PasswordField();

    public CadastroUsuarioView(Consumer<Usuario> aoEntrar, Runnable aoVoltar) {
        setAlignment(Pos.CENTER);
        setSpacing(16);
        setPadding(new Insets(40));
        getStyleClass().add("view-pane");

        Label titulo = new Label("Criar usuário");
        titulo.getStyleClass().add("titulo-tela");

        campoNome.setPromptText("Nome completo");
        campoNome.setMaxWidth(260);
        campoLogin.setPromptText("Login");
        campoLogin.setMaxWidth(260);
        campoSenha.setPromptText("Senha");
        campoSenha.setMaxWidth(260);
        campoConfirmarSenha.setPromptText("Confirmar senha");
        campoConfirmarSenha.setMaxWidth(260);

        Button cadastrar = new Button("Cadastrar e entrar");
        cadastrar.getStyleClass().add("botao-primario");
        cadastrar.setOnAction(e -> cadastrar(aoEntrar));

        Hyperlink voltar = new Hyperlink("Já tem usuário? Voltar para o login");
        voltar.setOnAction(e -> aoVoltar.run());

        getChildren().addAll(titulo, campoNome, campoLogin, campoSenha, campoConfirmarSenha, cadastrar, voltar);
    }

    private void cadastrar(Consumer<Usuario> aoEntrar) {
        if (campoNome.getText().isBlank() || campoLogin.getText().isBlank() || campoSenha.getText().isBlank()) {
            UiUtils.erro("Preencha nome, login e senha para criar o usuário.");
            return;
        }
        if (!campoSenha.getText().equals(campoConfirmarSenha.getText())) {
            UiUtils.erro("As senhas informadas não coincidem.");
            return;
        }
        try {
            Usuario usuario = new Usuario(campoNome.getText(), campoLogin.getText(), null);
            dao.inserir(usuario, campoSenha.getText());
            usuario = dao.autenticar(campoLogin.getText(), campoSenha.getText());
            Sessao.setUsuarioLogado(usuario);
            aoEntrar.accept(usuario);
        } catch (Exception e) {
            UiUtils.erro("Não foi possível criar o usuário: " + e.getMessage());
        }
    }
}
