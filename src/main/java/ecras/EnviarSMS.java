package ecras;

import bamer.ControllerEnviarSMS;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import objectos.ButaoMachina;

import java.io.IOException;
import java.net.URL;

import static utils.Constantes.SMS_MACHINA;
import static utils.Constantes.SMS_OPERADOR;

/**
 * by miguel.silva on 15-02-2017.
 */
public class EnviarSMS {
    public EnviarSMS(int tipoSMS, Node nodeOrigem) throws IOException {
        URL location = ClassLoader.getSystemResource("fxml/enviarSMS.fxml");
        FXMLLoader loader = new FXMLLoader(location);
        Parent root = loader.load();
        Stage stageEnviarSMS = new Stage();
        stageEnviarSMS.setTitle("Enviar SMS");
        stageEnviarSMS.setScene(new Scene(root));
        ControllerEnviarSMS controller = loader.getController();
        switch (tipoSMS) {
            case 0:
                controller.configVazio();
                break;
            case SMS_MACHINA:
                ButaoMachina butaoMachina = (ButaoMachina) nodeOrigem;
                controller.paraLabel.textProperty().bind(butaoMachina.maquinaCodigoProp);
                controller.tokenLabel.textProperty().bind(butaoMachina.tokenProp);
                controller.hboxparam.setManaged(false);
                controller.hboxparam.setVisible(false);
                break;
            case SMS_OPERADOR:
                butaoMachina = (ButaoMachina) nodeOrigem;
                controller.paraLabel.textProperty().bind(butaoMachina.operadorProp);
                controller.tokenLabel.textProperty().bind(butaoMachina.tokenProp);
                controller.hboxparam.setManaged(false);
                controller.hboxparam.setVisible(false);
                break;
        }
        controller.tipoSMS = tipoSMS;

        stageEnviarSMS.initModality(Modality.APPLICATION_MODAL);
        stageEnviarSMS.initStyle(StageStyle.UTILITY);
        stageEnviarSMS.show();
    }
}
