package objectos;

import bamer.ControllerEcraTabelaSMS;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import pojos.SMS;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

/**
 * by miguel.silva on 14-02-2017.
 */
public class GridListaSMS {
    public GridListaSMS(Machina machina, boolean lida, ArrayList<SMS> lista) throws IOException {
        URL location = ClassLoader.getSystemResource("fxml/ecraSMS.fxml");
        FXMLLoader loader = new FXMLLoader(location);
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setTitle("Lista SMS" + (lida ? " " : " não ") + "lidas em " + machina.getCodigo());
        Scene scene = new Scene(root);
        stage.setScene(scene);
        ControllerEcraTabelaSMS controller = loader.getController();
        controller.populate(lista);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UTILITY);
        stage.show();
    }
}
