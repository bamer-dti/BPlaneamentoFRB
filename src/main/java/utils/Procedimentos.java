package utils;

import bamer.AppMain;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import objectos.GridPaneCalendario;
import objectos.VBoxOSBO;
import pojos.ArtigoOSBO;
import sqlite.DBSQLite;

import java.awt.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

public class Procedimentos {
    public static void alerta(String mensagem, String selectable, Alert.AlertType tipoAlerta) {
        String titulo = "";
        switch (tipoAlerta) {
            case ERROR:
                titulo = "Erro";
                break;
            case INFORMATION:
                titulo = "Nota";
                break;
            case CONFIRMATION:
                titulo = "Resposta";
                break;
            case WARNING:
                titulo = "Aviso";
                break;
        }
//        mensagem = Funcoes.textoEmUTF8(mensagem);
        Alert alert = new Alert(tipoAlerta);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);

        // Get the Stage.
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();

// Add a custom icon.
        stage.getIcons().add(Funcoes.iconeBamer());
        if (!selectable.equals("")) {
            TextArea textArea = new TextArea(selectable);
            textArea.setEditable(false);
            textArea.setWrapText(true);

            textArea.setMaxWidth(Double.MAX_VALUE);
            textArea.setMaxHeight(Double.MAX_VALUE);
            GridPane.setVgrow(textArea, Priority.ALWAYS);
            GridPane.setHgrow(textArea, Priority.ALWAYS);

            GridPane expContent = new GridPane();
            expContent.setMaxWidth(Double.MAX_VALUE);
            expContent.add(textArea, 0, 0);
// Set expandable Exception into the dialog pane.
            alert.getDialogPane().setExpandableContent(expContent);
            alert.getDialogPane().setExpanded(true);
        }
        alert.showAndWait();
    }


    public static void alertaVersion(String versaoNova) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Nova versão " + versaoNova);
        alert.setHeaderText(null);

        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();

        stage.getIcons().add(Funcoes.iconeBamer());
        Hyperlink link = new Hyperlink();
        link.setText("A versão instalada está desactualizada. Clique aqui para instalar a mais recente!");
        link.setStyle("-fx-text-fill: red; -fx-background-insets: 0, 1 1 1 0 ;");
        link.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    URI uri = new URI("ftp://server.bamer.pt/SetupPlaneamentoFRB.exe");
                    Desktop.getDesktop().browse(uri);
                } catch (URISyntaxException | IOException e) {
                    e.printStackTrace();
                    alertaException(e);
                }
            }
        });

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);

        expContent.add(link, 0, 0);

        javafx.scene.control.Button btversion = new Button("ver histórico de versões");
        btversion.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    AppMain.getInstancia().abrirFicheiroVersionTXT();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        GridPane.setMargin(btversion, new Insets(10f, 10f, 10f, 10f));
        expContent.add(btversion, 0, 2);

        alert.getDialogPane().setContent(expContent);

        ButtonType botaoFechar = new ButtonType("Fechar");
        alert.getButtonTypes().setAll(botaoFechar);

        alert.setOnCloseRequest(new EventHandler<DialogEvent>() {
            @Override
            public void handle(DialogEvent event) {
                AppMain.eliminarFicheiroVersionTXT();
                System.exit(0);
            }
        });
        Optional<ButtonType> resultado = alert.showAndWait();
        if (resultado.orElse(null) == botaoFechar) {
            AppMain.eliminarFicheiroVersionTXT();
            System.exit(0);
        }
    }


    public static void alertaException(Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);

        alert.setTitle("Erro interno");
        alert.setHeaderText("Ocorreu um erro:");

        String content = "Descrição do erro: ";
        if (null != e) {
            content += e.toString() + "\n\n";
        }

        alert.setContentText(content);

        Exception ex = new Exception(e);

        //Create expandable Exception.
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);

        String exceptionText = sw.toString();

        //Set up TextArea
        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);


        textArea.setPrefHeight(600);
        textArea.setPrefWidth(800);


        //Set expandable Exception into the dialog pane.
        alert.getDialogPane().setExpandableContent(textArea);

        alert.showAndWait();
    }

    public static void colocarEstilo(Node node, String estilo) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < node.getStyleClass().size(); i++) {
                    String style = node.getStyleClass().get(i);
                    node.getStyleClass().remove(style);
                }
                node.getStyleClass().add(estilo);
            }
        });
    }

    public static void alertaMinimo(Alert.AlertType alertType, String titulo, String mensagem) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Alert alerta = new Alert(alertType, mensagem, ButtonType.OK);
                alerta.setTitle(titulo);
                alerta.setHeaderText(null);
                alerta.showAndWait();
            }
        });
    }

    public static void actualizar_OSBO(ArtigoOSBO artigoOSBO, int modo) {
        artigoOSBO.setCor(VBoxOSBO.COR_MOVIMENTO);
        DBSQLite.getInstancia().actualizarOSBO(artigoOSBO);
        ArrayList<ArtigoOSBO> lista = new ArrayList<>();
        lista.add(artigoOSBO);
        AppMain.getInstancia().actualizarGrelhaCalendario(lista, modo);
        DBSQLite.getInstancia().colocar_OSBO_em_Stack(artigoOSBO);
    }

    public static void actualizar_restantes_OSBOs(int colunaOriginal, int ordemOrigem) {
        System.out.println("Actualizar OSBOs da coluna " + colunaOriginal + " a partir da ordem " + ordemOrigem);
        GridPaneCalendario gridPane = AppMain.getInstancia().getCalendario();
        ObservableList<Node> childrens = gridPane.getChildren();
        for (Node node : childrens) {
            if (GridPane.getColumnIndex(node) == colunaOriginal) {
                if (node instanceof VBoxOSBO) {
                    VBoxOSBO vBoxOSBO = (VBoxOSBO) node;
                    ArtigoOSBO artigo = vBoxOSBO.getArtigoOSBOProp();
                    int ordem = artigo.getOrdem();
                    if (ordem > ordemOrigem) {
                        artigo.setOrdem(ordem - 1);
                        actualizar_OSBO(artigo, Constantes.Operacao.ACTUALIZAR);
                    }
                }
            }
        }
    }

    public static void actualizar_fila_ascendente_OSBO(int coluna, int ordemOrigem, int ordemDestino) {
        GridPaneCalendario gridPane = AppMain.getInstancia().getCalendario();
        ObservableList<Node> childrens = gridPane.getChildren();
        for (Node node : childrens) {
            if (GridPane.getColumnIndex(node) == coluna) {
                if (node instanceof VBoxOSBO) {
                    VBoxOSBO vBoxOSBO = (VBoxOSBO) node;
                    ArtigoOSBO artigo = vBoxOSBO.getArtigoOSBOProp();
                    int ordem = artigo.getOrdem();
                    if (ordem == ordemOrigem) {
                        artigo.setOrdem(ordemDestino);
                        actualizar_OSBO(artigo, Constantes.Operacao.ACTUALIZAR);
                    }
                    if (ordem > ordemOrigem && ordem <= ordemDestino) {
                        artigo.setOrdem(ordem - 1);
                        actualizar_OSBO(artigo, Constantes.Operacao.ACTUALIZAR);
                    }
                }
            }
        }
    }

    public static void actualizar_fila_descendente_OSBO(int coluna, int ordemOrigem, int ordemDestino) {
        GridPaneCalendario gridPane = AppMain.getInstancia().getCalendario();
        ObservableList<Node> childrens = gridPane.getChildren();
        for (Node node : childrens) {
            if (GridPane.getColumnIndex(node) == coluna) {
                if (node instanceof VBoxOSBO) {
                    VBoxOSBO vBoxOSBO = (VBoxOSBO) node;
                    ArtigoOSBO artigo = vBoxOSBO.getArtigoOSBOProp();
                    int ordem = artigo.getOrdem();
                    if (ordem == ordemOrigem) {
                        artigo.setOrdem(ordemDestino);
                        actualizar_OSBO(artigo, Constantes.Operacao.ACTUALIZAR);
                    }
                    if (ordem < ordemOrigem && ordem >= ordemDestino) {
                        artigo.setOrdem(ordem + 1);
                        actualizar_OSBO(artigo, Constantes.Operacao.ACTUALIZAR);
                    }
                }
            }
        }
    }

    public static void actualizar_OSBO_mudar_coluna(ArtigoOSBO artigoOSBOemDRAG, VBoxOSBO vboxemdrag, VBoxOSBO vBoxOSBODestino) {
        int colunaAntiga = vboxemdrag.getColuna();
        int ordemAntiga = artigoOSBOemDRAG.getOrdem();
        int colunaNova = vBoxOSBODestino.getColuna();
        int ordemNova = vBoxOSBODestino.getArtigoOSBOProp().getOrdem();
        GridPaneCalendario gridPane = AppMain.getInstancia().getCalendario();
        ObservableList<Node> childrens = gridPane.getChildren();
        for (Node node : childrens) {
            if (node instanceof VBoxOSBO) {
                VBoxOSBO vBoxOSBO = (VBoxOSBO) node;
                ArtigoOSBO artigoLoop = vBoxOSBO.getArtigoOSBOProp();
                int ordemLoop = artigoLoop.getOrdem();
                if (GridPane.getColumnIndex(node) == colunaNova) {
                    if (ordemLoop >= ordemNova) {
                        artigoLoop.setOrdem(artigoLoop.getOrdem() + 1);
                        actualizar_OSBO(artigoLoop, Constantes.Operacao.ACTUALIZAR);
                    }
                    continue;
                }
                if (GridPane.getColumnIndex(node) == colunaAntiga) {
                    if (ordemLoop > ordemAntiga) {
                        artigoLoop.setOrdem(artigoLoop.getOrdem() - 1);
                        actualizar_OSBO(artigoLoop, Constantes.Operacao.ACTUALIZAR);
                    }
                }
            }
        }
        LocalDate dataDeOperacao = Singleton.getInstancia().dataInicioAgenda.plusDays(colunaNova);
        artigoOSBOemDRAG.setDtoper(Funcoes.dToC(dataDeOperacao, "yyyy-MM-dd" + " 00:00:00"));
        artigoOSBOemDRAG.setOrdem(ordemNova);
        actualizar_OSBO(artigoOSBOemDRAG, Constantes.Operacao.ACTUALIZAR);
    }

    public static void actualizar_OSBO_colocar_em_planeamento(VBoxOSBO vBoxOSBO) {
        ArtigoOSBO artigoOSBO = vBoxOSBO.getArtigoOSBOProp();
        artigoOSBO.setOrdem(0);
        artigoOSBO.setDtoper("1900-01-01 00:00:00");
        actualizar_OSBO(artigoOSBO, Constantes.Operacao.REMOVER);
    }
}
