package view;

import dao.AlunoDAO;
import dao.AutorDAO;
import dao.EmprestimoLivroDAO;
import dao.LivroDAO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class DashboardView extends VBox {

    public DashboardView() {
        setPadding(new Insets(24));
        setSpacing(20);
        getStyleClass().add("view-pane");

        Label titulo = new Label("Visão geral");
        titulo.getStyleClass().add("titulo-tela");

        HBox cartoes = new HBox(16);

        try {
            long totalAlunos = new AlunoDAO().listarTodos().size();
            long totalLivros = new LivroDAO().listarTodos().size();
            long totalAutores = new AutorDAO().listarTodos().size();
            long emprestimosAtivos = new EmprestimoLivroDAO().listarTodos().stream()
                    .filter(item -> item.getDataDevolucao() == null)
                    .count();

            cartoes.getChildren().addAll(
                    criarCartao("Alunos", String.valueOf(totalAlunos)),
                    criarCartao("Livros", String.valueOf(totalLivros)),
                    criarCartao("Autores", String.valueOf(totalAutores)),
                    criarCartao("Empréstimos ativos", String.valueOf(emprestimosAtivos))
            );
        } catch (Exception e) {
            Label erro = new Label("Não foi possível conectar ao banco de dados: " + e.getMessage());
            erro.getStyleClass().add("mensagem-erro");
            cartoes.getChildren().add(erro);
        }

        getChildren().addAll(titulo, cartoes);
    }

    private VBox criarCartao(String rotulo, String valor) {
        Label valorLabel = new Label(valor);
        valorLabel.getStyleClass().add("cartao-valor");

        Label rotuloLabel = new Label(rotulo);
        rotuloLabel.getStyleClass().add("cartao-rotulo");

        VBox cartao = new VBox(8, valorLabel, rotuloLabel);
        cartao.setAlignment(Pos.CENTER);
        cartao.getStyleClass().add("cartao");
        return cartao;
    }
}
