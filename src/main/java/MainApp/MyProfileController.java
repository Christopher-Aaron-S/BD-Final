package MainApp;

import DBConnector.Connector;
import Entity.Mahasiswa;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MyProfileController {

    // FXML fields untuk tampilan baru
    @FXML private ScrollPane cardViewScrollPane;
    @FXML private FlowPane clubCardsFlowPane;
    @FXML private VBox detailsViewContainer;
    @FXML private Button backButton;
    @FXML private Label detailsClubNameLabel;
    @FXML private TableView<AbsenDisplay> attendanceTable;
    @FXML private TableColumn<AbsenDisplay, String> activityColumn;
    @FXML private TableColumn<AbsenDisplay, String> attendanceColumn;
    @FXML private TableColumn<AbsenDisplay, String> certificateColumn;

    private Mahasiswa currentUser;

    @FXML
    public void initialize() {
        this.currentUser = HelloApplication.getLoggedInUser();
        if (currentUser != null) {
            setupTableColumns();
            loadUserClubsAsCards();
        } else {
            clubCardsFlowPane.getChildren().add(new Label("Silakan login untuk melihat profil Anda."));
        }
    }

    private void setupTableColumns() {
        activityColumn.setCellValueFactory(cellData -> cellData.getValue().namaKegiatanProperty());
        attendanceColumn.setCellValueFactory(cellData -> cellData.getValue().statusKehadiranProperty());
        certificateColumn.setCellValueFactory(cellData -> cellData.getValue().statusSertifikatProperty());
    }

    private void loadUserClubsAsCards() {
        clubCardsFlowPane.getChildren().clear();
        String clubsQuery = "SELECT c.id_club, c.nama_club FROM club c " +
                "JOIN keanggotaan k ON c.id_club = k.id_club " +
                "WHERE k.id_mahasiswa = ?";

        try (Connection conn = Connector.getConnection();
             PreparedStatement clubsStmt = conn.prepareStatement(clubsQuery)) {
            clubsStmt.setString(1, currentUser.getNrp());
            ResultSet clubsRs = clubsStmt.executeQuery();

            while (clubsRs.next()) {
                int idClub = clubsRs.getInt("id_club");
                String namaClub = clubsRs.getString("nama_club");
                VBox card = createClubCard(idClub, namaClub);
                clubCardsFlowPane.getChildren().add(card);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            clubCardsFlowPane.getChildren().add(new Label("Gagal memuat data klub."));
        }
    }

    private VBox createClubCard(int idClub, String namaClub) {
        VBox card = new VBox(10);
        card.setPrefSize(180, 120);
        card.setAlignment(Pos.CENTER);
        card.setStyle("-fx-background-color: #6a0dad; -fx-border-color: #e0e0e0; -fx-border-radius: 15; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 5);");

        Label clubNameLabel = new Label(namaClub);
        clubNameLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        clubNameLabel.setStyle("-fx-text-fill: white;");
        clubNameLabel.setWrapText(true);

        Button detailsButton = new Button("Lihat Detail");
        detailsButton.setOnAction(e -> showClubDetails(idClub, namaClub));

        detailsButton.getStyleClass().add("details-button-purple");

        card.getChildren().addAll(clubNameLabel, detailsButton);
        return card;
    }

    private void showClubDetails(int idClub, String namaClub) {
        cardViewScrollPane.setVisible(false);
        detailsViewContainer.setVisible(true);

        detailsClubNameLabel.setText("Riwayat Kehadiran untuk Klub: " + namaClub);
        populateAttendanceTable(idClub);
    }

    @FXML
    private void handleBackButtonAction() {
        detailsViewContainer.setVisible(false); // Sembunyikan tampilan detail
        cardViewScrollPane.setVisible(true); // Tampilkan kembali tampilan kartu
    }

    private void populateAttendanceTable(int idClub) {
        attendanceTable.getItems().clear();
        String attendanceQuery = "SELECT kc.nama_kegiatan, a.status_kehadiran FROM kegiatan_club kc " +
                "LEFT JOIN absen a ON kc.id = a.id_kegiatan_club AND a.id_mahasiswa = ? " +
                "WHERE kc.id_club = ?";

        try (Connection conn = Connector.getConnection();
             PreparedStatement attendanceStmt = conn.prepareStatement(attendanceQuery)) {
            attendanceStmt.setString(1, currentUser.getNrp());
            attendanceStmt.setInt(2, idClub);
            ResultSet rs = attendanceStmt.executeQuery();

            while(rs.next()) {
                String namaKegiatan = rs.getString("nama_kegiatan");
                String statusKehadiran = rs.getString("status_kehadiran");

                if (statusKehadiran == null) statusKehadiran = "Belum Absen";
                String statusSertifikat = "Hadir".equalsIgnoreCase(statusKehadiran) ? "Dapat" : "Tidak Dapat";

                attendanceTable.getItems().add(new AbsenDisplay(namaKegiatan, statusKehadiran, statusSertifikat));
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    // Kelas helper untuk menampilkan data di TableView
    public static class AbsenDisplay {
        private final SimpleStringProperty namaKegiatan;
        private final SimpleStringProperty statusKehadiran;
        private final SimpleStringProperty statusSertifikat;

        public AbsenDisplay(String namaKegiatan, String statusKehadiran, String statusSertifikat) {
            this.namaKegiatan = new SimpleStringProperty(namaKegiatan);
            this.statusKehadiran = new SimpleStringProperty(statusKehadiran);
            this.statusSertifikat = new SimpleStringProperty(statusSertifikat);
        }

        public String getNamaKegiatan() { return namaKegiatan.get(); }
        public SimpleStringProperty namaKegiatanProperty() { return namaKegiatan; }
        public String getStatusKehadiran() { return statusKehadiran.get(); }
        public SimpleStringProperty statusKehadiranProperty() { return statusKehadiran; }
        public String getStatusSertifikat() { return statusSertifikat.get(); }
        public SimpleStringProperty statusSertifikatProperty() { return statusSertifikat; }
    }
}