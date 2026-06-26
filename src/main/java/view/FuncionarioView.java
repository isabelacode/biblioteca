package view;

import dao.FuncionarioDAO;
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
import model.Funcionario;
import util.Validador;

public class FuncionarioView extends BorderPane {

    private final FuncionarioDAO dao = new FuncionarioDAO();
    private final TableView<Funcionario> tabela = new TableView<>();
    private final EnderecoFields enderecoFields = new EnderecoFields();

    private final TextField campoNome = new TextField();
    private final TextField campoCpf = new TextField();
    private final TextField campoTelefone = new TextField();
    private final TextField campoEmail = new TextField();
    private final TextField campoCargo = new TextField();

    private Funcionario selecionado;

    public FuncionarioView() {
        getStyleClass().add("view-pane");
        setPadding(new Insets(24));

        Label titulo = new Label("Funcionários");
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
        UiUtils.addRow(grid, 4, "Cargo", campoCargo);
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

    private TableView<Funcionario> criarTabela() {
        TableColumn<Funcionario, String> colNome = new TableColumn<>("Nome");
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));

        TableColumn<Funcionario, String> colCargo = new TableColumn<>("Cargo");
        colCargo.setCellValueFactory(new PropertyValueFactory<>("cargo"));

        TableColumn<Funcionario, String> colTelefone = new TableColumn<>("Telefone");
        colTelefone.setCellValueFactory(new PropertyValueFactory<>("telefone"));

        TableColumn<Funcionario, String> colEmail = new TableColumn<>("E-mail");
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        tabela.getColumns().addAll(colNome, colCargo, colTelefone, colEmail);
        tabela.getSelectionModel().selectedItemProperty().addListener((obs, antiga, nova) -> popular(nova));
        return tabela;
    }

    private void carregar() {
        try {
            tabela.setItems(FXCollections.observableArrayList(dao.listarTodos()));
        } catch (Exception e) {
            UiUtils.erro("Não foi possível carregar os funcionários: " + e.getMessage());
        }
    }

    private void popular(Funcionario funcionario) {
        selecionado = funcionario;
        if (funcionario == null) {
            limpar();
            return;
        }
        campoNome.setText(funcionario.getNome());
        campoCpf.setText(funcionario.getCpf());
        campoTelefone.setText(funcionario.getTelefone());
        campoEmail.setText(funcionario.getEmail());
        campoCargo.setText(funcionario.getCargo());
        enderecoFields.setEndereco(funcionario.getEndereco());
    }

    private void salvar() {
        if (campoNome.getText().isBlank()) {
            UiUtils.erro("O nome do funcionário é obrigatório.");
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
            Funcionario funcionario = selecionado != null ? selecionado : new Funcionario();
            funcionario.setNome(campoNome.getText());
            funcionario.setCpf(Validador.apenasDigitos(campoCpf.getText()));
            funcionario.setTelefone(Validador.apenasDigitos(campoTelefone.getText()));
            funcionario.setEmail(campoEmail.getText());
            funcionario.setCargo(campoCargo.getText());
            funcionario.setEndereco(enderecoFields.getEndereco());

            if (selecionado == null) {
                dao.inserir(funcionario);
            } else {
                dao.atualizar(funcionario);
            }
            limpar();
            carregar();
        } catch (Exception e) {
            UiUtils.erro("Não foi possível salvar o funcionário: " + e.getMessage());
        }
    }

    private void excluir() {
        if (selecionado == null) {
            UiUtils.erro("Selecione um funcionário para excluir.");
            return;
        }
        if (!UiUtils.confirmar("Excluir o funcionário \"" + selecionado.getNome() + "\"?")) {
            return;
        }
        try {
            dao.excluir(selecionado.getIdFuncionario());
            limpar();
            carregar();
        } catch (IllegalStateException e) {
            UiUtils.erro(e.getMessage());
        } catch (Exception e) {
            UiUtils.erro("Não foi possível excluir o funcionário: " + e.getMessage());
        }
    }

    private void limpar() {
        selecionado = null;
        campoNome.clear();
        campoCpf.clear();
        campoTelefone.clear();
        campoEmail.clear();
        campoCargo.clear();
        enderecoFields.limpar();
        tabela.getSelectionModel().clearSelection();
    }
}
