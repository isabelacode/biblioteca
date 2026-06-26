package view;

import dao.ExemplarDAO;
import dao.LivroDAO;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.Exemplar;
import model.Livro;

public class ExemplarView extends BorderPane {

    private static final String[] STATUS = {"DISPONIVEL", "EMPRESTADO", "MANUTENCAO"};

    private final ExemplarDAO dao = new ExemplarDAO();
    private final TableView<Exemplar> tabela = new TableView<>();

    private final ComboBox<Livro> comboLivro = new ComboBox<>();
    private final ComboBox<String> comboStatus = new ComboBox<>(FXCollections.observableArrayList(STATUS));

    private Exemplar selecionado;

    public ExemplarView() {
        getStyleClass().add("view-pane");
        setPadding(new Insets(24));

        Label titulo = new Label("Exemplares");
        titulo.getStyleClass().add("titulo-tela");
        setTop(titulo);

        carregarLivros();
        setLeft(criarFormulario());
        setCenter(criarTabela());

        carregar();
    }

    private void carregarLivros() {
        try {
            comboLivro.setItems(FXCollections.observableArrayList(new LivroDAO().listarTodos()));
        } catch (Exception e) {
            UiUtils.erro("Não foi possível carregar os livros: " + e.getMessage());
        }
    }

    private VBox criarFormulario() {
        comboStatus.setValue(STATUS[0]);

        GridPane grid = UiUtils.criarGridFormulario();
        UiUtils.addRow(grid, 0, "Livro", comboLivro);
        UiUtils.addRow(grid, 1, "Status", comboStatus);

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
        form.setPrefWidth(380);
        return form;
    }

    private TableView<Exemplar> criarTabela() {
        TableColumn<Exemplar, Number> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("idExemplar"));

        TableColumn<Exemplar, Livro> colLivro = new TableColumn<>("Livro");
        colLivro.setCellValueFactory(new PropertyValueFactory<>("livro"));

        TableColumn<Exemplar, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<Exemplar, java.time.LocalDate> colDataEntrada = new TableColumn<>("Data de entrada");
        colDataEntrada.setCellValueFactory(new PropertyValueFactory<>("dataEntrada"));

        tabela.getColumns().addAll(colId, colLivro, colStatus, colDataEntrada);
        tabela.getSelectionModel().selectedItemProperty().addListener((obs, antiga, nova) -> popular(nova));
        return tabela;
    }

    private void carregar() {
        try {
            tabela.setItems(FXCollections.observableArrayList(dao.listarTodos()));
        } catch (Exception e) {
            UiUtils.erro("Não foi possível carregar os exemplares: " + e.getMessage());
        }
    }

    private void popular(Exemplar exemplar) {
        selecionado = exemplar;
        if (exemplar == null) {
            limpar();
            return;
        }
        comboLivro.setValue(comboLivro.getItems().stream()
                .filter(l -> l.getIdLivro() == exemplar.getLivro().getIdLivro())
                .findFirst().orElse(null));
        comboStatus.setValue(exemplar.getStatus());
    }

    private void salvar() {
        if (comboLivro.getValue() == null) {
            UiUtils.erro("Selecione o livro do exemplar.");
            return;
        }
        try {
            Exemplar exemplar = selecionado != null ? selecionado : new Exemplar();
            exemplar.setLivro(comboLivro.getValue());
            exemplar.setStatus(comboStatus.getValue());

            if (selecionado == null) {
                dao.inserir(exemplar);
            } else {
                dao.atualizar(exemplar);
            }
            limpar();
            carregar();
        } catch (Exception e) {
            UiUtils.erro("Não foi possível salvar o exemplar: " + e.getMessage());
        }
    }

    private void excluir() {
        if (selecionado == null) {
            UiUtils.erro("Selecione um exemplar para excluir.");
            return;
        }
        if (!UiUtils.confirmar("Excluir o exemplar #" + selecionado.getIdExemplar() + "?")) {
            return;
        }
        try {
            dao.excluir(selecionado.getIdExemplar());
            limpar();
            carregar();
        } catch (Exception e) {
            UiUtils.erro("Não foi possível excluir o exemplar: " + e.getMessage());
        }
    }

    private void limpar() {
        selecionado = null;
        comboLivro.setValue(null);
        comboStatus.setValue(STATUS[0]);
        tabela.getSelectionModel().clearSelection();
    }
}
