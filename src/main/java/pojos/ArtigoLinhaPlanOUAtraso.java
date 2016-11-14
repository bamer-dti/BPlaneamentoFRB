package pojos;


public class ArtigoLinhaPlanOUAtraso {
    private String bostamp;
    private int obrano;
    private String fref;
    private String nmfref;
    private String seccao;
    private String obs;
    private String dt1; //Data de cliente
    private String dt2; //Data de Expedição (planeamento) ou Data de Corte (atrasos)
    private String dtcliente;
    private String dtexpedi;
    private int qtt;

    public ArtigoLinhaPlanOUAtraso() {

    }

    public ArtigoLinhaPlanOUAtraso(String bostamp, int obrano, String fref, String nmfref, String seccao, String obs, String dt1, String dt2, int qtt) {
        this.bostamp = bostamp;
        this.obrano = obrano;
        this.fref = fref;
        this.nmfref = nmfref;
        this.seccao = seccao;
        this.obs = obs;
        this.dt1 = dt1;
        this.dt2 = dt2;
        this.qtt = qtt;
    }

    public String getDtcliente() {
        return dtcliente;
    }

    public void setDtcliente(String dtcliente) {
        this.dt1 = dtcliente;
        this.dtcliente = dtcliente;
    }

    public String getDtexpedi() {
        return dtexpedi;
    }

    public void setDtexpedi(String dtexpedi) {
        this.dt2 = dtexpedi;
        this.dtexpedi = dtexpedi;
    }

    @Override
    public String toString() {
        return "bostamp: " + bostamp + ", obrano: " + obrano + ", fref: " + fref
                + ", nmfref: " + nmfref + ", qtt: " + qtt + ", seccao: " + seccao
                + ", obs: " + obs
                + ", dt1: " + dt1
                + ", dt2: " + dt2
                ;
    }

    public String getBostamp() {
        return bostamp;
    }

    public void setBostamp(String bostamp) {
        this.bostamp = bostamp;
    }

    public int getObrano() {
        return obrano;
    }

    public void setObrano(int obrano) {
        this.obrano = obrano;
    }

    public String getFref() {
        return fref;
    }

    public void setFref(String fref) {
        this.fref = fref;
    }

    public String getNmfref() {
        return nmfref;
    }

    public void setNmfref(String nmfref) {
        this.nmfref = nmfref;
    }

    public String getSeccao() {
        return seccao;
    }

    public void setSeccao(String seccao) {
        this.seccao = seccao;
    }

    public String getObs() {
        return obs;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }

    public String getDt1() {
        return dt1;
    }

    public void setDt1(String dataTxt) {
        this.dt1 = dataTxt;
        this.dtcliente = dataTxt;
    }

    public String getDt2() {
        return dt2;
    }

    public void setDt2(String dataTxt) {
        this.dt2 = dataTxt;
        this.dtexpedi = dataTxt;
    }

    public int getQtt() {
        return qtt;
    }

    public void setQtt(int qtt) {
        this.qtt = qtt;
    }
}
