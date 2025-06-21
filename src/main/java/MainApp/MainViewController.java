package MainApp;

import Entity.Mahasiswa;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class MainViewController {

    @FXML
    private StackPane contentArea;

    private Mahasiswa currentUser;

    public void initialize() {
        // Tampilkan dashboard sebagai default saat pertama kali login
        showDashboard();
    }

    public void setUser(Mahasiswa user) {
        this.currentUser = user;
        // Muat ulang dashboard untuk memastikan data user ter-update
        showDashboard();
    }

    @FXML
    private void showDashboard() {
        loadPage("/com/example/projectbd/dashboard.fxml");
    }

    @FXML
    private void showClubs() {
        // Anda akan membuat ClubsView.fxml nanti
        // loadPage("/com/example/projectbd/ClubsView.fxml");
        System.out.println("Navigasi ke Halaman Clubs (belum diimplementasikan)");
    }

    @FXML
    private void showActivities() {
        // Anda akan membuat ActivitiesView.fxml nanti
        // loadPage("/com/example/projectbd/ActivitiesView.fxml");
        System.out.println("Navigasi ke Halaman Activities (belum diimplementasikan)");
    }

    @FXML
    private void showMyProfile() {
        System.out.println("Navigasi ke Halaman My Profile (belum diimplementasikan)");
    }

    @FXML
    private void showSettings() {
        System.out.println("Navigasi ke Halaman Settings (belum diimplementasikan)");
    }

    private void loadPage(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // Jika controller halaman perlu data user, kirimkan di sini
            if (fxmlPath.equals("/com/example/projectbd/dashboard.fxml")) {
                DashboardController controller = loader.getController();
                controller.setUser(currentUser);
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