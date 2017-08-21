package pojos;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

@SuppressWarnings("unused")
public class MaquinaStatus {
    SimpleBooleanProperty ligado = new SimpleBooleanProperty();
    SimpleStringProperty operador = new SimpleStringProperty();
    SimpleLongProperty unixtime = new SimpleLongProperty();

    public MaquinaStatus() {
    }

    public boolean isLigado() {
        return ligado.get();
    }

    public void setLigado(boolean ligado) {
        this.ligado.set(ligado);
    }

    public SimpleBooleanProperty ligadoProperty() {
        return ligado;
    }

    public String getOperador() {
        return operador.get();
    }

    public void setOperador(String operador) {
        this.operador.set(operador);
    }

    public SimpleStringProperty operadorProperty() {
        return operador;
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
