package pojos;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * by miguel.silva on 22-02-2017.
 */
@SuppressWarnings("unused")
public class ObjWUForecast {
    public SimpleStringProperty low = new SimpleStringProperty();
    public SimpleStringProperty data = new SimpleStringProperty();
    public SimpleLongProperty epoch = new SimpleLongProperty();
    public SimpleIntegerProperty period = new SimpleIntegerProperty();
    public SimpleStringProperty high = new SimpleStringProperty();
    public SimpleStringProperty icon = new SimpleStringProperty();
    public SimpleIntegerProperty maxwindkph = new SimpleIntegerProperty();
    public SimpleIntegerProperty maxwinddegrees = new SimpleIntegerProperty();
    public SimpleIntegerProperty avewindkph = new SimpleIntegerProperty();
    public SimpleIntegerProperty avewinddegrees = new SimpleIntegerProperty();
    public SimpleDoubleProperty avehumidity = new SimpleDoubleProperty();

    public ObjWUForecast() {

    }

    public String getHigh() {
        return high.get();
    }

    public void setHigh(String high) {
        this.high.set(high);
    }

    public SimpleStringProperty highProperty() {
        return high;
    }

    public int getPeriod() {
        return period.get();
    }

    public void setPeriod(int period) {
        this.period.set(period);
    }

    public SimpleIntegerProperty periodProperty() {
        return period;
    }

    public String getData() {
        return data.get();
    }

    public void setData(String data) {
        this.data.set(data);
    }

    public SimpleStringProperty dataProperty() {
        return data;
    }

    public long getEpoch() {
        return epoch.get();
    }

    public void setEpoch(long epoch) {
        this.epoch.set(epoch);
    }

    public SimpleLongProperty epochProperty() {
        return epoch;
    }

    public String getLow() {
        return low.get();
    }

    public void setLow(String low) {
        this.low.set(low);
    }

    public SimpleStringProperty lowProperty() {
        return low;
    }

    public String getIcon() {
        return icon.get();
    }

    public void setIcon(String icon) {
        this.icon.set(icon);
    }

    public SimpleStringProperty iconProperty() {
        return icon;
    }

    public int getMaxwindkph() {
        return maxwindkph.get();
    }

    public void setMaxwindkph(int maxwindkph) {
        this.maxwindkph.set(maxwindkph);
    }

    public SimpleIntegerProperty maxwindkphProperty() {
        return maxwindkph;
    }

    public int getMaxwinddegrees() {
        return maxwinddegrees.get();
    }

    public void setMaxwinddegrees(int maxwinddegrees) {
        this.maxwinddegrees.set(maxwinddegrees);
    }

    public SimpleIntegerProperty maxwinddegreesProperty() {
        return maxwinddegrees;
    }

    public int getAvewindkph() {
        return avewindkph.get();
    }

    public void setAvewindkph(int avewindkph) {
        this.avewindkph.set(avewindkph);
    }

    public SimpleIntegerProperty avewindkphProperty() {
        return avewindkph;
    }

    public int getAvewinddegrees() {
        return avewinddegrees.get();
    }

    public void setAvewinddegrees(int avewinddegrees) {
        this.avewinddegrees.set(avewinddegrees);
    }

    public SimpleIntegerProperty avewinddegreesProperty() {
        return avewinddegrees;
    }

    public double getAvehumidity() {
        return avehumidity.get();
    }

    public void setAvehumidity(double avehumidity) {
        this.avehumidity.set(avehumidity);
    }

    public SimpleDoubleProperty avehumidityProperty() {
        return avehumidity;
    }
}
