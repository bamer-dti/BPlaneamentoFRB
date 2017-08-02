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

    public static void actualizar(String seccao, String estado, boolean apenas_mostrador) {
        ArrayList<ArtigoLinhaPlanOUAtraso> lista = new DBSQLite().get_Lista_OS_Por_Planear(AppMain.getInstancia().getTextFieldFiltroPorPlanear().getText(), seccao, estado);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                AppMain.getInstancia().getBut_porPlanear().setText("por planear (" + lista.size() + ")");
            }
        });
        if (apenas_mostrador) {
            return;
        }
        gridPanePorPlanear = new GridPanePorPlanear();
        Label labelTotRecs = AppMain.getInstancia().getLabelTotRecsPorPlanear();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                labelTotRecs.setText("0");
            }
        });

        int linha = 0;
        for (ArtigoLinhaPlanOUAtraso artigoLinhaPlanOUAtraso : lista) {
            int obrano = artigoLinhaPlanOUAtraso.getObrano();
            String dtCliente = artigoLinhaPlanOUAtraso.getDtexp();
            String bostamp = artigoLinhaPlanOUAtraso.getBostamp();
            String dtexpedi = artigoLinhaPlanOUAtraso.getDtcli();
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
