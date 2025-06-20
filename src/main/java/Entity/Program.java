package Entity;

public class Program {
    private String idProgram;
    private String namaProgram;

    public Program(String idProgram, String namaProgram) {
        this.idProgram = idProgram;
        this.namaProgram = namaProgram;
    }

    public String getId() { return idProgram; }
    public String getNamaProgram() { return namaProgram; }
    public void setId(String id) { this.idProgram = idProgram; }
    public void setNamaProgram(String namaProgram) { this.namaProgram = namaProgram; }
}

