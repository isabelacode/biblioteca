package view;

import dao.AutorDAO;
import dao.CategoriaDAO;
import dao.EditoraDAO;
import dao.LivroDAO;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.Autor;
import model.Categoria;
import model.Editora;
import model.Livro;

import java.util.ArrayList;

public class LivroView extends BorderPane {

    private final LivroDAO dao = new LivroDAO();
    private final TableView<Livro> tabela = new TableView<>();

    private final TextField campoTitulo = new TextField();
    private final TextField campoAno = new TextField();
    private final TextField campoIsbn = new TextField();
    private final ComboBox<Categoria> comboCategoria = new ComboBox<>();
    private final ComboBox<Editora> comboEditora = new ComboBox<>();
    private final ListView<Autor> listaAutores = new ListView<>();

    private Livro selecionado;

    public LivroView() {
        getStyleClass().add("view-pane");
        setPadding(new Insets(24));

        Label titulo = new Label("Livros");
        titulo.getStyleClass().add("titulo-tela");
        setTop(titulo);

        carregarCombos();
        setLeft(criarFormulario());
        setCenter(criarTabela());

        carregar();
    }

    private void carregarCombos() {
        try {
            comboCategoria.setItems(FXCollections.observableArrayList(new CategoriaDAO().listarTodos()));
            comboEditora.setItems(FXCollections.observableArrayList(new EditoraDAO().listarTodos()));
            listaAutores.setItems(FXCollections.observableArrayList(new AutorDAO().listarTodos()));
            listaAutores.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        } catch (Exception e) {
            UiUtils.erro("Não foi possível carregar categorias/editoras/autores: " + e.getMessage());
        }
    }

    private ScrollPane criarFormulario() {
        listaAutores.setPrefHeight(120);

        GridPane grid = UiUtils.criarGridFormulario();
        UiUtils.addRow(grid, 0, "Título", campoTitulo);
        UiUtils.addRow(grid, 1, "Ano publicação", campoAno);
        UiUtils.addRow(grid, 2, "ISBN", campoIsbn);
        UiUtils.addRow(grid, 3, "Categoria", comboCategoria);
        UiUtils.addRow(grid, 4, "Editora", comboEditora);
        UiUtils.addRow(grid, 5, "Autores", listaAutores);

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

    private TableView<Livro> criarTabela() {
        TableColumn<Livro, String> colTitulo = new TableColumn<>("Título");
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));

        TableColumn<Livro, String> colAno = new TableColumn<>("Ano");
        colAno.setCellValueFactory(new PropertyValueFactory<>("ano_publicacao"));

        TableColumn<Livro, String> colIsbn = new TableColumn<>("ISBN");
        colIsbn.setCellValueFactory(new PropertyValueFactory<>("isbn"));

        TableColumn<Livro, Categoria> colCategoria = new TableColumn<>("Categoria");
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));

        TableColumn<Livro, Editora> colEditora = new TableColumn<>("Editora");
        colEditora.setCellValueFactory(new PropertyValueFactory<>("editora"));

        tabela.getColumns().addAll(colTitulo, colAno, colIsbn, colCategoria, colEditora);
        tabela.getSelectionModel().selectedItemProperty().addListener((obs, antiga, nova) -> popular(nova));
        return tabela;
    }

    private void carregar() {
        try {
            tabela.setItems(FXCollections.observableArrayList(dao.listarTodos()));
        } catch (Exception e) {
            UiUtils.erro("Não foi possível carregar os livros: " + e.getMessage());
        }
    }

    private void popular(Livro livro) {
        selecionado = livro;
        if (livro == null) {
            limpar();
            return;
        }
        campoTitulo.setText(livro.getTitulo());
        campoAno.setText(livro.getAno_publicacao() != null ? String.valueOf(livro.getAno_publicacao()) : "");
        campoIsbn.setText(livro.getIsbn());
        comboCategoria.setValue(buscarPorId(comboCategoria.getItems(), livro.getCategoria() != null ? livro.getCategoria().getIdCategoria() : -1));
        comboEditora.setValue(buscarPorIdEditora(livro.getEditora() != null ? livro.getEditora().getIdEditora() : -1));

        listaAutores.getSelectionModel().clearSelection();
        if (livro.getAutores() != null) {
            for (Autor autor : livro.getAutores()) {
                for (Autor candidato : listaAutores.getItems()) {
                    if (candidato.getIdAutor() == autor.getIdAutor()) {
                        listaAutores.getSelectionModel().select(candidato);
                    }
                }
            }
        }
    }

    private Categoria buscarPorId(java.util.List<Categoria> categorias, int id) {
        return categorias.stream().filter(c -> c.getIdCategoria() == id).findFirst().orElse(null);
    }

    private Editora buscarPorIdEditora(int id) {
        return comboEditora.getItems().stream().filter(ed -> ed.getIdEditora() == id).findFirst().orElse(null);
    }

    private void salvar() {
        if (campoTitulo.getText().isBlank()) {
            UiUtils.erro("O título do livro é obrigatório.");
            return;
        }
        Integer ano;
        try {
            ano = campoAno.getText().isBlank() ? null : Integer.valueOf(campoAno.getText());
        } catch (NumberFormatException e) {
            UiUtils.erro("O ano de publicação deve ser um número.");
            return;
        }
        try {
            Livro livro = selecionado != null ? selecionado : new Livro();
            livro.setTitulo(campoTitulo.getText());
            livro.setAno_publicacao(ano);
            livro.setIsbn(campoIsbn.getText());
            livro.setCategoria(comboCategoria.getValue());
            livro.setEditora(comboEditora.getValue());
            livro.setAutores(new ArrayList<>(listaAutores.getSelectionModel().getSelectedItems()));

            if (selecionado == null) {
                dao.inserir(livro);
            } else {
                dao.atualizar(livro);
            }
            limpar();
            carregar();
        } catch (Exception e) {
            UiUtils.erro("Não foi possível salvar o livro: " + e.getMessage());
        }
    }

    private void excluir() {
        if (selecionado == null) {
            UiUtils.erro("Selecione um livro para excluir.");
            return;
        }
        if (!UiUtils.confirmar("Excluir o livro \"" + selecionado.getTitulo() + "\"?")) {
            return;
        }
        try {
            dao.excluir(selecionado.getIdLivro());
            limpar();
            carregar();
        } catch (Exception e) {
            UiUtils.erro("Não foi possível excluir o livro: " + e.getMessage());
        }
    }

    private void limpar() {
        selecionado = null;
        campoTitulo.clear();
        campoAno.clear();
        campoIsbn.clear();
        comboCategoria.setValue(null);
        comboEditora.setValue(null);
        listaAutores.getSelectionModel().clearSelection();
        tabela.getSelectionModel().clearSelection();
    }
}
