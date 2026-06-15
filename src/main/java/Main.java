import database.Conexao;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Main extends Application {

    @Override
    public void start(Stage stage) {

        TextArea area = new TextArea();
        area.setEditable(false);

        StringBuilder sb = new StringBuilder();

        String sql = "SELECT id_aluno, nome, email FROM aluno";

        try (
                Connection conn = Conexao.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()
        ) {

            while (rs.next()) {
                sb.append(rs.getInt("id_aluno"))
                        .append(" | ")
                        .append(rs.getString("nome"))
                        .append(" | ")
                        .append(rs.getString("email"))
                        .append("\n");
            }

        } catch (Exception e) {
            sb.append("Erro: ").append(e.getMessage());
        }

        area.setText(sb.toString());

        VBox root = new VBox(area);

        Scene scene = new Scene(root, 600, 400);

        stage.setTitle("Biblioteca - Alunos");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}