package pojos;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import utils.Funcoes;

/**
 * by miguel.silva on 14-02-2017.
 */
@SuppressWarnings("unused")
public class SMS {
    private static final String TAG = SMS.class.getSimpleName();
    private String bostamp;
    private String para;
    private String de;
    private String assunto;
    private String mensagem;
    private String id;
    private LongProperty tempostamp = new SimpleLongProperty();
    private Long lidastamp;
    private String lidaQuem;
    private String dataTxt;
    private SimpleBooleanProperty lida = new SimpleBooleanProperty();
    private BooleanProperty arquivada = new SimpleBooleanProperty();

    public SMS() {
        SMS sms = this;
        tempostampProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                dataTxt = Funcoes.millis_em_DD_MM_AAAA_HH_MM_SS(newValue.longValue());
            }
        });
        arquivada.addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean antesArquivada, Boolean newValue) {
                System.out.println(TAG + ": Listener OldValue = " + antesArquivada + "; newValue = " + newValue);
                String keyLida = lida.get() ? "Lidas" : "NaoLidas";
                if (antesArquivada) {
                    DatabaseReference refA = FirebaseDatabase.getInstance().getReference("sms").child("arquivadas").child(para).child(id);
                    refA.setValue(null);
                    DatabaseReference refL = FirebaseDatabase.getInstance().getReference("sms").child(keyLida).child(para).child(id);
                    refL.setValue(sms);
                } else {
                    DatabaseReference refLida = FirebaseDatabase.getInstance().getReference("sms").child(keyLida).child(para).child(id);
                    refLida.setValue(null);
                    System.out.println(TAG + ": " + sms.toString());
                    DatabaseReference refArquivada = FirebaseDatabase.getInstance().getReference("sms").child("arquivadas").child(para).child(id);
                    refArquivada.setValue(sms);
                }
            }
        });
    }

    public static String getTAG() {
        return TAG;
    }

    @Override
    public String toString() {
        String str = "bostamp: " + bostamp
                + "; paraLabel: " + para
                + "; de: " + de
                + "; assunto: " + assunto
                + "; mensagem: " + mensagem
                + "; id: " + id
                + "; tempostamp: " + tempostamp
                + "; lida: " + lida.get()
                + "; lidaQuem: " + lidaQuem
                + "; arquivada: " + arquivada.get();
        return str;
    }

    public String getBostamp() {
        return bostamp;
    }

    public void setBostamp(String bostamp) {
        this.bostamp = bostamp;
    }

    public String getPara() {
        return para;
    }

    public void setPara(String para) {
        this.para = para;
    }

    public String getDe() {
        return de;
    }

    public void setDe(String de) {
        this.de = de;
    }

    public String getAssunto() {
        return assunto;
    }

    public void setAssunto(String assunto) {
        this.assunto = assunto;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getTempostamp() {
        return tempostamp.get();
    }

    public void setTempostamp(long tempostamp) {
        this.tempostamp.set(tempostamp);
    }

    public LongProperty tempostampProperty() {
        return tempostamp;
    }

    public Long getLidastamp() {
        return lidastamp;
    }

    public void setLidastamp(Long lidastamp) {
        this.lidastamp = lidastamp;
    }

    public String getLidaQuem() {
        return lidaQuem;
    }

    public void setLidaQuem(String lidaQuem) {
        this.lidaQuem = lidaQuem;
    }

    public boolean isLida() {
        return lida.get();
    }

    public void setLida(boolean lida) {
        this.lida.set(lida);
    }

    public SimpleBooleanProperty lidaProperty() {
        return lida;
    }

    public boolean isArquivada() {
        return arquivada.get();
    }

    public void setArquivada(boolean arquivada) {
        this.arquivada.set(arquivada);
    }

    public BooleanProperty arquivadaProperty() {
        return arquivada;
    }

    public String getDataTxt() {
        return dataTxt;
    }

    public void setDataTxt(String dataTxt) {
        this.dataTxt = dataTxt;
    }
}
