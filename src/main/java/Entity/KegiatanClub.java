package Entity;

public class KegiatanClub {
    private String idKegiatanClub;
    private String namaKegiatan;
    private int tanggalKegiatan;
    private Keanggotaan keanggotaan;
    private Ruang ruang;
    private JenisKegiatan jenis;

    public KegiatanClub(String idKegiatanClub, String namaKegiatan, int tanggalKegiatan, Keanggotaan keanggotaan, Ruang ruang, JenisKegiatan jenis) {
        this.idKegiatanClub = idKegiatanClub;
        this.namaKegiatan = namaKegiatan;
        this.tanggalKegiatan = tanggalKegiatan;
        this.keanggotaan = keanggotaan;
        this.ruang = ruang;
        this.jenis = jenis;
    }

    public String getIdKegiatanClub() {
        return idKegiatanClub;
    }

    public void setIdKegiatanClub(String idKegiatanClub) {
        this.idKegiatanClub = idKegiatanClub;
    }

    public String getNamaKegiatan() {
        return namaKegiatan;
    }

    public void setNamaKegiatan(String namaKegiatan) {
        this.namaKegiatan = namaKegiatan;
    }

    public int getTanggalKegiatan() {
        return tanggalKegiatan;
    }

    public void setTanggalKegiatan(int tanggalKegiatan) {
        this.tanggalKegiatan = tanggalKegiatan;
    }

    public Keanggotaan getKeanggotaan() {
        return keanggotaan;
    }

    public void setKeanggotaan(Keanggotaan keanggotaan) {
        this.keanggotaan = keanggotaan;
    }

    public Ruang getRuang() {
        return ruang;
    }

    public void setRuang(Ruang ruang) {
        this.ruang = ruang;
    }

    public JenisKegiatan getJenis() {
        return jenis;
    }

    public void setJenis(JenisKegiatan jenis) {
        this.jenis = jenis;
    }
}
