package objectos;

import bamer.AppMain;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import pojos.ArtigoLinhaPlanOUAtraso;
import utils.Funcoes;
import utils.Procedimentos;

import java.util.HashMap;
import java.util.Map;

public class HBoxLinhaPlanOUAtraso extends HBox {
    public static final int TIPO_PORPLANEAR = 1;
    public static final int TIPO_ATRASADO = 2;
    private int tipo;
    private String dtcliente;
    private int obrano;
    private String bostamp;
    private String dtexpedi;
    private String fref;
    private String nmfref;
    private String obs;
    private ArtigoLinhaPlanOUAtraso artigoLinhaPlanOUAtraso;
    private String estado;
    private String seccao;
    private HBoxLinhaPlanOUAtraso hBoxLinhaPlanOUAtraso = this;
    private int linha;

    public HBoxLinhaPlanOUAtraso(ArtigoLinhaPlanOUAtraso artigo, int linha, int tipoRegisto) {
        this.artigoLinhaPlanOUAtraso = artigo;
        this.seccao = artigo.getSeccao();
        this.dtcliente = artigo.getDtcli();
        this.obrano = artigo.getObrano();
        this.bostamp = artigo.getBostamp();
        this.dtexpedi = artigo.getDtexp();
        this.fref = artigo.getFref();
        this.nmfref = artigo.getNmfref();
        this.obs = artigo.getObs();
        this.linha = linha;
        this.tipo = tipoRegisto;
        this.estado = artigoLinhaPlanOUAtraso.getEstado();

        try {
            criarObjectos();
        } catch (Exception ignored) {
        }

        configurarEventos();
    }

    private void criarObjectos() {
        int qtt = artigoLinhaPlanOUAtraso.getQtt();
        {
            Label labelSeccao = new Label(seccao + "\n(" + estado + ")");
            labelSeccao.setWrapText(true);
            setMargin(labelSeccao, new Insets(2, 2, 2, 2));
            labelSeccao.setPrefWidth(130);
            Font font = labelSeccao.getFont();
            labelSeccao.setFont(new Font(11));
            this.getChildren().add(labelSeccao);

            Label labelOS = new Label("OS " + obrano);
            labelOS.setWrapText(true);
            setMargin(labelOS, new Insets(2, 2, 2, 20));
            this.getChildren().add(labelOS);

            Label labelDataCliente = new Label(Funcoes.dToC(Funcoes.cToD(dtcliente), "dd.MM.yyyy"));
            labelDataCliente.setWrapText(true);
            setMargin(labelDataCliente, new Insets(2, 2, 2, 20));
            this.getChildren().add(labelDataCliente);
            Tooltip t = new Tooltip("Data de cliente");
            Tooltip.install(labelDataCliente, t);

            if (tipo == TIPO_ATRASADO) {
                Label labelDataCorte = new Label(Funcoes.dToC(Funcoes.cToD(dtexpedi), "dd.MM.yyyy"));
                labelDataCorte.setWrapText(true);
                setMargin(labelDataCorte, new Insets(2, 2, 2, 20));
                this.getChildren().add(labelDataCorte);
                t = new Tooltip("Data de Corte");
                Tooltip.install(labelDataCorte, t);
            } else {
                Label labelDataExped = new Label(Funcoes.dToC(Funcoes.cToD(dtexpedi), "dd.MM.yyyy"));
                labelDataExped.setWrapText(true);
                setMargin(labelDataExped, new Insets(2, 2, 2, 20));
                this.getChildren().add(labelDataExped);
                t = new Tooltip("Data de Expedição");
                Tooltip.install(labelDataExped, t);
            }

            VBox vboxFref = new VBox();
            vboxFref.setPrefWidth(270);
            setMargin(vboxFref, new Insets(2, 2, 2, 20));

            Label labelFref = new Label(fref + " - " + nmfref);
            labelFref.setWrapText(true);
//        setMargin(labelFref, new Insets(2, 2, 2, 20));
            vboxFref.getChildren().add(labelFref);

            Label labelObs = new Label(obs);
            labelObs.setWrapText(true);
//        setMargin(labelObs, new Insets(2, 2, 2, 20));
            vboxFref.getChildren().add(labelObs);
            this.getChildren().add(vboxFref);

            VBox vBoxQtt = new VBox();
            setMargin(vBoxQtt, new Insets(2, 2, 2, 20));
            Label labelQtt = new Label("" + qtt);
            labelQtt.setWrapText(false);
            Font fontBase = labelQtt.getFont();
            labelQtt.setFont(Font.font(fontBase.getFamily(), FontWeight.BOLD, FontPosture.REGULAR, 16f));
            labelQtt.setStyle("-fx-text-fill: blue; -fx-background-insets: 0, 1 1 1 0 ;");
            vBoxQtt.setAlignment(Pos.CENTER_RIGHT);
//        setMargin(labelFref, new Insets(2, 2, 2, 20));
            vBoxQtt.getChildren().add(labelQtt);

            this.getChildren().add(vBoxQtt);
        }
    }

    private void configurarEventos() {
        setOnDragDetected(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                String seccaoEscolhida = AppMain.getInstancia().getComboSeccao().getValue().toString();
                if (seccao.equals(seccaoEscolhida)) {
                    Dragboard dragboard = startDragAndDrop(TransferMode.MOVE);
                    Map<DataFormat, Object> map = new HashMap<>();
                    map.put(DataFormat.RTF, hBoxLinhaPlanOUAtraso);
                    dragboard.setContent(map);
                    setStyle("-fx-background-color: orange; -fx-background-insets: 0, 1 1 1 0 ;");
                    Rectangle rect = new Rectangle(200, hBoxLinhaPlanOUAtraso.getHeight());
                    rect.setFill(Color.GAINSBORO);
                    Image image = rect.snapshot(null, null);
                    double x = hBoxLinhaPlanOUAtraso.getTranslateX();
                    double y = hBoxLinhaPlanOUAtraso.getTranslateY();
                    dragboard.setDragView(image, x, y);
                    event.consume();
                } else {
                    Procedimentos.alerta("Não pertence ao mesmo sector", "", Alert.AlertType.WARNING);
                }
            }
        });

        setOnDragDone(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                if (linha != 0)
                    setStyle("-fx-background-color: white; -fx-background-insets: 0, 1 1 1 0 ;");
                else {
                    setStyle("-fx-background-color: rgb(220,220,220); -fx-background-insets: 0, 1 1 1 0 ;");
                }
                event.consume();

            }
        });
    }

    public ArtigoLinhaPlanOUAtraso getArtigoLinhaPlanOUAtraso() {
        return artigoLinhaPlanOUAtraso;
    }

    public int getTipo() {
        return tipo;
    }
}
