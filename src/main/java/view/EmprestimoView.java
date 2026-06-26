package view;

import app.Sessao;
import dao.AlunoDAO;
import dao.EmprestimoDAO;
import dao.EmprestimoLivroDAO;
import dao.ExemplarDAO;
import dao.FuncionarioDAO;
import dao.LivroDAO;
import dao.MultaDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import model.Aluno;
import model.Emprestimo;
import model.EmprestimoLivro;
import model.Exemplar;
import model.Funcionario;
import model.Livro;

import java.time.LocalDate;
import java.util.ArrayList;

public class EmprestimoView extends BorderPane {

    private final EmprestimoDAO dao = new EmprestimoDAO();
    private final EmprestimoLivroDAO emprestimoLivroDAO = new EmprestimoLivroDAO();
    private final ExemplarDAO exemplarDAO = new ExemplarDAO();
    private final MultaDAO multaDAO = new MultaDAO();

    private final TableView<EmprestimoLivro> tabela = new TableView<>();

    private final ComboBox<Aluno> comboAluno = new ComboBox<>();
    private final ComboBox<Funcionario> comboFuncionario = new ComboBox<>();
    private final DatePicker campoDataEmprestimo = new DatePicker(LocalDate.now());
    private final DatePicker campoDataPrevista = new DatePicker(LocalDate.now().plusDays(7));

    private final ComboBox<Livro> comboLivro = new ComboBox<>();
    private final ComboBox<Exemplar> comboExemplar = new ComboBox<>();
    private final ListView<Exemplar> listaItens = new ListView<>();
    private final ObservableList<Exemplar> itensSelecionados = FXCollections.observableArrayList();

    public EmprestimoView() {
        getStyleClass().add("view-pane");
        setPadding(new Insets(24));

        Label titulo = new Label("Empréstimos");
        titulo.getStyleClass().add("titulo-tela");
        setTop(titulo);

        carregarCombos();
        setLeft(criarFormulario());
        setCenter(criarTabela());

        carregar();
    }

    private void carregarCombos() {
        try {
            comboAluno.setItems(FXCollections.observableArrayList(new AlunoDAO().listarTodos()));
            comboFuncionario.setItems(FXCollections.observableArrayList(new FuncionarioDAO().listarTodos()));
            comboLivro.setItems(FXCollections.observableArrayList(new LivroDAO().listarTodos()));
            comboLivro.setOnAction(e -> atualizarExemplaresDisponiveis());
        } catch (Exception e) {
            UiUtils.erro("Não foi possível carregar alunos/funcionários/livros: " + e.getMessage());
        }
    }

    private void atualizarExemplaresDisponiveis() {
        comboExemplar.setValue(null);
        Livro livro = comboLivro.getValue();
        if (livro == null) {
            comboExemplar.setItems(FXCollections.emptyObservableList());
            return;
        }
        try {
            comboExemplar.setItems(FXCollections.observableArrayList(exemplarDAO.listarDisponiveisPorLivro(livro.getIdLivro())));
        } catch (Exception e) {
            UiUtils.erro("Não foi possível carregar os exemplares disponíveis: " + e.getMessage());
        }
    }

    private VBox criarFormulario() {
        listaItens.setItems(itensSelecionados);
        listaItens.setPrefHeight(100);

        GridPane grid = UiUtils.criarGridFormulario();
        UiUtils.addRow(grid, 0, "Aluno", comboAluno);
        UiUtils.addRow(grid, 1, "Funcionário", comboFuncionario);
        UiUtils.addRow(grid, 2, "Data empréstimo", campoDataEmprestimo);
        UiUtils.addRow(grid, 3, "Devolução prevista", campoDataPrevista);
        UiUtils.addRow(grid, 4, "Livro", comboLivro);
        UiUtils.addRow(grid, 5, "Exemplar", comboExemplar);

        Button adicionarItem = new Button("Adicionar à lista");
        adicionarItem.setOnAction(e -> adicionarItem());

        Button removerItem = new Button("Remover selecionado");
        removerItem.setOnAction(e -> {
            Exemplar selecionado = listaItens.getSelectionModel().getSelectedItem();
            if (selecionado != null) {
                itensSelecionados.remove(selecionado);
            }
        });

        Button registrar = new Button("Registrar empréstimo");
        registrar.getStyleClass().add("botao-primario");
        registrar.setOnAction(e -> registrarEmprestimo());

        Button devolver = new Button("Registrar devolução");
        devolver.setOnAction(e -> devolver());

        Button pagarMulta = new Button("Marcar multa como paga");
        pagarMulta.setOnAction(e -> pagarMulta());

        Button excluir = new Button("Excluir empréstimo");
        excluir.setOnAction(e -> excluir());

        Button limpar = new Button("Limpar");
        limpar.setOnAction(e -> limpar());

        VBox botoesItem = new VBox(8, adicionarItem, removerItem);
        VBox botoesAcao = new VBox(8, registrar, devolver, pagarMulta, excluir, limpar);

        VBox form = new VBox(12, grid, listaItens, botoesItem, new javafx.scene.control.Separator(), botoesAcao);
        form.getStyleClass().add("form-container");
        form.setPadding(new Insets(0, 24, 0, 0));
        form.setPrefWidth(380);
        return form;
    }

    private void adicionarItem() {
        Exemplar exemplar = comboExemplar.getValue();
        if (exemplar == null) {
            UiUtils.erro("Selecione o livro e o exemplar a adicionar.");
            return;
        }
        if (itensSelecionados.stream().anyMatch(ex -> ex.getIdExemplar() == exemplar.getIdExemplar())) {
            UiUtils.erro("Esse exemplar já foi adicionado à lista.");
            return;
        }
        itensSelecionados.add(exemplar);
        comboExemplar.setValue(null);
    }

    private TableView<EmprestimoLivro> criarTabela() {
        tabela.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        TableColumn<EmprestimoLivro, String> colAluno = new TableColumn<>("Aluno");
        colAluno.setCellValueFactory(dados -> new SimpleStringProperty(dados.getValue().getEmprestimo().getAluno().getNome()));

        TableColumn<EmprestimoLivro, String> colLivro = new TableColumn<>("Livro");
        colLivro.setCellValueFactory(dados -> new SimpleStringProperty(dados.getValue().getExemplar().getLivro().getTitulo()));

        TableColumn<EmprestimoLivro, String> colFuncionario = new TableColumn<>("Funcionário");
        colFuncionario.setCellValueFactory(dados -> new SimpleStringProperty(
                dados.getValue().getEmprestimo().getFuncionario() != null
                        ? dados.getValue().getEmprestimo().getFuncionario().getNome() : "-"));

        TableColumn<EmprestimoLivro, String> colUsuario = new TableColumn<>("Registrado por");
        colUsuario.setCellValueFactory(dados -> new SimpleStringProperty(dados.getValue().getEmprestimo().getUsuario().getNomeUsuario()));

        TableColumn<EmprestimoLivro, LocalDate> colDataEmprestimo = new TableColumn<>("Empréstimo");
        colDataEmprestimo.setCellValueFactory(dados -> new javafx.beans.property.SimpleObjectProperty<>(dados.getValue().getEmprestimo().getDataEmprestimo()));

        TableColumn<EmprestimoLivro, LocalDate> colDataPrevista = new TableColumn<>("Prevista");
        colDataPrevista.setCellValueFactory(dados -> new javafx.beans.property.SimpleObjectProperty<>(dados.getValue().getEmprestimo().getDataDevolucaoPrevista()));

        TableColumn<EmprestimoLivro, LocalDate> colDataReal = new TableColumn<>("Devolvido em");
        colDataReal.setCellValueFactory(new PropertyValueFactory<>("dataDevolucao"));

        TableColumn<EmprestimoLivro, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(dados -> new SimpleStringProperty(situacao(dados.getValue())));

        TableColumn<EmprestimoLivro, String> colMulta = new TableColumn<>("Multa");
        colMulta.setCellValueFactory(dados -> {
            var multa = dados.getValue().getMulta();
            if (multa == null) {
                return new SimpleStringProperty("-");
            }
            return new SimpleStringProperty("R$ " + multa.getValor() + (multa.isPaga() ? " (pago)" : " (pendente)"));
        });

        tabela.getColumns().addAll(colAluno, colLivro, colFuncionario, colUsuario, colDataEmprestimo, colDataPrevista, colDataReal, colStatus, colMulta);
        return tabela;
    }

    private String situacao(EmprestimoLivro item) {
        if (item.getDataDevolucao() != null) {
            return "Devolvido";
        }
        if (LocalDate.now().isAfter(item.getEmprestimo().getDataDevolucaoPrevista())) {
            return "Atrasado";
        }
        return "Em andamento";
    }

    private void carregar() {
        try {
            tabela.setItems(FXCollections.observableArrayList(emprestimoLivroDAO.listarTodos()));
        } catch (Exception e) {
            UiUtils.erro("Não foi possível carregar os empréstimos: " + e.getMessage());
        }
    }

    private void registrarEmprestimo() {
        if (comboAluno.getValue() == null) {
            UiUtils.erro("Selecione o aluno.");
            return;
        }
        if (itensSelecionados.isEmpty()) {
            UiUtils.erro("Adicione ao menos um exemplar à lista do empréstimo.");
            return;
        }
        if (campoDataEmprestimo.getValue() == null || campoDataPrevista.getValue() == null) {
            UiUtils.erro("Informe as datas do empréstimo.");
            return;
        }
        try {
            Emprestimo emprestimo = new Emprestimo(
                    Sessao.getUsuarioLogado(),
                    comboAluno.getValue(),
                    comboFuncionario.getValue(),
                    campoDataEmprestimo.getValue(),
                    campoDataPrevista.getValue(),
                    "ATIVO"
            );
            dao.inserir(emprestimo, new ArrayList<>(itensSelecionados));
            limpar();
            carregar();
        } catch (Exception e) {
            UiUtils.erro("Não foi possível registrar o empréstimo: " + e.getMessage());
        }
    }

    private void devolver() {
        EmprestimoLivro selecionado = tabela.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            UiUtils.erro("Selecione um item na tabela para registrar a devolução.");
            return;
        }
        if (selecionado.getDataDevolucao() != null) {
            UiUtils.erro("Este item já foi devolvido.");
            return;
        }
        try {
            emprestimoLivroDAO.registrarDevolucao(selecionado.getIdEmprestimoLivro());
            carregar();
        } catch (Exception e) {
            UiUtils.erro("Não foi possível registrar a devolução: " + e.getMessage());
        }
    }

    private void pagarMulta() {
        EmprestimoLivro selecionado = tabela.getSelectionModel().getSelectedItem();
        if (selecionado == null || selecionado.getMulta() == null) {
            UiUtils.erro("Selecione um item com multa pendente.");
            return;
        }
        if (selecionado.getMulta().isPaga()) {
            UiUtils.erro("Esta multa já foi paga.");
            return;
        }
        try {
            multaDAO.marcarComoPago(selecionado.getMulta().getIdMulta());
            carregar();
        } catch (Exception e) {
            UiUtils.erro("Não foi possível registrar o pagamento: " + e.getMessage());
        }
    }

    private void excluir() {
        EmprestimoLivro selecionado = tabela.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            UiUtils.erro("Selecione um item para excluir o empréstimo correspondente.");
            return;
        }
        if (!UiUtils.confirmar("Excluir todo o empréstimo #" + selecionado.getEmprestimo().getIdEmprestimo() + "? Isso remove todos os itens e multas associadas.")) {
            return;
        }
        try {
            dao.excluir(selecionado.getEmprestimo().getIdEmprestimo());
            carregar();
        } catch (Exception e) {
            UiUtils.erro("Não foi possível excluir o empréstimo: " + e.getMessage());
        }
    }

    private void limpar() {
        comboAluno.setValue(null);
        comboFuncionario.setValue(null);
        comboLivro.setValue(null);
        comboExemplar.setValue(null);
        campoDataEmprestimo.setValue(LocalDate.now());
        campoDataPrevista.setValue(LocalDate.now().plusDays(7));
        itensSelecionados.clear();
    }
}
