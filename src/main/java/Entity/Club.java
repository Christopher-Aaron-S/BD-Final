package Entity;

public class Club {
    private String idClub;
    private String namaClub;
    private String deskripsi;
    private int tahunBerdiri;
    private Kategori kategori;

    public Club(String idClub, String namaClub, String deskripsi, int tahunBerdiri, Kategori kategori) {
        this.idClub = idClub;
        this.namaClub = namaClub;
        this.deskripsi = deskripsi;
        this.tahunBerdiri = tahunBerdiri;
        this.kategori = kategori;
    }

    public String getIdClub() { return idClub; }
    public String getNamaClub() { return namaClub; }
    public String getDeskripsi() { return deskripsi; }
    public int getTahunBerdiri() { return tahunBerdiri; }
    public Kategori getKategori() { return kategori; }

    public void setIdClub(String idClub) { this.idClub = idClub; }
    public void setNamaClub(String namaClub) { this.namaClub = namaClub; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }
    public void setTahunBerdiri(int tahunBerdiri) { this.tahunBerdiri = tahunBerdiri; }
    public void setKategori(Kategori kategori) { this.kategori = kategori; }
}
