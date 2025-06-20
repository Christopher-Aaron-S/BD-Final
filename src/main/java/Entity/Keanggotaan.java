package Entity;

public class Keanggotaan {
    private String id;
    private String peran;
    private int tanggalBergabung;
    private String status;
    private Mahasiswa mahasiswa;
    private Club club;

    public Keanggotaan(String id, String peran, int tanggalBergabung, String status, Mahasiswa mahasiswa, Club club) {
        this.id = id;
        this.peran = peran;
        this.tanggalBergabung = tanggalBergabung;
        this.status = status;
        this.mahasiswa = mahasiswa;
        this.club = club;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPeran() {
        return peran;
    }

    public void setPeran(String peran) {
        this.peran = peran;
    }

    public int getTanggalBergabung() {
        return tanggalBergabung;
    }

    public void setTanggalBergabung(int tanggalBergabung) {
        this.tanggalBergabung = tanggalBergabung;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Mahasiswa getMahasiswa() {
        return mahasiswa;
    }

    public void setMahasiswa(Mahasiswa mahasiswa) {
        this.mahasiswa = mahasiswa;
    }

    public Club getClub() {
        return club;
    }

    public void setClub(Club club) {
        this.club = club;
    }
}
