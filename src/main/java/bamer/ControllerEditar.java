package bamer;

import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import utils.Singleton;

import java.net.URL;
import java.util.ResourceBundle;

///**
// * Created by miguel.silva on 29-07-2016.
// */
@SuppressWarnings("unchecked")
public class ControllerEditar implements Initializable {
    public DatePicker dttransf;
    public Label fref;
    public Label os;
    public Label obs;
    public DatePicker dtembala;
    public DatePicker dtexpedi;
    public Button btgravar;
    public DatePicker dtcortef;
    public ComboBox combo_estado;
    public DatePicker dtoper;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setData(String estado) {
        combo_estado.getItems().clear();
        combo_estado.getItems().addAll(Singleton.getInstancia().getLista_de_estados());
        combo_estado.setValue(estado);
    }
}
