package MainApp;

import DBConnector.Connector;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Forgotpass {

    @FXML private TextField usernameField;

    // New Password Fields
    @FXML private PasswordField passwordField;
    @FXML private TextField visiblePasswordField;
    @FXML private Button togglePasswordButton;

    // Confirm Password Fields
    @FXML private PasswordField confirmPasswordField;
    @FXML private TextField visibleConfirmPasswordField;
    @FXML private Button toggleConfirmPasswordButton;

    // Error Message Box
    @FXML private HBox errorBox;
    @FXML private Label errorMessageLabel;
    private boolean isPasswordVisible = false;

    // ========================== TOGGLE PASSWORD ===========================

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

    @FXML
    private void toggleConfirmPasswordVisibility() {
        isPasswordVisible = !isPasswordVisible;

        if (isPasswordVisible) {
            visibleConfirmPasswordField.setText(passwordField.getText());
            visibleConfirmPasswordField.setVisible(true);
            visibleConfirmPasswordField.setManaged(true);
            confirmPasswordField.setVisible(false);
            confirmPasswordField.setManaged(false);
            toggleConfirmPasswordButton.setText("🙈");
        } else {
            confirmPasswordField.setText(visiblePasswordField.getText());
            confirmPasswordField.setVisible(true);
            confirmPasswordField.setManaged(true);
            visibleConfirmPasswordField.setVisible(false);
            visibleConfirmPasswordField.setManaged(false);
            toggleConfirmPasswordButton.setText("👁");
        }
    }

    // ========================== HANDLE RESET =============================

    @FXML
    private void handleResetPassword() {
        String username = usernameField.getText().toLowerCase();
        String newPassword = passwordField.isVisible() ? passwordField.getText() : visiblePasswordField.getText();
        String confirmPassword = confirmPasswordField.isVisible() ? confirmPasswordField.getText() : visibleConfirmPasswordField.getText();

        // Validation
        if (username.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showError("All fields must be filled.");
            return;
        }

        if (newPassword.length() < 8) {
            showError("Password must be at least 8 characters.");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showError("Passwords do not match.");
            return;
        }

        // Update Password in DB
        try (Connection conn = Connector.getConnection()) {
            String checkUserQuery = "SELECT * FROM users WHERE nrp = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkUserQuery);
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next()) {
                showError("Username not found.");
                return;
            }

            String updateQuery = "UPDATE users SET password = ? WHERE nrp = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
            updateStmt.setString(1, newPassword);
            updateStmt.setString(2, username);
            updateStmt.executeUpdate();

            // Simpan status ke HelloApplication agar bisa ditampilkan di login
            HelloApplication.setSuccessMessage("Password reset successful. Please log in.");

            HelloApplication.showLogin();  // Kembali ke login

        } catch (Exception e) {
            e.printStackTrace();
            showError("Database error: " + e.getMessage());
        }
    }

    // ========================== ERROR HANDLING =======================

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

    // ========================== NAVIGASI ==============================

    @FXML
    private void goToLogin() {
        try {
            HelloApplication.showLogin();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
