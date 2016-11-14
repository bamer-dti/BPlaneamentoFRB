package objectos;

import bamer.AppMain;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import pojos.ArtigoLinhaPlanOUAtraso;
import sqlite.DBSQLite;

import java.util.ArrayList;

public class GridPanePorPlanear extends GridPane {
    private static GridPanePorPlanear gridPanePorPlanear;

    public GridPanePorPlanear() {
        construct();
    }

    public static void actualizarLista() {
        ArrayList<ArtigoLinhaPlanOUAtraso> lista = new DBSQLite().getListaOSBOPLAN(AppMain.getInstancia().getTextFieldFiltroPorPlanear().getText());
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                AppMain.getInstancia().getBut_porPlanear().setText("por planear (" + new DBSQLite().getCountOSBOPLAN() + ")");
            }
        });

//        if (gridPanePorPlanear == null) {
            gridPanePorPlanear = new GridPanePorPlanear();
//        } else {
//            gridPanePorPlanear.getChildren().removeAll(gridPanePorPlanear.getChildren());
//        }
        Label labelTotRecs = AppMain.getInstancia().getLabelTotRecsPorPlanear();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                labelTotRecs.setText("0");
            }
        });


        int linha = 0;
        for (ArtigoLinhaPlanOUAtraso artigoLinhaPlanOUAtraso : lista) {
            String seccao = artigoLinhaPlanOUAtraso.getSeccao();
            int obrano = artigoLinhaPlanOUAtraso.getObrano();
            String dtCliente =  artigoLinhaPlanOUAtraso.getDt1();
            String bostamp = artigoLinhaPlanOUAtraso.getBostamp();
            String dtexpedi = artigoLinhaPlanOUAtraso.getDt2();
            String fref = artigoLinhaPlanOUAtraso.getFref();
            String nmfref = artigoLinhaPlanOUAtraso.getNmfref();
            String obs = artigoLinhaPlanOUAtraso.getObs();

            HBoxLinhaPlanOUAtraso hBoxOSPorPlanear = new HBoxLinhaPlanOUAtraso(artigoLinhaPlanOUAtraso, linha, HBoxLinhaPlanOUAtraso.TIPO_PORPLANEAR);
            hBoxOSPorPlanear.setId(bostamp);
            if (linha % 2 == 0)
                hBoxOSPorPlanear.setStyle("-fx-background-color: rgb(220,220,220); -fx-background-insets: 0, 1 1 1 0 ;");
            else
                hBoxOSPorPlanear.setStyle("-fx-background-color: rgb(255,255,255); -fx-background-insets: 0, 1 1 1 0 ;");
            gridPanePorPlanear.getChildren().add(hBoxOSPorPlanear);
            GridPane.setConstraints(hBoxOSPorPlanear, 0, linha);
            linha++;
        }
        int finalLinha = linha;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                labelTotRecs.setText("" + finalLinha);
            }
        });

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                ScrollPane scrollPane = new ScrollPane(gridPanePorPlanear);
                scrollPane.setFitToWidth(true);
                scrollPane.setFitToHeight(true);
                AppMain.getInstancia().borderPanePorPlanear.setCenter(scrollPane);
            }
        });

    }

    private void construct() {
        this.setPadding(new Insets(0, 5, 0, 15));
        this.getStyleClass().add("game-grid");
    }
}
