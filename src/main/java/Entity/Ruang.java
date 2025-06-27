package Entity;

public class Ruang {
    private int idRuang;
    private String noRuangan;

    public Ruang(int idRuang, String noRuangan) {
        this.idRuang = idRuang;
        this.noRuangan = noRuangan;
    }

    public int getIdRuang() {
        return idRuang;
    }

    public void setIdRuang(int idRuang) {
        this.idRuang = idRuang;
    }

    public String getNoRuangan() {
        return noRuangan;
    }

    public void setNoRuangan(String noRuangan) {
        this.noRuangan = noRuangan;
    }
    
    @Override
    public String toString() {
        return noRuangan;
    }

}
