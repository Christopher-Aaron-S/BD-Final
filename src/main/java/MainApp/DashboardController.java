package MainApp;

import DBConnector.Connector;
import Entity.Mahasiswa;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DashboardController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private HBox yourClubsPane;

    @FXML
    private VBox enrolledActivitiesPane;

    public void setUser(Mahasiswa user) {
        if (user != null) {
            welcomeLabel.setText("Welcome back, " + user.getNama());
            loadUserClubs(user.getNrp());
            loadUserActivities(user.getNrp());
        }
    }

    private void loadUserClubs(String nrp) {
        yourClubsPane.getChildren().clear(); // Bersihkan data lama
        String sql = "SELECT c.nama_club FROM club c " +
                "JOIN keanggotaan k ON c.id_club = k.id_club " +
                "WHERE k.nrp = ? AND k.status = 'aktif'";

        try (Connection conn = Connector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nrp);
            ResultSet rs = pstmt.executeQuery();

            if (!rs.isBeforeFirst()) { // Cek jika tidak ada data
                Label noClubLabel = new Label("You haven't joined any clubs yet.");
                yourClubsPane.getChildren().add(noClubLabel);
            } else {
                while (rs.next()) {
                    String clubName = rs.getString("nama_club");
                    yourClubsPane.getChildren().add(createClubCard(clubName));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Label errorLabel = new Label("Failed to load club data.");
            yourClubsPane.getChildren().add(errorLabel);
        }
    }

    private void loadUserActivities(String nrp) {
        // Mirip dengan loadUserClubs, Anda bisa implementasikan logika
        // untuk mengambil data kegiatan yang diikuti dari database.
        enrolledActivitiesPane.getChildren().clear();
        Label placeholder = new Label("No upcoming activities.");
        enrolledActivitiesPane.getChildren().add(placeholder);
    }

    // Helper untuk membuat card club secara dinamis
    private VBox createClubCard(String clubName) {
        VBox card = new VBox(5);
        card.setPadding(new Insets(15));
        card.setPrefSize(180, 100);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 0, 2);");

        Label nameLabel = new Label(clubName);
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 14));

        card.getChildren().add(nameLabel);
        return card;
    }
}