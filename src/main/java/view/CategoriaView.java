package view;

import dao.CategoriaDAO;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.Categoria;

public class CategoriaView extends BorderPane {

    private final CategoriaDAO dao = new CategoriaDAO();
    private final TableView<Categoria> tabela = new TableView<>();

    private final TextField campoNome = new TextField();
    private final TextArea campoDescricao = new TextArea();

    private Categoria selecionada;

    public CategoriaView() {
        getStyleClass().add("view-pane");
        setPadding(new Insets(24));

        Label titulo = new Label("Categorias");
        titulo.getStyleClass().add("titulo-tela");
        setTop(titulo);

        setLeft(criarFormulario());
        setCenter(criarTabela());

        carregar();
    }

    private VBox criarFormulario() {
        campoDescricao.setPrefRowCount(4);
        campoDescricao.setWrapText(true);

        GridPane grid = UiUtils.criarGridFormulario();
        UiUtils.addRow(grid, 0, "Nome", campoNome);
        UiUtils.addRow(grid, 1, "Descrição", campoDescricao);

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

    private TableView<Categoria> criarTabela() {
        TableColumn<Categoria, String> colNome = new TableColumn<>("Nome");
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));

        TableColumn<Categoria, String> colDescricao = new TableColumn<>("Descrição");
        colDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));

        tabela.getColumns().addAll(colNome, colDescricao);
        tabela.getSelectionModel().selectedItemProperty().addListener((obs, antiga, nova) -> popular(nova));
        return tabela;
    }

    private void carregar() {
        try {
            tabela.setItems(javafx.collections.FXCollections.observableArrayList(dao.listarTodos()));
        } catch (Exception e) {
            UiUtils.erro("Não foi possível carregar as categorias: " + e.getMessage());
        }
    }

    private void popular(Categoria categoria) {
        selecionada = categoria;
        if (categoria == null) {
            limpar();
            return;
        }
        campoNome.setText(categoria.getNome());
        campoDescricao.setText(categoria.getDescricao());
    }

    private void salvar() {
        if (campoNome.getText().isBlank()) {
            UiUtils.erro("O nome da categoria é obrigatório.");
            return;
        }
        try {
            if (selecionada == null) {
                Categoria categoria = new Categoria(campoNome.getText(), campoDescricao.getText());
                dao.inserir(categoria);
            } else {
                selecionada.setNome(campoNome.getText());
                selecionada.setDescricao(campoDescricao.getText());
                dao.atualizar(selecionada);
            }
            limpar();
            carregar();
        } catch (Exception e) {
            UiUtils.erro("Não foi possível salvar a categoria: " + e.getMessage());
        }
    }

    private void excluir() {
        if (selecionada == null) {
            UiUtils.erro("Selecione uma categoria para excluir.");
            return;
        }
        if (!UiUtils.confirmar("Excluir a categoria \"" + selecionada.getNome() + "\"?")) {
            return;
        }
        try {
            dao.excluir(selecionada.getIdCategoria());
            limpar();
            carregar();
        } catch (Exception e) {
            UiUtils.erro("Não foi possível excluir a categoria: " + e.getMessage());
        }
    }

    private void limpar() {
        selecionada = null;
        campoNome.clear();
        campoDescricao.clear();
        tabela.getSelectionModel().clearSelection();
    }
}
