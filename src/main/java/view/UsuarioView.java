package view;

import dao.UsuarioDAO;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.Usuario;

public class UsuarioView extends BorderPane {

    private final UsuarioDAO dao = new UsuarioDAO();
    private final TableView<Usuario> tabela = new TableView<>();

    private final TextField campoNome = new TextField();
    private final TextField campoLogin = new TextField();
    private final PasswordField campoSenha = new PasswordField();

    private Usuario selecionado;

    public UsuarioView() {
        getStyleClass().add("view-pane");
        setPadding(new Insets(24));

        Label titulo = new Label("Usuários do sistema");
        titulo.getStyleClass().add("titulo-tela");
        setTop(titulo);

        setLeft(criarFormulario());
        setCenter(criarTabela());

        carregar();
    }

    private VBox criarFormulario() {
        campoSenha.setPromptText("Deixe em branco para manter a senha atual");

        GridPane grid = UiUtils.criarGridFormulario();
        UiUtils.addRow(grid, 0, "Nome", campoNome);
        UiUtils.addRow(grid, 1, "Login", campoLogin);
        UiUtils.addRow(grid, 2, "Senha", campoSenha);

        Button salvar = new Button("Salvar");
        salvar.getStyleClass().add("botao-primario");
        salvar.setOnAction(e -> salvar());

        Button excluir = new Button("Excluir");
        excluir.setOnAction(e -> excluir());

        Button limpar = new Button("Limpar");
        limpar.setOnAction(e -> limpar());

        HBox botoes = new HBox(8, salvar, excluir, limpar);

        VBox form = new VBox(12, grid, botoes);
        form.getStyleClass().add("form-container");
        form.setPadding(new Insets(0, 24, 0, 0));
        form.setPrefWidth(360);
        return form;
    }

    private TableView<Usuario> criarTabela() {
        TableColumn<Usuario, String> colNome = new TableColumn<>("Nome");
        colNome.setCellValueFactory(new PropertyValueFactory<>("nomeUsuario"));

        TableColumn<Usuario, String> colLogin = new TableColumn<>("Login");
        colLogin.setCellValueFactory(new PropertyValueFactory<>("login"));

        tabela.getColumns().addAll(colNome, colLogin);
        tabela.getSelectionModel().selectedItemProperty().addListener((obs, antiga, nova) -> popular(nova));
        return tabela;
    }

    private void carregar() {
        try {
            tabela.setItems(FXCollections.observableArrayList(dao.listarTodos()));
        } catch (Exception e) {
            UiUtils.erro("Não foi possível carregar os usuários: " + e.getMessage());
        }
    }

    private void popular(Usuario usuario) {
        selecionado = usuario;
        if (usuario == null) {
            limpar();
            return;
        }
        campoNome.setText(usuario.getNomeUsuario());
        campoLogin.setText(usuario.getLogin());
        campoSenha.clear();
    }

    private void salvar() {
        if (campoNome.getText().isBlank() || campoLogin.getText().isBlank()) {
            UiUtils.erro("Informe nome e login.");
            return;
        }
        try {
            if (selecionado == null) {
                if (campoSenha.getText().isBlank()) {
                    UiUtils.erro("Informe a senha do novo usuário.");
                    return;
                }
                Usuario usuario = new Usuario(campoNome.getText(), campoLogin.getText(), null);
                dao.inserir(usuario, campoSenha.getText());
            } else {
                selecionado.setNomeUsuario(campoNome.getText());
                selecionado.setLogin(campoLogin.getText());
                dao.atualizar(selecionado, campoSenha.getText());
            }
            limpar();
            carregar();
        } catch (Exception e) {
            UiUtils.erro("Não foi possível salvar o usuário: " + e.getMessage());
        }
    }

    private void excluir() {
        if (selecionado == null) {
            UiUtils.erro("Selecione um usuário para excluir.");
            return;
        }
        if (!UiUtils.confirmar("Excluir o usuário \"" + selecionado.getNomeUsuario() + "\"?")) {
            return;
        }
        try {
            dao.excluir(selecionado.getIdUsuario());
            limpar();
            carregar();
        } catch (Exception e) {
            UiUtils.erro("Não foi possível excluir o usuário: " + e.getMessage());
        }
    }

    private void limpar() {
        selecionado = null;
        campoNome.clear();
        campoLogin.clear();
        campoSenha.clear();
        tabela.getSelectionModel().clearSelection();
    }
}
