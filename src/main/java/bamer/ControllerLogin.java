package bamer;

import com.google.firebase.internal.Log;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;
import utils.FirebaseAuthCustom;
import utils.Procedimentos;
import utils.Singleton;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ResourceBundle;

/**
 * by miguel.silva on 17/07/2017.
 */
public class ControllerLogin implements Initializable {
    public TextField tf_email;
    public PasswordField tf_password;
    public Button btlogin;
    public CheckBox chk_memorizar;
    public Label label_erro;
    public ProgressIndicator progresso;
    private Stage stage;
    private Scene scenePrincipal;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btlogin.setDisable(true);
        progresso.setVisible(true);
        label_erro.setText("A conectar...");
        label_erro.setTextFill(Color.GREEN);
        File f = new File("memo.txt");
        if (f.exists() && !f.isDirectory()) {
            ArrayList<String> linhas = new ArrayList<>();
            BufferedReader br;
            try {
                br = new BufferedReader(new FileReader(f));
                for (String line = br.readLine(); line != null; line = br.readLine()) {
                    linhas.add(line);
                }
                tf_email.setText(linhas.get(0));
                tf_password.setOnKeyPressed(new EventHandler<KeyEvent>() {
                    @Override
                    public void handle(KeyEvent event) {
                        if (event.getCode() == KeyCode.ENTER && !btlogin.isDisable()) {
                            btlogin.fire();
                        }
                    }
                });
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        tf_password.requestFocus();
                    }
                });

                chk_memorizar.setSelected(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void action_login() {
        progresso_Visivel(true);
        label_erro.setTextFill(Color.RED);
        String emailOuUser = tf_email.getText();
        String password = tf_password.getText();
        if (emailOuUser.equals("") || password.equals("")) {
            Procedimentos.alertaMinimo(Alert.AlertType.ERROR, "Inválido", "Dados em falta");
            progresso_Visivel(false);
            return;
        }

        try {
            //Verificar email e password via REST API
            JsonObject jsonObj = FirebaseAuthCustom.getInstance().auth2(emailOuUser + "@bamer.pt", password);
            System.out.println(jsonObj);
            if (jsonObj != null) {
                JsonElement idToken = jsonObj.get("idToken");
                if (idToken != null && jsonObj.get("idToken").getAsString() != null) {
                    Singleton.getInstancia().email_utilizador = emailOuUser;
                    Singleton.getInstancia().loginComSucesso = true;
                    stage.setScene(scenePrincipal);
                    //memorizar os dados
                    File ficheiro = new File("memo.txt");
                    if (chk_memorizar.isSelected()) {
                        Collection<String> linhas = new ArrayList<>();
                        linhas.add(emailOuUser);
                        try {
                            FileUtils.writeLines(ficheiro, linhas);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        //noinspection ResultOfMethodCallIgnored
                        ficheiro.delete();
                    }
                    progresso_Visivel(false);
                    return;
                }
                progresso_Visivel(false);
                String erro_retorno = jsonObj.getAsJsonObject("error").get("message").getAsString();
                Log.w("Check USER", erro_retorno);
                if (erro_retorno.equals("INVALID_PASSWORD")) {
                    label_erro.setText("Password errada");
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            tf_password.setText("");
                            tf_password.requestFocus();
                        }
                    });
                    return;
                }
                if (erro_retorno.equals("EMAIL_NOT_FOUND")) {
                    label_erro.setText("Utilizador não existe");
                    return;
                }
                if (erro_retorno.equals("TOO_MANY_ATTEMPTS_TRY_LATER")) {
                    label_erro.setText("várias tentativas falhadas, tente após 5s");
                    return;
                }
                label_erro.setText("Erro desconhecido... Tem internet?");
            }

        } catch (Exception e) {
            progresso_Visivel(false);
            e.printStackTrace();
        }

    }

    private void progresso_Visivel(boolean visivel) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                progresso.setVisible(visivel);
            }
        });
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setScenePrincipal(Scene scenePrincipal) {
        this.scenePrincipal = scenePrincipal;
    }
}
