package objectos;

import bamer.AppMain;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import pojos.ArtigoParaPlaneamento;

import java.util.List;

///**
// * Created by miguel.silva on 28-07-2016.
// */
public class GridPaneAprovisionamentos extends GridPane {
    public GridPaneAprovisionamentos() {
        construct();
    }

    //    public static void alimentarLista(List<QueryRow> lista) {
    public static void alimentarLista(List<ArtigoParaPlaneamento> lista) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                AppMain.getInstancia().getBut_aprovisionamento().setText("planeamento (" + lista.size() + ")");
            }
        });
        GridPaneAprovisionamentos gridPaneAprovados = new GridPaneAprovisionamentos();

        Label labelTotRecs = AppMain.getInstancia().getLabelTotRecsAprovisionamento();
        labelTotRecs.setText("0");
        if (lista == null)
            return;
        int linha = 0;
        for (ArtigoParaPlaneamento artigoParaPlaneamento : lista) {
            String filtroFref = AppMain.getInstancia().getTextFieldFiltroFrefAprovisionamento().getText();
            if (!filtroFref.equals("")) {
                if (!artigoParaPlaneamento.getFref().startsWith(filtroFref)) {
                    continue;
                }
            }

            HBoxOSAprovisionamento hBoxOSAprovisionamento = new HBoxOSAprovisionamento(artigoParaPlaneamento, linha, HBoxOSAprovisionamento.TIPO_APROVISIONAMENTO);
            hBoxOSAprovisionamento.setId(artigoParaPlaneamento.getBostamp());
            if (linha % 2 == 0)
                hBoxOSAprovisionamento.setStyle("-fx-background-color: rgb(220,220,220); -fx-background-insets: 0, 1 1 1 0 ;");
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    gridPaneAprovados.getChildren().add(hBoxOSAprovisionamento);
                }
            });
            GridPane.setConstraints(hBoxOSAprovisionamento, 0, linha);
            linha++;
            labelTotRecs.setText("" + linha);
        }


        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                ScrollPane scrollPane = new ScrollPane(gridPaneAprovados);
                scrollPane.setFitToWidth(true);
                scrollPane.setFitToHeight(true);
                AppMain.getInstancia().borderPaneAprovisionamento.setCenter(scrollPane);
            }
        });

    }

    private void construct() {
        this.setPadding(new Insets(0, 5, 0, 15));
        this.getStyleClass().add("game-grid");
        //todo buscar dados para lista
//        try {
//            ServicoCouchBase.getInstancia().liveAprovisionamento();
//        } catch (IOException | CouchbaseLiteException e) {
//            e.printStackTrace();
//        }
    }
}
