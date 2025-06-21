package MainApp;

import DBConnector.Connector;
import Entity.Mahasiswa;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private HBox errorBox;
    @FXML private Label errorMessageLabel;
    @FXML private HBox successBox;
    @FXML private Label successMessageLabel;

    @FXML private StackPane container;

    @FXML
    private void toggleView() {
        if (container.getStyleClass().contains("active")) {
            container.getStyleClass().remove("active");
        } else {
            container.getStyleClass().add("active");
        }
    }

    @FXML
    public void initialize() {
        hideError();
        hideSuccess();

        // Tampilkan pesan sukses jika ada dari halaman lain
        String success = HelloApplication.getSuccessMessage();
        if (success != null && !success.isEmpty()) {
            showSuccess(success);
            HelloApplication.clearSuccessMessage();
        }
    }

    @FXML
    private void handleLogin() throws IOException {
        hideError();
        hideSuccess();

        String user = usernameField.getText().toLowerCase().trim();
        String pass = passwordField.getText();

        if (user.isEmpty() || pass.isEmpty()) {
            showError("NRP and password cannot be empty.");
            return;
        }

        try (Connection conn = Connector.getConnection()) {
            String query = "SELECT * FROM users WHERE nrp = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, user);
            stmt.setString(2, pass);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String nama = rs.getString("nama");
                String email = rs.getString("email");
                int idProgram = rs.getInt("id_program");

                Mahasiswa loggedInUser = new Mahasiswa(user, nama, email, idProgram);
                HelloApplication.setLoggedInUser(loggedInUser);
                HelloApplication.showMainView();
            } else {
                showError("Invalid NRP or password.");
            }
        } catch (SQLException e) {
            showError("Database error occurred.");
            e.printStackTrace();
        } catch (IOException e) {
            showError("Unable to open dashboard.");
            e.printStackTrace();
        }
    }

    // Arahkan ke SignupController untuk handle signup
    @FXML
    private void handleSignup() {
        // Logika signup ada di SignupController,
        // namun FXML ini bisa memanggil method di LoginController.
        // Sebaiknya logika signup dipisah sepenuhnya.
        // Untuk sementara, kita bisa panggil method dari SignupController jika digabungkan.
        System.out.println("Sign Up button clicked. Logic should be in SignupController.");
    }

    @FXML
    private void goToForgotPassword() {
        try {
            HelloApplication.showForgot();
        } catch (IOException e) {
            showError("Unable to open forgot password page.");
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        if (errorMessageLabel != null) {
            errorMessageLabel.setText(message);
            errorBox.setVisible(true);
            errorBox.setManaged(true);
        }
    }

    @FXML
    private void hideError() {
        if (errorBox != null) {
            errorBox.setVisible(false);
            errorBox.setManaged(false);
        }
    }

    public void showSuccess(String message) {
        if (successMessageLabel != null) {
            successMessageLabel.setText(message);
            successBox.setVisible(true);
            successBox.setManaged(true);
        }
    }

    @FXML
    private void hideSuccess() {
        if (successBox != null) {
            successBox.setVisible(false);
            successBox.setManaged(false);
        }
    }
}