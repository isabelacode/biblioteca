package view;

import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.Node;

import java.util.Optional;

public class UiUtils {

    private UiUtils() {
    }

    public static void erro(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR, mensagem);
        alert.setHeaderText("Ocorreu um erro");
        alert.showAndWait();
    }

    public static void info(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, mensagem);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    public static boolean confirmar(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, mensagem, ButtonType.YES, ButtonType.NO);
        alert.setHeaderText("Confirmação");
        Optional<ButtonType> resultado = alert.showAndWait();
        return resultado.isPresent() && resultado.get() == ButtonType.YES;
    }

    public static void addRow(GridPane grid, int row, String rotulo, Node campo) {
        Label label = new Label(rotulo);
        label.getStyleClass().add("form-label");
        grid.add(label, 0, row);
        grid.add(campo, 1, row);
    }

    public static GridPane criarGridFormulario() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(16));
        grid.getStyleClass().add("form-grid");
        return grid;
    }
}
