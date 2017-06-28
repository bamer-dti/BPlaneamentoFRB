package pojos;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * by miguel.silva on 20-02-2017.
 */
@SuppressWarnings("unused")
public class ObjWeatherUnderground {
    public SimpleLongProperty local_epoch = new SimpleLongProperty();
    public SimpleDoubleProperty temp_c = new SimpleDoubleProperty();
    public SimpleStringProperty relative_humidity = new SimpleStringProperty();
    public SimpleStringProperty wind_dir = new SimpleStringProperty();
    public SimpleIntegerProperty wind_degrees = new SimpleIntegerProperty();
    public SimpleDoubleProperty wind_gust_kph = new SimpleDoubleProperty();
    public SimpleDoubleProperty pressure_mb = new SimpleDoubleProperty();
    public SimpleDoubleProperty feelslike_c = new SimpleDoubleProperty();
    public SimpleStringProperty visibility_km = new SimpleStringProperty();
    public SimpleStringProperty solarradiation = new SimpleStringProperty();
    public SimpleDoubleProperty uv = new SimpleDoubleProperty();
    public SimpleIntegerProperty precip_today_metric = new SimpleIntegerProperty();
    public SimpleStringProperty icon_url = new SimpleStringProperty();
    public SimpleDoubleProperty wind_kph = new SimpleDoubleProperty();
    public SimpleStringProperty icon = new SimpleStringProperty();
    public SimpleStringProperty city = new SimpleStringProperty();

    public ObjWeatherUnderground() {

    }

    public long getLocal_epoch() {
        return local_epoch.get();
    }

    public void setLocal_epoch(long local_epoch) {
        this.local_epoch.set(local_epoch);
    }

    public SimpleLongProperty local_epochProperty() {
        return local_epoch;
    }

    public double getTemp_c() {
        return temp_c.get();
    }

    public void setTemp_c(double temp_c) {
        this.temp_c.set(temp_c);
    }

    public SimpleDoubleProperty temp_cProperty() {
        return temp_c;
    }

    public String getRelative_humidity() {
        return relative_humidity.get();
    }

    public void setRelative_humidity(String relative_humidity) {
        this.relative_humidity.set(relative_humidity);
    }

    public SimpleStringProperty relative_humidityProperty() {
        return relative_humidity;
    }

    public String getWind_dir() {
        return wind_dir.get();
    }

    public void setWind_dir(String wind_dir) {
        this.wind_dir.set(wind_dir);
    }

    public SimpleStringProperty wind_dirProperty() {
        return wind_dir;
    }

    public int getWind_degrees() {
        return wind_degrees.get();
    }

    public void setWind_degrees(int wind_degrees) {
        this.wind_degrees.set(wind_degrees);
    }

    public SimpleIntegerProperty wind_degreesProperty() {
        return wind_degrees;
    }

    public double getWind_gust_kph() {
        return wind_gust_kph.get();
    }

    public void setWind_gust_kph(double wind_gust_kph) {
        this.wind_gust_kph.set(wind_gust_kph);
    }

    public SimpleDoubleProperty wind_gust_kphProperty() {
        return wind_gust_kph;
    }

    public double getPressure_mb() {
        return pressure_mb.get();
    }

    public void setPressure_mb(double pressure_mb) {
        this.pressure_mb.set(pressure_mb);
    }

    public SimpleDoubleProperty pressure_mbProperty() {
        return pressure_mb;
    }

    public double getFeelslike_c() {
        return feelslike_c.get();
    }

    public void setFeelslike_c(double feelslike_c) {
        this.feelslike_c.set(feelslike_c);
    }

    public SimpleDoubleProperty feelslike_cProperty() {
        return feelslike_c;
    }

    public String getSolarradiation() {
        return solarradiation.get();
    }

    public void setSolarradiation(String solarradiation) {
        this.solarradiation.set(solarradiation);
    }

    public SimpleStringProperty solarradiationProperty() {
        return solarradiation;
    }

    public double getUv() {
        return uv.get();
    }

    public void setUv(double uv) {
        this.uv.set(uv);
    }

    public SimpleDoubleProperty uvProperty() {
        return uv;
    }

    public int getPrecip_today_metric() {
        return precip_today_metric.get();
    }

    public void setPrecip_today_metric(int precip_today_metric) {
        this.precip_today_metric.set(precip_today_metric);
    }

    public SimpleIntegerProperty precip_today_metricProperty() {
        return precip_today_metric;
    }

    public String getIcon_url() {
        return icon_url.get();
    }

    public void setIcon_url(String icon_url) {
        this.icon_url.set(icon_url);
    }

    public SimpleStringProperty icon_urlProperty() {
        return icon_url;
    }

    public double getWind_kph() {
        return wind_kph.get();
    }

    public void setWind_kph(double wind_kph) {
        this.wind_kph.set(wind_kph);
    }

    public SimpleDoubleProperty wind_kphProperty() {
        return wind_kph;
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

    public String getVisibility_km() {
        return visibility_km.get();
    }

    public void setVisibility_km(String visibility_km) {
        this.visibility_km.set(visibility_km);
    }

    public SimpleStringProperty visibility_kmProperty() {
        return visibility_km;
    }

    public String getCity() {
        return city.get();
    }

    public void setCity(String city) {
        this.city.set(city);
    }

    public SimpleStringProperty cityProperty() {
        return city;
    }
}
