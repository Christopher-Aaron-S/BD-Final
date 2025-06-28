package MainApp;

import DBConnector.Connector;
import Entity.Club;
import Entity.Kategori;
import Entity.Mahasiswa;
import Entity.Keanggotaan;
import Entity.KegiatanClub;
import Entity.JenisKegiatan;
import Entity.Ruang; // Menggunakan Ruang sesuai permintaan
import javafx.scene.control.*;
import javafx.scene.control.ScrollPane;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos; // Import Pos
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javafx.scene.control.cell.PropertyValueFactory;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.util.Callback;


public class AdminPageController {

    @FXML private Label welcomeLabel;
    @FXML private HBox yourClubsPane;

    // FXML Elements untuk Menambah Club
    @FXML private TextField addClubNameField;
    @FXML private TextArea addClubDescriptionField;
    @FXML private TextField addClubYearField;
    @FXML private ComboBox<Kategori> addClubCategoryComboBox;
    @FXML private Button addClubButton;
    @FXML private ScrollPane activitiesScrollPane;
    @FXML private VBox yourActivitiesPane;

    @FXML private Button clubsButton;
    @FXML private Button logoutButton;
    @FXML private Button activitiesButton;

    @FXML private VBox clubListContainer;
    @FXML private VBox clubDetailsContainer;
    @FXML private VBox activityListContainer;

    // FXML ID untuk elemen di dalam clubDetailsContainer
    @FXML private Label detailClubNameLabel;
    @FXML private Label detailDescriptionLabel;
    @FXML private Label detailYearLabel;
    @FXML private Label detailCategoryLabel;
    @FXML private Label detailMembersHeader;

    // FX:ID untuk TableView anggota
    @FXML private TableView<KeanggotaanDisplay> anggotaClubTableView;
    @FXML private TableColumn<KeanggotaanDisplay, Integer> colNoMahasiswa;
    @FXML private TableColumn<KeanggotaanDisplay, String> colNamaMahasiswa;
    @FXML private TableColumn<KeanggotaanDisplay, String> colNRPMahasiswa;
    @FXML private TableColumn<KeanggotaanDisplay, LocalDate> colTanggalBergabung;
    @FXML private TableColumn<KeanggotaanDisplay, String> colStatusKeanggotaan;
    @FXML private TableColumn<KeanggotaanDisplay, Void> colAction;

    // FX:ID untuk search bar (anggota)
    @FXML private TextField searchNrpField;

    @FXML private Button backToClubListButton;

    // FXML Elements untuk Activities
    @FXML private VBox getYourActivitiesPane;
    @FXML private TextField addActivityNameField;
    @FXML private TextArea addActivityDescriptionField;
    @FXML private DatePicker addActivityStartDatePicker;
    @FXML private ComboBox<Ruang> addActivityRoomComboBox; // Tipe ComboBox menggunakan Ruang
    @FXML private ComboBox<JenisKegiatan> addActivityTypeComboBox;
    @FXML private ComboBox<Club> addActivityClubComboBox;
    @FXML private Button addActivityButton;


    private List<Button> sidebarButtons;
    private MainViewController mainController;
    private Mahasiswa currentUser;
    private int currentClubId;

    private ObservableList<KeanggotaanDisplay> masterAnggotaList;
    private FilteredList<KeanggotaanDisplay> filteredAnggotaList;


    public void setMainController(MainViewController mainController) {
        this.mainController = mainController;
    }

    public void setUser(Mahasiswa user) {
        this.currentUser = user;
        if (user != null) {
            String programName = loadProgramName(user.getIdProgram());
            welcomeLabel.setText(String.format("Welcome Back, Admin %s\nProgram: %s | NRP: %s", user.getNama(), programName, user.getNrp()));
            loadAllClubs();
            updateActiveButton(clubsButton);
            showClubListContainer();
        }
    }

    @FXML
    public void initialize() {
        sidebarButtons = Arrays.asList(clubsButton, activitiesButton);
        loadCategories();
        loadClubsForActivityComboBox();
        loadJenisKegiatanForActivityComboBox();
        loadRuanganForActivityComboBox(); // Muat data ruangan

        showClubListContainer();

        if (backToClubListButton != null) {
            backToClubListButton.setOnAction(event -> handleBackToClubList());
        }

        if (yourActivitiesPane != null && activitiesScrollPane != null) {
            yourActivitiesPane.prefWidthProperty().bind(activitiesScrollPane.widthProperty().subtract(15)); // dikurangi sedikit untuk padding
        }

        if (anggotaClubTableView != null) {
            colNoMahasiswa.setCellValueFactory(new PropertyValueFactory<>("noUrut"));
            colNamaMahasiswa.setCellValueFactory(new PropertyValueFactory<>("namaMahasiswa"));
            colNRPMahasiswa.setCellValueFactory(new PropertyValueFactory<>("nrpMahasiswa"));
            colTanggalBergabung.setCellValueFactory(new PropertyValueFactory<>("tanggalBergabung"));
            colStatusKeanggotaan.setCellValueFactory(new PropertyValueFactory<>("status"));

            colAction.setCellFactory(new Callback<TableColumn<KeanggotaanDisplay, Void>, TableCell<KeanggotaanDisplay, Void>>() {
                @Override
                public TableCell<KeanggotaanDisplay, Void> call(final TableColumn<KeanggotaanDisplay, Void> param) {
                    final TableCell<KeanggotaanDisplay, Void> cell = new TableCell<KeanggotaanDisplay, Void>() {
                        private final Button btn = new Button("Hapus");
                        {
                            btn.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-background-radius: 5;");
                            btn.setOnAction(event -> {
                                KeanggotaanDisplay anggota = getTableView().getItems().get(getIndex());
                                handleDeleteAnggota(anggota.getNrpMahasiswa(), currentClubId);
                            });
                        }
                        @Override
                        public void updateItem(Void item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty) {
                                setGraphic(null);
                            } else {
                                setGraphic(btn);
                            }
                        }
                    };
                    return cell;
                }
            });

            masterAnggotaList = FXCollections.observableArrayList();
            filteredAnggotaList = new FilteredList<>(masterAnggotaList, p -> true);

            SortedList<KeanggotaanDisplay> sortedList = new SortedList<>(filteredAnggotaList);
            sortedList.comparatorProperty().bind(anggotaClubTableView.comparatorProperty());
            anggotaClubTableView.setItems(sortedList);

            if (searchNrpField != null) {
                searchNrpField.textProperty().addListener((observable, oldValue, newValue) -> {
                    filteredAnggotaList.setPredicate(anggota -> {
                        if (newValue == null || newValue.isEmpty()) {
                            return true;
                        }
                        String lowerCaseFilter = newValue.toLowerCase();
                        if (anggota.getNrpMahasiswa().toLowerCase().contains(lowerCaseFilter)) {
                            return true;
                        }
                        return false;
                    });
                });
            }
        }
    }

    public static class KeanggotaanDisplay {
        private final SimpleIntegerProperty noUrut;
        private final SimpleStringProperty namaMahasiswa;
        private final SimpleStringProperty nrpMahasiswa;
        private final SimpleObjectProperty<LocalDate> tanggalBergabung;
        private final SimpleStringProperty status;

        public KeanggotaanDisplay(int noUrut, String namaMahasiswa, String nrpMahasiswa, LocalDate tanggalBergabung, String status) {
            this.noUrut = new SimpleIntegerProperty(noUrut);
            this.namaMahasiswa = new SimpleStringProperty(namaMahasiswa);
            this.nrpMahasiswa = new SimpleStringProperty(nrpMahasiswa);
            this.tanggalBergabung = new SimpleObjectProperty<>(tanggalBergabung);
            this.status = new SimpleStringProperty(status);
        }

        public int getNoUrut() { return noUrut.get(); }
        public SimpleIntegerProperty noUrutProperty() { return noUrut; }

        public String getNamaMahasiswa() { return namaMahasiswa.get(); }
        public SimpleStringProperty namaMahasiswaProperty() { return namaMahasiswa; }

        public String getNrpMahasiswa() { return nrpMahasiswa.get(); }
        public SimpleStringProperty nrpMahasiswaProperty() { return nrpMahasiswa; }

        public LocalDate getTanggalBergabung() { return tanggalBergabung.get(); }
        public SimpleObjectProperty<LocalDate> tanggalBergabungProperty() { return tanggalBergabung; }

        public String getStatus() { return status.get(); }
        public SimpleStringProperty statusProperty() { return status; }
    }

    private void showClubListContainer() {
        if (clubListContainer != null) clubListContainer.setVisible(true);
        if (clubDetailsContainer != null) clubDetailsContainer.setVisible(false);
        if (activityListContainer != null) activityListContainer.setVisible(false);
    }

    private void showClubDetailsContainer() {
        if (clubListContainer != null) clubListContainer.setVisible(false);
        if (clubDetailsContainer != null) clubDetailsContainer.setVisible(true);
        if (activityListContainer != null) activityListContainer.setVisible(false);
    }

    private void showActivityListContainer() {
        if (clubListContainer != null) clubListContainer.setVisible(false);
        if (clubDetailsContainer != null) clubDetailsContainer.setVisible(false);
        if (activityListContainer != null) activityListContainer.setVisible(true);
    }

    @FXML
    private void handleBackToClubList() {
        showClubListContainer();
        if (masterAnggotaList != null) {
            masterAnggotaList.clear();
        }

        if (searchNrpField != null) {
            searchNrpField.clear();
        }
        if (searchNrpField != null) {
            searchNrpField.clear();
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

    private void updateActiveButton(Button selectedButton) {
        if (sidebarButtons == null) return;

        for (Button button : sidebarButtons) {
            if (button != null) {
                button.getStyleClass().remove("active-sidebar-button");
                button.setStyle("-fx-background-color: #8a2be2; -fx-text-fill: white; -fx-background-radius: 8; -fx-font-size: 14;");
            }
        }
        if (selectedButton != null) {
            selectedButton.getStyleClass().add("active-sidebar-button");
            selectedButton.setStyle("-fx-background-color: #6a0dad; -fx-text-fill: white; -fx-background-radius: 8; -fx-font-size: 14;");
        }
    }

    public void loadAllClubs() {
        yourClubsPane.getChildren().clear();
        String sql = "SELECT id_club, nama_club, deskripsi, tahun_berdiri, id_kategori FROM club ORDER BY nama_club";

        try (Connection conn = Connector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (!rs.isBeforeFirst()) {
                yourClubsPane.getChildren().add(new Label("Belum ada klub terdaftar."));
            } else {
                while (rs.next()) {
                    int idClub = rs.getInt("id_club");
                    String namaClub = rs.getString("nama_club");
                    String deskripsiClub = rs.getString("deskripsi");
                    int tahunBerdiriClub = rs.getInt("tahun_berdiri");
                    int idKategoriClub = rs.getInt("id_kategori");


                    VBox clubCard = new VBox();
                    clubCard.setPrefSize(200, 250);
                    clubCard.setStyle("-fx-background-color: #6a0dad; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 5);");

                    VBox topWhiteArea = new VBox();
                    topWhiteArea.setPrefHeight(150);
                    topWhiteArea.setStyle("-fx-background-color: white; -fx-background-radius: 15 15 0 0;");

                    Label nameLabel = new Label(namaClub);
                    nameLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
                    nameLabel.setTextFill(javafx.scene.paint.Color.WHITE);
                    nameLabel.setPadding(new Insets(10, 0, 0, 0));

                    VBox textContainer = new VBox(nameLabel);
                    textContainer.setAlignment(Pos.CENTER);
                    VBox.setVgrow(textContainer, javafx.scene.layout.Priority.ALWAYS);

                    HBox buttonContainer = new HBox(5);
                    buttonContainer.setAlignment(Pos.CENTER);
                    buttonContainer.setPadding(new Insets(0, 10, 10, 10));

                    Button openButton = new Button("Open");
                    openButton.setStyle("-fx-background-color: #17a2b8; -fx-text-fill: white; -fx-background-radius: 5;");
                    openButton.setMaxWidth(Double.MAX_VALUE);
                    HBox.setHgrow(openButton, javafx.scene.layout.Priority.ALWAYS);
                    openButton.setOnAction(event -> handleOpenClubDetails(idClub, namaClub, deskripsiClub, tahunBerdiriClub, idKategoriClub));

                    Button deleteButton = new Button("Delete");
                    deleteButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-background-radius: 5;");
                    deleteButton.setMaxWidth(Double.MAX_VALUE);
                    HBox.setHgrow(deleteButton, javafx.scene.layout.Priority.ALWAYS);
                    deleteButton.setOnAction(event -> handleDeleteClub(idClub, namaClub));

                    buttonContainer.getChildren().addAll(openButton, deleteButton);

                    clubCard.getChildren().addAll(topWhiteArea, textContainer, buttonContainer);
                    yourClubsPane.getChildren().add(clubCard);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Label errorLabel = new Label("Gagal memuat data klub. Periksa skema DB.");
            yourClubsPane.getChildren().add(errorLabel);
        }
    }

    private void loadCategories() {
        ObservableList<Kategori> categories = FXCollections.observableArrayList();
        String sql = "SELECT id, nama_kategori FROM kategori";
        try (Connection conn = Connector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                categories.add(new Kategori(rs.getInt("id"), rs.getString("nama_kategori")));
            }
            addClubCategoryComboBox.setItems(categories);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Gagal memuat kategori: " + e.getMessage());
        }
    }

    private void loadClubsForActivityComboBox() {
        ObservableList<Club> clubs = FXCollections.observableArrayList();
        String sql = "SELECT id_club, nama_club FROM club ORDER BY nama_club";
        try (Connection conn = Connector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                clubs.add(new Club(rs.getInt("id_club"), rs.getString("nama_club")));
            }
            addActivityClubComboBox.setItems(clubs);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Gagal memuat klub untuk kegiatan: " + e.getMessage());
        }
    }

    private void loadJenisKegiatanForActivityComboBox() {
        ObservableList<JenisKegiatan> jenisKegiatanList = FXCollections.observableArrayList();
        String sql = "SELECT id, nama_jenis FROM jenis_kegiatan ORDER BY nama_jenis";
        try (Connection conn = Connector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                // Asumsi JenisKegiatan memiliki konstruktor JenisKegiatan(int id, String nama)
                jenisKegiatanList.add(new JenisKegiatan(rs.getInt("id"), rs.getString("nama_jenis")));
            }
            addActivityTypeComboBox.setItems(jenisKegiatanList);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Gagal memuat jenis kegiatan: " + e.getMessage());
        }
    }

    // Metode baru untuk memuat ruangan ke ComboBox
    private void loadRuanganForActivityComboBox() {
        ObservableList<Ruang> ruanganList = FXCollections.observableArrayList();
        // Query untuk mengambil ID (String) dan no_ruangan (String). id_lantai tidak diambil karena tidak digunakan oleh objek Ruang.
        String sql = "SELECT id, no_ruangan FROM ruang ORDER BY no_ruangan";
        try (Connection conn = Connector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String idRuangStr = rs.getString("id"); // ID dari DB adalah String
                String noRuangan = rs.getString("no_ruangan");

                int idRuangInt;
                try {
                    idRuangInt = Integer.parseInt(idRuangStr); // Konversi ID ke int
                } catch (NumberFormatException e) {
                    System.err.println("Error parsing idRuang '" + idRuangStr + "' to int. Skipping this room. " + e.getMessage());
                    continue; // Lewati baris ini jika konversi gagal
                }

                // Asumsi Entity.Ruang memiliki konstruktor Ruang(int idRuang, String noRuangan)
                ruanganList.add(new Ruang(idRuangInt, noRuangan));
            }
            addActivityRoomComboBox.setItems(ruanganList);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Gagal memuat daftar ruangan: " + e.getMessage());
        }
    }


    @FXML
    private void handleAddClub() {
        String namaClub = addClubNameField.getText();
        String deskripsi = addClubDescriptionField.getText();
        String tahunBerdiriStr = addClubYearField.getText();

        Kategori selectedCategory = addClubCategoryComboBox.getSelectionModel().getSelectedItem();

        if (namaClub.isEmpty() || deskripsi.isEmpty() || tahunBerdiriStr.isEmpty() || selectedCategory == null) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Nama Club, Deskripsi, Tahun Berdiri, dan Kategori harus diisi.");
            return;
        }

        int tahunBerdiri;
        int kategoriId = selectedCategory.getIdKategori();

        try {
            tahunBerdiri = Integer.parseInt(tahunBerdiriStr);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Tahun Berdiri harus berupa angka.");
            return;
        }

        String sql = "INSERT INTO club (nama_club, deskripsi, tahun_berdiri, id_kategori) VALUES (?, ?, ?, ?)";

        try (Connection conn = Connector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, namaClub);
            pstmt.setString(2, deskripsi);
            pstmt.setInt(3, tahunBerdiri);
            pstmt.setInt(4, kategoriId);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Sukses", "Club '" + namaClub + "' berhasil ditambahkan!");
                addClubNameField.clear();
                addClubDescriptionField.clear();
                addClubYearField.clear();
                addClubCategoryComboBox.getSelectionModel().clearSelection();
                loadAllClubs();
                loadClubsForActivityComboBox();
            } else {
                showAlert(Alert.AlertType.WARNING, "Gagal", "Club gagal ditambahkan.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Terjadi kesalahan database: " + e.getMessage());
        }
    }

    private void handleDeleteClub(int idClub, String namaClub) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Konfirmasi Penghapusan");
        alert.setHeaderText("Hapus Club Ini?");
        alert.setContentText("Anda yakin ingin menghapus club '" + namaClub + "'? Tindakan ini tidak dapat dibatalkan.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String sql = "DELETE FROM club WHERE id_club = ?";
            try (Connection conn = Connector.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, idClub);

                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Sukses", "Club '" + namaClub + "' berhasil dihapus.");
                    loadAllClubs();
                    loadClubsForActivityComboBox();
                } else {
                    showAlert(Alert.AlertType.WARNING, "Gagal", "Club gagal dihapus.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Database Error", "Terjadi kesalahan database saat menghapus club: " + e.getMessage());
            }
        }
    }

    private void handleOpenClubDetails(int idClub, String namaClub, String deskripsiClub, int tahunBerdiriClub, int idKategoriClub) {
        this.currentClubId = idClub;
        showClubDetailsContainer();

        detailClubNameLabel.setText("Nama Club: " + namaClub);
        detailDescriptionLabel.setText(deskripsiClub);
        detailYearLabel.setText("Tahun Berdiri: " + tahunBerdiriClub);

        String kategoriName = getKategoriNameById(idKategoriClub);
        detailCategoryLabel.setText("Kategori: " + kategoriName);

        masterAnggotaList.clear();
        if (searchNrpField != null) {
            searchNrpField.clear();
        }

        try {
            String sql = "SELECT u.nama, u.nrp, k.tanggal_bergabung, k.status " +
                    "FROM users u " +
                    "JOIN keanggotaan k ON u.nrp = k.id_mahasiswa " +
                    "WHERE k.id_club = ?";
            try (Connection conn = Connector.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, idClub);
                ResultSet rs = pstmt.executeQuery();
                int noUrut = 1;
                while (rs.next()) {
                    String namaMhs = rs.getString("nama");
                    String nrpMhs = rs.getString("nrp");
                    LocalDate tanggalBergabung = null;
                    if (rs.getDate("tanggal_bergabung") != null) {
                        tanggalBergabung = rs.getDate("tanggal_bergabung").toLocalDate();
                    }
                    String status = rs.getString("status");

                    masterAnggotaList.add(new KeanggotaanDisplay(noUrut++, namaMhs, nrpMhs, tanggalBergabung, status));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Gagal memuat anggota klub: " + e.getMessage());
        }
    }

    private void handleDeleteAnggota(String nrpMahasiswa, int idClub) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Konfirmasi Penghapusan Anggota");
        alert.setHeaderText("Hapus Anggota Ini?");
        alert.setContentText("Anda yakin ingin menghapus mahasiswa dengan NRP '" + nrpMahasiswa + "' dari klub ini?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String sql = "DELETE FROM keanggotaan WHERE id_mahasiswa = ? AND id_club = ?";
            try (Connection conn = Connector.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, nrpMahasiswa);
                pstmt.setInt(2, idClub);

                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Sukses", "Anggota berhasil dihapus dari klub.");
                    // Reload the club details to reflect the change
                    try (Connection conn2 = Connector.getConnection();
                         PreparedStatement pstmt2 = conn2.prepareStatement("SELECT nama_club, deskripsi, tahun_berdiri, id_kategori FROM club WHERE id_club = ?")) {
                        pstmt2.setInt(1, idClub);
                        ResultSet rs = pstmt2.executeQuery();
                        if (rs.next()) {
                            handleOpenClubDetails(idClub, rs.getString("nama_club"), rs.getString("deskripsi"), rs.getInt("tahun_berdiri"), rs.getInt("id_kategori"));
                        }
                    }

                } else {
                    showAlert(Alert.AlertType.WARNING, "Gagal", "Anggota gagal dihapus.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Database Error", "Terjadi kesalahan database saat menghapus anggota: " + e.getMessage());
            }
        }
    }


    private String getKategoriNameById(int idKategori) {
        String kategoriName = "Unknown Kategori";
        String sql = "SELECT nama_kategori FROM kategori WHERE id = ?";
        try (Connection conn = Connector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idKategori);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                kategoriName = rs.getString("nama_kategori");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return kategoriName;
    }

    private String getJenisKegiatanNameById(int idJenisKegiatan) {
        String jenisName = "Unknown Jenis";
        String sql = "SELECT nama_jenis FROM jenis_kegiatan WHERE id = ?";
        try (Connection conn = Connector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idJenisKegiatan);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                jenisName = rs.getString("nama_jenis");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return jenisName;
    }

    // Metode untuk mendapatkan no_ruangan dari ID ruangan (ID-nya string di DB, tapi Ruang entity menggunakan int)
    private String getRuanganNoById(int idRuang) { // Accept an int
        String ruanganNo = "Unknown Ruangan";
        String sql = "SELECT no_ruangan FROM ruang WHERE id = ?";
        try (Connection conn = Connector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idRuang); // Use setInt
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                ruanganNo = rs.getString("no_ruangan");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ruanganNo;
    }


    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);

        alert.setHeaderText(title);
        alert.setContentText(msg);

        DialogPane dialogPane = alert.getDialogPane();
        // Memastikan stylesheet utama (styles.css) ter-load
        dialogPane.getStylesheets().add(
                getClass().getResource("/com/example/projectbd/styles.css").toExternalForm());
        // Menambahkan kelas CSS custom kita
        dialogPane.getStyleClass().add("custom-alert");
        // -------------------------------------

        alert.showAndWait();
    }

    @FXML
    private void handleClubsClick() {
        showAlert(Alert.AlertType.INFORMATION, "Navigasi", "Membuka halaman Clubs (Admin).");
        updateActiveButton(clubsButton);
        loadAllClubs();
        showClubListContainer();
    }

    @FXML
    private void handleActivitiesClick() {
        showAlert(Alert.AlertType.INFORMATION, "Navigasi", "Membuka halaman Activities (Admin).");
        updateActiveButton(activitiesButton);
        loadAllActivities();
        showActivityListContainer();
    }

    @FXML
    private void handleLogoutClick() {
        showAlert(Alert.AlertType.INFORMATION, "Logout", "Anda telah logout.");
        try {
            HelloApplication.showLogin();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error Logout", "Gagal kembali ke halaman login.");
        }
    }

    // --- METHODS UNTUK MANAJEMEN ACTIVITIES ---

    public void loadAllActivities() {
        yourActivitiesPane.getChildren().clear();
        String sql = "SELECT kc.id, kc.nama_kegiatan, kc.deskripsi, kc.tanggal_kegiatan, " +
                "r.no_ruangan, jk.nama_jenis, c.nama_club, kc.id_ruang " + // Include kc.id_ruang
                "FROM kegiatan_club kc " +
                "JOIN club c ON kc.id_club = c.id_club " +
                "LEFT JOIN jenis_kegiatan jk ON kc.id_jenis = jk.id " +
                "LEFT JOIN ruang r ON kc.id_ruang = r.id " +
                "ORDER BY kc.tanggal_kegiatan DESC";

        try (Connection conn = Connector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (!rs.isBeforeFirst()) {
                yourActivitiesPane.getChildren().add(new Label("Belum ada kegiatan terdaftar."));
            } else {
                while (rs.next()) {
                    int idActivity = rs.getInt("id");
                    String namaActivity = rs.getString("nama_kegiatan");
                    String deskripsiActivity = rs.getString("deskripsi");
                    LocalDate tanggalMulai = rs.getDate("tanggal_kegiatan").toLocalDate();

                    int idRuangDb = rs.getInt("id_ruang");
                    String ruanganNo = getRuanganNoById(idRuangDb);

                    String jenisKegiatan = rs.getString("nama_jenis");
                    String namaClubPenyelenggara = rs.getString("nama_club");

                    VBox activityCard = new VBox(10);
                    activityCard.setPrefWidth(Double.MAX_VALUE);
                    activityCard.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 0, 2);");

                    Label nameLabel = new Label(namaActivity);
                    nameLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
                    Label clubLabel = new Label("Penyelenggara: " + namaClubPenyelenggara);
                    clubLabel.setStyle("-fx-text-fill: #555;");
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                    String tanggalText = "Tanggal: " + tanggalMulai.format(formatter);
                    Label dateLabel = new Label(tanggalText);
                    dateLabel.setStyle("-fx-text-fill: #555;");
                    Label roomLabel = new Label("Ruangan: " + (ruanganNo != null ? ruanganNo : "-")); // Tampilkan no_ruangan
                    roomLabel.setStyle("-fx-text-fill: #555;");
                    Label typeLabel = new Label("Jenis: " + (jenisKegiatan != null ? jenisKegiatan : "-"));
                    typeLabel.setStyle("-fx-text-fill: #555;");
                    Label descriptionLabel = new Label(deskripsiActivity);
                    descriptionLabel.setWrapText(true);
                    descriptionLabel.setStyle("-fx-text-fill: #333;");


                    HBox buttonContainer = new HBox(10);
                    buttonContainer.setAlignment(Pos.BOTTOM_RIGHT);
                    buttonContainer.setPadding(new Insets(10, 0, 0, 0));

                    Button editButton = new Button("Edit");
                    editButton.setStyle("-fx-background-color: #ffc107; -fx-text-fill: white; -fx-background-radius: 5;");
                    editButton.setOnAction(event -> {
                        showAlert(Alert.AlertType.INFORMATION, "Edit Kegiatan", "Fitur edit untuk kegiatan '" + namaActivity + "' belum diimplementasikan.");
                    });

                    Button deleteButton = new Button("Delete");
                    deleteButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-background-radius: 5;");
                    deleteButton.setOnAction(event -> handleDeleteActivity(idActivity, namaActivity));

                    buttonContainer.getChildren().addAll(editButton, deleteButton);

                    activityCard.getChildren().addAll(nameLabel, clubLabel, dateLabel, roomLabel, typeLabel, descriptionLabel, buttonContainer);
                    yourActivitiesPane.getChildren().add(activityCard);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Label errorLabel = new Label("Gagal memuat data kegiatan. Periksa skema DB dan nama kolom.");
            yourActivitiesPane.getChildren().add(errorLabel);
        }
    }


    @FXML
    private void handleAddActivity() {
        String namaActivity = addActivityNameField.getText();
        String deskripsi = addActivityDescriptionField.getText();
        LocalDate tanggalMulai = addActivityStartDatePicker.getValue();
        Ruang selectedRuangan = addActivityRoomComboBox.getSelectionModel().getSelectedItem();
        JenisKegiatan selectedJenisKegiatan = addActivityTypeComboBox.getSelectionModel().getSelectedItem();
        Club selectedClub = addActivityClubComboBox.getSelectionModel().getSelectedItem();

        if (namaActivity.isEmpty() || deskripsi.isEmpty() || tanggalMulai == null || selectedRuangan == null || selectedJenisKegiatan == null || selectedClub == null) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Semua kolom (Nama Kegiatan, Deskripsi, Tanggal Mulai, Ruangan, Jenis Kegiatan, Club Penyelenggara) harus diisi untuk menambahkan kegiatan.");
            return;
        }

        int idRuangInt = selectedRuangan.getIdRuang(); // Get idRuang as int from Ruang entity
        String idRuangString = String.valueOf(idRuangInt); // Convert int to String for DB insertion

        String sql = "INSERT INTO kegiatan_club (nama_kegiatan, deskripsi, tanggal_kegiatan, id_ruang, id_jenis, id_club) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = Connector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, namaActivity);
            pstmt.setString(2, deskripsi);
            pstmt.setDate(3, java.sql.Date.valueOf(tanggalMulai));
            pstmt.setString(4, idRuangString); // Set id_ruang as String
            pstmt.setInt(5, selectedJenisKegiatan.getIdJenisKegiatan());
            pstmt.setInt(6, selectedClub.getIdClub());

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Sukses", "Kegiatan '" + namaActivity + "' berhasil ditambahkan!");
                // Clear fields
                addActivityNameField.clear();
                addActivityDescriptionField.clear();
                addActivityStartDatePicker.setValue(null);
                addActivityRoomComboBox.getSelectionModel().clearSelection(); // Clear ComboBox selection
                addActivityTypeComboBox.getSelectionModel().clearSelection();
                addActivityClubComboBox.getSelectionModel().clearSelection();
                loadAllActivities();
            } else {
                showAlert(Alert.AlertType.WARNING, "Gagal", "Kegiatan gagal ditambahkan.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Terjadi kesalahan database saat menambahkan kegiatan: " + e.getMessage());
        }
    }

    private void handleDeleteActivity(int idActivity, String namaActivity) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Konfirmasi Penghapusan");
        alert.setHeaderText("Hapus Kegiatan Ini?");
        alert.setContentText("Anda yakin ingin menghapus kegiatan '" + namaActivity + "'? Tindakan ini tidak dapat dibatalkan.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String sql = "DELETE FROM kegiatan_club WHERE id = ?";
            try (Connection conn = Connector.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, idActivity);

                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Sukses", "Kegiatan '" + namaActivity + "' berhasil dihapus.");
                    loadAllActivities();
                } else {
                    showAlert(Alert.AlertType.WARNING, "Gagal", "Kegiatan gagal dihapus.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Database Error", "Terjadi kesalahan database saat menghapus kegiatan: " + e.getMessage());
            }
        }
    }
}