package Entity;

public class JenisKegiatan {
    private int idJenisKegiatan;
    private String namaJenis;

    public JenisKegiatan(int idJenisKegiatan, String namaJenis) {
        this.idJenisKegiatan = idJenisKegiatan;
        this.namaJenis = namaJenis;
    }

    public int getIdJenisKegiatan() {
        return idJenisKegiatan;
    }

    public void setIdJenisKegiatan(int idKegiatan) {
        this.idJenisKegiatan = idKegiatan;
    }

    public String getNamaJenis() {
        return namaJenis;
    }

    public void setNamaJenis(String namaJenis) {
        this.namaJenis = namaJenis;
    }
    @Override
    public String toString() {
        return namaJenis;
    }

}
