package Entity;

public class Lantai {
    private String idLantai;
    private String noLantai;
    private Gedung gedung;

    public Lantai(String idLantai, String noLantai, Gedung gedung) {
        this.idLantai = idLantai;
        this.noLantai = noLantai;
        this.gedung = gedung;
    }

    public String getIdLantai() {
        return idLantai;
    }

    public void setIdLantai(String idLantai) {
        this.idLantai = idLantai;
    }

    public String getNoLantai() {
        return noLantai;
    }

    public void setNoLantai(String noLantai) {
        this.noLantai = noLantai;
    }

    public Gedung getGedung() {
        return gedung;
    }

    public void setGedung(Gedung gedung) {
        this.gedung = gedung;
    }
}
