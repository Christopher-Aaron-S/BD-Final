package MainApp;

import Entity.Mahasiswa;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {

    public static Stage currentStage;
    private static Mahasiswa loggedInUser; // Untuk menyimpan user yang login
    private static String successMessage = null;


    @Override
    public void start(Stage stage) throws IOException {
        currentStage = stage;
        showLogin();
    }

    public static void showLogin() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/com/example/projectbd/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        LoginController controller = fxmlLoader.getController();
        if (successMessage != null) {
            controller.showSuccess(successMessage);
            clearSuccessMessage();
        }

        currentStage.setScene(scene);
        currentStage.setTitle("Login");
        currentStage.show();
    }

    public static void showSignup() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/com/example/projectbd/signup.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        currentStage.setScene(scene);
        currentStage.setTitle("Sign Up");
        currentStage.show(); // Tambahkan ini agar jendela terlihat
    }

    public static void showForgot() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/com/example/projectbd/forgotpass.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        currentStage.setScene(scene);
        currentStage.setTitle("Reset Your Password");
        currentStage.show();
    }

    public static void showMainView() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/com/example/projectbd/MainView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        MainViewController controller = fxmlLoader.getController();
        controller.setUser(getLoggedInUser());

        currentStage.setScene(scene);
        currentStage.setTitle("Student Club Portal");
        currentStage.show();
    }

    public static void showAdminPage() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/com/example/projectbd/AdminPage.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        // *** PENTING: Dapatkan controller dan set user-nya! ***
        AdminPageController controller = fxmlLoader.getController();
        if (getLoggedInUser() != null) {
            controller.setUser(getLoggedInUser()); // Panggil setUser di AdminPageController
        } else {
            System.err.println("Warning: Logged in user is null when showing Admin Page.");
            // Mungkin Anda ingin kembali ke halaman login jika user null
            showLogin();
            return;
        }

        currentStage.setScene(scene);
        currentStage.setTitle("Admin Page");
        currentStage.show();
    }
    

    public static void setLoggedInUser(Mahasiswa user) {
        loggedInUser = user;
    }

    public static Mahasiswa getLoggedInUser() {
        return loggedInUser;
    }

    public static void setSuccessMessage(String message) {
        successMessage = message;
    }

    public static void clearSuccessMessage() {
        successMessage = null;
    }

    public static void main(String[] args) {
        launch();
    }
}