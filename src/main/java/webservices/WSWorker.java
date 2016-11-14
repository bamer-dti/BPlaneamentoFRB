package webservices;

import bamer.AppMain;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import org.json.JSONObject;
import utils.Campos;
import utils.Funcoes;

import java.util.concurrent.ExecutionException;

import static java.lang.System.out;

public class WSWorker {

    private static final String URL_ALTERAR_ORDEM = "http://server.bamer.pt:99/bameros.svc/actualizar_ordem_planeamento";

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
                Unirest.post(URL_ALTERAR_ORDEM).body(json).asJsonAsync(new Callback<JsonNode>() {
                    @Override
                    public void completed(HttpResponse<JsonNode> response) {
                        if (response.getStatus() != 200) {
                            Funcoes.alerta("Erro ao gravar os dados:\n" + response.getStatusText(), Alert.AlertType.ERROR);
                        } else {
                            System.out.println("Sucesso: " + response.getBody().toString());
                        }
                    }

                    @Override
                    public void failed(UnirestException e) {
                        Funcoes.alerta("Erro ao gravar os dados:\n" + e.getMessage(), Alert.AlertType.ERROR);
                    }

                    @Override
                    public void cancelled() {
                        Funcoes.alerta("O pedido WebService foi cancelado", Alert.AlertType.ERROR);
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
}
