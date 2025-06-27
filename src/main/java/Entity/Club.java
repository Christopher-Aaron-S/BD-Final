package Entity;

public class Club {
    private int idClub;
    private String namaClub;
    private String deskripsi;
    private int tahunBerdiri;
    private Kategori kategori;

    public Club(int idClub, String namaClub, String deskripsi, int tahunBerdiri, Kategori kategori) {
        this.idClub = idClub;
        this.namaClub = namaClub;
        this.deskripsi = deskripsi;
        this.tahunBerdiri = tahunBerdiri;
        this.kategori = kategori;
    }
    public Club(int idClub, String namaClub) {
        this.idClub = idClub;
        this.namaClub = namaClub;
    }


    public int getIdClub() { return idClub; }
    public String getNamaClub() { return namaClub; }
    public String getDeskripsi() { return deskripsi; }
    public int getTahunBerdiri() { return tahunBerdiri; }
    public Kategori getKategori() { return kategori; }


    public void setIdClub(int idClub) { this.idClub = idClub; }
    public void setNamaClub(String namaClub) { this.namaClub = namaClub; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }
    public void setTahunBerdiri(int tahunBerdiri) { this.tahunBerdiri = tahunBerdiri; }
    public void setKategori(Kategori kategori) { this.kategori = kategori; }
    @Override
    public String toString() {
        return namaClub;
    }

}
