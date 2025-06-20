package Entity;

public class JenisKegiatan {
    private String idKegiatan;
    private String namaJenis;

    public JenisKegiatan(String idKegiatan, String namaJenis) {
        this.idKegiatan = idKegiatan;
        this.namaJenis = namaJenis;
    }

    public String getIdKegiatan() {
        return idKegiatan;
    }

    public void setIdKegiatan(String idKegiatan) {
        this.idKegiatan = idKegiatan;
    }

    public String getNamaJenis() {
        return namaJenis;
    }

    public void setNamaJenis(String namaJenis) {
        this.namaJenis = namaJenis;
    }
}
