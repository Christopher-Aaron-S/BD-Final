package MainApp;

import Entity.Mahasiswa;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {

    public static Stage currentStage;

    private static Mahasiswa loggedInUser;
    private static String successMessage = null;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/com/example/projectbd/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();

        currentStage = stage;

        // Tampilkan pesan sukses jika ada
        LoginController controller = fxmlLoader.getController();
        if (successMessage != null) {
            controller.showSuccess(successMessage);
            clearSuccessMessage();
        }
    }

    public static void showLogin() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/com/example/projectbd/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        // Ambil controller login
        LoginController controller = fxmlLoader.getController();

        // Jika ada pesan sukses, tampilkan
        if (successMessage != null) {
            controller.showSuccess(successMessage);
            clearSuccessMessage();
        }

        currentStage.setScene(scene);
        currentStage.setTitle("Login");
    }

    public static void showSignup() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/com/example/projectbd/signup.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        currentStage.setScene(scene);
        currentStage.setTitle("Sign Up");
    }

    public static void showForgot() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/com/example/projectbd/forgotpass.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        currentStage.setScene(scene);
        currentStage.setTitle("Reset Your Password");
    }

    public static void showMainView() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/com/example/projectbd/MainView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        // Kirim data user yang login ke MainViewController
        MainViewController controller = fxmlLoader.getController();
        controller.setUser(getLoggedInUser());

        currentStage.setScene(scene);
        currentStage.setTitle("Student Club Portal");
    }

    // -------------------------------
    // Helper untuk user login & pesan sukses
    // -------------------------------

    public static void setLoggedInUser(Mahasiswa user) {
        loggedInUser = user;
    }

    public static Mahasiswa getLoggedInUser() {
        return loggedInUser;
    }

    public static void setSuccessMessage(String message) {
        successMessage = message;
    }

    public static String getSuccessMessage() {
        return successMessage;
    }

    public static void clearSuccessMessage() {
        successMessage = null;
    }

    public static void main(String[] args) {
        launch();
    }
}
