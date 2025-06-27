package MainApp;

import DBConnector.Connector;
import Entity.Mahasiswa;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

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

    @FXML
    public void initialize() {
        hideError();
        hideSuccess();
    }

    @FXML
    private void handleLogin() {
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
                String role = rs.getString("role"); // ambil role dari database

                Mahasiswa loggedInUser = new Mahasiswa(user, nama, email, idProgram);
                HelloApplication.setLoggedInUser(loggedInUser);

                if ("admin".equalsIgnoreCase(role)) {
                    HelloApplication.showAdminPage(); // arahkan ke AdminPage
                } else {
                    HelloApplication.showMainView(); // arahkan ke halaman user biasa
                }
            }
            else {
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

    @FXML
    private void goToSignup() throws IOException {
        HelloApplication.showSignup();
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
        errorMessageLabel.setText(message);
        errorBox.setVisible(true);
        errorBox.setManaged(true);
    }

    @FXML
    private void hideError() {
        errorBox.setVisible(false);
        errorBox.setManaged(false);
    }

    public void showSuccess(String message) {
        successMessageLabel.setText(message);
        successBox.setVisible(true);
        successBox.setManaged(true);
    }

    @FXML
    private void hideSuccess() {
        successBox.setVisible(false);
        successBox.setManaged(false);
    }
}