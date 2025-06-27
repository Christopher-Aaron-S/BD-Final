package Entity;

import java.sql.Date; // Hati-hati dengan java.sql.Date, lebih baik java.time.LocalDate

public class Keanggotaan {
    private int id;
    // Jika Anda menyimpan tanggal sebagai int YYYYMMDD, properti ini harus int atau String
    // private int tanggalBergabung;
    private java.time.LocalDate tanggalBergabung; // Lebih baik menggunakan LocalDate
    private String status;
    private Mahasiswa mahasiswa; // Objek Mahasiswa
    private Club club; // Objek Club
    // private String peran; // HAPUS INI JIKA KOLOM 'peran' SUDAH DIHAPUS DARI DB

    // Konstruktor baru yang sesuai dengan kebutuhan Anda
    public Keanggotaan(int id, java.time.LocalDate tanggalBergabung, String status, Mahasiswa mahasiswa, Club club) {
        this.id = id;
        this.tanggalBergabung = tanggalBergabung;
        this.status = status;
        this.mahasiswa = mahasiswa;
        this.club = club;
        // this.peran = peran; // HAPUS INI
    }

    // Getter
    public int getId() { return id; }
    public java.time.LocalDate getTanggalBergabung() { return tanggalBergabung; }
    public String getStatus() { return status; }
    public Mahasiswa getMahasiswa() { return mahasiswa; }
    public Club getClub() { return club; }
    // public String getPeran() { return peran; } // HAPUS INI

    // Setter (jika diperlukan)
    public void setId(int id) { this.id = id; }
    public void setTanggalBergabung(java.time.LocalDate tanggalBergabung) { this.tanggalBergabung = tanggalBergabung; }
    public void setStatus(String status) { this.status = status; }
    public void setMahasiswa(Mahasiswa mahasiswa) { this.mahasiswa = mahasiswa; }
    public void setClub(Club club) { this.club = club; }
    // public void setPeran(String peran) { this.peran = peran; } // HAPUS INI
}