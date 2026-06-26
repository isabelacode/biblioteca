package view;

import dao.AlunoDAO;
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
import model.Aluno;
import util.Validador;

public class AlunoView extends BorderPane {

    private final AlunoDAO dao = new AlunoDAO();
    private final TableView<Aluno> tabela = new TableView<>();
    private final EnderecoFields enderecoFields = new EnderecoFields();

    private final TextField campoNome = new TextField();
    private final TextField campoCpf = new TextField();
    private final TextField campoTelefone = new TextField();
    private final TextField campoEmail = new TextField();

    private Aluno selecionado;

    public AlunoView() {
        getStyleClass().add("view-pane");
        setPadding(new Insets(24));

        Label titulo = new Label("Alunos");
        titulo.getStyleClass().add("titulo-tela");
        setTop(titulo);

        setLeft(criarFormulario());
        setCenter(criarTabela());

        carregar();
    }

    private ScrollPane criarFormulario() {
        GridPane grid = UiUtils.criarGridFormulario();
        UiUtils.addRow(grid, 0, "Nome", campoNome);
        UiUtils.addRow(grid, 1, "CPF", campoCpf);
        UiUtils.addRow(grid, 2, "Telefone", campoTelefone);
        UiUtils.addRow(grid, 3, "E-mail", campoEmail);
        enderecoFields.adicionarAoGrid(grid, 4);

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

    private TableView<Aluno> criarTabela() {
        TableColumn<Aluno, String> colNome = new TableColumn<>("Nome");
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));

        TableColumn<Aluno, String> colCpf = new TableColumn<>("CPF");
        colCpf.setCellValueFactory(new PropertyValueFactory<>("cpf"));

        TableColumn<Aluno, String> colTelefone = new TableColumn<>("Telefone");
        colTelefone.setCellValueFactory(new PropertyValueFactory<>("telefone"));

        TableColumn<Aluno, String> colEmail = new TableColumn<>("E-mail");
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        tabela.getColumns().addAll(colNome, colCpf, colTelefone, colEmail);
        tabela.getSelectionModel().selectedItemProperty().addListener((obs, antiga, nova) -> popular(nova));
        return tabela;
    }

    private void carregar() {
        try {
            tabela.setItems(FXCollections.observableArrayList(dao.listarTodos()));
        } catch (Exception e) {
            UiUtils.erro("Não foi possível carregar os alunos: " + e.getMessage());
        }
    }

    private void popular(Aluno aluno) {
        selecionado = aluno;
        if (aluno == null) {
            limpar();
            return;
        }
        campoNome.setText(aluno.getNome());
        campoCpf.setText(aluno.getCpf());
        campoTelefone.setText(aluno.getTelefone());
        campoEmail.setText(aluno.getEmail());
        enderecoFields.setEndereco(aluno.getEndereco());
    }

    private void salvar() {
        if (campoNome.getText().isBlank()) {
            UiUtils.erro("O nome do aluno é obrigatório.");
            return;
        }
        if (!Validador.isCpfValido(campoCpf.getText())) {
            UiUtils.erro("CPF inválido.");
            return;
        }
        if (!campoTelefone.getText().isBlank() && !Validador.isTelefoneValido(campoTelefone.getText())) {
            UiUtils.erro("Telefone inválido.");
            return;
        }
        try {
            Aluno aluno = selecionado != null ? selecionado : new Aluno();
            aluno.setNome(campoNome.getText());
            aluno.setCpf(campoCpf.getText());
            aluno.setTelefone(campoTelefone.getText());
            aluno.setEmail(campoEmail.getText());
            aluno.setEndereco(enderecoFields.getEndereco());

            if (selecionado == null) {
                dao.inserir(aluno);
            } else {
                dao.atualizar(aluno);
            }
            limpar();
            carregar();
        } catch (Exception e) {
            UiUtils.erro("Não foi possível salvar o aluno: " + e.getMessage());
        }
    }

    private void excluir() {
        if (selecionado == null) {
            UiUtils.erro("Selecione um aluno para excluir.");
            return;
        }
        if (!UiUtils.confirmar("Excluir o aluno \"" + selecionado.getNome() + "\"?")) {
            return;
        }
        try {
            dao.excluir(selecionado.getIdAluno());
            limpar();
            carregar();
        } catch (Exception e) {
            UiUtils.erro("Não foi possível excluir o aluno: " + e.getMessage());
        }
    }

    private void limpar() {
        selecionado = null;
        campoNome.clear();
        campoCpf.clear();
        campoTelefone.clear();
        campoEmail.clear();
        enderecoFields.limpar();
        tabela.getSelectionModel().clearSelection();
    }
}
