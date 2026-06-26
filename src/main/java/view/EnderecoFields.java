package view;

import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import model.Endereco;

public class EnderecoFields {

    private final TextField campoCep = new TextField();
    private final TextField campoNumero = new TextField();
    private final TextField campoEstado = new TextField();
    private final TextField campoCidade = new TextField();
    private final TextField campoBairro = new TextField();
    private final TextField campoRua = new TextField();
    private final TextField campoComplemento = new TextField();

    private Endereco enderecoAtual;

    public void adicionarAoGrid(GridPane grid, int linhaInicial) {
        campoEstado.setPromptText("UF");
        campoEstado.setPrefWidth(60);

        UiUtils.addRow(grid, linhaInicial, "Rua", campoRua);
        UiUtils.addRow(grid, linhaInicial + 1, "Número", campoNumero);
        UiUtils.addRow(grid, linhaInicial + 2, "Bairro", campoBairro);
        UiUtils.addRow(grid, linhaInicial + 3, "Cidade", campoCidade);
        UiUtils.addRow(grid, linhaInicial + 4, "Estado", campoEstado);
        UiUtils.addRow(grid, linhaInicial + 5, "CEP", campoCep);
        UiUtils.addRow(grid, linhaInicial + 6, "Complemento", campoComplemento);
    }

    public Endereco getEndereco() throws Exception {
        if (campoRua.getText().isBlank() && campoCidade.getText().isBlank() && campoCep.getText().isBlank()) {
            return enderecoAtual;
        }
        Endereco endereco = enderecoAtual != null ? enderecoAtual : new Endereco();
        endereco.setRua(campoRua.getText());
        endereco.setNumero(campoNumero.getText());
        endereco.setBairro(campoBairro.getText());
        endereco.setCidade(campoCidade.getText());
        endereco.setEstado(campoEstado.getText());
        endereco.setCep(campoCep.getText());
        endereco.setComplemento(campoComplemento.getText());
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        enderecoAtual = endereco;
        if (endereco == null) {
            limpar();
            return;
        }
        campoRua.setText(endereco.getRua());
        campoNumero.setText(endereco.getNumero());
        campoBairro.setText(endereco.getBairro());
        campoCidade.setText(endereco.getCidade());
        campoEstado.setText(endereco.getEstado());
        campoCep.setText(endereco.getCep());
        campoComplemento.setText(endereco.getComplemento());
    }

    public void limpar() {
        enderecoAtual = null;
        campoRua.clear();
        campoNumero.clear();
        campoBairro.clear();
        campoCidade.clear();
        campoEstado.clear();
        campoCep.clear();
        campoComplemento.clear();
    }
}
