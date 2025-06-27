package Entity;

public class Kategori {
    private int idKategori;
    private String namaKategori;

    public Kategori(int idKategori, String namaKategori) {
        this.idKategori = idKategori;
        this.namaKategori = namaKategori;
    }

    public int getIdKategori() { return idKategori; }
    public String getNamaKategori() { return namaKategori; }
    public void setIdKategori(int idKategori) { this.idKategori = idKategori; }
    public void setNamaKategori(String namaKategori) { this.namaKategori = namaKategori; }

    @Override
    public String toString() {
        // Ini SANGAT PENTING: ComboBox akan memanggil metode ini
        // untuk mendapatkan teks yang akan ditampilkan.
        return namaKategori;
    }
}
