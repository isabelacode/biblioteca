import app.Sessao;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import view.AlunoView;
import view.AutorView;
import view.CadastroUsuarioView;
import view.CategoriaView;
import view.DashboardView;
import view.EditoraView;
import view.EmprestimoView;
import view.ExemplarView;
import view.FuncionarioView;
import view.LivroView;
import view.LoginView;
import view.UsuarioView;

import java.util.function.Supplier;

public class Main extends Application {

    private final StackPane conteudo = new StackPane();
    private Stage stage;

    @Override
    public void start(Stage stage) {
        this.stage = stage;

        Scene scene = new Scene(criarTelaLogin(), 1280, 800);
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

        stage.setTitle("Sistema de Biblioteca");
        stage.setScene(scene);
        stage.setMinWidth(1024);
        stage.setMinHeight(700);
        stage.show();
    }

    private LoginView criarTelaLogin() {
        return new LoginView(usuario -> mostrarAplicacao(), this::mostrarTelaCadastro);
    }

    private void mostrarTelaLogin() {
        stage.getScene().setRoot(criarTelaLogin());
    }

    private void mostrarTelaCadastro() {
        CadastroUsuarioView cadastro = new CadastroUsuarioView(usuario -> mostrarAplicacao(), this::mostrarTelaLogin);
        stage.getScene().setRoot(cadastro);
    }

    private void mostrarAplicacao() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("root-pane");
        root.setLeft(criarMenuLateral());
        root.setCenter(conteudo);
        conteudo.getChildren().add(new DashboardView());

        stage.getScene().setRoot(root);
    }

    private VBox criarMenuLateral() {
        Label titulo = new Label("Biblioteca");
        titulo.getStyleClass().add("sidebar-titulo");

        Label usuarioLogado = new Label("Olá, " + Sessao.getUsuarioLogado().getNomeUsuario());
        usuarioLogado.getStyleClass().add("sidebar-usuario");

        ToggleGroup grupo = new ToggleGroup();

        Button sair = new Button("Sair");
        sair.getStyleClass().add("nav-button");
        sair.setMaxWidth(Double.MAX_VALUE);
        sair.setOnAction(e -> {
            Sessao.setUsuarioLogado(null);
            mostrarTelaLogin();
        });

        VBox menu = new VBox(
                titulo,
                usuarioLogado,
                criarBotao("Dashboard", grupo, DashboardView::new, true),
                criarBotao("Alunos", grupo, AlunoView::new, false),
                criarBotao("Funcionários", grupo, FuncionarioView::new, false),
                criarBotao("Autores", grupo, AutorView::new, false),
                criarBotao("Categorias", grupo, CategoriaView::new, false),
                criarBotao("Editoras", grupo, EditoraView::new, false),
                criarBotao("Livros", grupo, LivroView::new, false),
                criarBotao("Exemplares", grupo, ExemplarView::new, false),
                criarBotao("Empréstimos", grupo, EmprestimoView::new, false),
                criarBotao("Usuários", grupo, UsuarioView::new, false),
                sair
        );
        menu.getStyleClass().add("sidebar");
        menu.setPadding(new Insets(16, 0, 16, 0));

        return menu;
    }

    private ToggleButton criarBotao(String texto, ToggleGroup grupo, Supplier<Region> tela, boolean selecionado) {
        ToggleButton botao = new ToggleButton(texto);
        botao.getStyleClass().add("nav-button");
        botao.setMaxWidth(Double.MAX_VALUE);
        botao.setToggleGroup(grupo);
        botao.setSelected(selecionado);
        botao.setOnAction(e -> {
            if (!botao.isSelected()) {
                botao.setSelected(true);
                return;
            }
            conteudo.getChildren().setAll(tela.get());
        });
        return botao;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
