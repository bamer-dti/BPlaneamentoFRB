package objectos;

import bamer.AppMain;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import pojos.ArtigoLinhaPlanOUAtraso;
import pojos.ArtigoOSBO;
import sqlite.DBSQLite;
import utils.Funcoes;

import java.time.LocalDate;
import java.util.ArrayList;

///**
// * Created by miguel.silva on 28-07-2016.
// */
public class GridPaneAtrasados extends GridPane {
    public GridPaneAtrasados() {
        construct();
    }

    public static void actualizarLista() {
        String data = Funcoes.dToC(LocalDate.now().minusDays(1), "yyyy-MM-dd" + " 00:00:00");
        GridPaneAtrasados gridPaneAtrasados = new GridPaneAtrasados();

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    AppMain.getInstancia().getBut_atrasados().setText("atrasos (" + DBSQLite.getInstancia().getListaArtigoOSBOAtrasados(data, "").size() + ")");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        int linha = 0;

        String filtro = AppMain.getInstancia().getTextFieldFiltroFrefAtrasados().getText();
        ArrayList<ArtigoOSBO> lista = DBSQLite.getInstancia().getListaArtigoOSBOAtrasados(data, filtro);
        Label labelTotRecs = AppMain.getInstancia().getLabelTotRecsAtrasados();
        labelTotRecs.setText("" + lista.size());
        for (ArtigoOSBO artigoOSBO : lista) {
            try {
                int qtt = artigoOSBO.getPecas();
                ArtigoLinhaPlanOUAtraso artigoLinhaPlanOUAtras = new ArtigoLinhaPlanOUAtraso(artigoOSBO.getBostamp()
                        , artigoOSBO.getObrano()
                        , artigoOSBO.getFref()
                        , artigoOSBO.getNmfref()
                        , artigoOSBO.getSeccao()
                        , artigoOSBO.getObs()
                        , artigoOSBO.getDtexpedi()
                        , artigoOSBO.getDtcortef(),
                        qtt
                );
                HBoxLinhaPlanOUAtraso hBoxLinhaPlanOUAtraso = new HBoxLinhaPlanOUAtraso(artigoLinhaPlanOUAtras, linha, HBoxLinhaPlanOUAtraso.TIPO_ATRASADO);
                hBoxLinhaPlanOUAtraso.setId(artigoOSBO.getBostamp());
                if (linha % 2 == 0)
                    hBoxLinhaPlanOUAtraso.setStyle("-fx-background-color: rgb(220,220,220); -fx-background-insets: 0, 1 1 1 0 ;");
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        gridPaneAtrasados.getChildren().add(hBoxLinhaPlanOUAtraso);
                    }
                });
                GridPane.setConstraints(hBoxLinhaPlanOUAtraso, 0, linha);
                linha++;
                int finalLinha = linha;
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        labelTotRecs.setText("" + finalLinha);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

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
    }
}
