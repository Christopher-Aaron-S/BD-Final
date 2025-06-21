package MainApp;

import DBConnector.Connector;
import Entity.Mahasiswa;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DashboardController {

    @FXML private Label welcomeLabel;
    @FXML private HBox yourClubsPane;
    @FXML private VBox enrolledActivitiesPane;

    private MainViewController mainController;

    public void setMainController(MainViewController mainController) {
        this.mainController = mainController;
    }

    public void setUser(Mahasiswa user) {
        if (user != null) {
            String programName = loadProgramName(user.getIdProgram());
            welcomeLabel.setText(String.format("Welcome Back, %s\n%s %s", user.getNama(), programName, user.getNrp()));
            loadUserClubs(user.getNrp());
            loadUserActivities(user.getNrp());
        }
    }

    private String loadProgramName(int programId) {
        String programName = "Unknown Program";
        String sql = "SELECT nama_program FROM program WHERE id = ?";
        try (Connection conn = Connector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, programId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                programName = rs.getString("nama_program");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return programName;
    }

    private void loadUserClubs(String nrp) {
        yourClubsPane.getChildren().clear();
        String sql = "SELECT c.nama_club FROM club c " +
                "JOIN keanggotaan k ON c.id_club = k.id_club " +
                "WHERE k.nrp_mahasiswa = ? AND k.status = 'aktif'";

        try (Connection conn = Connector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nrp);
            ResultSet rs = pstmt.executeQuery();

            if (!rs.isBeforeFirst()) {
                yourClubsPane.getChildren().add(createJoinButton("Join Club", "clubs"));
            } else {
                while (rs.next()) {
                    String clubName = rs.getString("nama_club");
                    yourClubsPane.getChildren().add(createClubCard(clubName));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Label errorLabel = new Label("Failed to load club data. Check DB schema.");
            yourClubsPane.getChildren().add(errorLabel);
        }
    }

    private void loadUserActivities(String nrp) {
        enrolledActivitiesPane.getChildren().clear();
        // TODO: Ganti dengan logika pengecekan aktivitas yang sebenarnya
        if (true) {
            enrolledActivitiesPane.getChildren().add(createJoinButton("Join Activity", "activities"));
        }
    }

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

    private Button createJoinButton(String text, String view) {
        Button joinButton = new Button(text);
        joinButton.getStyleClass().add("join-card");
        Text plusIcon = new Text("+");
        plusIcon.setFont(Font.font("System", FontWeight.BOLD, 36));
        joinButton.setGraphic(plusIcon);
        joinButton.setOnAction(e -> {
            if (mainController != null) {
                if ("clubs".equals(view)) {
                    mainController.showClubs();
                } else if ("activities".equals(view)) {
                    mainController.showActivities();
                }
            }
        });
        return joinButton;
    }
}