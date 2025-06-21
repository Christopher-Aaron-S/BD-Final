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
    @FXML private PasswordField passwordField;
    @FXML private TextField visiblePasswordField;
    @FXML private Button togglePasswordButton;
    @FXML private PasswordField confirmPasswordField;
    @FXML private TextField visibleConfirmPasswordField;
    @FXML private Button toggleConfirmPasswordButton;
    @FXML private HBox errorBox;
    @FXML private Label errorMessageLabel;

    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;

    @FXML
    private void togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible;
        if (isPasswordVisible) {
            visiblePasswordField.setText(passwordField.getText());
            visiblePasswordField.setVisible(true);
            passwordField.setVisible(false);
            togglePasswordButton.setText("🙈");
        } else {
            passwordField.setText(visiblePasswordField.getText());
            passwordField.setVisible(true);
            visiblePasswordField.setVisible(false);
            togglePasswordButton.setText("👁");
        }
        visiblePasswordField.setManaged(isPasswordVisible);
        passwordField.setManaged(!isPasswordVisible);
    }

    @FXML
    private void toggleConfirmPasswordVisibility() {
        isConfirmPasswordVisible = !isConfirmPasswordVisible;
        if (isConfirmPasswordVisible) {
            visibleConfirmPasswordField.setText(confirmPasswordField.getText());
            visibleConfirmPasswordField.setVisible(true);
            confirmPasswordField.setVisible(false);
            toggleConfirmPasswordButton.setText("🙈");
        } else {
            confirmPasswordField.setText(visibleConfirmPasswordField.getText());
            confirmPasswordField.setVisible(true);
            visibleConfirmPasswordField.setVisible(false);
            toggleConfirmPasswordButton.setText("👁");
        }
        visibleConfirmPasswordField.setManaged(isConfirmPasswordVisible);
        confirmPasswordField.setManaged(!isConfirmPasswordVisible);
    }

    @FXML
    private void handleResetPassword() {
        String username = usernameField.getText().toLowerCase();
        String newPassword = passwordField.isVisible() ? passwordField.getText() : visiblePasswordField.getText();
        String confirmPassword = confirmPasswordField.isVisible() ? confirmPasswordField.getText() : visibleConfirmPasswordField.getText();

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

            HelloApplication.setSuccessMessage("Password reset successful. Please log in.");
            HelloApplication.showLogin();

        } catch (Exception e) {
            e.printStackTrace();
            showError("Database error: " + e.getMessage());
        }
    }

    private void showError(String message) {
        errorMessageLabel.setText(message);
        errorBox.setVisible(true);
        errorBox.setManaged(true);
    }

    @FXML
    private void goToLogin() {
        try {
            HelloApplication.showLogin();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}