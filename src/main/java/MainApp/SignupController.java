package MainApp;

import DBConnector.Connector;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class    SignupController implements Initializable {

    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private TextField nrpField;
    @FXML private PasswordField passwordField;
    @FXML private TextField visiblePasswordField;
    @FXML private ComboBox<String> programComboBox;
    @FXML private Button togglePasswordButton;

    @FXML private HBox errorBox;
    @FXML private Label errorMessageLabel;

    private boolean isPasswordVisible = false;
    private final ObservableList<String> programList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadPrograms();
        hideError();
        setupPasswordFields();
    }

    private void setupPasswordFields() {
        visiblePasswordField.setManaged(false);
        visiblePasswordField.setVisible(false);
    }

    private void loadPrograms() {
        try (Connection conn = Connector.getConnection()) {
            String query = "SELECT nama_program FROM program";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                programList.add(rs.getString("nama_program"));
            }

            programComboBox.setItems(programList);
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Failed to load program list.");
        }
    }

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
    private void handleSignup() {
        hideError();

        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim().toLowerCase();
        String nrp = nrpField.getText().trim();
        String password = isPasswordVisible ? visiblePasswordField.getText() : passwordField.getText();
        String selectedProgram = programComboBox.getValue();

        if (fullName.isEmpty() || email.isEmpty() || nrp.isEmpty() || password.isEmpty() || selectedProgram == null) {
            showError("Please fill in all fields.");
            return;
        }

        if (!email.endsWith("@john.petra.ac.id")) {
            showError("Email must end with @john.petra.ac.id");
            return;
        }

        if (password.length() < 8) {
            showError("Password must be at least 8 characters.");
            return;
        }

        try (Connection conn = Connector.getConnection()) {
            // Check if NRP already exists
            String checkQuery = "SELECT 1 FROM users WHERE nrp = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1, nrp);
            ResultSet checkResult = checkStmt.executeQuery();

            if (checkResult.next()) {
                showError("NRP already exists.");
                return;
            }

            // Get program ID
            String programQuery = "SELECT id FROM program WHERE nama_program = ?";
            PreparedStatement progStmt = conn.prepareStatement(programQuery);
            progStmt.setString(1, selectedProgram);
            ResultSet progResult = progStmt.executeQuery();

            if (!progResult.next()) {
                showError("Selected program not found.");
                return;
            }

            int programId = progResult.getInt("id");

            // Insert user
            String insertQuery = "INSERT INTO users (nrp, nama, email, password, id_program) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
            insertStmt.setString(1, nrp);
            insertStmt.setString(2, fullName);
            insertStmt.setString(3, email);
            insertStmt.setString(4, password);
            insertStmt.setInt(5, programId);
            insertStmt.executeUpdate();

            HelloApplication.setSuccessMessage("Sign up successful. Please log in.");
            HelloApplication.showLogin();

        } catch (SQLException e) {
            e.printStackTrace();
            showError("Sign up failed due to a database error.");
        } catch (IOException e) {
            e.printStackTrace();
            showError("Could not return to login page.");
        }
    }

    @FXML
    private void goToLogin() {
        try {
            HelloApplication.showLogin();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Could not go to login page.");
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
}
