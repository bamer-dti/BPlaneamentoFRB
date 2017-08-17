package pojos;


import org.json.JSONObject;
import utils.Campos;
import utils.Funcoes;

public class ArtigoOSBO {
    private long unixtime;
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
    private String dtcortef;
    private int ordem;
    private String dtcliente;
    private int pecas;
    private int pecasprodz;
    private String dtoper;

    @SuppressWarnings("unused")
    public ArtigoOSBO() {

    }

    public ArtigoOSBO(String bostamp, int obrano, String fref, String nmfref, String estado, String seccao
            , String obs, int cor, String dttransf, String dtembala, String dtexpedi, String dtcortef, int ordem, String dtcliente, int pecas, String dtoper, int pecasprodz) {
        this.bostamp = bostamp;
        this.obrano = obrano;
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
        this.pecas = pecas;
        this.pecasprodz = pecasprodz;
        this.dtoper = dtoper;
    }

    public ArtigoOSBO(String bostamp, String estado, int cor, int ordem, String dtoper, long unixtime) {
        this.bostamp = bostamp;
        this.estado = estado;
        this.cor = cor;
        this.ordem = ordem;
        this.dtoper = dtoper;
        this.unixtime = unixtime;
    }

    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Campos.BOSTAMP, bostamp);
        jsonObject.put(Campos.ESTADO, estado);
        jsonObject.put(Campos.COR, cor);
        jsonObject.put(Campos.ORDEM, ordem);
        jsonObject.put(Campos.DTOPER, Funcoes.cToSQL(dtoper));
        return jsonObject;
    }

    @Override
    public String toString() {
        return "bostamp: " + bostamp + ", obrano: " + obrano + ", fref: " + fref
                + ", nmfref: " + nmfref + ", estado: " + estado + ", seccao: " + seccao
                + ", obs: " + obs + ", cor: " + cor + ", dttransf: " + dttransf
                + ", dtembala: " + dtembala + ", dtexpedi: " + dtexpedi + ", dtcortef: " + dtcortef
                + ", ordem: " + ordem + ", dtcliente: " + dtcliente + ", pecas: " + pecas + ", pecasprodz: " + pecasprodz
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

    public int getCor() {
        return cor;
    }

    public void setCor(int cor) {
        this.cor = cor;
    }

    public String getDtcliente() {
        return dtcliente;
    }

    public int getPecas() {
        return pecas;
    }

    public String getDtoper() {
        return dtoper;
    }

    public void setDtoper(String dtoper) {
        this.dtoper = dtoper;
    }

    public int getPecasprodz() {
        return pecasprodz;
    }

    public void setPecasprodz(int pecasprodz) {
        this.pecasprodz = pecasprodz;
    }

    public void setOrdem(int ordem) {
        this.ordem = ordem;
    }

    public void setDtcortef(String dtcortef) {
        this.dtcortef = dtcortef;
    }

    public long getUnixtime() {
        return unixtime;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}

