package webservices;

import bamer.AppMain;
import bamer.ControllerEditar;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import objectos.VBoxOSBO;
import org.json.JSONObject;
import utils.Campos;
import utils.Funcoes;
import utils.Privado;

import java.util.concurrent.ExecutionException;

import static java.lang.System.out;

public class WSWorker {
    public static void actualizarOrdem(String bostamp, int ordemNova, String dtcortef, String dttransf, String seccao, String estado) throws ExecutionException, InterruptedException {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                AppMain.getInstancia().colocarObjectosVisiveis(false);
            }
        });
        JSONObject json = new JSONObject();
        json.put(Campos.BOSTAMP, bostamp);
        json.put(Campos.ORDEM, ordemNova);
        json.put(Campos.DTCORTEF, dtcortef);
        json.put(Campos.DTTRANSF, dttransf);
        json.put(Campos.SECCAO, seccao);
        json.put(Campos.ESTADO, estado);

        out.println("WSWorker " + json.toString());

        Task<Void> taskHttp = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Unirest.post(Privado.URL_ALTERAR_ORDEM).body(json).asJsonAsync(new Callback<JsonNode>() {
                    @Override
                    public void completed(HttpResponse<JsonNode> response) {
                        if (response.getStatus() != 200) {
                            Funcoes.alerta("Erro ao gravar os dados:", response.getStatusText(), Alert.AlertType.ERROR);
                        } else {
                            System.out.println("Sucesso: " + response.getBody().toString());
                        }
                    }

                    @Override
                    public void failed(UnirestException e) {
                        Funcoes.alerta("Erro ao gravar os dados:", e.getMessage(), Alert.AlertType.ERROR);
                    }

                    @Override
                    public void cancelled() {
                        Funcoes.alerta("O pedido WebService foi cancelado", "", Alert.AlertType.ERROR);
                    }
                });
                return null;
            }

            @Override
            protected void done() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        AppMain.getInstancia().colocarObjectosVisiveis(true);
                    }
                });
            }
        };
        new Thread(taskHttp).run();
    }

    public static void editarDadosOP(Stage stage, VBoxOSBO contexto, ControllerEditar controller, String bostamp, String dtcortef, String dttransf, String dtembala, String dtexpedi, String estado) {
        JSONObject json = new JSONObject();
        json.put(Campos.BOSTAMP, bostamp);
        json.put(Campos.DTCORTEF, dtcortef);
        json.put(Campos.DTTRANSF, dttransf);
        json.put(Campos.DTEMBALA, dtembala);
        json.put(Campos.DTEXPEDI, dtexpedi);
        json.put(Campos.ESTADO, estado);

        out.println("WSWorker " + json.toString());

        Task<Void> taskHttp = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Unirest.post(Privado.URL_ACTUALIZAR_COMPRMISSO).body(json).asJsonAsync(new Callback<JsonNode>() {
                    @Override
                    public void completed(HttpResponse<JsonNode> response) {
                        stage.close();
                        if (response.getStatus() != 200) {
                            Funcoes.alerta("Erro ao gravar os dados", response.getStatusText(), Alert.AlertType.ERROR);
                        } else {
                            System.out.println("Sucesso: " + response.getBody().toString());
                        }
                    }

                    @Override
                    public void failed(UnirestException e) {
                        stage.close();
                        Funcoes.alerta("Erro ao gravar os dados", e.getMessage(), Alert.AlertType.ERROR);
                    }

                    @Override
                    public void cancelled() {
                        stage.close();
                        Funcoes.alerta("O pedido WebService foi cancelado", "", Alert.AlertType.ERROR);
                    }
                });
                return null;
            }

            @Override
            protected void done() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        if (stage.isShowing()) {
                            contexto.dtcortefPropProperty().set(controller.dtcortef.getValue());
                            contexto.dttransfPropProperty().set(controller.dttransf.getValue());
                            contexto.dtembalaPropProperty().set(controller.dtembala.getValue());
                            contexto.dtexpediPropProperty().set(controller.dtexpedi.getValue());
                            stage.close();
                        }
                    }
                });
            }
        };
        new Thread(taskHttp).run();
    }

    public static void actualizarCor(String bostamp, int cor) {
        JSONObject json = new JSONObject();
        json.put(Campos.BOSTAMP, bostamp);
        json.put(Campos.COR, cor);

        out.println("WSWorker " + json.toString());

        Task<Void> taskHttp = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Unirest.post(Privado.URL_ACTUALIZAR_COR).body(json).asJsonAsync(new Callback<JsonNode>() {
                    @Override
                    public void completed(HttpResponse<JsonNode> response) {
                        if (response.getStatus() != 200) {
                            Funcoes.alerta("Erro ao gravar os dados", response.getStatusText(), Alert.AlertType.ERROR);
                        } else {
                            System.out.println("Sucesso: " + response.getBody().toString());
                        }
                    }

                    @Override
                    public void failed(UnirestException e) {
                        Funcoes.alerta("Erro ao gravar os dados", e.getMessage(), Alert.AlertType.ERROR);
                    }

                    @Override
                    public void cancelled() {
                        Funcoes.alerta("O pedido WebService foi cancelado", "", Alert.AlertType.ERROR);
                    }
                });
                return null;
            }
        };
        new Thread(taskHttp).run();
    }
}
