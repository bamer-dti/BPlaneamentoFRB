package objectos;

import bamer.AppMain;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import pojos.ArtigoParaPlaneamento;
import utils.Singleton;

import java.time.LocalDate;
import java.util.List;

///**
// * Created by miguel.silva on 28-07-2016.
// */
public class GridPaneAtrasados extends GridPane {
    public GridPaneAtrasados() {
        construct();
    }

    public static void alimentarLista(List<ArtigoParaPlaneamento> lista) {
        GridPaneAtrasados gridPaneAtrasados = new GridPaneAtrasados();
        if (lista == null)
            return;

        Label labelTotRecs = AppMain.getInstancia().getLabelTotRecsAtrasados();
        if (labelTotRecs != null)
            labelTotRecs.setText("0");

        int linha = 0;
        int recsTotal = 0;
        for (ArtigoParaPlaneamento artigoParaPlaneamento : lista) {
            LocalDate dtcortef;
            dtcortef = artigoParaPlaneamento.getDtcortef();

            LocalDate datainicio = Singleton.getInstancia().dataInicioAgenda.minusDays(1);
            if (!dtcortef.isBefore(datainicio)) {
                continue;
            }
            recsTotal++;

            String filtroFref = AppMain.getInstancia().getTextFieldFiltroFrefAtrasados().getText();
            if (!filtroFref.equals("")) {
                if (!artigoParaPlaneamento.getFref().startsWith(filtroFref)) {
                    continue;
                }
            }

            HBoxOSAprovisionamento hBoxOSAprovisionamento = new HBoxOSAprovisionamento(artigoParaPlaneamento, linha, HBoxOSAprovisionamento.TIPO_ATRASADO);
            hBoxOSAprovisionamento.setId(artigoParaPlaneamento.getBostamp());
            if (linha % 2 == 0)
                hBoxOSAprovisionamento.setStyle("-fx-background-color: rgb(220,220,220); -fx-background-insets: 0, 1 1 1 0 ;");
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    gridPaneAtrasados.getChildren().add(hBoxOSAprovisionamento);
                }
            });
            GridPane.setConstraints(hBoxOSAprovisionamento, 0, linha);
            linha++;
            if (labelTotRecs != null) {
                int finalLinha = linha;
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        labelTotRecs.setText("" + finalLinha);
                    }
                });
            }
        }

        int finalRecsTotal = recsTotal;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                AppMain.getInstancia().getBut_atrasados().setText("atrasos (" + finalRecsTotal + ")");
            }
        });

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                AppMain.getInstancia().borderPaneAtrasados.setCenter(gridPaneAtrasados);
            }
        });

    }

    private void construct() {
        this.setPadding(new Insets(0, 5, 0, 15));
        this.getStyleClass().add("game-grid");
        //todo lista live atrasados
//            ServicoCouchBase.getInstancia().liveAtrasados();
    }
}
