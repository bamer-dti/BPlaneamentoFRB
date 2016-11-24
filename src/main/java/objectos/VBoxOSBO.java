package objectos;

import bamer.AppMain;
import bamer.ControllerEditar;
import bamer.ControllerNotas;
import com.google.firebase.database.*;
import javafx.application.Platform;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.StringConverter;
import pojos.ArtigoOSBO;
import pojos.Nota;
import sqlite.DBSQLite;
import sqlite.PreferenciasEmSQLite;
import utils.*;
import webservices.WSWorker;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

public class VBoxOSBO extends VBox {
    private static final int COR_AZUL = 0;
    private static final int COR_AMARELO = 1;
    private static final int COR_VERMELHO = 2;
    private static final int COR_VERDE = 3;
    private int coluna;
    private VBoxOSBO contexto = this;
    private int linha;

    private SimpleObjectProperty<ArtigoOSBO> artigoOSBOProp = new SimpleObjectProperty<>();
    private SimpleStringProperty bostampProp = new SimpleStringProperty();
    private SimpleStringProperty frefProp = new SimpleStringProperty();
    private SimpleIntegerProperty obranoProp = new SimpleIntegerProperty();
    private SimpleStringProperty nmfrefProp = new SimpleStringProperty();
    private SimpleStringProperty estadoProp = new SimpleStringProperty();
    private SimpleStringProperty seccaoProp = new SimpleStringProperty();
    private SimpleStringProperty obsProp = new SimpleStringProperty();
    private SimpleObjectProperty<LocalDate> dtcortefProp = new SimpleObjectProperty<>();
    private SimpleObjectProperty<LocalDate> dttransfProp = new SimpleObjectProperty<>();
    private SimpleObjectProperty<LocalDate> dtembalaProp = new SimpleObjectProperty<>();
    private SimpleObjectProperty<LocalDate> dtexpediProp = new SimpleObjectProperty<>();
    private SimpleIntegerProperty qttProp = new SimpleIntegerProperty();
    private SimpleIntegerProperty qttProdProp = new SimpleIntegerProperty();
    private SimpleStringProperty notaProp = new SimpleStringProperty("");
    private SimpleIntegerProperty corProp = new SimpleIntegerProperty();
    private SimpleLongProperty tempoTotalProp = new SimpleLongProperty();
    private SimpleIntegerProperty ordemProp = new SimpleIntegerProperty();

    private ContextMenu contextMenu;
    private Label labelTempos;
    private Label labelCorte;
    private Label labelTransf;
    private Label labelEmbal;
    private Label labelExped;
    private Label labelProd;
    private Label labelResultado;
    private HBox hBoxNotificar;
    private ImageView imageNotas;
    private Timer timer;
    private Label labelQtt;
    private String textoOriginalNota;
    private Stage stageEditarNota;

    public VBoxOSBO(ArtigoOSBO artigoOSBO) {
        contexto = this;

        setId(artigoOSBO.getBostamp());

        criarObjectos();

        configurarBinds();

        setArtigoOSBOProp(artigoOSBO);

        setEstilo(getArtigoOSBOProp().getCor());

        colocarEmAgenda();

        configurarEventos();

        configurarContextMenu();
    }

    private void criarObjectos() {
        PreferenciasEmSQLite prefs = PreferenciasEmSQLite.getInstancia();
        int minWidth = prefs.getInt(Constantes.PREF_COMPRIMENTO_MINIMO, ValoresDefeito.COL_COMPRIMENTO);
        setPrefWidth(minWidth);
        setMinWidth(minWidth);

        hBoxNotificar = new HBox();
        setMargin(hBoxNotificar, new Insets(1));

        Image imageSource = Funcoes.imagemResource("notas.jpg", 16, 16);
        imageNotas = new ImageView(imageSource);
        imageNotas.setManaged(false);
        imageNotas.setVisible(false);
        HBox.setMargin(imageNotas, new Insets(1, 5, 1, 1));
        Tooltip tooltip = new Tooltip();
        tooltip.textProperty().bindBidirectional(notaProp);
        Tooltip.install(imageNotas, tooltip);
        imageNotas.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
                    try {
                        editarNota();
                    } catch (IOException e) {
                        Funcoes.AlertaException(e);
                        e.printStackTrace();
                    }
                }
                event.consume();
            }
        });
        hBoxNotificar.getChildren().add(imageNotas);
        hBoxNotificar.setManaged(false);

        labelTempos = new Label("...");
        labelTempos.setManaged(false);
        Font font = labelTempos.getFont();
        labelTempos.setFont(Font.font(font.getFamily(), FontWeight.BLACK, font.getSize()));
        hBoxNotificar.getChildren().add(labelTempos);

        getChildren().add(hBoxNotificar);

//        //SECCAO
//        Label labelSeccao = new Label();
//        labelSeccao.setWrapText(true);
//        labelSeccao.textProperty().bind(estadoPropProperty());
//        setMargin(labelSeccao, new Insets(2, 2, 2, 2));
//        this.getChildren().add(labelSeccao);

        //OBRA
        Label labelObra = new Label();
        labelObra.setWrapText(true);
        labelObra.textProperty().bind(frefPropProperty().concat(new SimpleStringProperty("-")).concat(nmfrefPropPorpery()));
        setMargin(labelObra, new Insets(2, 2, 2, 2));
        this.getChildren().add(labelObra);

        //OBRANO
        Label labelObrano = new Label();
        StringExpression textoObrano = new SimpleStringProperty("OS ").concat(obranoPropProperty()).concat(" (").concat(ordemProp.asString()).concat(")");
        labelObrano.textProperty().bind(textoObrano);
        setMargin(labelObrano, new Insets(2, 2, 2, 2));
        this.getChildren().add(labelObrano);

        //OBS
        Label labelObs = new Label();
        labelObs.textProperty().bind(obsPropProperty());
        labelObs.setWrapText(true);
        setMargin(labelObs, new Insets(2, 2, 2, 2));
        getChildren().add(labelObs);

        //GRELHA DAS DATAS
        GridPane grelhaDatas = new GridPane();
        tooltip = new Tooltip("(duplo clique para editar os dados)");
        Tooltip.install(grelhaDatas, tooltip);
        ColumnConstraints coluna1 = new ColumnConstraints();
        coluna1.setHalignment(HPos.RIGHT);
        grelhaDatas.getColumnConstraints().add(coluna1);

        grelhaDatas.getColumnConstraints().add(new ColumnConstraints());
        ColumnConstraints coluna3 = new ColumnConstraints(Double.MIN_VALUE, Double.MIN_VALUE, Double.MAX_VALUE);
        coluna3.setHgrow(Priority.SOMETIMES);
        grelhaDatas.getColumnConstraints().add(coluna3);

        //CORTE
        labelCorte = new Label("crt");
        GridPane.setMargin(labelCorte, new Insets(2, 2, 2, 2));
        GridPane.setHalignment(labelCorte, HPos.RIGHT);
        grelhaDatas.add(labelCorte, 0, 0);
        labelCorte = new Label();

        GridPane.setMargin(labelCorte, new Insets(2, 2, 2, 2));
        grelhaDatas.add(labelCorte, 1, 0);

        //TRANSFORMAÇÃO
        labelTransf = new Label("trf");
        GridPane.setMargin(labelTransf, new Insets(2, 2, 2, 2));
        GridPane.setHalignment(labelTransf, HPos.RIGHT);
        grelhaDatas.add(labelTransf, 0, 1);
        labelTransf = new Label();

        GridPane.setMargin(labelTransf, new Insets(2, 2, 2, 2));
        grelhaDatas.add(labelTransf, 1, 1);

        //EMBALAGEM
        labelEmbal = new Label("emb");
        GridPane.setMargin(labelEmbal, new Insets(2, 2, 2, 2));
        GridPane.setHalignment(labelEmbal, HPos.RIGHT);
        grelhaDatas.add(labelEmbal, 0, 2);
        labelEmbal = new Label();

        GridPane.setMargin(labelEmbal, new Insets(2, 2, 2, 2));
        grelhaDatas.add(labelEmbal, 1, 2);

        //EXPEDIÇÃO
        labelExped = new Label("exp");
        GridPane.setMargin(labelExped, new Insets(2, 2, 2, 2));
        GridPane.setHalignment(labelExped, HPos.RIGHT);
        grelhaDatas.add(labelExped, 0, 3);
        labelExped = new Label();

        GridPane.setMargin(labelExped, new Insets(2, 2, 2, 2));
        grelhaDatas.add(labelExped, 1, 3);

        //QUANTIDADE
        labelQtt = new Label();
        Font fontBase = labelQtt.getFont();
        labelQtt.setFont(Font.font(fontBase.getFamily(), FontWeight.BOLD, FontPosture.REGULAR, 16f));
        labelQtt.setStyle("-fx-text-fill: blue; -fx-background-insets: 0, 1 1 1 0 ;");

        //QUANTIDADE PRODUZIDA
        labelProd = new Label();
        fontBase = labelProd.getFont();
        labelProd.setFont(Font.font(fontBase.getFamily(), FontWeight.BOLD, FontPosture.REGULAR, 16f));
        labelProd.setStyle("-fx-text-fill: red; -fx-background-insets: 0, 1 1 1 0 ;");

        labelResultado = new Label();
        fontBase = labelResultado.getFont();
        labelResultado.setFont(Font.font(fontBase.getFamily(), FontWeight.BOLD, FontPosture.REGULAR, 16f));
        labelResultado.setStyle("-fx-text-fill: green; -fx-background-insets: 0, 1 1 1 0 ;");

        HBox hBoxqtts = new HBox(labelQtt, labelProd, labelResultado);
        hBoxqtts.setAlignment(Pos.CENTER);
        HBox.setMargin(labelQtt, new Insets(2, 2, 2, 2));

        HBox.setMargin(labelProd, new Insets(2, 2, 2, 2));

        GridPane.setValignment(hBoxqtts, VPos.CENTER);
        GridPane.setHalignment(hBoxqtts, HPos.CENTER);
        grelhaDatas.add(hBoxqtts, 2, 0, 1, 4);
        grelhaDatas.setStyle("-fx-background-color: white; -fx-background-insets: 0, 1 1 1 0 ;");

        getChildren().add(grelhaDatas);
    }

    private void configurarBinds() {
//        out.println("TEMPO DO ARTIGO: " + artigoOSBOProp.get().getTempoTotal());
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        corProp.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                String estilo = "game-grid-cell-" + newValue;
                System.out.println("Colocar estilo " + estilo);
                Funcoes.colocarEstilo(contexto, estilo);
            }
        });

        tempoTotalProp.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (timer != null) {
                    timer.cancel();
                    timer.purge();
                    timer = null;
                }
                String bostamp = bostampProp.get();
                long tempoCalculado = DBSQLite.getInstancia().getTempoTotal(artigoOSBOProp.get().getBostamp());
                int ultimaPosicao = DBSQLite.getInstancia().getUltimaPosicao(bostamp);
                if (ultimaPosicao == Constantes.STARTED) {
                    corProp.set(COR_VERMELHO);
                    mostrarRegistoEmModoStarted(bostampProp.get());
                } else {
                    tempoCalculado = DBSQLite.getInstancia().getTempoTotal(bostamp);
                    if (tempoCalculado != 0) {
                        String textoTempo = Funcoes.milisegundos_em_HH_MM_SS(tempoCalculado * 1000);
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                labelTempos.setText("" + textoTempo);
                            }
                        });

                    } else {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                labelTempos.setText("");
                            }
                        });
                    }
                }

                boolean resultado = !(notaProp.get().equals("") && newValue.longValue() == 0);
                hBoxNotificar.setManaged(resultado);
                hBoxNotificar.setVisible(resultado);
                labelTempos.setManaged(newValue.longValue() != 0);
                labelTempos.setVisible(resultado);
            }

            private void mostrarRegistoEmModoStarted(String bostamp) {
                long tempoTotal = 0;
                long ultimoTempo = 0;
                DBSQLite slq = DBSQLite.getInstancia();
                tempoTotal = slq.getTempoTotal(bostamp);
                ultimoTempo = slq.getUltimoTempo(bostamp);


                final long finalTempoTotal = tempoTotal;
                final long finalUltimoTempo = ultimoTempo;
                TimerTask actualizarTempos = new TimerTask() {
                    @Override
                    public void run() {

                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                long unixNow = System.currentTimeMillis() / 1000L;
                                long intervaloTempo = unixNow - finalUltimoTempo;
                                labelTempos.setText(
                                        "TT: " + Funcoes.milisegundos_em_HH_MM_SS(finalTempoTotal * 1000 + intervaloTempo * 1000)
                                                + " TP: " + Funcoes.milisegundos_em_HH_MM_SS(intervaloTempo * 1000)
                                );
                            }
                        });
                    }
                };
                if (timer != null) {
                    timer.cancel();
                    timer.purge();
                    timer = null;
                }
                timer = new Timer();
                timer.schedule(actualizarTempos, 1000, 1000);
            }
        });

        dtcortefProp.addListener(new ChangeListener<LocalDate>() {
            @Override
            public void changed(ObservableValue<? extends LocalDate> observable, LocalDate oldValue, LocalDate newValue) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        labelCorte.setText(dtf.format(newValue));
                    }
                });
            }
        });

        dttransfProp.addListener(new ChangeListener<LocalDate>() {
            @Override
            public void changed(ObservableValue<? extends LocalDate> observable, LocalDate oldValue, LocalDate newValue) {
                labelTransf.setText(dtf.format(newValue));
            }
        });

        dtembalaProp.addListener(new ChangeListener<LocalDate>() {
            @Override
            public void changed(ObservableValue<? extends LocalDate> observable, LocalDate oldValue, LocalDate newValue) {
                labelEmbal.setText(dtf.format(newValue));
            }
        });

        dtexpediProp.addListener(new ChangeListener<LocalDate>() {
            @Override
            public void changed(ObservableValue<? extends LocalDate> observable, LocalDate oldValue, LocalDate newValue) {
                labelExped.setText(dtf.format(newValue));
            }
        });

        qttProp.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        labelQtt.setText("" + newValue.intValue());
                    }
                });
                AppMain.getInstancia().actualizarTextoColunasZero(coluna);
            }
        });

        qttProdProp.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (newValue.intValue() == 0) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            labelProd.setText("");
                            labelResultado.setText("");
                            corProp.set(0);
                        }
                    });

                } else {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            labelProd.setText("-" + newValue);
                            labelResultado.setText("=" + (qttProp.get() - qttProdProp.get()));
                            if (newValue.intValue() == qttProp.get()) {
                                corProp.set(COR_VERDE);
                            }
                            if (newValue.intValue() != qttProp.get()) {
                                corProp.set(COR_AMARELO);
                            }
                        }
                    });
                }
                tempoTotalProp.set(tempoTotalProp.get());// pintar com tempos
                AppMain.getInstancia().actualizarTextoColunasZero(coluna);
            }
        });

        notaProp.addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
//                boolean resultado = !(newValue.equals("") && tempoTotalProp.get() == 0);
                boolean resultado = !(newValue.equals(""));
                hBoxNotificar.setManaged(resultado);
                hBoxNotificar.setVisible(resultado);
                imageNotas.setManaged(!newValue.equals(0));
                imageNotas.setVisible(true);
            }
        });
    }

    private void setEstilo(int conversor) {
        String estilo = "game-grid-cell-" + conversor;
        Funcoes.colocarEstilo(this, estilo);
    }

    private void colocarEmAgenda() {
        if (coluna < 0)
            return;
        GridPaneCalendario calendario = AppMain.getInstancia().getCalendario();
        linha = getOrdemProp();
        VBoxOSBO vBoxOSBO = this;

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                calendario.add(vBoxOSBO, coluna, linha);
            }
        });
    }

    private void configurarEventos() {
        VBoxOSBO source = this;
        setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
                    try {
                        abrirEdicao();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    event.consume();
                }
                if (event.isSecondaryButtonDown() && !contextMenu.isShowing()) {
                    contextMenu.show(source, event.getScreenX(), event.getScreenY());
                    event.consume();
                }
                if (event.isSecondaryButtonDown() && contextMenu.isShowing()) {
                    contextMenu.hide();
                    contextMenu.show(source, event.getScreenX(), event.getScreenY());
                    event.consume();
                }
                if (event.isPrimaryButtonDown() && contextMenu.isShowing()) {
                    contextMenu.hide();
                }
            }
        });

        this.setOnDragDetected(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (!AppMain.getInstancia().getComboSeccao().isVisible()) {
                    event.consume();
                    return;
                }
                Dragboard dragboard = startDragAndDrop(TransferMode.MOVE);
                Rectangle rect = new Rectangle(contexto.getWidth(), contexto.getHeight());
                rect.setFill(Color.DARKBLUE);
                Image image = rect.snapshot(null, null);
                double x = contexto.getTranslateX();
                double y = contexto.getTranslateY();
                dragboard.setDragView(image, x, y);
                Map<DataFormat, Object> map = new HashMap<>();
                map.put(DataFormat.RTF, contexto);
                dragboard.setContent(map);
                event.consume();
            }
        });

        this.setOnDragEntered(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Object object = event.getDragboard().getContent(DataFormat.RTF);
                if (object instanceof VBoxOSBO) {
                    Dragboard dragboard = event.getDragboard();
                    VBoxOSBO vBoxOSBO = (VBoxOSBO) dragboard.getContent(DataFormat.RTF);
                    ArtigoOSBO artigoOSBO = vBoxOSBO.getArtigoOSBOProp();
                    if (!artigoOSBO.getBostamp().equals(contexto.getId())) {
                        String idToFind = "#header0" + coluna;
                        Text mText = (Text) AppMain.getInstancia().getCalendario().lookup(idToFind);
                        Font font = mText.getFont();
                        mText.setFill(Color.INDIANRED);
                        mText.setFont(Font.font(font.getFamily(), FontWeight.BOLD, font.getSize()));
                    }
                    event.consume();
                }
            }
        });

        this.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Object object = event.getDragboard().getContent(DataFormat.RTF);
                if (object instanceof VBoxOSBO) {
                    Dragboard dragboard = event.getDragboard();
                    VBoxOSBO vBoxOSBOemDrag = (VBoxOSBO) dragboard.getContent(DataFormat.RTF);
                    ArtigoOSBO artigoOSBO = vBoxOSBOemDrag.getArtigoOSBOProp();
                    if (artigoOSBO.getBostamp().equals(contexto.getId())) {
                        event.acceptTransferModes(TransferMode.NONE);
                    } else {
                        event.acceptTransferModes(TransferMode.MOVE);
                        disableEnabledObjects(contexto, false);
                    }
                    event.consume();
                }
            }
        });

        this.setOnDragExited(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Object object = event.getDragboard().getContent(DataFormat.RTF);
                if (object instanceof VBoxOSBO) {
                    Dragboard dragboard = event.getDragboard();
                    VBoxOSBO vBoxOSBO = (VBoxOSBO) dragboard.getContent(DataFormat.RTF);
                    ArtigoOSBO artigoOSBO = vBoxOSBO.getArtigoOSBOProp();
                    if (artigoOSBO.getBostamp().equals(contexto.getId())) {
                        event.acceptTransferModes(TransferMode.NONE);
                    } else {
                        String idToFind = "#header0" + coluna;
                        Text mText = (Text) AppMain.getInstancia().getCalendario().lookup(idToFind);
                        Font font = mText.getFont();
                        mText.setFill(Color.BLACK);
                        mText.setFont(Font.font(font.getFamily(), FontWeight.NORMAL, font.getSize()));
                        disableEnabledObjects(contexto, true);
                    }
                    event.consume();
                }
            }
        });

        this.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Object object = event.getDragboard().getContent(DataFormat.RTF);
                if (object instanceof VBoxOSBO) {
                    Dragboard dragboard = event.getDragboard();
                    VBoxOSBO vboxEmDRAG = (VBoxOSBO) dragboard.getContent(DataFormat.RTF);
                    ArtigoOSBO artigoOSBOemDRAG = vboxEmDRAG.getArtigoOSBOProp();
                    if (artigoOSBOemDRAG.getBostamp().equals(getId())) {
                        event.consume();
                        return;
                    }
                    int ordemNova = getOrdemProp();
                    String bostamp = artigoOSBOemDRAG.getBostamp();
                    String seccao = artigoOSBOemDRAG.getSeccao();
                    String estado = artigoOSBOemDRAG.getEstado();
                    LocalDate u_dtcortef = getDtcortefProp();
                    LocalDate u_dttransf = u_dtcortef.plusDays(1);
                    try {
                        WSWorker.actualizarOrdem(bostamp, ordemNova, Funcoes.dToC(u_dtcortef, "yyyyMMdd"), Funcoes.dToC(u_dttransf, "yyyyMMdd"), seccao, estado);
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    event.consume();
                }
            }
        });
    }

    private void configurarContextMenu() {
        contextMenu = new ContextMenu();

//        Menu menuCor = new Menu("colorir");
//        MenuItem menuItemAmarelo = new MenuItem("amarelo");
//        menuItemAmarelo.setOnAction(new EventHandler<ActionEvent>() {
//            public void handle(ActionEvent e) {
//                pintar(COR_AMARELO);
//            }
//        });
//        menuCor.getItems().add(menuItemAmarelo);
//
//        MenuItem menuItemVermelho = new MenuItem("vermelho");
//        menuItemVermelho.setOnAction(new EventHandler<ActionEvent>() {
//            public void handle(ActionEvent e) {
//                pintar(COR_VERMELHO);
//            }
//        });
//        menuCor.getItems().add(menuItemVermelho);
//
//        MenuItem menuItemAzul = new MenuItem("azul");
//        menuItemAzul.setOnAction(new EventHandler<ActionEvent>() {
//            public void handle(ActionEvent e) {
//                pintar(COR_AZUL);
//            }
//        });
//        menuCor.getItems().add(menuItemAzul);
//
//        MenuItem menuItemVerde = new MenuItem("verde");
//        menuItemVerde.setOnAction(new EventHandler<ActionEvent>() {
//            public void handle(ActionEvent e) {
//                pintar(COR_VERDE);
//            }
//        });
//        menuCor.getItems().add(menuItemVerde);

        Menu menuAccoes = new Menu("tools");
        MenuItem itemNota = new MenuItem("notas");
        itemNota.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    editarNota();
                } catch (IOException e) {
                    Funcoes.AlertaException(e);
                    e.printStackTrace();
                }
            }
        });

//        MenuItem testes = new MenuItem("Abrir info versão");
//        testes.setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent event) {
//                try {
//                    AppMain.abrirFicheiroVersionTXT();
//                } catch (IOException e) {
//                    Funcoes.AlertaException(e);
//                    e.printStackTrace();
//                }
//            }
//        });
        menuAccoes.getItems().add(itemNota);

//        contextMenu.getItems().addAll(menuCor, menuAccoes);
//        contextMenu.getItems().addAll(menuAccoes, testes);
        contextMenu.getItems().addAll(menuAccoes);
    }

    private void pintar(int cor) {
        if (cor != getArtigoOSBOProp().getCor()) {
            corProp.set(cor);
            artigoOSBOProp.get().setCor(cor);
            WSWorker.actualizarCor(bostampProp.get(), cor);
        }
    }

    private void editarNota() throws IOException {
        URL location = ClassLoader.getSystemResource("editarNota.fxml");
        FXMLLoader loader = new FXMLLoader(location);
        Parent root = loader.load();
        stageEditarNota = new Stage();
        stageEditarNota.setTitle("Notas");
        stageEditarNota.setScene(new Scene(root));
        ControllerNotas controller = loader.getController();
        controller.areaDoTexto.textProperty().bindBidirectional(notaProp);
        stageEditarNota.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                controller.areaDoTexto.setPrefWidth(newValue.intValue());
            }
        });
        textoOriginalNota = notaProp.get();
        stageEditarNota.initModality(Modality.APPLICATION_MODAL);
        stageEditarNota.initStyle(StageStyle.UTILITY);
        stageEditarNota.show();
        stageEditarNota.setOnHiding(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                guardarNota();
            }
        });
    }

    private void guardarNota() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ArtigoOSBO artigoOSBO = artigoOSBOProp.get();
        String bostamp = artigoOSBO.getBostamp();
        String data = Funcoes.currentTimeStringStamp("dd.MM.yyyy HH:mm");
        String texto = notaProp.get();
        Nota nota = new Nota(data, texto);
        if (texto.equals("")) {
            ref.child(Campos.KEY_NOTAS).child(bostamp).removeValue();
            return;
        }
        if (!texto.equals(textoOriginalNota)) {
            ref.child(Campos.KEY_NOTAS).child(bostamp).setValue(nota);
        }
    }

    private void abrirEdicao() throws IOException {
        URL location = ClassLoader.getSystemResource("editarCompromisso.fxml");
        FXMLLoader loader = new FXMLLoader(location);
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setTitle("Editar");
        stage.setScene(new Scene(root));
        ControllerEditar controller = loader.getController();

        StringConverter<LocalDate> converter = new StringConverter<LocalDate>() {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }
        };

        controller.fref.setText(frefPropProperty().get() + "-" + nmfrefPropPorpery().get());
        controller.os.setText(obranoPropProperty().get() + "");
        controller.obs.setText(obsPropProperty().get());

        DatePicker dateObj = controller.dtcortef;
        dateObj.setConverter(converter);
        dateObj.setValue(dtcortefProp.get());
        dateObj.setEditable(false);

        dateObj = controller.dttransf;
        dateObj.setConverter(converter);
        dateObj.setValue(dttransfPropProperty().get());
        dateObj.setEditable(false);

        dateObj = controller.dtembala;
        dateObj.setConverter(converter);
        dateObj.setValue(dtembalaPropProperty().get());
        dateObj.setEditable(false);

        dateObj = controller.dtexpedi;
        dateObj.setConverter(converter);
        LocalDate dexped = dtexpediPropProperty().get();
        dateObj.setValue(dexped);
        dateObj.setEditable(false);
        dateObj.setDisable(dexped.equals(LocalDate.of(1900, Month.JANUARY, 1)));
//        dateObj.setStyle("-fx-opacity: 1");
//        dateObj.getEditor().setStyle("-fx-opacity: 1");

        Button botao_gravar = controller.btgravar;
        botao_gravar.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String dtcortef = Funcoes.dToSQL(controller.dtcortef.getValue());
                String dttransf = Funcoes.dToSQL(controller.dttransf.getValue());
                String dtembala = Funcoes.dToSQL(controller.dtembala.getValue());
                String dtexpedi = Funcoes.dToSQL(controller.dtexpedi.getValue());
                String estado = controller.combo_estado.getValue().toString();

                int rows = 0;
                //                    rows = BamerSqlServer.getInstancia().editarEactualizarDatas(bostampProp.get(), dtcortef, dttransf, dtembala, dtexpedi, estado);
                WSWorker.editarDadosOP(stage, contexto, controller, bostampProp.get(), dtcortef, dttransf, dtembala, dtexpedi, estado);

                if (rows > 0) {

                }

                stage.close();
            }
        });

        controller.setData(estadoProp.getValue());

        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UTILITY);
        stage.show();
    }

    private void disableEnabledObjects(VBoxOSBO context, Boolean visible) {
        ObservableList<Node> childs = context.getChildren();
        for (Node node : childs) {
            if (node instanceof Label) {
                node.setVisible(visible);
            }
            if (node instanceof GridPane) {
                node.setVisible(visible);
            }
        }
    }

    public ArtigoOSBO getArtigoOSBOProp() {
        return artigoOSBOProp.get();
    }

    public void setArtigoOSBOProp(ArtigoOSBO artigoOSBO) {
        artigoOSBOProp.set(artigoOSBO);
        setBostampProp(artigoOSBO.getBostamp());
        frefProp.set(artigoOSBO.getFref());
        nmfrefProp.set(artigoOSBO.getNmfref());
        obranoProp.set(artigoOSBO.getObrano());
        estadoProp.set(artigoOSBO.getEstado());
        seccaoProp.set(artigoOSBO.getSeccao());
        obsProp.set(artigoOSBO.getObs());

        dtcortefProp.set(Funcoes.cToD(artigoOSBO.getDtcortef()));
        dttransfProp.set(Funcoes.cToD(artigoOSBO.getDttransf()));
        dtembalaProp.set(Funcoes.cToD(artigoOSBO.getDtembala()));
        dtexpediProp.set(Funcoes.cToD(artigoOSBO.getDtexpedi()));

        ordemProp.set(artigoOSBO.getOrdem());

        corProp.set(artigoOSBO.getCor());

        coluna = calcularColuna();

        if (coluna < 0) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        GridPaneCalendario gridPaneCalendario = AppMain.getInstancia().getCalendario();
                        VBoxOSBO vBoxOSBO = (VBoxOSBO) gridPaneCalendario.lookup("#" + artigoOSBO.getBostamp());
                        gridPaneCalendario.getChildren().remove(vBoxOSBO);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            DBSQLite sql = DBSQLite.getInstancia();
            String bostamp = bostampProp.get();
            if (ordemProp.get() < 99) {
                qttProp.set(sql.getQtdPedidaBostamp(bostamp));
                qttProdProp.set(sql.getQtdProduzidaBostamp(bostamp));
                tempoTotalProp.set(sql.getTempoTotal(bostamp));
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        GridPane.setConstraints(contexto, coluna, ordemProp.get());
                    }
                });
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(Campos.KEY_NOTAS).child(bostamp);
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (stageEditarNota == null)
                            return;
                        if (stageEditarNota.isShowing())
                            return;
                        Nota nota = dataSnapshot.getValue(Nota.class);
                        if (nota != null) {
                            System.out.println(dataSnapshot);
                            notaProp.set(nota.texto);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Funcoes.AlertaException(databaseError.toException());
                    }
                });

            }
        }
    }

    private SimpleStringProperty frefPropProperty() {
        return frefProp;
    }

    private SimpleStringProperty nmfrefPropPorpery() {
        return nmfrefProp;
    }

    private SimpleStringProperty obsPropProperty() {
        return obsProp;
    }

    private int calcularColuna() {
        return (int) Singleton.getInstancia().dataInicioAgenda.until(getDtcortefProp(), ChronoUnit.DAYS);
    }

    public void setBostampProp(String bostamp) {
        this.bostampProp.set(bostamp);
    }

    public SimpleIntegerProperty obranoPropProperty() {
        return obranoProp;
    }

    public LocalDate getDtcortefProp() {
        return dtcortefProp.get();
    }

    public SimpleObjectProperty<LocalDate> dttransfPropProperty() {
        return dttransfProp;
    }

    public SimpleObjectProperty<LocalDate> dtembalaPropProperty() {
        return dtembalaProp;
    }

    public SimpleObjectProperty<LocalDate> dtexpediPropProperty() {
        return dtexpediProp;
    }

    public int getOrdemProp() {
        return ordemProp.get();
    }

    public int getColuna() {
        return coluna;
    }

    public void actualizarQtdPedida() {
        int qtt = DBSQLite.getInstancia().getQtdPedidaBostamp(bostampProp.get());
        qttProp.set(qtt);
    }

    public void actualizarQtdProduzida() {
        int qtt = DBSQLite.getInstancia().getQtdProduzidaBostamp(bostampProp.get());
        qttProdProp.set(qtt);
    }

    public void actualizarCronometros() {
        tempoTotalProp.set(-1);
    }

    public SimpleObjectProperty<LocalDate> dtcortefPropProperty() {
        return dtcortefProp;
    }
}
