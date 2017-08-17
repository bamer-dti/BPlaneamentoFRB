package pojos;

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Created by miguel.silva on 29/06/2017.
 */
@SuppressWarnings("unused")
public class Estado {
    private SimpleStringProperty stamp = new SimpleStringProperty();
    private SimpleStringProperty titulo = new SimpleStringProperty();
    private SimpleLongProperty ordem = new SimpleLongProperty();
    private SimpleStringProperty tipo = new SimpleStringProperty();

    public String getStamp() {
        return stamp.get();
    }

    public void setStamp(String stamp) {
        this.stamp.set(stamp);
    }

    public SimpleStringProperty stampProperty() {
        return stamp;
    }

    public String getTitulo() {
        return titulo.get();
    }

    public void setTitulo(String titulo) {
        this.titulo.set(titulo);
    }

    public SimpleStringProperty tituloProperty() {
        return titulo;
    }

    public long getOrdem() {
        return ordem.get();
    }

    public void setOrdem(long ordem) {
        this.ordem.set(ordem);
    }

    public SimpleLongProperty ordemProperty() {
        return ordem;
    }

    public String getTipo() {
        return tipo.get();
    }

    public void setTipo(String tipo) {
        this.tipo.set(tipo);
    }

    public SimpleStringProperty tipoProperty() {
        return tipo;
    }
}
