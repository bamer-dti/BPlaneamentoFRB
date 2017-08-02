package utils;

import bamer.AppMain;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import objectos.GridPaneCalendario;
import objectos.VBoxOSBO;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

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
import java.util.concurrent.TimeUnit;

import static java.lang.System.out;

@SuppressWarnings("unused")
public class Funcoes {

    public final static long ONE_SECOND = 1000;
    public final static long ONE_MINUTE = ONE_SECOND * 60;
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

    public static int obter_ordem_do_ultimo_VBoxOSBO_da_coluna(int coluna) {
        GridPaneCalendario gridPane = AppMain.getInstancia().getCalendario();
        ObservableList<Node> childrens = gridPane.getChildren();
        int ordem = 0;
        for (Node node : childrens) {
            if (GridPane.getColumnIndex(node) == coluna) {
                if (node instanceof VBoxOSBO) {
                    VBoxOSBO vBoxOSBO = (VBoxOSBO) node;
                    if (vBoxOSBO.getOrdemProp() >= ordem) {
                        ordem = vBoxOSBO.getOrdemProp();
                    }
                }
            }
        }
        return ordem;
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

    public static org.joda.time.LocalDate toJoda(java.time.LocalDate input) {
        return new org.joda.time.LocalDate(input.getYear(),
                input.getMonthValue(),
                input.getDayOfMonth());
    }
}
