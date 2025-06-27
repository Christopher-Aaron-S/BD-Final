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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
// import java.util.UUID; // Tidak lagi dibutuhkan

public class ClubsController implements Initializable {

    @FXML private ScrollPane scrollPane;
    @FXML private HBox clubHBox;
    @FXML private Button leftButton;
    @FXML private Button rightButton;
    @FXML private VBox popupContainer;
    @FXML private Label popupClubName;
    @FXML private Text popupClubDescription;

    private Club selectedClub;
    private MainViewController mainController;

    private final double CARD_WIDTH = 300;
    private final double CARD_HEIGHT = 450;

    public void setMainController(MainViewController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (popupContainer != null) popupContainer.setVisible(false);
        loadClubsFromDB();
    }

    private void loadClubsFromDB() {
        if (clubHBox == null) {
            System.err.println("clubHBox belum diinisialisasi. Periksa FXML.");
            return;
        }

        clubHBox.getChildren().clear();
        Mahasiswa currentUser = HelloApplication.getLoggedInUser();

        if (currentUser == null) {
            System.err.println("User belum login.");
            return;
        }

        System.out.println("NRP saat ini: " + currentUser.getNrp());

        String query = """
            SELECT c.id_club, c.nama_club, c.deskripsi, c.tahun_berdiri,
                   cat.id AS kategori_id, cat.nama_kategori,
                   k.id_mahasiswa AS status_keanggotaan
            FROM club c
            JOIN kategori cat ON c.id_kategori = cat.id
            LEFT JOIN keanggotaan k ON c.id_club = k.id_club AND k.id_mahasiswa = ?
            """;

        try (Connection conn = Connector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, currentUser.getNrp());

            try (ResultSet rs = stmt.executeQuery()) {
                int count = 0;
                while (rs.next()) {
                    count++;
                    Kategori kategori = new Kategori(
                            rs.getInt("kategori_id"),
                            rs.getString("nama_kategori")
                    );

                    Club club = new Club(
                            rs.getInt("id_club"),
                            rs.getString("nama_club"),
                            rs.getString("deskripsi"),
                            rs.getInt("tahun_berdiri"),
                            kategori
                    );

                    boolean isMember = rs.getString("status_keanggotaan") != null;
                    StackPane card = createClubCard(club, isMember);
                    clubHBox.getChildren().add(card);
                }
                System.out.println("Clubs ditemukan: " + count);
                if (count == 0) System.err.println("Tidak ada club ditemukan!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private StackPane createClubCard(Club club, boolean isMember) {
        StackPane cardPane = new StackPane();
        cardPane.setPrefSize(CARD_WIDTH, CARD_HEIGHT);
        cardPane.getStyleClass().add("club-carousel-card");

        Region imagePlaceholder = new Region();
        imagePlaceholder.setPrefSize(240, 200);
        imagePlaceholder.getStyleClass().add("club-carousel-image-placeholder");

        Label title = new Label(club.getNamaClub());
        title.getStyleClass().add("club-carousel-title");

        Button joinButton = new Button();
        joinButton.getStyleClass().add("club-carousel-join-button");

        if (isMember) {
            joinButton.setText("JOINED");
            joinButton.setDisable(true);
        } else {
            joinButton.setText("JOIN");
            joinButton.setDisable(false);
            joinButton.setOnAction(e -> showJoinPopup(club));
        }

        VBox contentBox = new VBox(20, title, joinButton);
        contentBox.setAlignment(Pos.CENTER);

        VBox layout = new VBox(25, imagePlaceholder, contentBox);
        layout.setAlignment(Pos.CENTER);
        cardPane.getChildren().add(layout);

        return cardPane;
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

        // --- PERBAIKAN DI SINI ---
        // Hapus 'peran' dari daftar kolom di INSERT INTO
        // Hapus 'id' dari daftar kolom di INSERT INTO (karena otomatis oleh DB)
        // Sesuaikan indeks parameter yang tersisa
        String sql = """
            INSERT INTO keanggotaan (id_mahasiswa, id_club, tanggal_bergabung, status)
            VALUES (?, ?, ?, ?)
            """;

        try (Connection conn = Connector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Parameter pertama sekarang adalah id_mahasiswa
            stmt.setString(1, currentUser.getNrp());

            // Parameter kedua sekarang adalah id_club
            stmt.setInt(2, selectedClub.getIdClub());

            // Parameter ketiga adalah tanggal_bergabung
            stmt.setDate(3, new Date(System.currentTimeMillis()));

            // Parameter keempat adalah status
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
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void scrollTo(double hValue) {
        if (scrollPane != null) scrollPane.setHvalue(hValue);
    }

    @FXML
    private void scrollLeft() {
        if (clubHBox != null && scrollPane != null && clubHBox.getWidth() > scrollPane.getViewportBounds().getWidth()) {
            double scrollAmount = (CARD_WIDTH + clubHBox.getSpacing()) /
                    (clubHBox.getWidth() - scrollPane.getViewportBounds().getWidth());
            scrollTo(Math.max(0, scrollPane.getHvalue() - scrollAmount));
        }
    }

    @FXML
    private void scrollRight() {
        if (clubHBox != null && scrollPane != null && clubHBox.getWidth() > scrollPane.getViewportBounds().getWidth()) {
            double scrollAmount = (CARD_WIDTH + clubHBox.getSpacing()) /
                    (clubHBox.getWidth() - scrollPane.getViewportBounds().getWidth());
            scrollTo(Math.min(1, scrollPane.getHvalue() + scrollAmount));
        }
    }
}