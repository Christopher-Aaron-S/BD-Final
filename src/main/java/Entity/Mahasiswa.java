package Entity;
public class Mahasiswa {
    private String nrp;
    private String nama;
    private String email;
    private int idProgram;

    public Mahasiswa(String nrp, String nama, String email, int idProgram) {
        this.nrp = nrp;
        this.nama = nama;
        this.email = email;
        this.idProgram = idProgram;
    }

    public String getNrp() {
        return nrp;
    }

    public String getNama() {
        return nama;
    }

    public String getEmail() {
        return email;
    }

    public int getIdProgram() {
        return idProgram;
    }
}
