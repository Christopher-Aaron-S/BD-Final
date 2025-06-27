package MainApp;

import DBConnector.Connector;
import Entity.Mahasiswa;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SettingsController {

    @FXML private TextField usernameField;
    @FXML private Button saveUsernameButton;
    @FXML private Label usernameStatusLabel;

    @FXML private TextField emailField;
    @FXML private Button saveEmailButton;
    @FXML private Label emailStatusLabel;

    @FXML private PasswordField currentPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button savePasswordButton;
    @FXML private Label passwordStatusLabel;

    private Mahasiswa currentUser;

    @FXML
    public void initialize() {
        this.currentUser = HelloApplication.getLoggedInUser();
        if (currentUser != null) {
            loadUserData();
        }
    }

    private void loadUserData() {
        usernameField.setText(currentUser.getNama());
        emailField.setText(currentUser.getEmail());
    }

    @FXML
    private void handleSaveUsername() {
        String newUsername = usernameField.getText().trim();
        if (newUsername.isEmpty()) {
            usernameStatusLabel.setText("Username tidak boleh kosong.");
            return;
        }

        String sql = "UPDATE users SET nama = ? WHERE nrp = ?";
        try (Connection conn = Connector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newUsername);
            pstmt.setString(2, currentUser.getNrp());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                usernameStatusLabel.setText("Username berhasil diubah.");
                // Update user object
                currentUser = new Mahasiswa(currentUser.getNrp(), newUsername, currentUser.getEmail(), currentUser.getIdProgram());
                HelloApplication.setLoggedInUser(currentUser);
            } else {
                usernameStatusLabel.setText("Gagal mengubah username.");
            }
        } catch (SQLException e) {
            usernameStatusLabel.setText("Error database: " + e.getMessage());
        }
    }

    @FXML
    private void handleSaveEmail() {
        String newEmail = emailField.getText().trim();
        if (newEmail.isEmpty() || !newEmail.endsWith("@john.petra.ac.id")) {
            emailStatusLabel.setText("Email tidak valid.");
            return;
        }

        String sql = "UPDATE users SET email = ? WHERE nrp = ?";
        try (Connection conn = Connector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newEmail);
            pstmt.setString(2, currentUser.getNrp());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                emailStatusLabel.setText("Email berhasil diubah.");
                // Update user object
                currentUser = new Mahasiswa(currentUser.getNrp(), currentUser.getNama(), newEmail, currentUser.getIdProgram());
                HelloApplication.setLoggedInUser(currentUser);
            } else {
                emailStatusLabel.setText("Gagal mengubah email.");
            }
        } catch (SQLException e) {
            emailStatusLabel.setText("Error database: " + e.getMessage());
        }
    }

    @FXML
    private void handleSavePassword() {
        String currentPassword = currentPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            passwordStatusLabel.setText("Semua field password harus diisi.");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            passwordStatusLabel.setText("Password baru tidak cocok.");
            return;
        }

        // Verify current password
        String sqlVerify = "SELECT password FROM users WHERE nrp = ?";
        try (Connection conn = Connector.getConnection();
             PreparedStatement pstmtVerify = conn.prepareStatement(sqlVerify)) {
            pstmtVerify.setString(1, currentUser.getNrp());
            ResultSet rs = pstmtVerify.executeQuery();
            if (rs.next()) {
                String dbPassword = rs.getString("password");
                if (!dbPassword.equals(currentPassword)) {
                    passwordStatusLabel.setText("Password lama salah.");
                    return;
                }
            }
        } catch (SQLException e) {
            passwordStatusLabel.setText("Error database: " + e.getMessage());
            return;
        }

        // Update password
        String sqlUpdate = "UPDATE users SET password = ? WHERE nrp = ?";
        try (Connection conn = Connector.getConnection();
             PreparedStatement pstmtUpdate = conn.prepareStatement(sqlUpdate)) {
            pstmtUpdate.setString(1, newPassword);
            pstmtUpdate.setString(2, currentUser.getNrp());
            int affectedRows = pstmtUpdate.executeUpdate();
            if (affectedRows > 0) {
                passwordStatusLabel.setText("Password berhasil diubah.");
                currentPasswordField.clear();
                newPasswordField.clear();
                confirmPasswordField.clear();
            } else {
                passwordStatusLabel.setText("Gagal mengubah password.");
            }
        } catch (SQLException e) {
            passwordStatusLabel.setText("Error database: " + e.getMessage());
        }
    }
}