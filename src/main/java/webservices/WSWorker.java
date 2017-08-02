package webservices;

import bamer.ControllerEditar;
import com.google.firebase.internal.Log;
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
import pojos.ArtigoOSBO;
import sqlite.DBSQLite;
import utils.Campos;
import utils.Privado;
import utils.Procedimentos;
import utils.StackWorker;

import static java.lang.System.out;

public class WSWorker {
    public static void actualizarOSBO(StackWorker stackWorker, ArtigoOSBO artigoOSBO) {
        JSONObject json = artigoOSBO.toJSON();
        System.out.println(json);
        stackWorker.setBusy(true);
        Task<Void> tarefa = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Unirest.post(Privado.URL_ACTUALIZAR_OSBO).body(json).asJsonAsync(new Callback<JsonNode>() {
                    @Override
                    public void completed(HttpResponse<JsonNode> response) {
                        if (response.getStatus() != 200) {
                            String mensagem = response.getBody().getObject().getString(Campos.WS_MENSAGEM_OSBO);
                            System.out.println("Ocorreu um erro ao gravar " + json + "\n" + mensagem);
                            stackWorker.setBusy(false);
                        } else {
                            boolean ok = response.getBody().getObject().getBoolean(Campos.WS_OS_OSBO);
                            if (ok) {
                                System.out.println("Sucesso: " + response.getBody().toString());
                                int t = DBSQLite.getInstancia().deleteStack(artigoOSBO);
                                if (t == 0) {
                                    Log.wtf(WSWorker.class.getSimpleName(), "Erro ao eliminar do STACK", new Exception("Erro ao eliminar o STACK"));
                                }
                            } else {
                                System.out.println("ERRO SQLServer: " + response.getBody().toString());
                            }
                            stackWorker.setBusy(false);
                        }
                    }

                    @Override
                    public void failed(UnirestException e) {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                Procedimentos.alerta("Erro ao gravar os dados", e.getMessage(), Alert.AlertType.ERROR);
                            }
                        });
                    }

                    @Override
                    public void cancelled() {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                Procedimentos.alerta("O pedido WebService foi cancelado", "", Alert.AlertType.ERROR);
                            }
                        });

                    }
                });
                return null;
            }
        };
        new Thread(tarefa).run();
    }

    public static void editarDadosOP(Stage stage, VBoxOSBO contexto, ControllerEditar controller, String bostamp, String dtcortef, String dttransf, String dtembala, String dtexpedi
            , String estado, String dtoper) {
        JSONObject json = new JSONObject();
        json.put(Campos.BOSTAMP, bostamp);
        json.put(Campos.DTCORTEF, dtcortef);
        json.put(Campos.DTTRANSF, dttransf);
        json.put(Campos.DTEMBALA, dtembala);
        json.put(Campos.DTEXPEDI, dtexpedi);
        json.put(Campos.ESTADO, estado);
        json.put(Campos.DTOPER, dtoper);

        out.println("WSWorker " + json.toString());

        Task<Void> taskHttp = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Unirest.post(Privado.URL_ACTUALIZAR_COMPRMISSO).body(json).asJsonAsync(new Callback<JsonNode>() {
                    @Override
                    public void completed(HttpResponse<JsonNode> response) {
                        stage.close();
                        if (response.getStatus() != 200) {
                            Procedimentos.alerta("Erro ao gravar os dados", response.getStatusText(), Alert.AlertType.ERROR);
                        } else {
                            System.out.println("Sucesso: code" + response.getStatus() + "-> " + response.getBody().toString());
                        }
                    }

                    @Override
                    public void failed(UnirestException e) {
                        stage.close();
                        Procedimentos.alerta("Erro ao gravar os dados", e.getMessage(), Alert.AlertType.ERROR);
                    }

                    @Override
                    public void cancelled() {
                        stage.close();
                        Procedimentos.alerta("O pedido WebService foi cancelado", "", Alert.AlertType.ERROR);
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

    public void enviarSMS(Stage stage, JSONObject json) {
        stage.setTitle("A enviar...");
        Task<Void> taskHttp = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Unirest.post(Privado.URL_ENVIAR_SMS).body(json).asJsonAsync(new Callback<JsonNode>() {
                    @Override
                    public void completed(HttpResponse<JsonNode> response) {
                        if (response.getStatus() != 200) {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    Procedimentos.alerta("Erro ao gravar os dados", response.getStatusText(), Alert.AlertType.ERROR);
                                }
                            });

                        } else {
                            System.out.println("Sucesso: " + response.getBody().toString());
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    Procedimentos.alerta("Colocada em fila com sucesso", "", Alert.AlertType.INFORMATION);
                                    stage.close();
                                }
                            });
                        }
                    }

                    @Override
                    public void failed(UnirestException e) {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                Procedimentos.alerta("Erro ao gravar os dados", e.getMessage(), Alert.AlertType.ERROR);
                            }
                        });
                    }

                    @Override
                    public void cancelled() {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                Procedimentos.alerta("O pedido WebService foi cancelado", "", Alert.AlertType.ERROR);
                            }
                        });
                    }
                });
                return null;
            }
        };
        new Thread(taskHttp).run();
    }
}
