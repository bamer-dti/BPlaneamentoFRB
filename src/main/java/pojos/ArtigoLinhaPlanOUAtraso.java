package pojos;


public class ArtigoLinhaPlanOUAtraso {
    private String estado;
    private String bostamp;
    private int obrano;
    private String fref;
    private String nmfref;
    private String seccao;
    private String obs;
    private String dtexp; //Data de cliente
    private String dtcli; //Data de Expedição (planeamento) ou Data de Corte (atrasos)
    private String dtcliente;
    private String dtexpedi;
    private int qtt;

    public ArtigoLinhaPlanOUAtraso() {

    }

    public ArtigoLinhaPlanOUAtraso(String bostamp, int obrano, String fref, String nmfref, String seccao, String obs, String dtexp, String dtcli, int qtt, String estado) {
        this.bostamp = bostamp;
        this.obrano = obrano;
        this.fref = fref;
        this.nmfref = nmfref;
        this.seccao = seccao;
        this.obs = obs;
        this.dtexp = dtexp;
        this.dtcli = dtcli;
        this.qtt = qtt;
        this.estado = estado;
    }

    public String getDtcliente() {
        return dtcliente;
    }

    public void setDtcliente(String dtcliente) {
        this.dtexp = dtcliente;
        this.dtcliente = dtcliente;
    }

    public String getDtexpedi() {
        return dtexpedi;
    }

    public void setDtexpedi(String dtexpedi) {
        this.dtcli = dtexpedi;
        this.dtexpedi = dtexpedi;
    }

    @Override
    public String toString() {
        return "bostamp: " + bostamp + ", obrano: " + obrano + ", fref: " + fref
                + ", nmfref: " + nmfref + ", qtt: " + qtt + ", seccao: " + seccao
                + ", obs: " + obs
                + ", dtexp: " + dtexp
                + ", dtcli: " + dtcli
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

    public String getDtexp() {
        return dtexp;
    }

    public void setDtexp(String dataTxt) {
        this.dtexp = dataTxt;
        this.dtcliente = dataTxt;
    }

    public String getDtcli() {
        return dtcli;
    }

    public void setDtcli(String dataTxt) {
        this.dtcli = dataTxt;
        this.dtexpedi = dataTxt;
    }

    public int getQtt() {
        return qtt;
    }

    public void setQtt(int qtt) {
        this.qtt = qtt;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public ArtigoOSBO transformar_Em_OSBO(int ordem, String data) {
        System.out.println("transformar_Em_OSBO: " + data);
        ArtigoOSBO ar = new ArtigoOSBO(bostamp, obrano, fref, nmfref, estado, seccao, obs, 1, data, data, data, data, ordem, data, qtt, data, 0);
        return ar;
    }
}
