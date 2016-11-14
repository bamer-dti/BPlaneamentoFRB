package pojos;


public class ArtigoOSBO {
    private String bostamp;
    private int obrano;
    private String fref;
    private String nmfref;
    private String estado;
    private String seccao;
    private String obs;
    private int cor;
    private String dttransf;
    private String dtembala;
    private String dtexpedi;
    private long tempoParcial = 0L;
    private long tempoTotal = 0L;
    private String dtcortef;
    private int ordem;
    private String dtcliente;

    public ArtigoOSBO() {

    }

    public ArtigoOSBO(String bostamp, int obrano, String fref, String nmfref, String estado, String seccao
            , String obs, int cor, String dttransf, String dtembala, String dtexpedi, String dtcortef, int ordem, String dtcliente) {
        this.bostamp = bostamp;
        this.obrano  = obrano;
        this.fref = fref;
        this.nmfref = nmfref;
        this.estado = estado;
        this.seccao = seccao;
        this.obs = obs;
        this.cor = cor;
        this.dttransf = dttransf;
        this.dtembala = dtembala;
        this.dtexpedi = dtexpedi;
        this.dtcortef = dtcortef;
        this.ordem = ordem;
        this.dtcliente = dtcliente;
    }

    public ArtigoOSBO(String bostamp) {
        this.bostamp = bostamp;
    }

    @Override
    public String toString() {
        return "bostamp: " + bostamp + ", obrano: " + obrano + ", fref: " + fref
                + ", nmfref: " + nmfref + ", estado: " + estado + ", seccao: " + seccao
                + ", obs: " + obs + ", cor: " + cor + ", dttransf: " + dttransf
                + ", dtembala: " + dtembala + ", dtexpedi: " + dtexpedi + ", dtcortef: " + dtcortef
                + ", ordem: " + ordem + ", dtcliente: " + dtcliente
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

    public String getFref() {
        return fref;
    }

    public String getNmfref() {
        return nmfref;
    }

    public String getEstado() {
        return estado;
    }

    public String getSeccao() {
        return seccao;
    }

    public String getObs() {
        return obs;
    }

    public String getDtcortef() {
        return dtcortef;
    }

    public void setDtcortef(String dtcorte) {
        this.dtcortef = dtcorte;
    }

    public String getDttransf() {
        return dttransf;
    }

    public String getDtembala() {
        return dtembala;
    }

    public String getDtexpedi() {
        return dtexpedi;
    }

    public int getOrdem() {
        return ordem;
    }

    public void setOrdem(int ordem) {
        this.ordem = ordem;
    }

    public int getCor() {
        return cor;
    }

    public long getTempoParcial() {
        return tempoParcial;
    }

    public long getTempoTotal() {
        return tempoTotal;
    }


    public String getDtcliente() {
        return dtcliente;
    }


}
