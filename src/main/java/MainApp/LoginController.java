package MainApp;

import DBConnector.Connector;
import Entity.Mahasiswa;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField visiblePasswordField;
    @FXML private Button togglePasswordButton;

    @FXML private HBox errorBox;
    @FXML private Label errorMessageLabel;

    @FXML private HBox successBox;
    @FXML private Label successMessageLabel;

    private boolean isPasswordVisible = false;

    // ================================
    // INITIALIZATION
    // ================================
    @FXML
    public void initialize() {
        hideError();
        hideSuccess();

        // Tampilkan success message jika ada
        String success = HelloApplication.getSuccessMessage();
        if (success != null) {
            showSuccess(success);
            HelloApplication.clearSuccessMessage();
        }
    }

    // ================================
    // LOGIN HANDLER
    // ================================
    @FXML
    private void handleLogin() {
        hideError();
        hideSuccess();

        String user = usernameField.getText().toLowerCase();
        String pass = isPasswordVisible ? visiblePasswordField.getText() : passwordField.getText();

        if (user.isEmpty() || pass.isEmpty()) {
            showError("Username and password cannot be empty.");
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

                HelloApplication.showDashboard();
            } else {
                showError("Invalid username or password.");
            }

        } catch (SQLException e) {
            showError("Database error occurred.");
            e.printStackTrace();
        } catch (IOException e) {
            showError("Unable to open dashboard.");
            e.printStackTrace();
        }
    }

    // ================================
    // TOGGLE PASSWORD VISIBILITY
    // ================================
    @FXML
    private void togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible;

        if (isPasswordVisible) {
            visiblePasswordField.setText(passwordField.getText());
            visiblePasswordField.setVisible(true);
            visiblePasswordField.setManaged(true);

            passwordField.setVisible(false);
            passwordField.setManaged(false);

            togglePasswordButton.setText("🙈");
        } else {
            passwordField.setText(visiblePasswordField.getText());
            passwordField.setVisible(true);
            passwordField.setManaged(true);

            visiblePasswordField.setVisible(false);
            visiblePasswordField.setManaged(false);

            togglePasswordButton.setText("👁");
        }
    }

    // ================================
    // NAVIGATION
    // ================================
    @FXML
    private void goToSignup() {
        try {
            HelloApplication.showSignup();
        } catch (IOException e) {
            showError("Unable to open sign-up page.");
            e.printStackTrace();
        }
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

    // ================================
    // MESSAGE HANDLING
    // ================================
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
