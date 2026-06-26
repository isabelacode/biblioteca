package view;

import dao.AutorDAO;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.Autor;
import util.Validador;

public class AutorView extends BorderPane {

    private final AutorDAO dao = new AutorDAO();
    private final TableView<Autor> tabela = new TableView<>();
    private final EnderecoFields enderecoFields = new EnderecoFields();

    private final TextField campoNome = new TextField();
    private final TextField campoTelefone = new TextField();
    private final TextField campoEmail = new TextField();
    private final TextField campoPseudonimo = new TextField();
    private final TextField campoNacionalidade = new TextField();

    private Autor selecionado;

    public AutorView() {
        getStyleClass().add("view-pane");
        setPadding(new Insets(24));

        Label titulo = new Label("Autores");
        titulo.getStyleClass().add("titulo-tela");
        setTop(titulo);

        setLeft(criarFormulario());
        setCenter(criarTabela());

        carregar();
    }

    private ScrollPane criarFormulario() {
        GridPane grid = UiUtils.criarGridFormulario();
        UiUtils.addRow(grid, 0, "Nome", campoNome);
        UiUtils.addRow(grid, 1, "Telefone", campoTelefone);
        UiUtils.addRow(grid, 2, "E-mail", campoEmail);
        UiUtils.addRow(grid, 3, "Pseudônimo", campoPseudonimo);
        UiUtils.addRow(grid, 4, "Nacionalidade", campoNacionalidade);
        enderecoFields.adicionarAoGrid(grid, 5);

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

        ScrollPane scroll = new ScrollPane(form);
        scroll.setFitToWidth(true);
        scroll.setPrefWidth(380);
        return scroll;
    }

    private TableView<Autor> criarTabela() {
        TableColumn<Autor, String> colNome = new TableColumn<>("Nome");
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));

        TableColumn<Autor, String> colPseudonimo = new TableColumn<>("Pseudônimo");
        colPseudonimo.setCellValueFactory(new PropertyValueFactory<>("pseudonimo"));

        TableColumn<Autor, String> colNacionalidade = new TableColumn<>("Nacionalidade");
        colNacionalidade.setCellValueFactory(new PropertyValueFactory<>("nacionalidade"));

        tabela.getColumns().addAll(colNome, colPseudonimo, colNacionalidade);
        tabela.getSelectionModel().selectedItemProperty().addListener((obs, antiga, nova) -> popular(nova));
        return tabela;
    }

    private void carregar() {
        try {
            tabela.setItems(FXCollections.observableArrayList(dao.listarTodos()));
        } catch (Exception e) {
            UiUtils.erro("Não foi possível carregar os autores: " + e.getMessage());
        }
    }

    private void popular(Autor autor) {
        selecionado = autor;
        if (autor == null) {
            limpar();
            return;
        }
        campoNome.setText(autor.getNome());
        campoTelefone.setText(autor.getTelefone());
        campoEmail.setText(autor.getEmail());
        campoPseudonimo.setText(autor.getPseudonimo());
        campoNacionalidade.setText(autor.getNacionalidade());
        enderecoFields.setEndereco(autor.getEndereco());
    }

    private void salvar() {
        if (campoNome.getText().isBlank()) {
            UiUtils.erro("O nome do autor é obrigatório.");
            return;
        }
        if (!campoTelefone.getText().isBlank() && !Validador.isTelefoneValido(campoTelefone.getText())) {
            UiUtils.erro("Telefone inválido.");
            return;
        }
        try {
            Autor autor = selecionado != null ? selecionado : new Autor();
            autor.setNome(campoNome.getText());
            autor.setTelefone(campoTelefone.getText());
            autor.setEmail(campoEmail.getText());
            autor.setPseudonimo(campoPseudonimo.getText());
            autor.setNacionalidade(campoNacionalidade.getText());
            autor.setEndereco(enderecoFields.getEndereco());

            if (selecionado == null) {
                dao.inserir(autor);
            } else {
                dao.atualizar(autor);
            }
            limpar();
            carregar();
        } catch (Exception e) {
            UiUtils.erro("Não foi possível salvar o autor: " + e.getMessage());
        }
    }

    private void excluir() {
        if (selecionado == null) {
            UiUtils.erro("Selecione um autor para excluir.");
            return;
        }
        if (!UiUtils.confirmar("Excluir o autor \"" + selecionado.getNome() + "\"?")) {
            return;
        }
        try {
            dao.excluir(selecionado.getIdAutor());
            limpar();
            carregar();
        } catch (Exception e) {
            UiUtils.erro("Não foi possível excluir o autor: " + e.getMessage());
        }
    }

    private void limpar() {
        selecionado = null;
        campoNome.clear();
        campoTelefone.clear();
        campoEmail.clear();
        campoPseudonimo.clear();
        campoNacionalidade.clear();
        enderecoFields.limpar();
        tabela.getSelectionModel().clearSelection();
    }
}
