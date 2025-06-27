package MainApp;

import DBConnector.Connector;
import Entity.Mahasiswa;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class ActivitiesController implements Initializable {

    @FXML private ScrollPane scrollPane;
    @FXML private FlowPane activityFlowPane;

    private Mahasiswa currentUser;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.currentUser = HelloApplication.getLoggedInUser();
        if (currentUser != null) {
            loadUserActivities();
        } else {
            // Tampilkan pesan jika user tidak ditemukan
            activityFlowPane.getChildren().add(new Label("Silakan login untuk melihat kegiatan Anda."));
        }
    }

    private void loadUserActivities() {
        activityFlowPane.getChildren().clear();

        // Query untuk mengambil kegiatan HANYA dari klub yang user telah masuki
        String sql = "SELECT kc.nama_kegiatan, kc.tanggal_kegiatan, c.nama_club " +
                "FROM kegiatan_club kc " +
                "JOIN club c ON kc.id_club = c.id_club " +
                "JOIN keanggotaan k ON kc.id_club = k.id_club " +
                "WHERE k.id_mahasiswa = ? " + // Menggunakan NRP (id_mahasiswa) dari user yang login
                "ORDER BY kc.tanggal_kegiatan DESC";

        try (Connection conn = Connector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, currentUser.getNrp());
            ResultSet rs = pstmt.executeQuery();

            if (!rs.isBeforeFirst()) {
                activityFlowPane.getChildren().add(new Label("Anda belum memiliki jadwal kegiatan dari klub manapun."));
            }

            while (rs.next()) {
                String namaKegiatan = rs.getString("nama_kegiatan");
                LocalDate tanggal = rs.getDate("tanggal_kegiatan").toLocalDate();
                String namaClub = rs.getString("nama_club");

                VBox activityCard = createActivityCard(namaKegiatan, tanggal, namaClub);
                activityFlowPane.getChildren().add(activityCard);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            activityFlowPane.getChildren().add(new Label("Gagal memuat data kegiatan."));
        }
    }

    private VBox createActivityCard(String namaKegiatan, LocalDate tanggal, String namaClub) {
        VBox card = new VBox(10);
        card.getStyleClass().add("activity-grid-card");
        card.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label(namaKegiatan);
        titleLabel.getStyleClass().add("activity-card-title");
        titleLabel.setWrapText(true);

        // Format tanggal agar lebih mudah dibaca
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
        Label dateLabel = new Label("Tanggal: " + tanggal.format(formatter));
        dateLabel.getStyleClass().add("activity-card-details");

        Label clubLabel = new Label("Dari: " + namaClub);
        clubLabel.getStyleClass().add("activity-card-details");

        card.getChildren().addAll(titleLabel, dateLabel, clubLabel);
        return card;
    }
}