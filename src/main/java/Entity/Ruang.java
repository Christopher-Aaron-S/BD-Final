package Entity;

public class Ruang {
    private String idRuang;
    private String noRuangan;
    private int tanggalKegiatan;
    private Lantai lantai;

    public Ruang(String idRuang, String noRuangan, int tanggalKegiatan, Lantai lantai) {
        this.idRuang = idRuang;
        this.noRuangan = noRuangan;
        this.tanggalKegiatan = tanggalKegiatan;
        this.lantai = lantai;
    }

    public String getIdRuang() {
        return idRuang;
    }

    public void setIdRuang(String idRuang) {
        this.idRuang = idRuang;
    }

    public String getNoRuangan() {
        return noRuangan;
    }

    public void setNoRuangan(String noRuangan) {
        this.noRuangan = noRuangan;
    }

    public int getTanggalKegiatan() {
        return tanggalKegiatan;
    }

    public void setTanggalKegiatan(int tanggalKegiatan) {
        this.tanggalKegiatan = tanggalKegiatan;
    }

    public Lantai getLantai() {
        return lantai;
    }

    public void setLantai(Lantai lantai) {
        this.lantai = lantai;
    }
}
