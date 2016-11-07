package pojos;

@SuppressWarnings("unused")
public class ArtigoOSBI {
    private String bostamp;
    private String bistamp;
    private String design;
    private String dim;
    private String familia;
    private String mk;
    private String numlinha;
    private int qtt;
    private String ref;
    private String tipo;

    public ArtigoOSBI() {
    }

    public String getBostamp() {
        return bostamp;
    }

    public void setBostamp(String bostamp) {
        this.bostamp = bostamp;
    }

    public String getBistamp() {
        return bistamp;
    }

    public void setBistamp(String bistamp) {
        this.bistamp = bistamp;
    }

    public String getDesign() {
        return design;
    }

    public String getDim() {
        return dim;
    }

    public String getFamilia() {
        return familia;
    }

    public String getMk() {
        return mk;
    }

    public String getNumlinha() {
        return numlinha;
    }

    public int getQtt() {
        return qtt;
    }

    public String getRef() {
        return ref;
    }

    public String getTipo() {
        return tipo;
    }

    @Override
    public String toString() {
        return "bostamp: " + bostamp + ", bistamp: " + bistamp + ", design: " + design
                + ", dim: " + dim + ", familia: " + familia + ", mk: " + mk
                + ", numlinha: " + numlinha + ", qtt: " + qtt + ", ref: " + ref
                + ", tipo: " + tipo
                ;
    }
}
