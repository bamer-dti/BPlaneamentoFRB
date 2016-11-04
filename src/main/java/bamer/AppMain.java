package bamer;

///**
// * Created by miguel.silva on 08-06-2016.
// */

import com.jfoenix.controls.JFXButton;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import objectos.GridPaneCalendario;
import objectos.VBoxOSBO;
import pojos.ArtigoOSBO;
import sql.BamerSqlServer;
import sqlite.PreferenciasEmSQLite;
import utils.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static java.lang.System.out;

public class AppMain extends Application {
    private static final int MINIMO_COLUNAS = 4; //dias = + 1
    private static final String TAG = AppMain.class.getSimpleName();
    private static AppMain app;
    public BorderPane borderPaneAtrasados;
    public BorderPane borderPaneAprovisionamento;
    private Label labelCols;
    private GridPaneCalendario calendario;
    private Stage stageAprovisionamento;
    private ScrollPane scrollPaneCalendario;
    private GridPaneCalendario calendarioTopo;
    private ScrollPane scrollPaneTopo;
    private ComboBox<String> comboSeccao;
    private Stage taskUpdateStage;
    private Stage stageAtrasados;
    private JFXButton but_atrasados;
    private JFXButton but_aprovisionamento;
    private TextField textFieldFiltroFrefAprovisionamento;
    private Label labelTotRecsAprovisionamento;
    private TextField textFieldFiltroFrefAtrasados;
    private Label labelTotRecsAtrasados;
    private Stage mainStage;

    public static void main(String[] args) {
        launch(args);
    }

    public static AppMain getInstancia() {
        return app;
    }

    @Override
    public void start(Stage mainStage) {
        if (app == null) {
            app = this;
        }

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

        JFXButton but_menos = new JFXButton("-");
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

        JFXButton but_mais = new JFXButton("+");
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
                        System.out.println("Your name: " + result.get());
                        prefs.putInt(Constantes.PREF_AGENDA_NUMCOLS, Integer.parseInt(result.get()));
                        setColunas();
                        labelCols.setText(result.get());
                    }
                }
            }
        });
        topBox.getChildren().add(labelCols);

        //BOTÃO POR AGENDAR (ESTADO 00)
        but_aprovisionamento = new JFXButton("aprovisionamento");
        but_aprovisionamento.getStyleClass().add("button-raised-bamer-aprovados");
        but_aprovisionamento.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (!stageAprovisionamento.isShowing()) {
                    stageAprovisionamento.show();

                }
                stageAprovisionamento.toFront();
            }
        });
        HBox.setMargin(but_aprovisionamento, new Insets(10f, 10f, 10f, 10f));
        topBox.getChildren().add(but_aprovisionamento);

        //BOTÂO ATRASADOS
        but_atrasados = new JFXButton("atrasos");
        but_atrasados.getStyleClass().add("button-raised-bamer-aprovados");
        but_atrasados.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
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
        String seccao = prefs.get(Constantes.PREF_SECCAO, ValoresDefeito.SECCAO);
        comboSeccao = new ComboBox<>();
        comboSeccao.getSelectionModel().select(seccao);
        //todo alimentar o combo de secção
//                comboSeccao.getItems().add(queryRow.getKey().toString());

        comboSeccao.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                out.println("Seccção alterada de " + oldValue + " para " + newValue);
                if (oldValue.equals(newValue)) {
                    return;
                }
                PreferenciasEmSQLite prefs = PreferenciasEmSQLite.getInstancia();
                prefs.put(Constantes.PREF_SECCAO, newValue);
                setColunas();
            }
        });
        topBox.getChildren().add(comboSeccao);

        Region region = new Region();
        HBox.setHgrow(region, Priority.ALWAYS);
        topBox.getChildren().add(region);

        ProgressIndicator pi = new ProgressIndicator();

        HBox.setMargin(pi, new Insets(2, 30, 0, 30));
        Singleton.getInstancia().setPi(pi);

        topBox.getChildren().add(pi);

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
                stageAprovisionamento.close();
                stageAtrasados.close();
                Platform.exit();
                System.exit(0);
            }
        });

        iniciarServicos();

        configurarTaskStage();

        configurarStageAtrasados();

        configurarStageAprovados();
    }

    private void configurarTaskStage() {
        final double wndwWidth = 300.0d;
        Label updateLabel = new Label("A actualizar a base de dados");
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
        //todo painel de atrasados
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
//               todo filtrar a lista de atrasados
//                GridPaneAtrasados.alimentarLista(lista);
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

    private void configurarStageAprovados() {
//        GridPaneAprovados gridPaneAprovados = new GridPaneAprovados();
        borderPaneAprovisionamento = new BorderPane();

        //FILTROS
        Label labelObra = new Label("obra");
        HBox.setMargin(labelObra, new Insets(2));

        textFieldFiltroFrefAprovisionamento = new TextField();
        HBox.setMargin(textFieldFiltroFrefAprovisionamento, new Insets(2));
        textFieldFiltroFrefAprovisionamento.textProperty().addListener(new ChangeListener<String>() {
            @SuppressWarnings("unchecked")
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d*")) {
                    textFieldFiltroFrefAprovisionamento.setText(newValue.replaceAll("[^\\d]", ""));
                }
                //todo filtrar a lista atrasados por obra
//                    GridPaneAprovisionamentos.alimentarLista(ServicoCouchBase.getInstancia().getListaAprovisionamento());
            }
        });

        labelTotRecsAprovisionamento = new Label();
        HBox.setMargin(labelTotRecsAprovisionamento, new Insets(2));

        HBox hBoxFiltros = new HBox();
        hBoxFiltros.setAlignment(Pos.CENTER_LEFT);
        hBoxFiltros.getChildren().addAll(labelObra, textFieldFiltroFrefAprovisionamento, labelTotRecsAprovisionamento);

        borderPaneAprovisionamento.setTop(hBoxFiltros);

        Scene scene = new Scene(borderPaneAprovisionamento, 755, 700);
        scene.getStylesheets().add("styles.css");

        stageAprovisionamento = new Stage();
        stageAprovisionamento.setScene(scene);
        stageAprovisionamento.getIcons().add(Funcoes.iconeBamer());
        stageAprovisionamento.setTitle("aprovisionamentos");
    }

    private void setColunas() {
//        scrollPaneCalendario.getChildren().removeAll(calendario);
        calendario = new GridPaneCalendario(GridPaneCalendario.TIPO_GRELHA);
        scrollPaneCalendario.setContent(calendario);

        calendarioTopo = new GridPaneCalendario(GridPaneCalendario.TIPO_TOPO);
        scrollPaneTopo.setContent(calendarioTopo);

        //todo lista ArtigoOSBO para alimentar grelha
        ArrayList<ArtigoOSBO> lista = new ArrayList<>();
        try {
            inserirOuActualizarOSBO(lista);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void iniciarServicos() {
        BamerSqlServer.getInstancia();
    }

    private void updateLabelCols() {
        PreferenciasEmSQLite prefs = PreferenciasEmSQLite.getInstancia();
        int colunas = prefs.getInt(Constantes.PREF_AGENDA_NUMCOLS, ValoresDefeito.AGENDA_NUMCOLS);
        labelCols.setText(colunas + " dias");
    }

    public void eliminar(String id) {
        GridPaneCalendario gridPaneCalendario = AppMain.getInstancia().calendario;
        VBoxOSBO vBoxOSBO = null;
        try {
            vBoxOSBO = (VBoxOSBO) gridPaneCalendario.lookup("#" + id);
        } catch (Exception e) {
            e.printStackTrace();
            out.println("ERROR QUANDO EXISTE APENAS UM OBECTO!!?!??!");
        }

        //Não existe, coloca novo
        if (vBoxOSBO != null) {
            VBoxOSBO finalVBoxOSBO = vBoxOSBO;
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    out.println("REMOVER " + finalVBoxOSBO.getColuna() + ":" + finalVBoxOSBO.getOrdemProp() + " id " + id);
                    calendario.getChildren().remove(finalVBoxOSBO);
                }
            });
        }
    }

    public void inserirOuActualizarOSBO(ArrayList<ArtigoOSBO> listaDocsOSBO) throws IOException {
        if (listaDocsOSBO == null)
            return;
//        ServicoCouchBase.getInstancia().liveQueryAddChangeDocs.stop();
        String seccao = AppMain.getInstancia().getComboSeccao().getValue().toString();
        for (ArtigoOSBO artigoOSBO : listaDocsOSBO) {
            //Existe?
            GridPaneCalendario gridPaneCalendario = AppMain.getInstancia().calendario;
            VBoxOSBO vBoxOSBO = null;
            try {
                vBoxOSBO = (VBoxOSBO) gridPaneCalendario.lookup("#" + artigoOSBO.getBostamp());
            } catch (Exception e) {
                e.printStackTrace();
                out.println("ERROR QUANDO EXISTE APENAS UM OBECTO!!?!??!");
            }

            if (!artigoOSBO.getSeccao().equals(seccao)) {
                continue;
            }

            //Não existe, coloca novo
            if (vBoxOSBO == null) {
                if (artigoOSBO.getEstado().equals(NomesDeCampos.ESTADO_01_CORTE)) {
                    new VBoxOSBO(artigoOSBO);
                }
            } else {
                if (!artigoOSBO.getEstado().equals(NomesDeCampos.ESTADO_01_CORTE)) {
                    VBoxOSBO finalVBoxOSBO = vBoxOSBO;
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            out.println("REMOVER " + finalVBoxOSBO.getColuna() + ":" + finalVBoxOSBO.getOrdemProp() + " OS " + artigoOSBO.getObrano());
                            calendario.getChildren().remove(finalVBoxOSBO);
                        }
                    });
                } else {
                    //existem alterações?
                    ArtigoOSBO artigoOriginal = vBoxOSBO.getArtigoOSBOProp();
                    ArtigoOSBO artigoNovo = artigoOSBO;
                    if (artigoOriginal.getOrdem() != artigoNovo.getOrdem()
                            || !artigoOriginal.getDtcortef().equals(artigoNovo.getDtcortef())
                            || !artigoOriginal.getDttransf().equals(artigoNovo.getDttransf())
                            || !artigoOriginal.getDtexpedi().equals(artigoNovo.getDtexpedi())
                            || artigoOriginal.getCor() != artigoNovo.getCor()
                            ) {
                        //Reposicionar o objecto
                        VBoxOSBO finalVBoxOSBO1 = vBoxOSBO;
                        VBoxOSBO finalVBoxOSBO2 = vBoxOSBO;
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        finalVBoxOSBO1.setArtigoOSBOProp(artigoNovo);
                                        try {
                                            GridPane.setConstraints(finalVBoxOSBO1, finalVBoxOSBO2.getColuna(), artigoNovo.getOrdem());
                                        } catch (Exception e) {
                                            out.println("A OS " + artigoNovo.getObrano() + " tem uma ordem de " + artigoNovo.getOrdem());
                                        }
                                    }
                                });
                            }
                        });
                        continue;
                    }
                    if (artigoOriginal.getEstado().equals(artigoNovo.getEstado())) {
                        VBoxOSBO finalVBoxOSBO3 = vBoxOSBO;
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                finalVBoxOSBO3.setArtigoOSBOProp(artigoNovo);
                            }
                        });
                    }
                }
            }
        }
        actualizarTextoColunasZero();
//        ServicoCouchBase.getInstancia().liveQueryAddChangeDocs.start();
    }

    public void actualizarTextoColunasZero() {
        PreferenciasEmSQLite prefs = PreferenciasEmSQLite.getInstancia();
        int colunas = prefs.getInt(Constantes.PREF_AGENDA_NUMCOLS, ValoresDefeito.AGENDA_NUMCOLS);
        LocalDateTime dataInicioAgenda = Singleton.getInstancia().dataInicioAgenda;

        for (int i = 0; i < colunas; i++) {
            LocalDateTime localDateTime = dataInicioAgenda.plusDays(i);
            String data = Funcoes.dToCZeroHour(localDateTime);
            //todo getPecasPorData
//                int qtt = ServicoCouchBase.getInstancia().getPecasPorData(data);
            int qtt = 0;
            // todo getPecasFeitasPorData
//                int qttFeita = ServicoCouchBase.getInstancia().getPecasFeitasPorData(data);
            Text textQttTotal = (Text) calendarioTopo.lookup("#qtttot" + i);
            int qttFeita = 0;
            Text textQttFeita = (Text) calendarioTopo.lookup("#qttfeita" + i);
            if (textQttTotal != null) {
                textQttTotal.setText("" + (qtt == 0 ? "" : qtt));
            }

            if (textQttFeita != null) {
                textQttFeita.setText("");
                int num = qtt - qttFeita;
                if (num != qtt)
                    textQttFeita.setText("" + (num == 0 ? "" : num));
            }
        }
    }

    public void actualizarTextoColunasZero(int coluna) {
        PreferenciasEmSQLite prefs = PreferenciasEmSQLite.getInstancia();
        int colunas = prefs.getInt(Constantes.PREF_AGENDA_NUMCOLS, ValoresDefeito.AGENDA_NUMCOLS);
        LocalDateTime dataInicioAgenda = Singleton.getInstancia().dataInicioAgenda;

        for (int i = 0; i < colunas; i++) {
            if (i != coluna)
                continue;
            LocalDateTime localDateTime = dataInicioAgenda.plusDays(i);
            String data = Funcoes.dToCZeroHour(localDateTime);
            //todo getPecasPorData
//                int qtt = ServicoCouchBase.getInstancia().getPecasPorData(data);
            int qtt = 0;
            // todo getPecasFeitasPorData
//                int qttFeita = ServicoCouchBase.getInstancia().getPecasFeitasPorData(data);
            int qttFeita = 0;
            Text textQttTotal = (Text) calendarioTopo.lookup("#qtttot" + i);
            Text textQttFeita = (Text) calendarioTopo.lookup("#qttfeita" + i);
            if (textQttTotal != null) {
                textQttTotal.setText("" + (qtt == 0 ? "" : qtt));
            }

            if (textQttFeita != null) {
                textQttFeita.setText("");
                int num = qtt - qttFeita;
                if (num != qtt)
                    textQttFeita.setText("" + (num == 0 ? "" : num));
            }
        }
    }

    public GridPaneCalendario getCalendario() {
        return calendario;
    }

    public void actualizarOSPRODqtt(String bostamp) {
        GridPaneCalendario gridPaneCalendario = getCalendario();
        VBoxOSBO vBoxOSBO = (VBoxOSBO) gridPaneCalendario.lookup("#" + bostamp);
        if (vBoxOSBO != null) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    vBoxOSBO.setBostampProp(bostamp);
                }
            });
        }
    }

    public ComboBox getComboSeccao() {
        return comboSeccao;
    }

    public JFXButton getBut_atrasados() {
        return but_atrasados;
    }

    public JFXButton getBut_aprovisionamento() {
        return but_aprovisionamento;
    }

    //todo actualizarNota(nota)
    public void actualizarNota(Document document) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                String bostamp = ""; //TODO document.getProperty(NomesDeCampos.FIELD_BOSTAMP).toString();
                String nota = ""; //todo document.getProperty(NomesDeCampos.FIELD_TEXTO).toString();
                Node obj = calendario.lookup("#" + bostamp);
                if (obj instanceof VBoxOSBO) {
                    VBoxOSBO vBox = (VBoxOSBO) obj;
                    vBox.setNotaProp(nota);
                }
            }
        });
    }

    public TextField getTextFieldFiltroFrefAprovisionamento() {
        return textFieldFiltroFrefAprovisionamento;
    }

    public TextField getTextFieldFiltroFrefAtrasados() {
        return textFieldFiltroFrefAtrasados;
    }

    public Label getLabelTotRecsAprovisionamento() {
        return labelTotRecsAprovisionamento;
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

    private class Document {
    }
}
