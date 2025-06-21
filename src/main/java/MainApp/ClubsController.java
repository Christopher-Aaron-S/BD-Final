package MainApp;

import DBConnector.Connector;
import Entity.Club;
import Entity.Kategori;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class ClubsController implements Initializable {

    @FXML
    private TableView<Club> clubTableView;
    @FXML
    private TableColumn<Club, String> namaClubColumn;
    @FXML
    private TableColumn<Club, Kategori> kategoriColumn;
    @FXML
    private TableColumn<Club, Integer> tahunBerdiriColumn;
    @FXML
    private TableColumn<Club, String> deskripsiColumn;

    private final ObservableList<Club> clubList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        namaClubColumn.setCellValueFactory(new PropertyValueFactory<>("namaClub"));

        // Untuk menampilkan nama kategori dari objek Kategori
        kategoriColumn.setCellValueFactory(new PropertyValueFactory<>("kategori"));
        kategoriColumn.setCellFactory(column -> new javafx.scene.control.TableCell<Club, Kategori>() {
            @Override
            protected void updateItem(Kategori item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getNamaKategori());
                }
            }
        });

        tahunBerdiriColumn.setCellValueFactory(new PropertyValueFactory<>("tahunBerdiri"));
        deskripsiColumn.setCellValueFactory(new PropertyValueFactory<>("deskripsi"));

        loadClubsFromDB();
    }

    private void loadClubsFromDB() {
        clubList.clear();
        String query = "SELECT c.id_club, c.nama_club, c.deskripsi, c.tahun_berdiri, cat.id_kategori, cat.nama_kategori " +
                "FROM club c JOIN kategori cat ON c.id_kategori = cat.id_kategori";

        try (Connection conn = Connector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Kategori kategori = new Kategori(rs.getString("id_kategori"), rs.getString("nama_kategori"));
                Club club = new Club(
                        rs.getString("id_club"),
                        rs.getString("nama_club"),
                        rs.getString("deskripsi"),
                        rs.getInt("tahun_berdiri"),
                        kategori
                );
                clubList.add(club);
            }
            clubTableView.setItems(clubList);

        } catch (SQLException e) {
            e.printStackTrace();
            // Handle error, maybe show an alert
        }
    }
}