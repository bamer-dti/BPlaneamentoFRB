package bamer;

///**
// * Created by miguel.silva on 08-06-2016.
// */

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.*;
import com.google.firebase.internal.Log;
import com.jfoenix.controls.JFXButton;
import com.mashape.unirest.http.Unirest;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import objectos.GridPaneAtrasados;
import objectos.GridPaneCalendario;
import objectos.GridPanePorPlanear;
import objectos.VBoxOSBO;
import pojos.*;
import sqlite.DBSQLite;
import sqlite.PreferenciasEmSQLite;
import utils.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AppMain extends Application {
    private static final int MINIMO_COLUNAS = 4; //dias = + 1
    private static final String TAG = AppMain.class.getSimpleName();
    private static final int ADICIONAR = 1;
    private static final int ACTUALIZAR = 2;
    private static final int REMOVER = 3;
    private static AppMain app;
    public BorderPane borderPaneAtrasados;
    public BorderPane borderPanePorPlanear;
    private String seccao;
    private Label labelCols;
    private GridPaneCalendario calendario;
    private Stage stagePorPlanear;
    private ScrollPane scrollPaneCalendario;
    private GridPaneCalendario calendarioTopo;
    private ScrollPane scrollPaneTopo;
    private ComboBox<String> comboSeccao;
    private Stage taskUpdateStage;
    private Stage stageAtrasados;
    private JFXButton but_atrasados;
    private JFXButton but_porPlanear;
    private TextField textFieldFiltroPorPlanear;
    private Label labelTotRecsPorPlanear;
    private TextField textFieldFiltroFrefAtrasados;
    private Label labelTotRecsAtrasados;
    private Stage mainStage;
    private ProgressIndicator progressIndicator;
    private DBSQLite sqlite;
    private ChildEventListener listenerFirebaseOSBO;
    private ChildEventListener listenerFirebaseOSBOPLAN;
    private ChildEventListener listenerFirebaseOSBI03;
    private ChildEventListener listenerFirebaseOSPROD;
    private ChildEventListener listenerFirebaseOSTIMER;
    private JFXButton but_mais;
    private JFXButton but_menos;
    private List<ArtigoLinhaPlanOUAtraso> listaDeAtrasos;

    public static void main(String[] args) {
        launch(args);
    }

    public static AppMain getInstancia() {
        return app;
    }

    public static void showExceptionDialog(Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);

        alert.setTitle("Exception Dialog");
        alert.setHeaderText("An error occurred:");

        String content = "Error: ";
        if (null != e) {
            content += e.toString() + "\n\n";
        }

        alert.setContentText(content);

        Exception ex = new Exception(e);

        //Create expandable Exception.
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);

        String exceptionText = sw.toString();

        //Set up TextArea
        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);


        textArea.setPrefHeight(600);
        textArea.setPrefWidth(800);


        //Set expandable Exception into the dialog pane.
        alert.getDialogPane().setExpandableContent(textArea);


        alert.showAndWait();
    }

    @Override
    public void start(Stage mainStage) {
        if (app == null) {
            app = this;
        }

        sqlite = DBSQLite.getInstancia();

        setMainStage(mainStage);

        BorderPane borderPane = new BorderPane();

        calendario = new GridPaneCalendario(GridPaneCalendario.TIPO_GRELHA);
        calendarioTopo = new GridPaneCalendario(GridPaneCalendario.TIPO_TOPO);

        scrollPaneTopo = new ScrollPane(calendarioTopo);
//        scrollPaneTopo.setFitToWidth(true);
        scrollPaneTopo.setMinHeight(43);
//        scrollPaneTopo.setPrefHeight();
//        scrollPaneTopo.setFitToHeight(true);
        scrollPaneTopo.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPaneTopo.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
//        scrollPaneTopo.getStyleClass().add("scroll-bar");

        scrollPaneCalendario = new ScrollPane(calendario);
        scrollPaneCalendario.setFitToWidth(true);
        scrollPaneCalendario.setFitToHeight(true);
        scrollPaneCalendario.getStyleClass().add("scroll-bar");

        scrollPaneTopo.hvalueProperty().bindBidirectional(scrollPaneCalendario.hvalueProperty());

        VBox vBox = new VBox();
        vBox.getChildren().addAll(scrollPaneTopo, scrollPaneCalendario);
        VBox.setVgrow(scrollPaneCalendario, Priority.ALWAYS);
        borderPane.setCenter(vBox);

        HBox topBox = new HBox();
        topBox.setId("topBox");

        labelCols = new Label("0");
        updateLabelCols();

        but_menos = new JFXButton("-");
        but_menos.getStyleClass().add("button-raised-bamer");
        but_menos.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                PreferenciasEmSQLite prefs = PreferenciasEmSQLite.getInstancia();
                int colunas = prefs.getInt(Constantes.PREF_AGENDA_NUMCOLS, ValoresDefeito.AGENDA_NUMCOLS);
                if (colunas < MINIMO_COLUNAS + 2)
                    return;
                colunas--;
                prefs.putInt(Constantes.PREF_AGENDA_NUMCOLS, colunas);

                setColunas();
                updateLabelCols();
            }
        });
        HBox.setMargin(but_menos, new Insets(10f, 10f, 10f, 10f));
        topBox.getChildren().add(but_menos);

        but_mais = new JFXButton("+");
        but_mais.getStyleClass().add("button-raised-bamer");
        but_mais.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                PreferenciasEmSQLite prefs = PreferenciasEmSQLite.getInstancia();
                int colunas = prefs.getInt(Constantes.PREF_AGENDA_NUMCOLS, ValoresDefeito.AGENDA_NUMCOLS);
                colunas++;
                prefs.putInt(Constantes.PREF_AGENDA_NUMCOLS, colunas);

                setColunas();
                updateLabelCols();
            }
        });
        HBox.setMargin(but_mais, new Insets(10f, 10f, 10f, 10f));
        topBox.getChildren().add(but_mais);

        HBox.setMargin(labelCols, new Insets(10f, 10f, 10f, 10f));
        labelCols.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.isPrimaryButtonDown() && event.getClickCount() > 1) {
                    PreferenciasEmSQLite prefs = PreferenciasEmSQLite.getInstancia();
                    int colunas = prefs.getInt(Constantes.PREF_AGENDA_NUMCOLS, ValoresDefeito.AGENDA_NUMCOLS);
                    TextInputDialog dialog = new TextInputDialog("" + colunas);
                    dialog.setTitle("Configuração de colunas");
//                    alerta.setHeaderText("Look, a Text Input Dialog");
                    dialog.setContentText("Nº de colunas:");

                    Optional<String> result = dialog.showAndWait();
                    if (result.isPresent()) {
                        prefs.putInt(Constantes.PREF_AGENDA_NUMCOLS, Integer.parseInt(result.get()));
                        setColunas();
                        labelCols.setText(result.get());
                    }
                }
            }
        });
        topBox.getChildren().add(labelCols);

        //BOTÃO POR AGENDAR (ESTADO 00)
        but_porPlanear = new JFXButton("por planear (0)");
        but_porPlanear.getStyleClass().add("button-raised-bamer-aprovados");
        but_porPlanear.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (!stagePorPlanear.isShowing()) {
                    stagePorPlanear.show();
                }
                stagePorPlanear.toFront();
            }
        });
        HBox.setMargin(but_porPlanear, new Insets(10f, 10f, 10f, 10f));
        topBox.getChildren().add(but_porPlanear);

        //BOTÂO ATRASADOS
        but_atrasados = new JFXButton("atrasos");
        but_atrasados.getStyleClass().add("button-raised-bamer-aprovados");
        but_atrasados.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                GridPaneAtrasados.actualizarLista();
                if (!stageAtrasados.isShowing()) {
                    stageAtrasados.show();
                }
                stageAtrasados.toFront();
            }
        });
        HBox.setMargin(but_atrasados, new Insets(10f, 10f, 10f, 10f));
        topBox.getChildren().add(but_atrasados);

        //COMBOBOX CENTRO
        PreferenciasEmSQLite prefs = PreferenciasEmSQLite.getInstancia();
        seccao = prefs.get(Constantes.PREF_SECCAO, ValoresDefeito.SECCAO);
        comboSeccao = new ComboBox<>();
        comboSeccao.getSelectionModel().select(seccao);
//                comboSeccao.getItems().add(queryRow.getKey().toString());
        comboSeccao.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                System.out.println("Seccção alterada de " + oldValue + " para " + newValue);
                seccao = newValue;
                if (oldValue.equals(newValue)) {
                    return;
                }
                PreferenciasEmSQLite prefs = PreferenciasEmSQLite.getInstancia();
                prefs.put(Constantes.PREF_SECCAO, newValue);
                setColunas();
//                actualizarTextoColunasZero();
            }
        });
        comboSeccao.setVisible(false);
        topBox.getChildren().add(comboSeccao);

        Region region = new Region();
        HBox.setHgrow(region, Priority.ALWAYS);
        topBox.getChildren().add(region);

        progressIndicator = new ProgressIndicator();

        HBox.setMargin(progressIndicator, new Insets(2, 30, 0, 30));
        progressIndicator.setProgress(100d);
        progressIndicator.setPrefWidth(30);
        topBox.getChildren().add(progressIndicator);

        topBox.setAlignment(Pos.CENTER_LEFT);

        borderPane.setTop(topBox);

        Scene scenePrincipal = new Scene(borderPane, 1600, 780);
        scenePrincipal.getStylesheets().add("styles.css");

        mainStage.setScene(scenePrincipal);
        mainStage.getIcons().add(Funcoes.iconeBamer());
        mainStage.setTitle(ValoresDefeito.TITULO_APP);
        mainStage.show();

        mainStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                stagePorPlanear.close();
                stageAtrasados.close();
                try {
                    Unirest.shutdown();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Platform.exit();
                System.exit(0);
            }
        });

        iniciarFirebase();

        colocarObjectosVisiveis(false);

        configurarTaskStage();

        configurarStageAtrasados();

        configurarStagePorPlanear();
    }

    public void colocarObjectosVisiveis(boolean isVisivel) {
        if (!isVisivel) {
            progressIndicator.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        } else {
            progressIndicator.setProgress(100);
        }

        but_porPlanear.setVisible(isVisivel);
        but_atrasados.setVisible(isVisivel);
        but_menos.setVisible(isVisivel);
        but_mais.setVisible(isVisivel);
        labelCols.setVisible(isVisivel);
        comboSeccao.setVisible(isVisivel);
    }

    private void iniciarFirebase() {

        DBSQLite.getInstancia().resetDados();

        try {
            InputStream file = ClassLoader.getSystemResourceAsStream("firebase_auth.json");
            System.out.println("Tamanho do ficheiro JSON: " + file.available());
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setServiceAccount(file)
                    .setDatabaseUrl("https://bamer-os-section.firebaseio.com/")
                    .build();

            FirebaseApp.initializeApp(options);

        } catch (IOException e) {
            e.printStackTrace();
        }

        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            public static final String TAG = "CHECK_FIREBASE";

            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                Log.i(TAG, "FIREBAS ONLINE: " + connected);
                if (connected) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            taskUpdateStage.hide();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled");
            }
        });

        configurarListenersOSBO();
        DatabaseReference refDataFireBase = FirebaseDatabase.getInstance().getReference(Campos.KEY_OSBO);
        refDataFireBase.addChildEventListener(listenerFirebaseOSBO);

        configurarListenerOSBI();
        refDataFireBase = FirebaseDatabase.getInstance().getReference(Campos.KEY_OSBI03);
        refDataFireBase.addChildEventListener(listenerFirebaseOSBI03);

        configurarListenerOSPROD();
        refDataFireBase = FirebaseDatabase.getInstance().getReference(Campos.KEY_OSPROD);
        refDataFireBase.addChildEventListener(listenerFirebaseOSPROD);

        configurarListenerOSTIMER();
        refDataFireBase = FirebaseDatabase.getInstance().getReference(Campos.KEY_OSTIMER);
        refDataFireBase.addChildEventListener(listenerFirebaseOSTIMER);

        configurarListenerOSBOPLAN();
        refDataFireBase = FirebaseDatabase.getInstance().getReference(Campos.KEY_OSBOPLAN);
        refDataFireBase.addChildEventListener(listenerFirebaseOSBOPLAN);

        //ALIMENTAR COMBO SECÇÃO
        FirebaseDatabase.getInstance().getReference(Campos.KEY_SECCAO).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    comboSeccao.getItems().add(d.getKey());
                }
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        colocarObjectosVisiveis(true);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void configurarListenersOSBO() {
        listenerFirebaseOSBO = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                Log.i(TAG, "OSBO: onChildAdded: " + dataSnapshot.toString());
                Task<DataSnapshot> taskInserirObjectoOSBO = new Task<DataSnapshot>() {
                    @Override
                    protected DataSnapshot call() throws Exception {
                        ArrayList<ArtigoOSBO> lista = new ArrayList<>();
                        String bostamp = dataSnapshot.getKey();
                        ArtigoOSBO artigoOSBO = dataSnapshot.getValue(ArtigoOSBO.class);
                        artigoOSBO.setBostamp(bostamp);
                        sqlite.guardarOSBO(artigoOSBO);
                        if (artigoOSBO.getSeccao().equals(seccao)) {
                            lista.add(artigoOSBO);
                            actualizarGrelhaCalendario(lista, ADICIONAR);
                        }
                        return null;
                    }
                };
                new Thread(taskInserirObjectoOSBO).run();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//                Log.i(TAG, "OSBO onChildChanged: " + dataSnapshot.toString());
                Task<DataSnapshot> tarefa = new Task<DataSnapshot>() {
                    @Override
                    protected DataSnapshot call() throws Exception {
                        ArrayList<ArtigoOSBO> lista = new ArrayList<>();
                        String bostamp = dataSnapshot.getKey();
                        ArtigoOSBO artigoOSBO = dataSnapshot.getValue(ArtigoOSBO.class);
                        artigoOSBO.setBostamp(bostamp);
                        sqlite.actualizarOSBO(artigoOSBO);
                        lista.add(artigoOSBO);
                        actualizarGrelhaCalendario(lista, ACTUALIZAR);
                        return null;
                    }
                };
                new Thread(tarefa).run();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
//                Log.i(TAG, "OSBO onChildRemoved: " + dataSnapshot.toString());
                Task<DataSnapshot> tarefa = new Task<DataSnapshot>() {
                    @Override
                    protected DataSnapshot call() throws Exception {
                        ArrayList<ArtigoOSBO> lista = new ArrayList<>();
                        String bostamp = dataSnapshot.getKey();
                        ArtigoOSBO artigoOSBO = dataSnapshot.getValue(ArtigoOSBO.class);
                        artigoOSBO.setBostamp(bostamp);
                        sqlite.removerOSBO(artigoOSBO);
                        sqlite.removerOSBIbostamp(artigoOSBO);
                        lista.add(artigoOSBO);
                        actualizarGrelhaCalendario(lista, REMOVER);
                        return null;
                    }
                };
                new Thread(tarefa).run();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    private void configurarListenerOSBOPLAN() {
        listenerFirebaseOSBOPLAN = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                Log.i(TAG, "OSBOPLAN: onChildAdded: " + dataSnapshot.toString());
                Task<DataSnapshot> taskInserirObjectoOSBO = new Task<DataSnapshot>() {
                    @Override
                    protected DataSnapshot call() throws Exception {
                        String bostamp = dataSnapshot.getKey();
                        ArtigoLinhaPlanOUAtraso artigoOSBO = dataSnapshot.getValue(ArtigoLinhaPlanOUAtraso.class);
                        artigoOSBO.setBostamp(bostamp);
                        sqlite.guardarOSBOPLAN(artigoOSBO);
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                but_porPlanear.setText("por planear (" + new DBSQLite().getNumPlaneamento() + ")");
                            }
                        });
                        GridPanePorPlanear.actualizarLista();
                        return null;
                    }
                };
                new Thread(taskInserirObjectoOSBO).run();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.i(TAG, "OSBOPLAN onChildChanged: " + dataSnapshot.toString());
                Task<DataSnapshot> tarefa = new Task<DataSnapshot>() {
                    @Override
                    protected DataSnapshot call() throws Exception {
                        String bostamp = dataSnapshot.getKey();
                        ArtigoLinhaPlanOUAtraso artigoLinhaPlanOUAtraso = dataSnapshot.getValue(ArtigoLinhaPlanOUAtraso.class);
                        artigoLinhaPlanOUAtraso.setBostamp(bostamp);
                        sqlite.actualizarOSBOPLAN(artigoLinhaPlanOUAtraso);
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                but_porPlanear.setText("por planear (" + new DBSQLite().getNumPlaneamento() + ")");
                            }
                        });
                        GridPanePorPlanear.actualizarLista();
                        return null;
                    }
                };
                new Thread(tarefa).run();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.i(TAG, "OSBO onChildRemoved: " + dataSnapshot.toString());
                Task<DataSnapshot> tarefa = new Task<DataSnapshot>() {
                    @Override
                    protected DataSnapshot call() throws Exception {
                        String bostamp = dataSnapshot.getKey();
                        ArtigoLinhaPlanOUAtraso artigoOSBO = dataSnapshot.getValue(ArtigoLinhaPlanOUAtraso.class);
                        artigoOSBO.setBostamp(bostamp);
                        sqlite.removerOSBOPLAN(artigoOSBO);
                        GridPanePorPlanear.actualizarLista();
                        return null;
                    }
                };
                new Thread(tarefa).run();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

    }

    private void configurarListenerOSBI() {
        listenerFirebaseOSBI03 = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                Log.i(TAG, "OSBI03: onChildAdded: " + dataSnapshot.toString());
                Task<DataSnapshot> tarefa = new Task<DataSnapshot>() {
                    @Override
                    protected DataSnapshot call() throws Exception {
                        String bostamp = dataSnapshot.getKey();
                        for (DataSnapshot d : dataSnapshot.getChildren()) {
                            String bistamp = d.getKey();
                            ArtigoOSBI artigoOSBI = d.getValue(ArtigoOSBI.class);
                            artigoOSBI.setBostamp(bostamp);
                            artigoOSBI.setBistamp(bistamp);
                            sqlite.guardarOSBI(artigoOSBI);
                        }
                        actualizarQtdPedida(bostamp);
                        return null;
                    }
                };
                new Thread(tarefa).run();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.i(TAG, "OSBI onChildChanged: " + dataSnapshot.toString());
                Task<DataSnapshot> tarefa = new Task<DataSnapshot>() {
                    @Override
                    protected DataSnapshot call() throws Exception {
                        String bostamp = dataSnapshot.getKey();
                        sqlite.removerOSBIbostamp(new ArtigoOSBO(bostamp));
                        for (DataSnapshot d : dataSnapshot.getChildren()) {
                            String bistamp = d.getKey();
                            ArtigoOSBI artigoOSBI = d.getValue(ArtigoOSBI.class);
                            artigoOSBI.setBostamp(bostamp);
                            artigoOSBI.setBistamp(bistamp);
                            sqlite.guardarOSBI(artigoOSBI);
                        }
                        actualizarQtdPedida(bostamp);
                        return null;
                    }
                };
                new Thread(tarefa).run();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.i(TAG, "OSBI onChildRemoved: " + dataSnapshot.toString());
                Task<DataSnapshot> tarefa = new Task<DataSnapshot>() {
                    @Override
                    protected DataSnapshot call() throws Exception {
                        String bostamp = dataSnapshot.getKey();
                        for (DataSnapshot d : dataSnapshot.getChildren()) {
                            String bistamp = d.getKey();
                            ArtigoOSBI artigoOSBI = d.getValue(ArtigoOSBI.class);
                            artigoOSBI.setBostamp(bostamp);
                            artigoOSBI.setBistamp(bistamp);
                            sqlite.removerOSBIbostamp(artigoOSBI);
                        }
                        actualizarQtdPedida(bostamp);
                        return null;
                    }
                };
                new Thread(tarefa).run();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    private void configurarListenerOSPROD() {
        listenerFirebaseOSPROD = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                Log.i(TAG, "OSBOPROD: onChildAdded: " + dataSnapshot.toString());
                Task<DataSnapshot> tarefa = new Task<DataSnapshot>() {
                    @Override
                    protected DataSnapshot call() throws Exception {
                        String bostamp = dataSnapshot.getKey();
                        for (DataSnapshot d : dataSnapshot.getChildren()) {
                            String bistamp = d.getKey();
                            ArtigoOSPROD artigoOSPROD = d.getValue(ArtigoOSPROD.class);
                            artigoOSPROD.setBostamp(bostamp);
                            artigoOSPROD.setBistamp(bistamp);
                            sqlite.guardarOSPROD(artigoOSPROD);
                        }
                        actualizarQtdProduzida(bostamp);
                        return null;
                    }
                };
                new Thread(tarefa).run();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.i(TAG, "OSPROD onChildChanged: " + dataSnapshot.toString());
                Task<DataSnapshot> tarefa = new Task<DataSnapshot>() {
                    @Override
                    protected DataSnapshot call() throws Exception {
                        String bostamp = dataSnapshot.getKey();
                        for (DataSnapshot d : dataSnapshot.getChildren()) {
                            String bistamp = d.getKey();
                            ArtigoOSPROD artigoOSPROD = d.getValue(ArtigoOSPROD.class);
                            artigoOSPROD.setBostamp(bostamp);
                            artigoOSPROD.setBistamp(bistamp);
                            sqlite.actualizarOSPROD(artigoOSPROD);
                        }
                        actualizarQtdProduzida(bostamp);
                        return null;
                    }
                };
                new Thread(tarefa).run();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.i(TAG, "OSBI onChildRemoved: " + dataSnapshot.toString());
                Task<DataSnapshot> tarefa = new Task<DataSnapshot>() {
                    @Override
                    protected DataSnapshot call() throws Exception {
                        String bostamp = dataSnapshot.getKey();
                        for (DataSnapshot d : dataSnapshot.getChildren()) {
                            String bistamp = d.getKey();
                            ArtigoOSPROD artigoOSBI = d.getValue(ArtigoOSPROD.class);
                            artigoOSBI.setBostamp(bostamp);
                            artigoOSBI.setBistamp(bistamp);
                            sqlite.removerOSPROD(artigoOSBI);
                        }
                        actualizarQtdProduzida(bostamp);
                        return null;
                    }
                };
                new Thread(tarefa).run();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    private void configurarListenerOSTIMER() {
        listenerFirebaseOSTIMER = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                Log.i(TAG, "OSTIMER: onChildAdded: " + dataSnapshot.toString());
                Task<DataSnapshot> tarefa = new Task<DataSnapshot>() {
                    @Override
                    protected DataSnapshot call() throws Exception {
                        String bostamp = dataSnapshot.getKey();
                        for (DataSnapshot d : dataSnapshot.getChildren()) {
                            String stamp = d.getKey();
                            ArtigoOSTIMER artigoOSTIMER = d.getValue(ArtigoOSTIMER.class);
                            artigoOSTIMER.setBostamp(bostamp);
                            artigoOSTIMER.setStamp(stamp);
                            sqlite.guardarOSTIMER(artigoOSTIMER);
                        }
                        actualizarTempo(bostamp);
                        return null;
                    }
                };
                new Thread(tarefa).run();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.i(TAG, "OSTIMER onChildChanged: " + dataSnapshot.toString());
                Task<DataSnapshot> tarefa = new Task<DataSnapshot>() {
                    @Override
                    protected DataSnapshot call() throws Exception {
                        String bostamp = dataSnapshot.getKey();
                        for (DataSnapshot d : dataSnapshot.getChildren()) {
                            String stamp = d.getKey();
                            ArtigoOSTIMER artigoOSTIMER = d.getValue(ArtigoOSTIMER.class);
                            artigoOSTIMER.setBostamp(bostamp);
                            artigoOSTIMER.setStamp(stamp);
                            sqlite.actualizarOSTIMER(artigoOSTIMER);
                        }
                        actualizarTempo(bostamp);
                        return null;
                    }
                };
                new Thread(tarefa).run();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.i(TAG, "OSTIMER onChildRemoved: " + dataSnapshot.toString());
                Task<DataSnapshot> tarefa = new Task<DataSnapshot>() {
                    @Override
                    protected DataSnapshot call() throws Exception {
                        String bostamp = dataSnapshot.getKey();
                        for (DataSnapshot d : dataSnapshot.getChildren()) {
                            String stamp = d.getKey();
                            ArtigoOSTIMER artigoOSTIMER = d.getValue(ArtigoOSTIMER.class);
                            artigoOSTIMER.setBostamp(bostamp);
                            artigoOSTIMER.setStamp(stamp);
                            sqlite.removerOSTIMER(artigoOSTIMER);
                        }
                        actualizarTempo(bostamp);
                        return null;
                    }
                };
                new Thread(tarefa).run();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    private void actualizarTempo(String bostamp) {
        VBoxOSBO vBoxOSBO = (VBoxOSBO) calendario.lookup("#" + bostamp);
        if (vBoxOSBO != null) {
            vBoxOSBO.actualizarCronometros();
        }
    }

    private void actualizarQtdPedida(String bostamp) {
        VBoxOSBO vBoxOSBO = (VBoxOSBO) calendario.lookup("#" + bostamp);
        if (vBoxOSBO != null) {
            vBoxOSBO.actualizarQtdPedida();
        }
    }

    private void actualizarQtdProduzida(String bostamp) {
        VBoxOSBO vBoxOSBO = (VBoxOSBO) calendario.lookup("#" + bostamp);
        if (vBoxOSBO != null) {
            vBoxOSBO.actualizarQtdProduzida();
        }
    }

    public void actualizarGrelhaCalendario(ArrayList<ArtigoOSBO> listaDocsOSBO, int operacao) {
        GridPaneAtrasados.actualizarLista();

        switch (operacao) {
            case ADICIONAR:
                for (ArtigoOSBO artigoOSBO : listaDocsOSBO) {
                    if (artigoOSBO.getSeccao().equals(seccao)) {
                        new VBoxOSBO(artigoOSBO);
                    }
                }
                break;

            case ACTUALIZAR:
                for (ArtigoOSBO artigoOSBO : listaDocsOSBO) {
                    VBoxOSBO vBoxOSBO = (VBoxOSBO) calendario.lookup("#" + artigoOSBO.getBostamp());
                    //O artigo já existe
                    if (vBoxOSBO != null) {
                        //Já não pertence à mesma seccção
                        if (!artigoOSBO.getSeccao().equals(seccao)) {
                            ArrayList<ArtigoOSBO> listaUnica = new ArrayList<>();
                            listaUnica.add(artigoOSBO);
                            actualizarGrelhaCalendario(listaUnica, REMOVER);
                            continue;
                        }
                        //Pertence à mesma secção, actualizarOSBO
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                vBoxOSBO.setArtigoOSBOProp(artigoOSBO);
                            }
                        });
                        continue;
                    }
                    //O artigo ainda não existe, lançar novo
                    ArrayList<ArtigoOSBO> listaUnica = new ArrayList<>();
                    listaUnica.add(artigoOSBO);
                    actualizarGrelhaCalendario(listaUnica, ADICIONAR);
                }
                break;

            case REMOVER:
                for (ArtigoOSBO artigoOSBO : listaDocsOSBO) {
                    VBoxOSBO vBoxOSBO = (VBoxOSBO) calendario.lookup("#" + artigoOSBO.getBostamp());
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            calendario.getChildren().removeAll(vBoxOSBO);
                        }
                    });
                }
                break;

            default:
                break;
        }
    }

    private void configurarTaskStage() {
        final double wndwWidth = 300.0d;
        Label updateLabel = new Label("A ligar à base de dados remota");
        updateLabel.setPrefWidth(wndwWidth);
        ProgressBar progress = new ProgressBar();
        progress.setPrefWidth(wndwWidth);

        VBox updatePane = new VBox();
        updatePane.setPadding(new Insets(10));
        updatePane.setSpacing(5.0d);
        updatePane.getChildren().addAll(updateLabel, progress);

        taskUpdateStage = new Stage(StageStyle.UTILITY);
        taskUpdateStage.setScene(new Scene(updatePane));
        taskUpdateStage.initModality(Modality.WINDOW_MODAL);
        taskUpdateStage.initOwner(getMainStage().getScene().getWindow());
        taskUpdateStage.show();
    }

    private void configurarStageAtrasados() {
        borderPaneAtrasados = new BorderPane();

        //FILTROS
        Label labelObra = new Label("obra");
        HBox.setMargin(labelObra, new Insets(2));

        textFieldFiltroFrefAtrasados = new TextField();
        HBox.setMargin(textFieldFiltroFrefAtrasados, new Insets(2));
        textFieldFiltroFrefAtrasados.textProperty().addListener(new ChangeListener<String>() {
            @SuppressWarnings("unchecked")
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d*")) {
                    textFieldFiltroFrefAtrasados.setText(newValue.replaceAll("[^\\d]", ""));
                }
                GridPaneAtrasados.actualizarLista();
            }
        });

        labelTotRecsAtrasados = new Label();
        HBox.setMargin(labelTotRecsAtrasados, new Insets(2));

        HBox hBoxFiltros = new HBox();
        hBoxFiltros.setAlignment(Pos.CENTER_LEFT);
        hBoxFiltros.getChildren().addAll(labelObra, textFieldFiltroFrefAtrasados, labelTotRecsAtrasados);

        borderPaneAtrasados.setTop(hBoxFiltros);

        ScrollPane scrollPane = new ScrollPane(borderPaneAtrasados);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        Scene scene = new Scene(scrollPane, 755, 700);
        scene.getStylesheets().add("styles.css");

        stageAtrasados = new Stage();
        stageAtrasados.setScene(scene);
        stageAtrasados.getIcons().add(Funcoes.iconeBamer());
        stageAtrasados.setTitle("atrasos");

    }

    private void configurarStagePorPlanear() {
        borderPanePorPlanear = new BorderPane();
        //FILTROS
        Label labelObra = new Label("obra");
        HBox.setMargin(labelObra, new Insets(2));

        textFieldFiltroPorPlanear = new TextField();
        HBox.setMargin(textFieldFiltroPorPlanear, new Insets(2));
        textFieldFiltroPorPlanear.textProperty().addListener(new ChangeListener<String>() {
            @SuppressWarnings("unchecked")
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d*")) {
                    textFieldFiltroPorPlanear.setText(newValue.replaceAll("[^\\d]", ""));
                }
                GridPanePorPlanear.actualizarLista();
            }
        });

        labelTotRecsPorPlanear = new Label();
        HBox.setMargin(labelTotRecsPorPlanear, new Insets(2));

        HBox hBoxFiltros = new HBox();
        hBoxFiltros.setAlignment(Pos.CENTER_LEFT);
        hBoxFiltros.getChildren().addAll(labelObra, textFieldFiltroPorPlanear, labelTotRecsPorPlanear);

        borderPanePorPlanear.setTop(hBoxFiltros);

        Scene scene = new Scene(borderPanePorPlanear, 755, 700);
        scene.getStylesheets().add("styles.css");

        stagePorPlanear = new Stage();
        stagePorPlanear.setScene(scene);
        stagePorPlanear.getIcons().add(Funcoes.iconeBamer());
        stagePorPlanear.setTitle("para planear");
    }

    private void setColunas() {
//        scrollPaneCalendario.getChildren().removeAll(calendario);
        calendario = new GridPaneCalendario(GridPaneCalendario.TIPO_GRELHA);
        scrollPaneCalendario.setContent(calendario);

        calendarioTopo = new GridPaneCalendario(GridPaneCalendario.TIPO_TOPO);
        scrollPaneTopo.setContent(calendarioTopo);

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                ArrayList<ArtigoOSBO> lista = sqlite.getListaArtigoOSBO(seccao);
                actualizarGrelhaCalendario(lista, ADICIONAR);
                return null;
            }
        };
        new Thread(task).run();
    }

    private void updateLabelCols() {
        PreferenciasEmSQLite prefs = PreferenciasEmSQLite.getInstancia();
        int colunas = prefs.getInt(Constantes.PREF_AGENDA_NUMCOLS, ValoresDefeito.AGENDA_NUMCOLS);
        labelCols.setText(colunas + " dias");
    }

    public void actualizarTextoColunasZero(int coluna) {
        PreferenciasEmSQLite prefs = PreferenciasEmSQLite.getInstancia();
        int colunas = prefs.getInt(Constantes.PREF_AGENDA_NUMCOLS, ValoresDefeito.AGENDA_NUMCOLS);
        LocalDate dataInicioAgenda = Singleton.getInstancia().dataInicioAgenda;

        for (int i = 0; i < colunas; i++) {
            if (i != coluna)
                continue;
            LocalDate localDateTime = dataInicioAgenda.plusDays(i);
            String data = Funcoes.dToC(localDateTime, "yyy-MM-dd 00:00:00");
            int qtt = DBSQLite.getInstancia().getQtdPedidaData(data, seccao);
            int qttFeita = DBSQLite.getInstancia().getQtdProduzidaData(data, seccao);
            Text textQttTotal = (Text) calendarioTopo.lookup("#qtttot" + i);
            Text textQttFeita = (Text) calendarioTopo.lookup("#qttfeita" + i);
            if (textQttTotal != null) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        textQttTotal.setText("" + (qtt == 0 ? "" : qtt));
                    }
                });
            }

            if (textQttFeita != null) {
                textQttFeita.setText("");
                int num = qtt - qttFeita;
                if (num != qtt)
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            textQttFeita.setText("" + (num == 0 ? "" : num));
                        }
                    });
            }
        }
    }

    public GridPaneCalendario getCalendario() {
        return calendario;
    }

    public ComboBox getComboSeccao() {
        return comboSeccao;
    }

    public JFXButton getBut_atrasados() {
        return but_atrasados;
    }

    //todo actualizarNota(nota)
//    public void actualizarNota(Document document) {
//        Platform.runLater(new Runnable() {
//            @Override
//            public void run() {
//                String bostamp = "";
//                String nota = "";
//                Node obj = calendario.lookup("#" + bostamp);
//                if (obj instanceof VBoxOSBO) {
//                    VBoxOSBO vBox = (VBoxOSBO) obj;
//                    vBox.setNotaProp(nota);
//                }
//            }
//        });
//    }

    public JFXButton getBut_porPlanear() {
        return but_porPlanear;
    }

    public TextField getTextFieldFiltroPorPlanear() {
        return textFieldFiltroPorPlanear;
    }

    public TextField getTextFieldFiltroFrefAtrasados() {
        return textFieldFiltroFrefAtrasados;
    }

    public Label getLabelTotRecsPorPlanear() {
        return labelTotRecsPorPlanear;
    }

    public Label getLabelTotRecsAtrasados() {
        return labelTotRecsAtrasados;
    }

    public Stage getMainStage() {
        return mainStage;
    }

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    public List<ArtigoLinhaPlanOUAtraso> getListaDeAtrasos() {
        return listaDeAtrasos;
    }
}
