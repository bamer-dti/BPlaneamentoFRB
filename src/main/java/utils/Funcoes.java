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
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.awt.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static java.lang.System.out;

@SuppressWarnings("unused")
public class Funcoes {

    public final static long ONE_SECOND = 1000;
    public final static long ONE_MINUTE = ONE_SECOND * 60;

    //    public static String textoEmUTF8(String texto) {
//        byte ptext[] = texto.getBytes(ISO_8859_1);
//        return new String(ptext, UTF_8);
//    }
    public final static long ONE_HOUR = ONE_MINUTE * 60;
    public final static long ONE_DAY = ONE_HOUR * 24;
    public static String FORMATO_A_M_DTh_m_s_sssZ = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static String FORMATO_A_M__HH_mm_ss = "dd.MM.yyyy HH:mm:ss";
    public static String FORMATO_h_m_s = "HH:mm:ss";

    public static String dToCZeroHour(LocalDateTime data) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd 00:00:00");
        String formattedDateTime = data.format(formatter);
        return formattedDateTime;
    }

    public static String tToC(LocalDateTime data) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String formattedDateTime = data.format(formatter);
        return formattedDateTime;
    }

    public static String dToCddMMyyyy(LocalDate data) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String formattedDateTime = data.format(formatter);
        return formattedDateTime;
    }

    public static String dToC(LocalDate data, String formato) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formato);
        String formattedDateTime = data.format(formatter);
        return formattedDateTime;
    }

    public static String dToC(DateTime data, String formato) {
        org.joda.time.format.DateTimeFormatter dtf = DateTimeFormat.forPattern(formato);
        String formattedDateTime = dtf.print(data);
        return formattedDateTime;
    }

    public static Image iconeBamer() {
        URL icoURL = ClassLoader.getSystemResource("bamer.jpg");
        Image ico = new Image(icoURL.toExternalForm());
        out.println(ico.getWidth() + " x " + ico.getHeight());
        return ico;
    }

    public static Image imagemResource(String imagem, int largura, int altura) {
        URL icoURL = ClassLoader.getSystemResource(imagem);
        Image image = new Image(icoURL.toExternalForm(), largura, altura, true, true);
        return image;
    }

    public static String currentTimeStringStamp(String formato) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat(formato);
        Calendar calendar = GregorianCalendar.getInstance();
        return dateFormatter.format(calendar.getTime());
    }

    public static LocalDateTime cToT(String datastring) {
        LocalDateTime data = Timestamp.valueOf(datastring).toLocalDateTime().truncatedTo(ChronoUnit.DAYS);
        return data;
    }

    public static LocalDateTime sToT(String datastring) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDate localDate = LocalDate.parse(datastring, formatter);
        LocalDateTime localDateTime = localDate.atStartOfDay();
        return localDateTime;
    }

    public static Node getNodeByRowColumnIndex(final int row, final int column, GridPane gridPane) {
        Node result = null;
        ObservableList<Node> childrens = gridPane.getChildren();
        for (Node node : childrens) {
            if (GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == column) {
                result = node;
                break;
            }
        }
        return result;
    }

    public static String dToSQL(LocalDate value) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String valor = value.format(formatter);
        return valor;
    }

    public static String cToSQL(String data) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(data, formatter);
        //Em sql
        formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String sqlData = dateTime.format(formatter);
        return sqlData;
    }

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
        link.setText("A versão instalada está desactualizada. Clique aqui paraLabel efectuar instalar a mais recente!");
        link.setStyle("-fx-text-fill: red; -fx-background-insets: 0, 1 1 1 0 ;");
        link.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    URI uri = new URI("https://dl.dropboxusercontent.com/u/6390478/Bamer/Apps/SetupPlaneamentoFRB.exe");
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

        Button btversion = new Button("ver histórico de versões");
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

    public static String milisegundos_em_HH_MM_SS(long millis) {
        String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
        return hms;
    }

    public static String segundos_em_DD_MM_AAAA_HH_MM_SS(long segundos) {
        DateTime someDate = new DateTime(segundos);
        DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault());
        String data = formatter.format(someDate.getMillis());
        return data;
    }

    public static LocalDate cToD(String datastring) {
        LocalDate data = Timestamp.valueOf(datastring).toLocalDateTime().truncatedTo(ChronoUnit.DAYS).toLocalDate();
        return data;
    }

    public static String millis_em_FormatoAEscolher(long millis, String formato) {
        DateTime someDate = new DateTime(millis);
        DateFormat formatter = new SimpleDateFormat(formato, Locale.getDefault());
        String data = formatter.format(someDate.getMillis());
        return data;
    }
}
