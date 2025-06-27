package Entity;

import java.time.LocalDate;

public class KegiatanClub {
    private int idActivity;
    private String namaActivity;
    private String deskripsi;
    private LocalDate tanggalMulai;
    private LocalDate tanggalSelesai;
    private String idRuangan; // UBAH KEMBALI KE String
    private int idJenisKegiatan;
    private int idClub;

    public KegiatanClub(int idActivity, String namaActivity, String deskripsi,
                    LocalDate tanggalMulai, LocalDate tanggalSelesai,
                    String idRuangan, int idJenisKegiatan, int idClub) { // UBAH idRuangan parameter
        this.idActivity = idActivity;
        this.namaActivity = namaActivity;
        this.deskripsi = deskripsi;
        this.tanggalMulai = tanggalMulai;
        this.tanggalSelesai = tanggalSelesai;
        this.idRuangan = idRuangan; // Set String
        this.idJenisKegiatan = idJenisKegiatan;
        this.idClub = idClub;
    }

    // Getters and Setters
    public int getIdActivity() {
        return idActivity;
    }

    public void setIdActivity(int idActivity) {
        this.idActivity = idActivity;
    }

    public String getNamaActivity() {
        return namaActivity;
    }

    public void setNamaActivity(String namaActivity) {
        this.namaActivity = namaActivity;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public LocalDate getTanggalMulai() {
        return tanggalMulai;
    }

    public void setTanggalMulai(LocalDate tanggalMulai) {
        this.tanggalMulai = tanggalMulai;
    }

    public LocalDate getTanggalSelesai() {
        return tanggalSelesai;
    }

    public void setTanggalSelesai(LocalDate tanggalSelesai) {
        this.tanggalSelesai = tanggalSelesai;
    }

    public String getIdRuangan() { // UBAH KEMBALI KE String
        return idRuangan;
    }

    public void setIdRuangan(String idRuangan) { // UBAH idRuangan parameter
        this.idRuangan = idRuangan;
    }

    public int getIdJenisKegiatan() {
        return idJenisKegiatan;
    }

    public void setIdJenisKegiatan(int idJenisKegiatan) {
        this.idJenisKegiatan = idJenisKegiatan;
    }

    public int getIdClub() {
        return idClub;
    }

    public void setIdClub(int idClub) {
        this.idClub = idClub;
    }
}