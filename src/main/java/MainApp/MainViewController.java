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

    @FXML
    public void initialize() {
        // Inisialisasi daftar tombol sidebar
        sidebarButtons = Arrays.asList(dashboardButton, clubsButton, activitiesButton, myProfileButton, settingsButton);
        // Tampilkan dashboard saat pertama kali dibuka
        if (currentUser == null) {
            showDashboard();
        }
    }

    public void setUser(Mahasiswa user) {
        this.currentUser = user;
        // Setelah user di-set, tampilkan dashboard sebagai halaman awal
        showDashboard();
    }

    private void updateActiveButton(Button selectedButton) {
        // Hapus kelas 'active' dari semua tombol
        for (Button button : sidebarButtons) {
            if (button != null) {
                button.getStyleClass().remove("active");
            }
        }
        // Tambahkan kelas 'active' hanya pada tombol yang dipilih
        if (selectedButton != null) {
            selectedButton.getStyleClass().add("active");
        }
    }

    @FXML
    public void showDashboard() {
        loadPage("/com/example/projectbd/dashboard.fxml");
        // PERBAIKAN: Pastikan tombol yang benar (dashboardButton) diaktifkan
        updateActiveButton(dashboardButton);
    }

    @FXML
    public void showClubs() {
        loadPage("/com/example/projectbd/ClubView.fxml");
        // PERBAIKAN: Pastikan tombol yang benar (clubsButton) diaktifkan
        updateActiveButton(clubsButton);
    }

    @FXML
    public void showActivities() {
        loadPage("/com/example/projectbd/ActivitiesView.fxml");
        // PERBAIKAN: Pastikan tombol yang benar (activitiesButton) diaktifkan
        updateActiveButton(activitiesButton);
    }

    @FXML
    private void showMyProfile() {
        System.out.println("Navigasi ke Halaman My Profile (belum diimplementasikan)");
        // PERBAIKAN: Pastikan tombol yang benar (myProfileButton) diaktifkan
        updateActiveButton(myProfileButton);
    }

    @FXML
    private void showSettings() {
        loadPage("/com/example/projectbd/Settings.fxml");
        // PERBAIKAN: Pastikan tombol yang benar (settingsButton) diaktifkan
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
            } else if (fxmlPath.contains("ClubView.fxml")) {
                ClubsController controller = loader.getController();
                controller.setMainController(this);
            } else if (fxmlPath.contains("Settings.fxml")) {
                // Tidak ada setup khusus untuk SettingsController saat ini
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