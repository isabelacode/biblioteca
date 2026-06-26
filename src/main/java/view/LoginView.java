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

public class LoginView extends VBox {

    private final UsuarioDAO dao = new UsuarioDAO();

    private final TextField campoLogin = new TextField();
    private final PasswordField campoSenha = new PasswordField();

    public LoginView(Consumer<Usuario> aoEntrar, Runnable aoCriarConta) {
        setAlignment(Pos.CENTER);
        setSpacing(16);
        setPadding(new Insets(40));
        getStyleClass().add("view-pane");

        Label titulo = new Label("Sistema de Biblioteca");
        titulo.getStyleClass().add("titulo-tela");

        campoLogin.setPromptText("Login");
        campoLogin.setMaxWidth(260);
        campoSenha.setPromptText("Senha");
        campoSenha.setMaxWidth(260);

        Button entrar = new Button("Entrar");
        entrar.getStyleClass().add("botao-primario");
        entrar.setOnAction(e -> autenticar(aoEntrar));

        Hyperlink criarConta = new Hyperlink("Não tem usuário? Criar agora");
        criarConta.setOnAction(e -> aoCriarConta.run());

        getChildren().addAll(titulo, campoLogin, campoSenha, entrar, criarConta);
    }

    private void autenticar(Consumer<Usuario> aoEntrar) {
        if (campoLogin.getText().isBlank() || campoSenha.getText().isBlank()) {
            UiUtils.erro("Informe login e senha.");
            return;
        }
        try {
            Usuario usuario = dao.autenticar(campoLogin.getText(), campoSenha.getText());
            if (usuario == null) {
                UiUtils.erro("Login ou senha inválidos.");
                return;
            }
            Sessao.setUsuarioLogado(usuario);
            aoEntrar.accept(usuario);
        } catch (Exception e) {
            UiUtils.erro("Não foi possível conectar ao banco de dados: " + e.getMessage());
        }
    }
}
