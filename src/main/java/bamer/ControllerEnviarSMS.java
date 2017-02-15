package bamer;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.json.JSONObject;
import webservices.WSWorker;

import java.net.URL;
import java.util.ResourceBundle;

import static utils.Constantes.SMS_MACHINA;

/**
 * by miguel.silva on 13-02-2017.
 */
public class ControllerEnviarSMS implements Initializable {
    @FXML
    public Label para;

    @FXML
    public Label tokenLabel;

    @FXML
    public TextArea mensagem;

    public int tipoSMS;
    public TextField assunto;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tokenLabel.setManaged(false);
    }

    public void gravar(ActionEvent actionEvent) {
        String texto = mensagem.getText();
        if (texto.equals("")) {
            System.out.println("O SMS está vazio!");
            alertaMensagemVazia();
            return;
        }
        System.out.println("A enviar o texto " + texto + " para " + para.getText());

        Node source = (Node) actionEvent.getSource();
        Stage stage = (Stage) source.getScene().getWindow();

        JSONObject json = new JSONObject();
        json.put("de", "planeamento");
        json.put("para", para.getText());
        json.put("token", tokenLabel.getText());
        json.put("assunto", assunto.getText());
        json.put("mensagem", mensagem.getText());
        json.put("bostamp", "");
        json.put("time_to_live", tipoSMS == SMS_MACHINA ? 3600 : 0);
        new WSWorker().enviarSMS(stage, json);
    }

    private void alertaMensagemVazia() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Dados em falta");
        alert.setHeaderText(null);
        alert.setContentText("Não pode enviar um SMS com mensagem vazia!");

        alert.showAndWait();
    }

    public void cancelar(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }
}
