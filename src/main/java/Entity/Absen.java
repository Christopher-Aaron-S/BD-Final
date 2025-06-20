package Entity;

public class Absen {
    private String idAbsen;
    private String statusKehadiran;
    private String statusSertifikat;
    private String noSertifikat;
    private Mahasiswa mahasiswa;
    private KegiatanClub kegiatan;

    public Absen(String idAbsen, String statusKehadiran, String statusSertifikat, String noSertifikat, Mahasiswa mahasiswa, KegiatanClub kegiatan) {
        this.idAbsen = idAbsen;
        this.statusKehadiran = statusKehadiran;
        this.statusSertifikat = statusSertifikat;
        this.noSertifikat = noSertifikat;
        this.mahasiswa = mahasiswa;
        this.kegiatan = kegiatan;
    }

    public String getIdAbsen() {
        return idAbsen;
    }

    public void setIdAbsen(String idAbsen) {
        this.idAbsen = idAbsen;
    }

    public String getStatusKehadiran() {
        return statusKehadiran;
    }

    public void setStatusKehadiran(String statusKehadiran) {
        this.statusKehadiran = statusKehadiran;
    }

    public String getStatusSertifikat() {
        return statusSertifikat;
    }

    public void setStatusSertifikat(String statusSertifikat) {
        this.statusSertifikat = statusSertifikat;
    }

    public String getNoSertifikat() {
        return noSertifikat;
    }

    public void setNoSertifikat(String noSertifikat) {
        this.noSertifikat = noSertifikat;
    }

    public Mahasiswa getMahasiswa() {
        return mahasiswa;
    }

    public void setMahasiswa(Mahasiswa mahasiswa) {
        this.mahasiswa = mahasiswa;
    }

    public KegiatanClub getKegiatan() {
        return kegiatan;
    }

    public void setKegiatan(KegiatanClub kegiatan) {
        this.kegiatan = kegiatan;
    }
}
