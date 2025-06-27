//package MainApp;
//
//import DBConnector.Connector;
//import Entity.*;
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import javafx.fxml.FXML;
//import javafx.scene.control.*;
//
//import java.sql.*;
//import java.time.LocalDate;
//
//public class AdminKegiatanController {
//
//    @FXML private TextField tfNamaKegiatan;
//    @FXML private DatePicker dpTanggalKegiatan;
//    @FXML private ComboBox<Club> cbClub;
//    @FXML private ComboBox<JenisKegiatan> cbJenis;
//    @FXML private ComboBox<Ruang> cbRuang;
//    @FXML private TableView<KegiatanClub> tableKegiatan; // nanti bisa kamu isi datanya
//
//    private Connection conn;
//
//    public void initialize() throws SQLException {
//        conn = Connector.getConnection();
//        loadComboBoxes();
//        loadTableData();
//    }
//
//    private void loadComboBoxes() {
//        cbClub.setItems(getClubList());
//        cbJenis.setItems(getJenisList());
//        cbRuang.setItems(getRuangList());
//    }
//
//    private ObservableList<Club> getClubList() {
//        ObservableList<Club> list = FXCollections.observableArrayList();
//        try {
//            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM club");
//            while (rs.next()) {
//                Club c = new Club(rs.getInt("id"), rs.getString("nama_club"));
//                list.add(c);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return list;
//    }
//
//    private ObservableList<JenisKegiatan> getJenisList() {
//        ObservableList<JenisKegiatan> list = FXCollections.observableArrayList();
//        try {
//            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM jenis_kegiatan");
//            while (rs.next()) {
//                JenisKegiatan j = new JenisKegiatan(rs.getInt("id"), rs.getString("nama_jenis"));
//                list.add(j);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return list;
//    }
//
//    private ObservableList<Ruang> getRuangList() {
//        ObservableList<Ruang> list = FXCollections.observableArrayList();
//        try {
//            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM ruang");
//            while (rs.next()) {
//                Ruang r = new Ruang(String.valueOf(rs.getInt("id")), rs.getString("no_ruangan"), 0, null);
//                list.add(r);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return list;
//    }
//
//    private void loadTableData() {
//        // Nanti kamu bisa isi kalau table view sudah dikonfigurasi kolomnya
//    }
//
//    @FXML
//    private void handleTambahKegiatan() {
//        try {
//            String sql = "INSERT INTO kegiatan_club (nama_kegiatan, tanggal_kegiatan, club_id, jenis_id, ruang_id) VALUES (?, ?, ?, ?, ?)";
//            PreparedStatement stmt = conn.prepareStatement(sql);
//            stmt.setString(1, tfNamaKegiatan.getText());
//            stmt.setDate(2, Date.valueOf(dpTanggalKegiatan.getValue()));
//            stmt.setInt(3, cbClub.getValue().getId());
//            stmt.setInt(4, cbJenis.getValue().getIdKegiatan());
//            stmt.setInt(5, Integer.parseInt(cbRuang.getValue().getIdRuang())); // karena idRuang pakai String
//
//            stmt.executeUpdate();
//            new Alert(Alert.AlertType.INFORMATION, "Kegiatan berhasil ditambahkan").show();
//            loadTableData(); // refresh jika ada table
//        } catch (Exception e) {
//            e.printStackTrace();
//            new Alert(Alert.AlertType.ERROR, "Gagal menambahkan kegiatan").show();
//        }
//    }
//}
