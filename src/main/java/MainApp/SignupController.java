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

public class SignupController implements Initializable {

    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private TextField nrpField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> programComboBox;

    // Deklarasi FXML untuk semua label error
    @FXML private Label fullNameErrorLabel;
    @FXML private Label emailErrorLabel;
    @FXML private Label nrpErrorLabel;
    @FXML private Label passwordErrorLabel;
    @FXML private Label programErrorLabel;

    @FXML private HBox errorBox;
    @FXML private Label errorMessageLabel;

    private final ObservableList<String> programList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadPrograms();
        resetAllErrors();
    }

    private void showFieldError(Control field, Label label, String message) {
        label.setText(message);
        label.setVisible(true);
        label.setManaged(true);
        if (!field.getStyleClass().contains("text-field-error")) {
            field.getStyleClass().add("text-field-error");
        }
    }

    private void resetAllErrors() {
        // Sembunyikan dan non-aktifkan semua label error individual
        Label[] errorLabels = {fullNameErrorLabel, emailErrorLabel, nrpErrorLabel, passwordErrorLabel, programErrorLabel};
        for (Label label : errorLabels) {
            label.setVisible(false);
            label.setManaged(false);
        }

        // Hapus style class error dari semua input fields
        fullNameField.getStyleClass().remove("text-field-error");
        emailField.getStyleClass().remove("text-field-error");
        nrpField.getStyleClass().remove("text-field-error");
        passwordField.getStyleClass().remove("text-field-error");
        programComboBox.getStyleClass().remove("text-field-error");

        // Sembunyikan juga error box global
        errorBox.setVisible(false);
        errorBox.setManaged(false);
    }

    @FXML
    private void handleSignup() {
        resetAllErrors();
        boolean hasError = false;

        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim().toLowerCase();
        String nrp = nrpField.getText().trim();
        String password = passwordField.getText();
        String selectedProgram = programComboBox.getValue();

        // --- Validasi Per-Field ---
        if (fullName.isEmpty()) {
            showFieldError(fullNameField, fullNameErrorLabel, "Full name cannot be empty.");
            hasError = true;
        }
        if (email.isEmpty()) {
            showFieldError(emailField, emailErrorLabel, "Email cannot be empty.");
            hasError = true;
        } else if (!email.endsWith("@john.petra.ac.id")) {
            showFieldError(emailField, emailErrorLabel, "Invalid email, must use @john.petra.ac.id");
            hasError = true;
        }

        if (nrp.isEmpty()) {
            showFieldError(nrpField, nrpErrorLabel, "NRP cannot be empty.");
            hasError = true;
        } else if (nrp.length() != 9) {
            showFieldError(nrpField, nrpErrorLabel, "NRP must be exactly 9 characters.");
            hasError = true;
        } else if (!nrp.substring(0, 4).equals("c142")) {
            showFieldError(nrpField, nrpErrorLabel, "NRP Invalid");
            hasError = true;
        }
        if (password.isEmpty()) {
            showFieldError(passwordField, passwordErrorLabel, "Password cannot be empty.");
            hasError = true;
        } else if (password.length() < 8) {
            showFieldError(passwordField, passwordErrorLabel, "Password must be at least 8 characters.");
            hasError = true;
        }
        if (selectedProgram == null) {
            showFieldError(programComboBox, programErrorLabel, "Please select a program.");
            hasError = true;
        }

        if (hasError) {
            return;
        }

        // --- Proses ke Database ---
        try (Connection conn = Connector.getConnection()) {
            String checkQuery = "SELECT 1 FROM users WHERE nrp = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1, nrp);
            if (checkStmt.executeQuery().next()) {
                showFieldError(nrpField, nrpErrorLabel, "NRP already exists.");
                return;
            }

            String programQuery = "SELECT id FROM program WHERE nama_program = ?";
            PreparedStatement progStmt = conn.prepareStatement(programQuery);
            progStmt.setString(1, selectedProgram);
            ResultSet progResult = progStmt.executeQuery();
            if (!progResult.next()) {
                showFieldError(programComboBox, programErrorLabel, "Selected program not found.");
                return;
            }
            int programId = progResult.getInt("id");

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
            showGlobalError("Sign up failed due to a database error.");
        } catch (IOException e) {
            e.printStackTrace();
            showGlobalError("Could not return to login page.");
        }
    }

    @FXML
    private void goToLogin() {
        try {
            HelloApplication.showLogin();
        } catch (IOException e) {
            e.printStackTrace();
            showGlobalError("Could not go to login page.");
        }
    }

    private void showGlobalError(String message) {
        errorMessageLabel.setText(message);
        errorBox.setVisible(true);
        errorBox.setManaged(true);
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
            showGlobalError("Failed to load program list.");
        }
    }
}