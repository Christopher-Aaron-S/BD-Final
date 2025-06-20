package MainApp;

import Entity.Mahasiswa;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.Parent;

import java.io.IOException;

public class DashboardController {

    @FXML
    private Label nameLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private Label programLabel;

    public void setUser(Mahasiswa user) {
        nameLabel.setText("HI, " + user.getNama().toUpperCase());
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projectbd/login.fxml"));
            Parent loginRoot = loader.load();

            Scene loginScene = new Scene(loginRoot);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.setScene(loginScene);
            stage.setTitle("Login Page");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Placeholder agar tidak error jika tombol "forgot password" dipakai
    @FXML
    private void goToForgotPassword() {
        // Kosong, tapi harus ada untuk mencegah FXML error
    }

    @FXML
    private void goToSignup() {
        // Kosong, tapi harus ada jika FXML referensikan
    }
}
