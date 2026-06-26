package view;

import dao.EditoraDAO;
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
import model.Editora;
import util.Validador;

public class EditoraView extends BorderPane {

    private final EditoraDAO dao = new EditoraDAO();
    private final TableView<Editora> tabela = new TableView<>();
    private final EnderecoFields enderecoFields = new EnderecoFields();

    private final TextField campoNome = new TextField();
    private final TextField campoCnpj = new TextField();
    private final TextField campoTelefone = new TextField();
    private final TextField campoEmail = new TextField();
    private final TextField campoNacionalidade = new TextField();
    private final TextField campoSiteWeb = new TextField();

    private Editora selecionada;

    public EditoraView() {
        getStyleClass().add("view-pane");
        setPadding(new Insets(24));

        Label titulo = new Label("Editoras");
        titulo.getStyleClass().add("titulo-tela");
        setTop(titulo);

        setLeft(criarFormulario());
        setCenter(criarTabela());

        carregar();
    }

    private ScrollPane criarFormulario() {
        GridPane grid = UiUtils.criarGridFormulario();
        UiUtils.addRow(grid, 0, "Nome", campoNome);
        UiUtils.addRow(grid, 1, "CNPJ", campoCnpj);
        UiUtils.addRow(grid, 2, "Telefone", campoTelefone);
        UiUtils.addRow(grid, 3, "E-mail", campoEmail);
        UiUtils.addRow(grid, 4, "Nacionalidade", campoNacionalidade);
        UiUtils.addRow(grid, 5, "Site", campoSiteWeb);
        enderecoFields.adicionarAoGrid(grid, 6);

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

    private TableView<Editora> criarTabela() {
        TableColumn<Editora, String> colNome = new TableColumn<>("Nome");
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));

        TableColumn<Editora, String> colCnpj = new TableColumn<>("CNPJ");
        colCnpj.setCellValueFactory(new PropertyValueFactory<>("cnpj"));

        TableColumn<Editora, String> colEmail = new TableColumn<>("E-mail");
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        tabela.getColumns().addAll(colNome, colCnpj, colEmail);
        tabela.getSelectionModel().selectedItemProperty().addListener((obs, antiga, nova) -> popular(nova));
        return tabela;
    }

    private void carregar() {
        try {
            tabela.setItems(FXCollections.observableArrayList(dao.listarTodos()));
        } catch (Exception e) {
            UiUtils.erro("Não foi possível carregar as editoras: " + e.getMessage());
        }
    }

    private void popular(Editora editora) {
        selecionada = editora;
        if (editora == null) {
            limpar();
            return;
        }
        campoNome.setText(editora.getNome());
        campoCnpj.setText(editora.getCnpj());
        campoTelefone.setText(editora.getTelefone());
        campoEmail.setText(editora.getEmail());
        campoNacionalidade.setText(editora.getNacionalidade());
        campoSiteWeb.setText(editora.getEndereco_web());
        enderecoFields.setEndereco(editora.getEndereco());
    }

    private void salvar() {
        if (campoNome.getText().isBlank()) {
            UiUtils.erro("O nome da editora é obrigatório.");
            return;
        }
        if (!campoTelefone.getText().isBlank() && !Validador.isTelefoneValido(campoTelefone.getText())) {
            UiUtils.erro("Telefone inválido.");
            return;
        }
        try {
            Editora editora = selecionada != null ? selecionada : new Editora();
            editora.setNome(campoNome.getText());
            editora.setCnpj(campoCnpj.getText());
            editora.setTelefone(campoTelefone.getText());
            editora.setEmail(campoEmail.getText());
            editora.setNacionalidade(campoNacionalidade.getText());
            editora.setEndereco_web(campoSiteWeb.getText());
            editora.setEndereco(enderecoFields.getEndereco());

            if (selecionada == null) {
                dao.inserir(editora);
            } else {
                dao.atualizar(editora);
            }
            limpar();
            carregar();
        } catch (Exception e) {
            UiUtils.erro("Não foi possível salvar a editora: " + e.getMessage());
        }
    }

    private void excluir() {
        if (selecionada == null) {
            UiUtils.erro("Selecione uma editora para excluir.");
            return;
        }
        if (!UiUtils.confirmar("Excluir a editora \"" + selecionada.getNome() + "\"?")) {
            return;
        }
        try {
            dao.excluir(selecionada.getIdEditora());
            limpar();
            carregar();
        } catch (Exception e) {
            UiUtils.erro("Não foi possível excluir a editora: " + e.getMessage());
        }
    }

    private void limpar() {
        selecionada = null;
        campoNome.clear();
        campoCnpj.clear();
        campoTelefone.clear();
        campoEmail.clear();
        campoNacionalidade.clear();
        campoSiteWeb.clear();
        enderecoFields.limpar();
        tabela.getSelectionModel().clearSelection();
    }
}
