package MainApp;

import DBConnector.Connector;
import Entity.Club;
import Entity.Kategori;
import Entity.Mahasiswa;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.control.DialogPane;

import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ClubsController implements Initializable {

    @FXML private ScrollPane scrollPane;
    @FXML private FlowPane clubFlowPane;
    @FXML private VBox popupContainer;
    @FXML private Label popupClubName;
    @FXML private Text popupClubDescription;

    private Club selectedClub;
    private MainViewController mainController;

    public void setMainController(MainViewController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (popupContainer != null) popupContainer.setVisible(false);
        loadClubsFromDB();
    }

    private void loadClubsFromDB() {
        if (clubFlowPane == null) {
            System.err.println("clubFlowPane belum diinisialisasi. Periksa FXML.");
            return;
        }

        clubFlowPane.getChildren().clear();
        Mahasiswa currentUser = HelloApplication.getLoggedInUser();

        if (currentUser == null) {
            System.err.println("User belum login.");
            return;
        }

        String query = "SELECT c.id_club, c.nama_club, c.deskripsi, c.tahun_berdiri, " +
                "cat.id AS kategori_id, cat.nama_kategori, " +
                "k.id_mahasiswa AS status_keanggotaan " +
                "FROM club c " +
                "JOIN kategori cat ON c.id_kategori = cat.id " +
                "LEFT JOIN keanggotaan k ON c.id_club = k.id_club AND k.id_mahasiswa = ? AND k.status = 'aktif'";

        try (Connection conn = Connector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, currentUser.getNrp());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Kategori kategori = new Kategori(rs.getInt("kategori_id"), rs.getString("nama_kategori"));
                    Club club = new Club(rs.getInt("id_club"), rs.getString("nama_club"), rs.getString("deskripsi"), rs.getInt("tahun_berdiri"), kategori);
                    boolean isMember = rs.getString("status_keanggotaan") != null;

                    VBox card = createClubCard(club, isMember);
                    clubFlowPane.getChildren().add(card);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private VBox createClubCard(Club club, boolean isMember) {
        VBox card = new VBox(15);
        card.getStyleClass().add("club-grid-card");
        card.setAlignment(Pos.CENTER);

        Label title = new Label(club.getNamaClub());
        title.getStyleClass().add("club-grid-title");

        Button joinButton = new Button("JOIN");
        joinButton.getStyleClass().add("club-grid-join-button");

        if (isMember) {
            joinButton.setText("JOINED");
            joinButton.setDisable(true);
            joinButton.getStyleClass().add("joined");
        } else {
            joinButton.setOnAction(e -> showJoinPopup(club));
        }

        card.getChildren().addAll(title, joinButton);
        return card;
    }

    private void showJoinPopup(Club club) {
        this.selectedClub = club;
        popupClubName.setText(club.getNamaClub());
        popupClubDescription.setText(club.getDeskripsi());
        popupContainer.setVisible(true);
    }

    @FXML
    private void hideJoinPopup() {
        popupContainer.setVisible(false);
        this.selectedClub = null;
    }

    @FXML
    private void handleJoinClub() {
        Mahasiswa currentUser = HelloApplication.getLoggedInUser();
        if (selectedClub == null || currentUser == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Klub atau user tidak ditemukan.");
            return;
        }

        String sql = "INSERT INTO keanggotaan (id_mahasiswa, id_club, tanggal_bergabung, status) VALUES (?, ?, ?, ?)";

        try (Connection conn = Connector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, currentUser.getNrp());
            stmt.setInt(2, selectedClub.getIdClub());
            stmt.setDate(3, new Date(System.currentTimeMillis()));
            stmt.setString(4, "aktif");

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Berhasil", "Anda bergabung dengan " + selectedClub.getNamaClub());
                loadClubsFromDB();
                if (mainController != null) mainController.refreshDashboardData();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error DB", "Gagal bergabung dengan klub.");
        } finally {
            hideJoinPopup();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);

        alert.setHeaderText(title);
        alert.setContentText(msg);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(
                getClass().getResource("/com/example/projectbd/styles.css").toExternalForm());
        dialogPane.getStyleClass().add("custom-alert");

        alert.showAndWait();
    }
}