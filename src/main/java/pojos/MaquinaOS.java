package pojos;

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

@SuppressWarnings("unused")
public class MaquinaOS {
    private SimpleStringProperty bostamp = new SimpleStringProperty();
    private SimpleStringProperty key_bo = new SimpleStringProperty();
    private SimpleLongProperty obrano = new SimpleLongProperty();
    private SimpleLongProperty unixtime = new SimpleLongProperty();

    public MaquinaOS() {

    }

    public String getBostamp() {
        return bostamp.get();
    }

    public void setBostamp(String bostamp) {
        this.bostamp.set(bostamp);
    }

    public SimpleStringProperty bostampProperty() {
        return bostamp;
    }

    public String getKey_bo() {
        return key_bo.get();
    }

    public void setKey_bo(String key_bo) {
        this.key_bo.set(key_bo);
    }

    public SimpleStringProperty key_boProperty() {
        return key_bo;
    }

    public long getObrano() {
        return obrano.get();
    }

    public void setObrano(long obrano) {
        this.obrano.set(obrano);
    }

    public SimpleLongProperty obranoProperty() {
        return obrano;
    }

    public long getUnixtime() {
        return unixtime.get();
    }

    public void setUnixtime(long unixtime) {
        this.unixtime.set(unixtime);
    }

    public SimpleLongProperty unixtimeProperty() {
        return unixtime;
    }
}
