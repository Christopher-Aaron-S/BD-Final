package Entity;

public class Gedung {
    private String idGedung;
    private String noGedung;

    public Gedung(String idGedung, String noGedung) {
        this.idGedung = idGedung;
        this.noGedung = noGedung;
    }

    public String getIdGedung() {
        return idGedung;
    }

    public void setIdGedung(String idGedung) {
        this.idGedung = idGedung;
    }

    public String getNoGedung() {
        return noGedung;
    }

    public void setNoGedung(String noGedung) {
        this.noGedung = noGedung;
    }
}
