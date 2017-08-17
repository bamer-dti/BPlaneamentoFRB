package bamer;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.*;
import com.google.firebase.internal.Log;
import com.jfoenix.controls.JFXButton;
import com.mashape.unirest.http.Unirest;
import ecras.EnviarSMS;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.*;
import objectos.*;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;
import pojos.ArtigoOSBO;
import pojos.Estado;
import pojos.VersaoObj;
import sqlite.DBSQLite;
import sqlite.PreferenciasEmSQLite;
import utils.*;

import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

public class AppMain extends Application {
    public static final long INTERVALO_CRONOS = 1;
    private static final String VERSAO = "3.1.2";
    public static final String TITULO_APP = "Planeamento " + VERSAO;
    @SuppressWarnings("unused")
    private static final String TAG = AppMain.class.getSimpleName();
    private static final String NOME_FICHEIRO_HISTORICO_VERSAO = "history.html";

    private static final double STAGE_ATRASOS_COMPRIMENTO = 750f;
    private static final double STAGE_ATRASOS_ALTURA = 770f;
    private static AppMain app;
    public BorderPane borderPaneAtrasados;
    public BorderPane borderPanePorPlanear;
    private String seccao;
    private String estado;
    private Label labelCols;
    private GridPaneCalendario calendario;
    private Stage stagePorPlanear;
    private ScrollPane scrollPaneCalendario;
    private GridPaneCalendario calendarioTopo;
    private ScrollPane scrollPaneTopo;
    private ComboBox<String> comboSeccao;
    private ComboBox<String> comboEstado;
    private Stage taskUpdateStage;
    private Stage stageAtrasados;
    private JFXButton but_atrasados;
    private JFXButton but_porPlanear;
    private TextField textFieldFiltroPorPlanear;
    private Label labelTotRecsPorPlanear;
    private TextField textFieldFiltroFrefAtrasados;
    private Label labelCountadorAtrasados;
    private Stage mainStage;
    private ProgressIndicator progressIndicator;
    private DBSQLite sqlite;
    private ChildEventListener listenerFirebaseOSBO;
    private JFXButton but_mais;
    private JFXButton but_menos;
    private VersaoObj versaoObj;
    private HBoxMachinas hboxMaquinas;
    private Scene scenePrincipal;
    private StackWorker timerStack;
    private BorderPane borderPanePrincipal;
    private ControllerLogin controllerLogin;
    private TextField textField_filtro_obra;
    private Label labelResultadoObra;
    private Label labelFiltroObra;

    public static void main(String[] args) {
        launch(args);
    }

    public static AppMain getInstancia() {
        return Singleton.getInstancia().appMain;
    }

    public static void eliminarFicheiroFirebase() {
        System.out.println("A tentar eliminar o ficheiro " + Constantes.NOME_BASE_DADOS_SQL);
        File file = new File(Constantes.NOME_BASE_DADOS_SQL);
        boolean test = file.delete();
        if (!test) {
            System.out.println("Não foi possivel eliminar o ficheiro " + Constantes.NOME_BASE_DADOS_SQL);
        } else {
            System.out.println("Ficheiro " + Constantes.NOME_BASE_DADOS_SQL + " eliminado com  sucesso");
        }
    }

    public static void eliminarFicheiroVersionTXT() {
        System.out.println("A tentar eliminar o ficheiro info.txt");
        File file = new File(NOME_FICHEIRO_HISTORICO_VERSAO);
        boolean test = file.delete();
        if (!test) {
            System.out.println("Não foi possivel eliminar o ficheiro info.txt");
        }
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        setMainStage(primaryStage);
        if (app == null) {
            app = this;
        }
        //todo verificar se as imagens ON/OFF não serão uma boa aposta
//        Image imageOn = Funcoes.imagemResource("on.png", 16, 16);
//        Image imageOff = Funcoes.imagemResource("off.png", 16, 16);

        eliminarFicheiroFirebase();

        sqlite = DBSQLite.getInstancia();

        iniciarFirebase();

        borderPanePrincipal = new BorderPane();
        scenePrincipal = new Scene(borderPanePrincipal, 1600, 780);
        scenePrincipal.getStylesheets().add("styles.css");

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        Parent parent = fxmlLoader.load();
        SceneWithId sceneLogin = new SceneWithId(parent);
        controllerLogin = fxmlLoader.getController();
        controllerLogin.setStage(mainStage);
        controllerLogin.setScenePrincipal(scenePrincipal);


        getMainStage().setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                if (stagePorPlanear != null)
                    stagePorPlanear.close();
                if (stageAtrasados != null)
                    stageAtrasados.close();
                try {
                    Unirest.shutdown();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                timerStack.getTimer().cancel();
                timerStack.getTimer().purge();
                Platform.exit();
                System.exit(0);
            }
        });

        getMainStage().sceneProperty().addListener(new ChangeListener<Scene>() {
            @Override
            public void changed(ObservableValue<? extends Scene> observable, Scene oldValue, Scene newValue) {
                if (newValue == scenePrincipal) {
                    interfacePlaneamento();

                    Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
                    getMainStage().setX((primScreenBounds.getWidth() - primaryStage.getWidth()) / 2);
                    getMainStage().setY((primScreenBounds.getHeight() - primaryStage.getHeight()) / 2);

                    colocarObjectosVisiveis(false);
                    configurarTaskStage();
                    configurarStageAtrasados();
                    configurarStagePorPlanear();
                    getMainStage().setResizable(true);

                    getMainStage().setMaximized(true);

                    povoarAgendamento();

                    GridPanePorPlanear.actualizar(seccao, estado, true);
                    GridPaneAtrasados.actualizar(seccao, estado, true);
                }
            }
        });

        timerStack = new StackWorker();
        Singleton.getInstancia().appMain = this;

        mainStage.setScene(sceneLogin);
        mainStage.getIcons().add(Funcoes.iconeBamer());
        mainStage.setTitle(TITULO_APP);
        mainStage.show();
        mainStage.setResizable(false);
    }

    private void interfacePlaneamento() {
        calendario = new GridPaneCalendario(GridPaneCalendario.TIPO_GRELHA);
        calendarioTopo = new GridPaneCalendario(GridPaneCalendario.TIPO_TOPO);

        scrollPaneTopo = new ScrollPane(calendarioTopo);
        scrollPaneTopo.setMinHeight(50);
        scrollPaneTopo.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPaneTopo.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        scrollPaneCalendario = new ScrollPane(calendario);
        scrollPaneCalendario.setFitToWidth(true);
        scrollPaneCalendario.setFitToHeight(true);

        scrollPaneCalendario.getStyleClass().add("scroll-bar");

        scrollPaneTopo.hvalueProperty().bindBidirectional(scrollPaneCalendario.hvalueProperty());

        VBox vBox = new VBox();
        vBox.getChildren().addAll(scrollPaneTopo, scrollPaneCalendario);
//        VBox.setVgrow(scrollPaneCalendario, Priority.ALWAYS);
        borderPanePrincipal.setCenter(vBox);

        MenuBar menuBar = new MenuBar();
//        menuBar.prefWidthProperty().bind(mainStage.widthProperty());

        Menu menuSistema = new Menu("Sistema");
        menuSistema.setGraphic(new ImageView(new Image("bamer_16.png")));
        FontIcon icone = new FontIcon();
        icone.setIconCode(FontAwesome.SIGN_OUT);
        icone.setIconColor(Color.BLACK);
        MenuItem menuItemSair = new MenuItem("Sair");
        menuItemSair.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.exit(0);
            }
        });
        menuItemSair.setGraphic(icone);
        menuSistema.getItems().add(menuItemSair);

//        MENU GESTÃO
        icone = new FontIcon();
        icone.setIconCode(FontAwesome.COG);
        icone.setIconColor(Color.BLACK);
        Menu menuGestao = new Menu("Gestão");
        menuGestao.setGraphic(icone);

        icone = new FontIcon();
        icone.setIconCode(FontAwesome.ENVELOPE_O);
        icone.setIconColor(Color.BLACK);
        Menu suBMenuItemSMS = new Menu("SMS");
        suBMenuItemSMS.setGraphic(icone);

        icone = new FontIcon();
        icone.setIconCode(FontAwesome.COMMENTING_O);
        icone.setIconColor(Color.BLACK);
        MenuItem subMenuSMS_Enviar = new MenuItem("novo SMS");
        subMenuSMS_Enviar.setGraphic(icone);
        subMenuSMS_Enviar.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    new EnviarSMS(0, null);
                } catch (IOException e) {
                    e.printStackTrace();
                    Procedimentos.alertaException(e);
                }
            }
        });
        suBMenuItemSMS.getItems().addAll(subMenuSMS_Enviar);
        menuGestao.getItems().addAll(suBMenuItemSMS);

//        MENU HELP
        icone = new FontIcon();
        icone.setIconCode(FontAwesome.QUESTION_CIRCLE);
        icone.setIconColor(Color.BLACK);
        Menu menuAjuda = new Menu("Ajuda");
        menuAjuda.setGraphic(icone);
        MenuItem menuItemHistorico = new MenuItem("Histórico de versões");
        menuItemHistorico.setGraphic(new ImageView(new Image("version_histori_16.png")));
        menuItemHistorico.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    abrirFicheiroVersionTXT();
                } catch (IOException e) {
                    Procedimentos.alertaException(e);
                    e.printStackTrace();
                }
            }
        });
        menuAjuda.getItems().addAll(menuItemHistorico);

        menuBar.getMenus().addAll(menuSistema, menuGestao, menuAjuda);

        HBox hboxBarraFerramentas = new HBox();
        hboxBarraFerramentas.setId("topBox");

        labelCols = new Label("0");
        updateLabelCols();

        icone = new FontIcon();
        icone.setIconCode(FontAwesome.MINUS_SQUARE);
        icone.setIconColor(Color.ALICEBLUE);
        but_menos = new JFXButton();
        but_menos.getStyleClass().add("button-mais-menos-colunas");
        but_menos.setGraphic(icone);
        but_menos.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                PreferenciasEmSQLite prefs = PreferenciasEmSQLite.getInstancia();
                int colunas = prefs.getInt(Constantes.Preferencias.PREF_AGENDA_NUMCOLS, ValoresDefeito.AGENDA_NUMCOLS);
                if (colunas < 8)
                    return;
                colunas--;
                prefs.putInt(Constantes.Preferencias.PREF_AGENDA_NUMCOLS, colunas);

                povoarAgendamento();
                updateLabelCols();
            }
        });
        HBox.setMargin(but_menos, new Insets(10f, 10f, 10f, 10f));
        hboxBarraFerramentas.getChildren().add(but_menos);

        icone = new FontIcon();
        icone.setIconColor(Color.ALICEBLUE);
        icone.setIconCode(FontAwesome.PLUS_SQUARE);
        but_mais = new JFXButton();
        but_mais.setGraphic(icone);
        but_mais.getStyleClass().add("button-mais-menos-colunas");
        but_mais.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                PreferenciasEmSQLite prefs = PreferenciasEmSQLite.getInstancia();
                int colunas = prefs.getInt(Constantes.Preferencias.PREF_AGENDA_NUMCOLS, ValoresDefeito.AGENDA_NUMCOLS);
                colunas++;
                prefs.putInt(Constantes.Preferencias.PREF_AGENDA_NUMCOLS, colunas);

                povoarAgendamento();
                updateLabelCols();
            }
        });
        HBox.setMargin(but_mais, new Insets(10f, 10f, 10f, 10f));
        hboxBarraFerramentas.getChildren().add(but_mais);

        HBox.setMargin(labelCols, new Insets(10f, 10f, 10f, 10f));
        labelCols.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.isPrimaryButtonDown() && event.getClickCount() > 1) {
                    PreferenciasEmSQLite prefs = PreferenciasEmSQLite.getInstancia();
                    int colunas = prefs.getInt(Constantes.Preferencias.PREF_AGENDA_NUMCOLS, ValoresDefeito.AGENDA_NUMCOLS);
                    TextInputDialog dialog = new TextInputDialog("" + colunas);
                    dialog.setTitle("Configuração de colunas");
//                    alerta.setHeaderText("Look, a Text Input Dialog");
                    dialog.setContentText("Nº de colunas:");

                    Optional<String> result = dialog.showAndWait();
                    if (result.isPresent()) {
                        prefs.putInt(Constantes.Preferencias.PREF_AGENDA_NUMCOLS, Integer.parseInt(result.get()));
                        povoarAgendamento();
                        labelCols.setText(result.get());
                    }
                }
            }
        });
        hboxBarraFerramentas.getChildren().add(labelCols);

        //BOTÃO POR AGENDAR (ESTADO 00)
        icone = new FontIcon();
        icone.setIconCode(FontAwesome.CALENDAR_O);
        icone.setIconColor(Color.ALICEBLUE);
        but_porPlanear = new JFXButtonPlanear("por planear (0)");
        but_porPlanear.setGraphic(icone);
        but_porPlanear.getStyleClass().add("button-raised-bamer-aprovados");
        but_porPlanear.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                GridPanePorPlanear.actualizar(seccao, estado, false);
                if (!stagePorPlanear.isShowing()) {
                    stagePorPlanear.show();
                }
                stagePorPlanear.toFront();
            }
        });
        HBox.setMargin(but_porPlanear, new Insets(10f, 10f, 10f, 10f));
        hboxBarraFerramentas.getChildren().add(but_porPlanear);

        //BOTÂO ATRASADOS
        icone = new FontIcon();
        icone.setIconCode(FontAwesome.CLOCK_O);
        icone.setIconColor(Color.ALICEBLUE);
        icone.setIconSize(16);
        but_atrasados = new JFXButton("atrasos");
        but_atrasados.setGraphic(icone);
        but_atrasados.getStyleClass().add("button-raised-bamer-aprovados");
        but_atrasados.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                GridPaneAtrasados.actualizar(seccao, estado, false);
                if (!stageAtrasados.isShowing()) {
                    stageAtrasados.show();
                }
                stageAtrasados.toFront();
            }
        });
        HBox.setMargin(but_atrasados, new Insets(10f, 10f, 10f, 10f));
        hboxBarraFerramentas.getChildren().add(but_atrasados);

        //COMBOBOX SECÇÃO
        PreferenciasEmSQLite prefs = PreferenciasEmSQLite.getInstancia();
        seccao = prefs.get(Constantes.Preferencias.PREF_SECCAO, ValoresDefeito.SECCAO);
        comboSeccao = new ComboBox<>();
        comboSeccao.getStyleClass().add("combo_seccao");
        comboSeccao.getSelectionModel().select(seccao);
//                comboSeccao.getItems().add(queryRow.getKey().toString());
        comboSeccao.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                System.out.println("Secção alterada de " + oldValue + " paraLabel " + newValue);
                seccao = newValue;
                if (oldValue.equals(newValue)) {
                    return;
                }
                PreferenciasEmSQLite prefs = PreferenciasEmSQLite.getInstancia();
                prefs.put(Constantes.Preferencias.PREF_SECCAO, newValue);
                povoarAgendamento();
//                actualizarTextoColunasZero();
                actualizarMostradorMaquinas();
                GridPanePorPlanear.actualizar(seccao, estado, true);
                GridPaneAtrasados.actualizar(seccao, estado, true);
            }
        });
        comboSeccao.setVisible(false);
        HBox.setMargin(comboSeccao, new Insets(10f, 10f, 10f, 10f));
        hboxBarraFerramentas.getChildren().add(comboSeccao);

        //COMBO ESTADO
        estado = prefs.get(Constantes.Preferencias.PREF_ESTADO, Constantes.ESTADO_01_CORTE);
        comboEstado = new ComboBox<>();
        comboEstado.getStyleClass().add("combo_estado");
        comboEstado.getSelectionModel().select(estado);
        comboEstado.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (oldValue.equals(newValue)) {
                    return;
                }
                estado = newValue;
                PreferenciasEmSQLite prefs = PreferenciasEmSQLite.getInstancia();
                prefs.put(Constantes.Preferencias.PREF_ESTADO, newValue);
                povoarAgendamento();
//                actualizarTextoColunasZero();
                actualizarMostradorMaquinas();
                GridPanePorPlanear.actualizar(seccao, estado, true);
                GridPaneAtrasados.actualizar(seccao, estado, true);
            }
        });
        comboEstado.setVisible(false);
        HBox.setMargin(comboEstado, new Insets(10f, 10f, 10f, 10f));
        hboxBarraFerramentas.getChildren().add(comboEstado);

        labelFiltroObra = new Label("filtro obra: ");
        labelResultadoObra = new Label("");
        //Campo de pesquisa de obra
        textField_filtro_obra = new TextField();
        textField_filtro_obra.setPrefWidth(70f);
        textField_filtro_obra.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d*")) {
                    textField_filtro_obra.setText(newValue.replaceAll("[^\\d]", ""));
                }
                povoarAgendamento();
            }
        });
        HBox.setMargin(labelFiltroObra, new Insets(3));
        HBox.setMargin(labelResultadoObra, new Insets(3));
        hboxBarraFerramentas.getChildren().addAll(labelFiltroObra, textField_filtro_obra, labelResultadoObra);

        Region region = new Region();
        HBox.setHgrow(region, Priority.ALWAYS);
        hboxBarraFerramentas.getChildren().add(region);

        progressIndicator = new ProgressIndicator();

        HBox.setMargin(progressIndicator, new Insets(2, 30, 0, 30));
        progressIndicator.setProgress(100d);
        progressIndicator.setPrefWidth(30);
        hboxBarraFerramentas.getChildren().add(progressIndicator);

        hboxBarraFerramentas.setAlignment(Pos.CENTER_LEFT);

        hboxMaquinas = setupHBoxMaquinas();

        VBox vboxTOP = new VBox(menuBar, hboxBarraFerramentas, hboxMaquinas);

        borderPanePrincipal.setTop(vboxTOP);

        configurarRestantesListenersFirebase();
    }

    public void actualizarMostradorMaquinas() {
        HBoxMachinas objBase = hboxMaquinas;
        ObservableList<Node> children = objBase.getChildren();
        for (Node child : children) {
            if (child instanceof GridButtonMachina) {
                GridButtonMachina botao = (GridButtonMachina) child;
                Machina machina = botao.getMachinaProp();
                if (machina.getSeccao().equals(seccao)) {
                    botao.setVisible(true);
                    botao.setManaged(true);
                } else {
                    botao.setVisible(false);
                    botao.setManaged(false);
                }
            }
        }
    }

    private HBoxMachinas setupHBoxMaquinas() {
        HBoxMachinas boxMachinas = new HBoxMachinas();
        return boxMachinas;
    }

    public void colocarObjectosVisiveis(boolean isVisivel) {
        if (!isVisivel) {
            progressIndicator.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        } else {
            progressIndicator.setProgress(100);
        }
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (isVisivel)
                    taskUpdateStage.hide();
                else
                    taskUpdateStage.show();
//                calendario.setVisible(isVisivel);
//                calendarioTopo.setVisible(isVisivel);
                but_porPlanear.setVisible(isVisivel);
                but_atrasados.setVisible(isVisivel);
                but_menos.setVisible(isVisivel);
                but_mais.setVisible(isVisivel);
                labelCols.setVisible(isVisivel);
                comboSeccao.setVisible(isVisivel);
                comboEstado.setVisible(isVisivel);
                textField_filtro_obra.setVisible(isVisivel);
                labelFiltroObra.setVisible(isVisivel);
                labelResultadoObra.setVisible(isVisivel);
            }
        });
    }

    private void iniciarFirebase() {
        DBSQLite.getInstancia().resetDados();
        try {
            InputStream file = ClassLoader.getSystemResourceAsStream(Constantes.Firebase.FICHEIRO_CREDENCIAIS_GOOGLE);
            System.out.println("Tamanho do ficheiro JSON: " + file.available());
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setServiceAccount(file)
                    .setDatabaseUrl(Constantes.Firebase.FIREBASE_PROJECT_URL)
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
                            if (!Singleton.getInstancia().loginComSucesso) {
                                controllerLogin.btlogin.setDisable(false);
                                controllerLogin.progresso.setVisible(false);
                                controllerLogin.label_erro.setText("");
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled");
            }
        });

        DatabaseReference refDataFireBase = FirebaseDatabase.getInstance().getReference(Campos.KEY_VERSIONS);
        refDataFireBase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    if (d.getKey().equals("planning")) {
                        versaoObj = d.getValue(VersaoObj.class);
                        System.out.println("Versão cloud: " + versaoObj);
                        if (versaoObj.versao.compareToIgnoreCase(VERSAO) > 0) {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    Procedimentos.alertaVersion(versaoObj.versao);
                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void configurarRestantesListenersFirebase() {
        System.out.println("configurarRestantesListenersFirebase");
        if (!Privado.TESTING) {
            configurarListenersOSBO();
            DatabaseReference refDataFireBase = FirebaseDatabase.getInstance().getReference(Campos.KEY_OSBO);
            refDataFireBase.addChildEventListener(listenerFirebaseOSBO);
        }

        //ALIMENTAR COMBO SECÇÃO
        FirebaseDatabase.getInstance().getReference(Campos.KEY_SECCAO).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    comboSeccao.getItems().add(d.getKey());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //ALIMENTAR COMBO ESTADO
        FirebaseDatabase.getInstance().getReference(Campos.KEY_ESTADO).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    comboEstado.getItems().add(d.getValue(Estado.class).getTitulo());
                }
                Singleton.getInstancia().setLista_de_estados(comboEstado.getItems());
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
                        if (artigoOSBO.getSeccao().equals(seccao) && artigoOSBO.getEstado().equals(estado)) {
                            lista.add(artigoOSBO);
                            actualizarGrelhaCalendario(lista, Constantes.Operacao.ADICIONAR);
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
                        actualizarGrelhaCalendario(lista, Constantes.Operacao.ACTUALIZAR);
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
                        lista.add(artigoOSBO);
                        actualizarGrelhaCalendario(lista, Constantes.Operacao.REMOVER);
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

    public void actualizarGrelhaCalendario(ArrayList<ArtigoOSBO> listaDocsOSBO, int constantes_operacao) {
//        Log.i("actualizarGrelhaCalendario", "listaDocsOSBO: " + listaDocsOSBO.size() + ", operacao " + operacao);
        switch (constantes_operacao) {
            case Constantes.Operacao.ADICIONAR:
                for (ArtigoOSBO artigoOSBO : listaDocsOSBO) {
                    if (artigoOSBO.getSeccao().equals(seccao) && artigoOSBO.getEstado().equals(estado)) {
                        new VBoxOSBO(artigoOSBO);
                    }
                }
                break;

            case Constantes.Operacao.ACTUALIZAR:
                for (ArtigoOSBO artigoOSBO : listaDocsOSBO) {
                    VBoxOSBO vBoxOSBO = (VBoxOSBO) calendario.lookup("#" + artigoOSBO.getBostamp());
                    //O artigo já existe
                    if (vBoxOSBO != null) {
                        //Já não pertence à mesma secção ou estado
                        if (!artigoOSBO.getSeccao().equals(seccao) || !artigoOSBO.getEstado().equals(estado)) {
                            ArrayList<ArtigoOSBO> listaUnica = new ArrayList<>();
                            listaUnica.add(artigoOSBO);
                            actualizarGrelhaCalendario(listaUnica, Constantes.Operacao.REMOVER);
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
                    actualizarGrelhaCalendario(listaUnica, Constantes.Operacao.ADICIONAR);
                }
                break;

            case Constantes.Operacao.REMOVER:
                for (ArtigoOSBO artigoOSBO : listaDocsOSBO) {
                    VBoxOSBO vBoxOSBO = (VBoxOSBO) calendario.lookup("#" + artigoOSBO.getBostamp());
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            calendario.getChildren().removeAll(vBoxOSBO);
                            actualizarTextoColunasZero(vBoxOSBO.getColuna());
                        }
                    });
                }
                break;

            default:
                break;
        }
        GridPanePorPlanear.actualizar(seccao, estado, false);
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
                GridPaneAtrasados.actualizar(seccao, estado, false);
            }
        });

        labelCountadorAtrasados = new Label();
        HBox.setMargin(labelCountadorAtrasados, new Insets(2));

        HBox hBoxFiltros = new HBox();
        hBoxFiltros.setAlignment(Pos.CENTER_LEFT);
        hBoxFiltros.getChildren().addAll(labelObra, textFieldFiltroFrefAtrasados, labelCountadorAtrasados);

        borderPaneAtrasados.setTop(hBoxFiltros);

        ScrollPane scrollPane = new ScrollPane(borderPaneAtrasados);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        Scene scene = new Scene(scrollPane, 770, 770);
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
                GridPanePorPlanear.actualizar(seccao, estado, false);
            }
        });

        labelTotRecsPorPlanear = new Label();
        HBox.setMargin(labelTotRecsPorPlanear, new Insets(2));

        HBox hBoxFiltros = new HBox();
        hBoxFiltros.setAlignment(Pos.CENTER_LEFT);
        hBoxFiltros.getChildren().addAll(labelObra, textFieldFiltroPorPlanear, labelTotRecsPorPlanear);

        borderPanePorPlanear.setTop(hBoxFiltros);

        Scene scene = new Scene(borderPanePorPlanear, STAGE_ATRASOS_COMPRIMENTO, STAGE_ATRASOS_ALTURA);
        scene.getStylesheets().add("styles.css");

        stagePorPlanear = new Stage();
        stagePorPlanear.setScene(scene);
        stagePorPlanear.getIcons().add(Funcoes.iconeBamer());
        stagePorPlanear.setTitle("por planear");
    }

    private void povoarAgendamento() {
//        scrollPaneCalendario.getChildren().removeAll(calendario);
        calendario = null;
        calendario = new GridPaneCalendario(GridPaneCalendario.TIPO_GRELHA);
        scrollPaneCalendario.setContent(calendario);

        if (calendarioTopo != null) {
            ObservableList<Node> childs = calendarioTopo.getChildren();
            for (Node node : childs) {
                if (node instanceof VBoxDia) {
                    VBoxDia vboxdia = (VBoxDia) node;
                    if (vboxdia.chronoWeather != null) {
                        vboxdia.chronoWeather.cancel();
                        vboxdia.chronoWeather.purge();
                    }
                }
            }
        }
        calendarioTopo = new GridPaneCalendario(GridPaneCalendario.TIPO_TOPO);
        scrollPaneTopo.setContent(calendarioTopo);

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                ArrayList<ArtigoOSBO> lista = sqlite.getListaArtigoOSBO(seccao, estado, textField_filtro_obra.getText());
                actualizarGrelhaCalendario(lista, Constantes.Operacao.ADICIONAR);
                labelResultadoObra.setText("" + lista.size());
                return null;
            }
        };
        new Thread(task).run();
    }

    private void updateLabelCols() {
        PreferenciasEmSQLite prefs = PreferenciasEmSQLite.getInstancia();
        int colunas = prefs.getInt(Constantes.Preferencias.PREF_AGENDA_NUMCOLS, ValoresDefeito.AGENDA_NUMCOLS);
        labelCols.setText(colunas + " dias");
    }

    public void actualizarTextoColunasZero(int coluna) {
        PreferenciasEmSQLite prefs = PreferenciasEmSQLite.getInstancia();
        int colunas = prefs.getInt(Constantes.Preferencias.PREF_AGENDA_NUMCOLS, ValoresDefeito.AGENDA_NUMCOLS);
        LocalDate dataInicioAgenda = Singleton.getInstancia().dataInicioAgenda;

        for (int i = 0; i < colunas; i++) {
            if (i != coluna)
                continue;
            LocalDate localDateTime = dataInicioAgenda.plusDays(i);
            String data = Funcoes.dToC(localDateTime, "yyyy-MM-dd 00:00:00");
            int qtt = DBSQLite.getInstancia().getQtdPedidaData(data, seccao, estado);
            int qttFeita = DBSQLite.getInstancia().getQtdProduzidaData(data, seccao, estado);
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

    public void abrirFicheiroVersionTXT() throws IOException {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                progressIndicator.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
            }
        });

        DatabaseReference refDataFireBase = FirebaseDatabase.getInstance().getReference(Campos.KEY_VERSIONS);
        refDataFireBase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    if (d.getKey().equals("planning")) {
                        versaoObj = d.getValue(VersaoObj.class);
                        System.out.println("Versão cloud: " + versaoObj);
                        File file = new File(NOME_FICHEIRO_HISTORICO_VERSAO);
                        InputStream inputStream = new ByteArrayInputStream(versaoObj.getHistory().getBytes(StandardCharsets.UTF_8));
                        try {
                            OutputStream outputStream = new FileOutputStream(file);
                            byte[] buffer = new byte[1024];
                            int length;
                            while ((length = inputStream.read(buffer)) > 0) {
                                outputStream.write(buffer, 0, length);
                            }
                            outputStream.close();
                            inputStream.close();
                            Desktop.getDesktop().open(file);
                        } catch (IOException e) {
                            Procedimentos.alertaException(e);
                            e.printStackTrace();
                        }
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                progressIndicator.setProgress(100);
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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

    public Label getLabelCountadorAtrasados() {
        return labelCountadorAtrasados;
    }

    public Stage getMainStage() {
        return mainStage;
    }

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    public String getSeccao() {
        return seccao;
    }

    public String getEstado() {
        return estado;
    }

    public String getFiltroDeObra() {
        return textField_filtro_obra.getText();
    }
}
