package MainApp;

import DBConnector.Connector;
import Entity.Club;
import Entity.Kategori;
import Entity.Mahasiswa;
import Entity.Keanggotaan;
import Entity.KegiatanClub;
import Entity.JenisKegiatan;
import Entity.Ruang;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner; // Import Spinner

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time; // Import java.sql.Time
import java.time.LocalDate;
import java.time.LocalTime; // Import LocalTime
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.util.Callback;
import javafx.scene.control.TableCell;
import javafx.scene.control.SpinnerValueFactory; // Import SpinnerValueFactory
import javafx.scene.control.cell.ComboBoxTableCell;


// No longer needed for pop-up
// import javafx.stage.Modality;
// import javafx.stage.Stage;
// import javafx.scene.Scene;
import javafx.scene.control.Separator; // Added for visual separation in details


public class AdminPageController {

    @FXML private Label welcomeLabel;
    @FXML private HBox yourClubsPane;

    // FXML Elements untuk Menambah Club
    @FXML private TextField addClubNameField;
    @FXML private TextArea addClubDescriptionField;
    @FXML private TextField addClubYearField;
    @FXML private ComboBox<Kategori> addClubCategoryComboBox;
    @FXML private Button addClubButton;

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
    @FXML private HBox yourActivitiesPane;
    @FXML private TextField addActivityNameField;
    @FXML private TextArea addActivityDescriptionField;
    @FXML private DatePicker addActivityStartDatePicker;
    // Hapus @FXML private DatePicker addActivityEndDatePicker;

    // Tambahkan Spinner untuk Jam Mulai dan Jam Selesai
    @FXML private Spinner<Integer> addActivityStartHourSpinner;
    @FXML private Spinner<Integer> addActivityStartMinuteSpinner;
    @FXML private Spinner<Integer> addActivityEndHourSpinner;
    @FXML private Spinner<Integer> addActivityEndMinuteSpinner;


    @FXML private ComboBox<Ruang> addActivityRoomComboBox;
    @FXML private ComboBox<JenisKegiatan> addActivityTypeComboBox;
    @FXML private ComboBox<Club> addActivityClubComboBox;
    @FXML private Button addActivityButton;

    // NEW FXML Elements for Activity Details Container
    @FXML private VBox activityDetailsContainer;
    @FXML private Button backToActivityListButton;
    @FXML private Label detailActivityNameLabel;
    @FXML private Label detailActivityDescriptionLabel;
    @FXML private Label detailActivityClubLabel;
    @FXML private Label detailActivityDateLabel;
    @FXML private Label detailActivityTimeLabel;
    @FXML private Label detailActivityRoomLabel;
    @FXML private Label detailActivityTypeLabel;

    // FXML elements for Absensi Table
    @FXML private TableView<AbsensiDisplay> anggotaKegiatanTableView;
    @FXML private TableColumn<AbsensiDisplay, Integer> colNoMahasiswaAbsen;
    @FXML private TableColumn<AbsensiDisplay, String> colNamaMahasiswaAbsen;
    @FXML private TableColumn<AbsensiDisplay, String> colNRPMahasiswaAbsen;
    @FXML private TableColumn<AbsensiDisplay, String> colKehadiranAbsen;
    @FXML private TableColumn<AbsensiDisplay, String> colSertifikatAbsen;
    @FXML private TableColumn<AbsensiDisplay, Void> colActionAbsen;


    private List<Button> sidebarButtons;
    private MainViewController mainController;
    private Mahasiswa currentUser;
    private int currentClubId;
    private int currentActivityId; // To store the ID of the currently viewed activity

    private ObservableList<KeanggotaanDisplay> masterAnggotaList;
    private FilteredList<KeanggotaanDisplay> filteredAnggotaList;

    private ObservableList<AbsensiDisplay> masterAbsensiList;


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

        // Ensure only one main container is visible at startup
        showClubListContainer();

        if (backToClubListButton != null) {
            backToClubListButton.setOnAction(event -> handleBackToClubList());
        }
        // Initialize the new back button for activities
        if (backToActivityListButton != null) {
            backToActivityListButton.setOnAction(event -> handleBackToActivityList());
        }

        // Inisialisasi Spinner untuk Jam/Menit
        // Inisialisasi Spinner untuk Jam Mulai
        SpinnerValueFactory<Integer> startHourValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 0);
        addActivityStartHourSpinner.setValueFactory(startHourValueFactory);
        SpinnerValueFactory<Integer> startMinuteValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0);
        addActivityStartMinuteSpinner.setValueFactory(startMinuteValueFactory);
        addActivityStartHourSpinner.setEditable(true);
        addActivityStartMinuteSpinner.setEditable(true);

        // Inisialisasi Spinner untuk Jam Selesai
        SpinnerValueFactory<Integer> endHourValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 0);
        addActivityEndHourSpinner.setValueFactory(endHourValueFactory);
        SpinnerValueFactory<Integer> endMinuteValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0);
        addActivityEndMinuteSpinner.setValueFactory(endMinuteValueFactory);
        addActivityEndHourSpinner.setEditable(true);
        addActivityEndMinuteSpinner.setEditable(true);


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

        // Initialize Absensi Table
        // Initialize Absensi Table
        if (anggotaKegiatanTableView != null) {
            colNoMahasiswaAbsen.setCellValueFactory(new PropertyValueFactory<>("noUrut"));
            colNamaMahasiswaAbsen.setCellValueFactory(new PropertyValueFactory<>("namaMahasiswa"));
            colNRPMahasiswaAbsen.setCellValueFactory(new PropertyValueFactory<>("nrpMahasiswa"));

            // Setup ComboBox for Kehadiran
            ObservableList<String> kehadiranOptions = FXCollections.observableArrayList("Hadir", "Izin", "Alpha");
            colKehadiranAbsen.setCellValueFactory(new PropertyValueFactory<>("kehadiran"));
            colKehadiranAbsen.setEditable(true); // <--- ADD THIS LINE
            colKehadiranAbsen.setCellFactory(ComboBoxTableCell.forTableColumn(kehadiranOptions));
            colKehadiranAbsen.setOnEditCommit(event -> {
                AbsensiDisplay absensi = event.getRowValue();
                absensi.setKehadiran(event.getNewValue());
                // Update sertifikat based on kehadiran
                if ("Hadir".equals(event.getNewValue())) {
                    absensi.setSertifikat("Dapat");
                } else {
                    absensi.setSertifikat("Tidak Dapat");
                }
                anggotaKegiatanTableView.refresh(); // Refresh the table to show certificate change
            });

            colSertifikatAbsen.setCellValueFactory(new PropertyValueFactory<>("sertifikat"));
            // Make Sertifikat column non-editable
            colSertifikatAbsen.setEditable(false);

            colActionAbsen.setCellFactory(new Callback<TableColumn<AbsensiDisplay, Void>, TableCell<AbsensiDisplay, Void>>() {
                @Override
                public TableCell<AbsensiDisplay, Void> call(final TableColumn<AbsensiDisplay, Void> param) {
                    final TableCell<AbsensiDisplay, Void> cell = new TableCell<AbsensiDisplay, Void>() {
                        private final Button btn = new Button("Hapus");
                        {
                            btn.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-background-radius: 5;");
                            btn.setOnAction(event -> {
                                AbsensiDisplay absensi = getTableView().getItems().get(getIndex());
                                handleDeleteAbsensi(currentActivityId, absensi.getNrpMahasiswa());
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

            masterAbsensiList = FXCollections.observableArrayList();
            anggotaKegiatanTableView.setItems(masterAbsensiList);
            anggotaKegiatanTableView.setEditable(true); // Make the table editable for attendance
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

    public static class AbsensiDisplay {
        private final SimpleIntegerProperty noUrut;
        private final SimpleStringProperty namaMahasiswa;
        private final SimpleStringProperty nrpMahasiswa;
        private final SimpleStringProperty kehadiran;
        private final SimpleStringProperty sertifikat;

        public AbsensiDisplay(int noUrut, String namaMahasiswa, String nrpMahasiswa, String kehadiran, String sertifikat) {
            this.noUrut = new SimpleIntegerProperty(noUrut);
            this.namaMahasiswa = new SimpleStringProperty(namaMahasiswa);
            this.nrpMahasiswa = new SimpleStringProperty(nrpMahasiswa);
            this.kehadiran = new SimpleStringProperty(kehadiran);
            this.sertifikat = new SimpleStringProperty(sertifikat);
        }

        public int getNoUrut() { return noUrut.get(); }
        public SimpleIntegerProperty noUrutProperty() { return noUrut; }

        public String getNamaMahasiswa() { return namaMahasiswa.get(); }
        public SimpleStringProperty namaMahasiswaProperty() { return namaMahasiswa; }

        public String getNrpMahasiswa() { return nrpMahasiswa.get(); }
        public SimpleStringProperty nrpMahasiswaProperty() { return nrpMahasiswa; }

        public String getKehadiran() { return kehadiran.get(); }
        public void setKehadiran(String kehadiran) { this.kehadiran.set(kehadiran); }
        public SimpleStringProperty kehadiranProperty() { return kehadiran; }

        public String getSertifikat() { return sertifikat.get(); }
        public void setSertifikat(String sertifikat) { this.sertifikat.set(sertifikat); }
        public SimpleStringProperty sertifikatProperty() { return sertifikat; }
    }


    private void showClubListContainer() {
        if (clubListContainer != null) clubListContainer.setVisible(true);
        if (clubDetailsContainer != null) clubDetailsContainer.setVisible(false);
        if (activityListContainer != null) activityListContainer.setVisible(false);
        if (activityDetailsContainer != null) activityDetailsContainer.setVisible(false); // Ensure activity details is hidden
    }

    private void showClubDetailsContainer() {
        if (clubListContainer != null) clubListContainer.setVisible(false);
        if (clubDetailsContainer != null) clubDetailsContainer.setVisible(true);
        if (activityListContainer != null) activityListContainer.setVisible(false);
        if (activityDetailsContainer != null) activityDetailsContainer.setVisible(false); // Ensure activity details is hidden
    }

    private void showActivityListContainer() {
        if (clubListContainer != null) clubListContainer.setVisible(false);
        if (clubDetailsContainer != null) clubDetailsContainer.setVisible(false);
        if (activityListContainer != null) activityListContainer.setVisible(true);
        if (activityDetailsContainer != null) activityDetailsContainer.setVisible(false); // Ensure activity details is hidden
    }

    // New method to show activity details container
    private void showActivityDetailsContainer() {
        if (clubListContainer != null) clubListContainer.setVisible(false);
        if (clubDetailsContainer != null) clubDetailsContainer.setVisible(false);
        if (activityListContainer != null) activityListContainer.setVisible(false);
        if (activityDetailsContainer != null) activityDetailsContainer.setVisible(true);
    }

    @FXML
    private void handleBackToClubList() {
        showClubListContainer();
        if (anggotaClubTableView != null) {
            anggotaClubTableView.getItems().clear();
        }
        if (searchNrpField != null) {
            searchNrpField.clear();
        }
    }

    @FXML
    private void handleBackToActivityList() {
        showActivityListContainer();
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
    public void loadAllActivities() {
        yourActivitiesPane.getChildren().clear();
        String sql = "SELECT kc.id, kc.nama_kegiatan, kc.deskripsi, kc.tanggal_kegiatan, kc.jam_mulai, kc.jam_selesai,\n" +
                "       kc.id_ruang, r.no_ruangan, jk.nama_jenis, c.nama_club\n" +
                "FROM kegiatan_club kc\n" +
                "JOIN club c ON kc.id_club = c.id_club\n" +
                "LEFT JOIN jenis_kegiatan jk ON kc.id_jenis = jk.id\n" +
                "LEFT JOIN ruang r ON kc.id_ruang = r.id\n" +
                "ORDER BY kc.tanggal_kegiatan DESC, kc.jam_mulai ASC";

        try (Connection conn = Connector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (!rs.isBeforeFirst()) {
                Label noActivitiesLabel = new Label("Belum ada kegiatan yang terdaftar.");
                noActivitiesLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #888;");
                // For FlowPane, centering can be achieved by wrapping in a VBox/HBox and setting alignment
                VBox centeredMessage = new VBox(noActivitiesLabel);
                centeredMessage.setAlignment(Pos.CENTER);
                centeredMessage.setPrefSize(yourActivitiesPane.getPrefWidth(), yourActivitiesPane.getPrefHeight()); // Occupy available space
                yourActivitiesPane.getChildren().add(centeredMessage);
            } else {
                // FlowPane automatically handles wrapping, so no need for manual alignment reset here.
                // Padding and spacing are set in the constructor for FlowPane.

                while (rs.next()) {
                    int idActivity = rs.getInt("id");
                    String namaActivity = rs.getString("nama_kegiatan");
                    String deskripsiActivity = rs.getString("deskripsi");
                    LocalDate tanggalKegiatan = rs.getDate("tanggal_kegiatan") != null ? rs.getDate("tanggal_kegiatan").toLocalDate() : null;
                    LocalTime jamMulai = rs.getTime("jam_mulai") != null ? rs.getTime("jam_mulai").toLocalTime() : null;
                    LocalTime jamSelesai = rs.getTime("jam_selesai") != null ? rs.getTime("jam_selesai").toLocalTime() : null;
                    String ruanganNo = rs.getString("no_ruangan"); // Directly get no_ruangan from JOIN
                    String namaJenis = rs.getString("nama_jenis");
                    String namaClub = rs.getString("nama_club");

                    // Create the activity card similar to clubCard
                    VBox activityCard = new VBox();
                    activityCard.setPrefSize(200, 250); // Consistent size
                    activityCard.setStyle("-fx-background-color: #6a0dad; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 5);");

                    // White area for details
                    VBox topWhiteArea = new VBox();
                    topWhiteArea.setPrefHeight(150); // Consistent height
                    topWhiteArea.setStyle("-fx-background-color: white; -fx-background-radius: 15 15 0 0;");
                    topWhiteArea.setAlignment(Pos.TOP_LEFT); // Align content inside the white area
                    topWhiteArea.setPadding(new Insets(10)); // Padding inside the white area
                    topWhiteArea.setSpacing(5); // Spacing between labels in white area

                    Label clubNameLabel = new Label("Klub: " + namaClub);
                    clubNameLabel.setFont(Font.font("System", FontWeight.NORMAL, 12));
                    topWhiteArea.getChildren().add(clubNameLabel);

                    if (tanggalKegiatan != null) {
                        Label dateLabel = new Label("Tanggal: " + tanggalKegiatan.format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
                        dateLabel.setFont(Font.font("System", FontWeight.NORMAL, 12));
                        topWhiteArea.getChildren().add(dateLabel);
                    }
                    if (jamMulai != null && jamSelesai != null) {
                        Label timeLabel = new Label("Waktu: " + jamMulai.format(DateTimeFormatter.ofPattern("HH:mm")) + " - " + jamSelesai.format(DateTimeFormatter.ofPattern("HH:mm")));
                        timeLabel.setFont(Font.font("System", FontWeight.NORMAL, 12));
                        topWhiteArea.getChildren().add(timeLabel);
                    }
                    if (ruanganNo != null && !ruanganNo.isEmpty()) {
                        Label roomLabel = new Label("Ruangan: " + ruanganNo);
                        roomLabel.setFont(Font.font("System", FontWeight.NORMAL, 12));
                        topWhiteArea.getChildren().add(roomLabel);
                    }
                    if (namaJenis != null && !namaJenis.isEmpty()) {
                        Label typeLabel = new Label("Jenis: " + namaJenis);
                        typeLabel.setFont(Font.font("System", FontWeight.NORMAL, 12));
                        topWhiteArea.getChildren().add(typeLabel);
                    }

                    // Activity Name Label (moved to purple section)
                    Label activityNameLabel = new Label(namaActivity);
                    activityNameLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
                    activityNameLabel.setTextFill(javafx.scene.paint.Color.WHITE);
                    activityNameLabel.setPadding(new Insets(10, 0, 0, 0)); // Padding from top of purple area
                    activityNameLabel.setWrapText(true); // Allow text to wrap

                    VBox textContainer = new VBox(activityNameLabel);
                    textContainer.setAlignment(Pos.CENTER);
                    VBox.setVgrow(textContainer, javafx.scene.layout.Priority.ALWAYS); // Allow textContainer to grow vertically


                    HBox buttonContainer = new HBox(5);
                    buttonContainer.setAlignment(Pos.CENTER);
                    buttonContainer.setPadding(new Insets(0, 10, 10, 10));

                    Button openButton = new Button("Open");
                    openButton.setStyle("-fx-background-color: #17a2b8; -fx-text-fill: white; -fx-background-radius: 5;");
                    openButton.setMaxWidth(Double.MAX_VALUE);
                    HBox.setHgrow(openButton, javafx.scene.layout.Priority.ALWAYS);
                    // Pass all relevant details to the handleOpenActivityDetails method
                    openButton.setOnAction(event -> handleOpenActivityDetails(idActivity, namaActivity, deskripsiActivity, tanggalKegiatan, jamMulai, jamSelesai, ruanganNo, namaJenis, namaClub));


                    Button deleteButton = new Button("Delete");
                    deleteButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-background-radius: 5;");
                    deleteButton.setMaxWidth(Double.MAX_VALUE);
                    HBox.setHgrow(deleteButton, javafx.scene.layout.Priority.ALWAYS);
                    deleteButton.setOnAction(event -> handleDeleteActivity(idActivity, namaActivity));

                    buttonContainer.getChildren().addAll(openButton, deleteButton);

                    // Add elements to the activity card, mirroring clubCard structure
                    activityCard.getChildren().addAll(topWhiteArea, textContainer, buttonContainer);
                    yourActivitiesPane.getChildren().add(activityCard);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Gagal memuat daftar kegiatan: " + e.getMessage());
        }
    }

    private void handleOpenActivityDetails(int idActivity, String namaActivity, String deskripsiActivity, LocalDate tanggalKegiatan, LocalTime jamMulai, LocalTime jamSelesai, String ruanganNo, String namaJenis, String namaClub) {
        this.currentActivityId = idActivity; // Set the current activity ID
        showActivityDetailsContainer();

        detailActivityNameLabel.setText("Nama Kegiatan: " + namaActivity);
        detailActivityDescriptionLabel.setText("Deskripsi: " + deskripsiActivity);
        detailActivityClubLabel.setText("Club Penyelenggara: " + namaClub);
        detailActivityDateLabel.setText("Tanggal: " + (tanggalKegiatan != null ? tanggalKegiatan.format(DateTimeFormatter.ofPattern("dd MMMM yyyy")) : "N/A"));
        detailActivityTimeLabel.setText("Waktu: " + (jamMulai != null ? jamMulai.format(DateTimeFormatter.ofPattern("HH:mm")) : "N/A") + " - " + (jamSelesai != null ? jamSelesai.format(DateTimeFormatter.ofPattern("HH:mm")) : "N/A"));
        detailActivityRoomLabel.setText("Ruangan: " + (ruanganNo != null && !ruanganNo.isEmpty() ? ruanganNo : "N/A"));
        detailActivityTypeLabel.setText("Jenis Kegiatan: " + (namaJenis != null && !namaJenis.isEmpty() ? namaJenis : "N/A"));

        loadAbsensiForActivity(idActivity);
    }

    private void loadAbsensiForActivity(int idActivity) {
        masterAbsensiList.clear();
        // Corrected table name to 'absen' and column names to 'status_kehadiran', 'status_sertifikat'
        String sql = "SELECT u.nama, u.nrp, a.status_kehadiran, a.status_sertifikat " +
                "FROM absen a " +
                "JOIN users u ON a.id_mahasiswa = u.nrp " +
                "WHERE a.id_kegiatan_club = ? " +
                "ORDER BY u.nama";
        try (Connection conn = Connector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idActivity);
            ResultSet rs = pstmt.executeQuery();
            int noUrut = 1;
            while (rs.next()) {
                String namaMhs = rs.getString("nama");
                String nrpMhs = rs.getString("nrp");
                // Corrected column names to 'status_kehadiran', 'status_sertifikat'
                String kehadiran = rs.getString("status_kehadiran"); // <-- CHANGED HERE
                String sertifikat = rs.getString("status_sertifikat"); // <-- CHANGED HERE

                // If no record in absen table (due to LEFT JOIN), default to "Alpha" and "Tidak Dapat"
                if (kehadiran == null) {
                    kehadiran = "Alpha";
                    sertifikat = "Tidak Dapat";
                }
                masterAbsensiList.add(new AbsensiDisplay(noUrut++, namaMhs, nrpMhs, kehadiran, sertifikat));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Gagal memuat daftar absensi: " + e.getMessage());
        }
    }

    @FXML
    private void handleSaveAbsensi() {
        boolean allUpdatesSuccessful = true; // To track if all intended updates found a record
        for (AbsensiDisplay absensi : masterAbsensiList) {
            // SQL to update an existing record
            String updateSql = "UPDATE absen SET status_kehadiran = ?, status_sertifikat = ? WHERE id_kegiatan_club = ? AND id_mahasiswa = ?";
            try (Connection conn = Connector.getConnection();
                 PreparedStatement updatePstmt = conn.prepareStatement(updateSql)) {
                updatePstmt.setString(1, absensi.getKehadiran());
                updatePstmt.setString(2, absensi.getSertifikat());
                updatePstmt.setInt(3, currentActivityId);
                updatePstmt.setString(4, absensi.getNrpMahasiswa());

                int rowsAffected = updatePstmt.executeUpdate();

                if (rowsAffected == 0) {
                    // If no record was updated, it means it doesn't exist.
                    // As per your request, we do NOT insert new records here.
                    // You might want to log this or show a specific message to the user.
                    System.out.println("Peringatan: Catatan absensi tidak ditemukan untuk " + absensi.getNamaMahasiswa() + " (NRP: " + absensi.getNrpMahasiswa() + ") pada kegiatan ini. Penyimpanan dilewati.");
                    allUpdatesSuccessful = false; // Mark that at least one update failed to find a record
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Database Error", "Gagal menyimpan absensi untuk " + absensi.getNamaMahasiswa() + ": " + e.getMessage());
                allUpdatesSuccessful = false; // Mark that at least one update resulted in an error
                // You might choose to `return;` here to stop processing on the first error,
                // but continuing allows other records to potentially be saved.
            }
        }

        if (allUpdatesSuccessful) {
            showAlert(Alert.AlertType.INFORMATION, "Sukses", "Absensi berhasil diperbarui!");
        } else {
            showAlert(Alert.AlertType.WARNING, "Pembaruan Sebagian/Gagal", "Beberapa catatan absensi mungkin tidak diperbarui. Pastikan data mahasiswa sudah ada di database absensi untuk kegiatan ini.");
        }
        loadAbsensiForActivity(currentActivityId); // Reload the attendance list to show confirmed changes
    }

    private void handleDeleteAbsensi(int idKegiatanClub, String nrpMahasiswa) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Konfirmasi Penghapusan Absensi");
        alert.setHeaderText("Hapus Absensi Ini?");
        alert.setContentText("Anda yakin ingin menghapus catatan absensi mahasiswa dengan NRP '" + nrpMahasiswa + "' untuk kegiatan ini?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String sql = "DELETE FROM absen WHERE id_kegiatan_club = ? AND id_mahasiswa = ?";
            try (Connection conn = Connector.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, idKegiatanClub);
                pstmt.setString(2, nrpMahasiswa);

                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Sukses", "Catatan absensi berhasil dihapus.");
                    loadAbsensiForActivity(idKegiatanClub); // Reload absensi after deletion
                } else {
                    showAlert(Alert.AlertType.WARNING, "Gagal", "Catatan absensi gagal dihapus.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Database Error", "Terjadi kesalahan database saat menghapus absensi: " + e.getMessage());
            }
        }
    }


    private void handleDeleteActivity(int idActivity, String namaActivity) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Konfirmasi Penghapusan Kegiatan");
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
                    loadAllActivities(); // Reload activities after deletion
                } else {
                    showAlert(Alert.AlertType.WARNING, "Gagal", "Kegiatan gagal dihapus.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Database Error", "Terjadi kesalahan database saat menghapus kegiatan: " + e.getMessage());
            }
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
        // Asumsi kolom ID di jenis_kegiatan adalah 'id' (integer)
        String sql = "SELECT id, nama_jenis FROM jenis_kegiatan ORDER BY nama_jenis";
        try (Connection conn = Connector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
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
        // Sesuai skema image_cd89ba.png, ID di tabel 'ruang' adalah INTEGER.
        String sql = "SELECT id, no_ruangan FROM ruang ORDER BY no_ruangan";
        try (Connection conn = Connector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int idRuang = rs.getInt("id"); // Mengambil ID ruangan sebagai INTEGER
                String noRuangan = rs.getString("no_ruangan");
                // Sesuaikan konstruktor Ruang Anda jika memerlukan objek Lantai,
                // atau pastikan ada konstruktor yang menerima int id dan String noRuangan.
                // Untuk sementara, saya asumsikan konstruktor Ruang(int, String, Lantai) ada,
                // dan Lantai bisa null atau objek default jika tidak relevan untuk ComboBox.
                ruanganList.add(new Ruang(idRuang, noRuangan)); // Lantai di set null, sesuaikan jika perlu
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

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
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
        showActivityListContainer(); // Initially show the list, not the details
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


    @FXML
    private void handleAddActivity() {
        String namaActivity = addActivityNameField.getText();
        String deskripsi = addActivityDescriptionField.getText();
        LocalDate tanggalKegiatan = addActivityStartDatePicker.getValue(); // Ini akan jadi tanggal mulai

        // Ambil nilai jam dan menit dari spinner
        Integer startHour = addActivityStartHourSpinner.getValue();
        Integer startMinute = addActivityStartMinuteSpinner.getValue();
        Integer endHour = addActivityEndHourSpinner.getValue();
        Integer endMinute = addActivityEndMinuteSpinner.getValue();

        // Validasi null untuk spinner
        if (startHour == null || startMinute == null || endHour == null || endMinute == null) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Jam Mulai dan Jam Selesai harus diisi.");
            return;
        }

        // Buat objek LocalTime
        LocalTime jamMulai = LocalTime.of(startHour, startMinute);
        LocalTime jamSelesai = LocalTime.of(endHour, endMinute);

        Ruang selectedRuangan = addActivityRoomComboBox.getSelectionModel().getSelectedItem();
        JenisKegiatan selectedJenisKegiatan = addActivityTypeComboBox.getSelectionModel().getSelectedItem();
        Club selectedClub = addActivityClubComboBox.getSelectionModel().getSelectedItem();

        if (namaActivity.isEmpty() || deskripsi.isEmpty() || tanggalKegiatan == null || selectedRuangan == null || selectedJenisKegiatan == null || selectedClub == null) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Semua kolom (Nama Kegiatan, Deskripsi, Tanggal Kegiatan, Jam Mulai, Jam Selesai, Ruangan, Jenis Kegiatan, Club Penyelenggara) harus diisi untuk menambahkan kegiatan.");
            return;
        }

        // Validasi waktu (misal, jam selesai tidak boleh sebelum jam mulai pada tanggal yang sama)
        if (jamSelesai.isBefore(jamMulai)) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Jam Selesai tidak boleh sebelum Jam Mulai.");
            return;
        }

        // Perbaikan: ID Ruangan sekarang adalah INTEGER
        int idRuang = selectedRuangan.getIdRuang(); // Mengambil ID ruangan sebagai INTEGER dari objek Ruang yang dipilih

        // Perhatikan perubahan pada SQL INSERT statement dan tipe data yang di-set
        String sql = "INSERT INTO kegiatan_club (nama_kegiatan, deskripsi, tanggal_kegiatan, jam_mulai, jam_selesai, id_ruang, id_jenis, id_club) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = Connector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, namaActivity);
            pstmt.setString(2, deskripsi);
            pstmt.setDate(3, java.sql.Date.valueOf(tanggalKegiatan)); // Tanggal
            pstmt.setTime(4, java.sql.Time.valueOf(jamMulai));       // Jam Mulai
            pstmt.setTime(5, java.sql.Time.valueOf(jamSelesai));     // Jam Selesai
            pstmt.setInt(6, idRuang); // Gunakan idRuang (integer) yang sudah diambil
            pstmt.setInt(7, selectedJenisKegiatan.getIdJenisKegiatan()); // Asumsi JenisKegiatan memiliki getId() yang mengembalikan int
            pstmt.setInt(8, selectedClub.getIdClub()); // Asumsi Club memiliki getIdClub() yang mengembalikan int

            int rowsAffected = pstmt.executeUpdate(); // Ambil jumlah baris yang terpengaruh

            if (rowsAffected > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Sukses", "Kegiatan '" + namaActivity + "' berhasil ditambahkan!");
                // Clear fields
                addActivityNameField.clear();
                addActivityDescriptionField.clear();
                addActivityStartDatePicker.setValue(null);
                // Bersihkan spinner
                addActivityStartHourSpinner.getValueFactory().setValue(0);
                addActivityStartMinuteSpinner.getValueFactory().setValue(0);
                addActivityEndHourSpinner.getValueFactory().setValue(0);
                addActivityEndMinuteSpinner.getValueFactory().setValue(0);

                addActivityRoomComboBox.getSelectionModel().clearSelection();
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
}