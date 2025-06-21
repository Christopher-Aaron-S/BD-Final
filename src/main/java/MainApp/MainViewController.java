package MainApp;

import Entity.Mahasiswa;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class MainViewController {

    @FXML
    private StackPane contentArea;
    private Mahasiswa currentUser;

    public void initialize() {
        showDashboard();
    }

    public void setUser(Mahasiswa user) {
        this.currentUser = user;
        showDashboard();
    }

    @FXML
    public void showDashboard() {
        loadPage("/com/example/projectbd/dashboard.fxml");
    }

    @FXML
    public void showClubs() {
        loadPage("/com/example/projectbd/ClubsView.fxml");
    }

    @FXML
    public void showActivities() {
        loadPage("/com/example/projectbd/ActivitiesView.fxml");
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

            if (fxmlPath.equals("/com/example/projectbd/dashboard.fxml")) {
                DashboardController controller = loader.getController();
                controller.setUser(currentUser);
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