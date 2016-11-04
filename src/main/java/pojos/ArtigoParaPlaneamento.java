package pojos;

import java.time.LocalDateTime;

///**
// * Created by miguel.silva on 28-07-2016.
// */
public class ArtigoParaPlaneamento {
    private String seccao;
    private String dtcliente;
    private int obrano;
    private String bostamp;
    private String dtexpedi;
    private String fref;
    private String nmfref;
    private String obs;
    private LocalDateTime dtcortef;

    public ArtigoParaPlaneamento(Builder builder) {
        seccao = builder.seccao;
        dtcliente = builder.dtcliente;
        obrano = builder.obrano;
        bostamp = builder.bostamp;
        dtexpedi = builder.dtexpedi;
        fref = builder.fref;
        nmfref = builder.nmfref;
        obs = builder.obs;
    }

    public String getSeccao() {
        return seccao;
    }

    public String getDtcliente() {
        return dtcliente;
    }

    public int getObrano() {
        return obrano;
    }

    public String getBostamp() {
        return bostamp;
    }

    public String getDtexpedi() {
        return dtexpedi;
    }

    public String getFref() {
        return fref;
    }

    public String getNmfref() {
        return nmfref;
    }

    public String getObs() {
        return obs;
    }

    public LocalDateTime getDtcortef() {
        return dtcortef;
    }

    public void setDtcortef(LocalDateTime dtcortef) {
        this.dtcortef = dtcortef;
    }

    public static class Builder {
        private String seccao;
        private String dtcliente;
        private int obrano;
        private String bostamp;
        private String dtexpedi;
        private String fref;
        private String nmfref;
        private String obs;

        public Builder seccao(String seccao) {
            this.seccao = seccao;
            return this;
        }

        public Builder dtexpedi(String data) {
            this.dtexpedi = data;
            return this;
        }

        public Builder dtcliente(String data) {
            this.dtcliente = data;
            return this;
        }

        public Builder obrano(int obrano) {
            this.obrano = obrano;
            return this;
        }

        public Builder bostamp(String bostamp) {
            this.bostamp = bostamp;
            return this;
        }

        public ArtigoParaPlaneamento build() {
            return new ArtigoParaPlaneamento(this);
        }

        public Builder fref(String fref) {
            this.fref = fref;
            return this;
        }

        public Builder nmfref(String nmfref) {
            this.nmfref = nmfref;
            return this;
        }
        public Builder obs(String obs) {
            this.obs = obs;
            return this;
        }
    }
}
