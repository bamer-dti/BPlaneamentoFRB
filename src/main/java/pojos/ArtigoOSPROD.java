package pojos;

public class ArtigoOSPROD {
    private String bostamp;
    private String bistamp;
    private String design;
    private String dim;
    private String mk;
    private String numlinha;
    private int qtt;
    private String ref;

    public ArtigoOSPROD() {
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

    @Override
    public String toString() {
        return "bostamp: " + bostamp + ", bistamp: " + bistamp + ", design: " + design
                + ", dim: " + dim + ", mk: " + mk
                + ", numlinha: " + numlinha + ", qtt: " + qtt + ", ref: " + ref
                ;
    }
}
