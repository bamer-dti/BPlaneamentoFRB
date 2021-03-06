package objectos;

import bamer.AppMain;
import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;
import pojos.ArtigoLinhaPlanOUAtraso;
import pojos.ArtigoOSBO;
import sqlite.DBSQLite;
import sqlite.PreferenciasEmSQLite;
import utils.*;

import java.time.LocalDate;
import java.util.Timer;

import static java.lang.System.out;

public class VBoxDia extends VBox {
    @SuppressWarnings("unused")
    private static final String TAG = VBoxDia.class.getSimpleName();
    public Timer chronoWeather;
    public int tempoRemanescente;
    public String keyIPMA;
    private int coluna;
    private Text textDiaDaSemana;
    private Text textDiaMes;
    private Text textQtd;
    private Text textQtdFeita;
    private boolean mostraResize;
    private Text textSemana;
    private ImageView imageViewTempo;
    private Label labelTempMax;
    private Label labelTempMin;


    VBoxDia(Boolean mostraResizee) {
        this.mostraResize = mostraResizee;
        objectos();
        configurarEventos();
        resize();
    }

    public Label getLabelTempMax() {
        return labelTempMax;
    }

    public Label getLabelTempMin() {
        return labelTempMin;
    }

    public ImageView getImageViewTempo() {
        return imageViewTempo;
    }

    public void resize() {
        PreferenciasEmSQLite prefs = PreferenciasEmSQLite.getInstancia();
        int minWidth = prefs.getInt(Constantes.Preferencias.PREF_COMPRIMENTO_MINIMO, ValoresDefeito.COL_COMPRIMENTO);
        setPrefWidth(minWidth);
        setMinWidth(minWidth);
    }

    private void objectos() {
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER);

        VBox vBoxTemp = new VBox(1);
        labelTempMax = new Label("--º");
        labelTempMin = new Label("--º");
        labelTempMax.setManaged(false);
        labelTempMin.setManaged(false);
        vBoxTemp.setAlignment(Pos.TOP_RIGHT);
        vBoxTemp.getChildren().addAll(labelTempMax, labelTempMin);
        hBox.getChildren().add(vBoxTemp);

        imageViewTempo = new ImageView();
        imageViewTempo.setManaged(false);
        imageViewTempo.setVisible(false);
        hBox.getChildren().add(imageViewTempo);

        if (mostraResize) {
            VBox vBoxPlusMinus = new VBox();

            FontIcon icone = new FontIcon();
            icone.setIconCode(FontAwesome.PLUS);
            icone.setIconColor(Color.ALICEBLUE);
            JFXButton btplus = new JFXButton();
            btplus.setGraphic(icone);
            btplus.getStyleClass().add("button-raised-bamer-sizecolumn");
            btplus.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    PreferenciasEmSQLite prefs = PreferenciasEmSQLite.getInstancia();
                    int tamanho = prefs.getInt(Constantes.Preferencias.PREF_COMPRIMENTO_MINIMO, ValoresDefeito.COL_COMPRIMENTO);
                    tamanho = tamanho + 1;
                    prefs.putInt(Constantes.Preferencias.PREF_COMPRIMENTO_MINIMO, tamanho);
                    alterarTamanhos(tamanho);
                    out.println("Plus clicked! Novo tamanho = " + tamanho);
                    event.consume();
                }
            });
            vBoxPlusMinus.getChildren().add(btplus);

            icone = new FontIcon();
            icone.setIconCode(FontAwesome.MINUS);
            icone.setIconColor(Color.ALICEBLUE);
            JFXButton btminus = new JFXButton();
            btminus.setGraphic(icone);
            btminus.getStyleClass().add("button-raised-bamer-sizecolumn");
            btminus.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    out.println("Minus clicked!");
                    PreferenciasEmSQLite prefs = PreferenciasEmSQLite.getInstancia();
                    int tamanho = prefs.getInt(Constantes.Preferencias.PREF_COMPRIMENTO_MINIMO, ValoresDefeito.COL_COMPRIMENTO);
                    tamanho = tamanho - 1;
                    prefs.putInt(Constantes.Preferencias.PREF_COMPRIMENTO_MINIMO, tamanho);
                    alterarTamanhos(tamanho);
                    out.println("Plus clicked! Novo tamanho = " + tamanho);
                    event.consume();
                }
            });
            vBoxPlusMinus.getChildren().add(btminus);

            hBox.getChildren().add(vBoxPlusMinus);
        }

        textDiaDaSemana = new Text();
        textDiaDaSemana.setFont(Font.font(textDiaDaSemana.getFont().getSize() * 1.5));
        HBox.setMargin(textDiaDaSemana, new Insets(2, 2, 2, 2));
        hBox.getChildren().add(textDiaDaSemana);

        VBox vBoxDiaMesSemana = new VBox();

        textDiaMes = new Text();
//        textDiaMes.setId("header0" + coluna);
        VBox.setMargin(textDiaMes, new Insets(0, 2, 0, 2));
        vBoxDiaMesSemana.getChildren().add(textDiaMes);

        textSemana = new Text();
        Font fonte = textDiaDaSemana.getFont();
        textSemana.setFont(Font.font(fonte.getFamily(), FontPosture.ITALIC, 12f));
        textSemana.setFill(Color.DARKBLUE);
        VBox.setMargin(textSemana, new Insets(0, 2, 0, 2));
        vBoxDiaMesSemana.getChildren().add(textSemana);
        vBoxDiaMesSemana.setAlignment(Pos.CENTER);

        hBox.getChildren().add(vBoxDiaMesSemana);

        textQtd = new Text();
        Font font = textQtd.getFont();
        textQtd.setFont(Font.font(font.getFamily(), FontWeight.BOLD, font.getSize() * 1.5));
        textQtd.setFill(Color.web("#548045"));
        HBox.setMargin(textQtd, new Insets(2, 2, 2, 10));

        textQtdFeita = new Text();
        font = textQtdFeita.getFont();
        textQtdFeita.setFont(Font.font(font.getFamily(), FontWeight.BOLD, font.getSize() * 1.5));
        textQtdFeita.setFill(Color.web("#E31751"));
        HBox.setMargin(textQtdFeita, new Insets(2, 2, 2, 10));

        hBox.getChildren().addAll(textQtd, textQtdFeita);

        getChildren().add(hBox);
    }

    private void alterarTamanhos(int tamanho) {
        ObservableList<Node> childs = AppMain.getInstancia().getCalendario().getChildren();
        for (Node node : childs) {
            if (node instanceof VBoxOSBO) {
                VBoxOSBO vb = (VBoxOSBO) node;
                vb.setPrefWidth(tamanho);
                vb.setMinWidth(tamanho);
            }
        }
    }

    private void configurarEventos() {
        this.setOnDragEntered(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Object object = event.getDragboard().getContent(DataFormat.RTF);
//                out.println("OnDragEntered: " + object.getClass().getSimpleName());
                if (object instanceof VBoxOSBO) {
                    Dragboard dragboard = event.getDragboard();
                    VBoxOSBO vBoxOSBO = (VBoxOSBO) dragboard.getContent(DataFormat.RTF);
                    if (vBoxOSBO.getColuna() == coluna) {
                        return;
                    }
                    if (!AppMain.getInstancia().getFiltroDeObra().equals("")) {
                        return;
                    }
                    Font font = textDiaDaSemana.getFont();
                    textDiaDaSemana.setFill(Color.INDIANRED);
                    textDiaDaSemana.setFont(Font.font(font.getFamily(), FontWeight.BOLD, font.getSize()));
                    event.consume();
                }

                if (object instanceof HBoxLinhaPlanOUAtraso) {
                    Font font = textDiaDaSemana.getFont();
                    textDiaDaSemana.setFill(Color.INDIANRED);
                    textDiaDaSemana.setFont(Font.font(font.getFamily(), FontWeight.BOLD, font.getSize()));
                    event.consume();
                }
            }
        });

        this.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Object object = event.getDragboard().getContent(DataFormat.RTF);
//                out.println("OnDragOver: " + object.getClass().getSimpleName());
                if (object instanceof VBoxOSBO) {
                    Dragboard dragboard = event.getDragboard();
                    VBoxOSBO vBoxOSBO = (VBoxOSBO) dragboard.getContent(DataFormat.RTF);
                    if (vBoxOSBO.getColuna() == coluna) {
                        event.acceptTransferModes(TransferMode.NONE);
                    } else {
                        event.acceptTransferModes(TransferMode.MOVE);
                    }
                    if (!AppMain.getInstancia().getFiltroDeObra().equals("")) {
                        event.acceptTransferModes(TransferMode.NONE);
                    }
                    event.consume();
                }

                if (object instanceof HBoxLinhaPlanOUAtraso) {
                    event.acceptTransferModes(TransferMode.MOVE);
                    event.consume();
                }
            }
        });

        this.setOnDragExited(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Object object = event.getDragboard().getContent(DataFormat.RTF);
//                out.println("OnDragExited: " + object.getClass().getSimpleName());
                if (object instanceof VBoxOSBO) {
                    Dragboard dragboard = event.getDragboard();
                    VBoxOSBO vBoxOSBO = (VBoxOSBO) dragboard.getContent(DataFormat.RTF);
                    if (vBoxOSBO.getColuna() == coluna) {
                        return;
                    }
                    if (!AppMain.getInstancia().getFiltroDeObra().equals("")) {
                        return;
                    }
                    Font font = textDiaDaSemana.getFont();
                    textDiaDaSemana.setFill(Color.BLACK);
                    textDiaDaSemana.setFont(Font.font(font.getFamily(), FontWeight.NORMAL, font.getSize()));
                    event.consume();
                }

                if (object instanceof HBoxLinhaPlanOUAtraso) {
                    Font font = textDiaDaSemana.getFont();
                    textDiaDaSemana.setFill(Color.BLACK);
                    textDiaDaSemana.setFont(Font.font(font.getFamily(), FontWeight.NORMAL, font.getSize()));
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

                    if (vboxEmDRAG.getColuna() == coluna) {
                        return;
                    }
                    if (!AppMain.getInstancia().getFiltroDeObra().equals("")) {
                        return;
                    }
                    ArtigoOSBO artigoOSBOemDRAG = vboxEmDRAG.getArtigoOSBOProp();
//                    if (artigoOSBOemDRAG.getBostamp().equals(getId())) {
//                        event.consume();
//                        return;
//                    }
                    int ordemOriginal = artigoOSBOemDRAG.getOrdem();

                    //Calcular a ordem do último OSBO da coluna
                    System.out.println("Coluna do Drag: " + vboxEmDRAG.getColuna() + ", VBoxDia.coluna: " + coluna);
//                    int ultima_ordem_coluna = Funcoes.obter_ordem_do_ultimo_VBoxOSBO_da_coluna(coluna);
                    LocalDate dataDeOperacao = Singleton.getInstancia().dataInicioAgenda.plusDays(coluna);
                    String data = dataDeOperacao + " 00:00:00";
                    int ultima_ordem_coluna = DBSQLite.getInstancia().select_max_ordem(AppMain.getInstancia().getSeccao(), AppMain.getInstancia().getEstado(), data);
//                    Funcoes.actualizar_fila_ascendente_OSBO(vboxEmDRAG.getColuna(), artigoOSBOemDRAG.getOrdem(), 999);
                    artigoOSBOemDRAG.setDtoper(data);
                    artigoOSBOemDRAG.setOrdem(ultima_ordem_coluna + 1);
                    Procedimentos.actualizar_OSBO(artigoOSBOemDRAG, Constantes.Operacao.ADICIONAR);
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            AppMain.getInstancia().getCalendario().getChildren().remove(object);
                            Procedimentos.actualizar_restantes_OSBOs(vboxEmDRAG.getColuna(), ordemOriginal);
                        }

                    });
                    event.consume();
                }

                //Se for um objecto POR PLANEAR ou EM ATRASO
                if (object instanceof HBoxLinhaPlanOUAtraso) {
                    Dragboard dragboard = event.getDragboard();
                    HBoxLinhaPlanOUAtraso hBoxOSPorPlanear = (HBoxLinhaPlanOUAtraso) dragboard.getContent(DataFormat.RTF);
                    LocalDate dataDeOperacao = Singleton.getInstancia().dataInicioAgenda.plusDays(coluna);
                    ArtigoLinhaPlanOUAtraso artigoLinhaPlanOUAtraso = hBoxOSPorPlanear.getArtigoLinhaPlanOUAtraso();
                    artigoLinhaPlanOUAtraso.setDtcli(Funcoes.dToC(dataDeOperacao, "yyyy-MM-dd" + " 00:00:00"));

//                    String bostamp = artigoLinhaPlanOUAtraso.getBostamp();
//                    int ordemNova = 999;
//                    LocalDate u_dtcortef = dataDeCorte;
//                    LocalDate u_dttransf = dataDeCorte.plusDays(1);
//                    String seccao = artigoLinhaPlanOUAtraso.getSeccao();
//                    String estado = Constantes.ESTADO_01_CORTE;
//                    try {
//                        WSWorker.actualizarOrdem(bostamp, ordemNova, Funcoes.dToC(u_dtcortef, "yyyyMMdd"), Funcoes.dToC(u_dttransf, "yyyyMMdd"), seccao, estado);
//                    } catch (ExecutionException | InterruptedException e) {
//                        e.printStackTrace();
//                    }

                    GridPaneCalendario gridPaneCalendario = AppMain.getInstancia().getCalendario();
                    int ordem = 0;
                    for (Node node : gridPaneCalendario.getChildren()) {
                        if (node instanceof VBoxOSBO && GridPane.getColumnIndex(node) == coluna) {
                            VBoxOSBO vBoxOSBO = (VBoxOSBO) node;
                            ordem = vBoxOSBO.getArtigoOSBOProp().getOrdem() > ordem ? vBoxOSBO.getArtigoOSBOProp().getOrdem() : ordem;
                        }
                    }
                    ordem++;
                    String data = dataDeOperacao + " 00:00:00";
                    ArtigoOSBO artigoOSBO = artigoLinhaPlanOUAtraso.transformar_Em_OSBO(ordem, data);

                    Procedimentos.actualizar_OSBO(artigoOSBO, Constantes.Operacao.ACTUALIZAR);

                    if (hBoxOSPorPlanear.getTipo() == HBoxLinhaPlanOUAtraso.TIPO_ATRASADO) {
                        GridPaneAtrasados.actualizar(AppMain.getInstancia().getSeccao(), AppMain.getInstancia().getEstado(), false);
                    }
                    if (hBoxOSPorPlanear.getTipo() == HBoxLinhaPlanOUAtraso.TIPO_PORPLANEAR) {
                        GridPanePorPlanear.actualizar(AppMain.getInstancia().getSeccao(), AppMain.getInstancia().getEstado(), false);
                    }

                    event.consume();
                }
            }
        });
    }

    public void setDataText(String dataText) {
        textDiaDaSemana.setText(dataText);
    }

    public void setTextoDiaMes(String textoDiaMes) {
        textDiaMes.setText(textoDiaMes);
    }

    public void setTextoSemana(String textoSemana) {
        textSemana.setText(textoSemana);
    }

    public int getColuna() {
        return coluna;
    }

    void setColuna(int coluna) {
        this.coluna = coluna;
        textDiaDaSemana.setId("header0" + coluna);
        textQtd.setId("qtttot" + coluna);
        textQtdFeita.setId("qttfeita" + coluna);
    }
}
