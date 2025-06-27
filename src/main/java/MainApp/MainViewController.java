package MainApp;

import Entity.Mahasiswa;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class MainViewController {

    @FXML
    private StackPane contentArea;
    private Mahasiswa currentUser;

    @FXML private Button dashboardButton;
    @FXML private Button clubsButton;
    @FXML private Button activitiesButton;
    @FXML private Button myProfileButton;
    @FXML private Button settingsButton;

    private List<Button> sidebarButtons;
    private DashboardController dashboardController;

    public void refreshDashboardData() {
        if (dashboardController != null && currentUser != null) {
            dashboardController.loadUserClubs(currentUser.getNrp());
        }
    }

    public void initialize() {
        sidebarButtons = Arrays.asList(dashboardButton, clubsButton, activitiesButton, myProfileButton, settingsButton);
        showDashboard();
    }

    public void setUser(Mahasiswa user) {
        this.currentUser = user;
        showDashboard();
    }

    private void updateActiveButton(Button selectedButton) {
        for (Button button : sidebarButtons) {
            if (button != null) {
                button.getStyleClass().remove("active");
            }
        }
        if (selectedButton != null) {
            selectedButton.getStyleClass().add("active");
            selectedButton.requestFocus();
        }
    }

    @FXML
    public void showDashboard() {
        loadPage("/com/example/projectbd/dashboard.fxml");
        updateActiveButton(dashboardButton);
    }

    @FXML
    public void showClubs() {
        // --- PERBAIKAN DI SINI ---
        loadPage("/com/example/projectbd/ClubView.fxml");
        updateActiveButton(clubsButton);
    }

    @FXML
    public void showActivities() {
        loadPage("/com/example/projectbd/ActivitiesView.fxml");
        updateActiveButton(activitiesButton);
    }

    @FXML
    private void showMyProfile() {
        System.out.println("Navigasi ke Halaman My Profile (belum diimplementasikan)");
        updateActiveButton(myProfileButton);
    }

    @FXML
    private void showSettings() {
        System.out.println("Navigasi ke Halaman Settings (belum diimplementasikan)");
        updateActiveButton(settingsButton);
    }

    private void loadPage(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            if (fxmlPath.contains("dashboard.fxml")) {
                this.dashboardController = loader.getController();
                dashboardController.setUser(currentUser);
                dashboardController.setMainController(this);
            } else if (fxmlPath.contains("ClubView.fxml")) { // Sekarang kondisi ini akan terpenuhi
                ClubsController controller = loader.getController();
                controller.setMainController(this);
            }

            contentArea.getChildren().setAll(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) throws IOException {
        HelloApplication.showLogin();
    }
}