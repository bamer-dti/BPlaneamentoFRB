package utils;

import bamer.AppMain;
import com.google.firebase.database.*;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import objectos.GridPaneCalendario;
import objectos.VBoxDia;
import org.joda.time.DateTime;
import pojos.ObjWUForecast;
import pojos.ObjWeatherUnderground;

import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * by miguel.silva on 22-02-2017.
 */
public class ChronoWeather {
    private static final String ICONSET = "k";
    private final GridPaneCalendario pane;
    private int rotacao;

    public ChronoWeather(GridPaneCalendario gridPaneCalendario) {
        this.pane = gridPaneCalendario;
        rotacao = (int) TimeUnit.MINUTES.toSeconds(AppMain.INTERVALO_CRONOS);
        configurarWeather();
    }

    public void configurarWeather() {
        ObservableList<Node> childs = pane.getChildren();
        for (Node node : childs) {
            if (node instanceof VBoxDia) {
                VBoxDia vboxdia = (VBoxDia) node;
                if (vboxdia.chronoWeather == null) {
                    vboxdia.chronoWeather = new TimerChronoWeather(vboxdia);
                    if (vboxdia.getColuna() == 1) {
                        vboxdia.chronoWeather.schedule(new taskWeatherHoje(vboxdia), 0, 1000);
                    }
                    if (vboxdia.getColuna() > 1) {
                        DateTime now = new DateTime();
                        vboxdia.keyIPMA = Funcoes.millis_em_FormatoAEscolher(now.plusDays(vboxdia.getColuna() - 1).getMillis(), "yyyyMMdd");
                        vboxdia.chronoWeather.schedule(new taskWeatherForecast(vboxdia), 0, 1000);
                    }
                }
            }
        }
    }

    private class taskWeatherForecast extends TimerTask {
        private final VBoxDia vboxdia;

        public taskWeatherForecast(VBoxDia vboxdia) {
            this.vboxdia = vboxdia;
        }

        @Override
        public void run() {
            vboxdia.tempoRemanescente--;
            int tempoRemanescente = vboxdia.tempoRemanescente;
            if (tempoRemanescente <= 0) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("weather").child("wunderground").child("forecast");
                String keyIPMA = vboxdia.keyIPMA;
                Query q = ref.orderByKey().startAt(keyIPMA).limitToFirst(1);
                q.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot d : dataSnapshot.getChildren()) {
                            ObjWUForecast objWUForecast = d.getValue(ObjWUForecast.class);
                            ImageView imageViewTempo = vboxdia.getImageViewTempo();
                            Label labelTempMax = vboxdia.getLabelTempMax();
                            Label labelTempMin = vboxdia.getLabelTempMin();
                            if (objWUForecast != null) {
                                imageViewTempo.setManaged(true);
                                imageViewTempo.setVisible(true);
                                String urlImagemTempo = "http://icons-ak.wxug.com/i/c/" + ICONSET + "/" + objWUForecast.getIcon() + ".gif";
                                imageViewTempo.setImage(new Image(urlImagemTempo, 32, 32, true, true));

                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        labelTempMax.setManaged(true);
                                        labelTempMax.setText(objWUForecast.getHigh() + "º");

                                        labelTempMin.setManaged(true);
                                        labelTempMin.setText(objWUForecast.getLow() + "º");
                                    }
                                });
                            } else {
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        imageViewTempo.setManaged(false);
                                        imageViewTempo.setVisible(false);
                                        labelTempMax.setManaged(false);
                                        labelTempMin.setManaged(false);
                                    }
                                });
                            }
                        }

                        vboxdia.tempoRemanescente = rotacao;
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        vboxdia.tempoRemanescente = rotacao;
                    }
                });
            }
        }
    }

    private class taskWeatherHoje extends TimerTask {
        private final VBoxDia vboxdia;

        public taskWeatherHoje(VBoxDia vboxdia) {
            this.vboxdia = vboxdia;
        }

        @Override
        public void run() {
            vboxdia.tempoRemanescente--;
            long tempoRemanescente = vboxdia.tempoRemanescente;
            if (vboxdia.getColuna() == 1) {
//                DateTime dateTime = new DateTime();
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        AppMain.getInstancia().getMainStage().setTitle(AppMain.TITULO_APP + " (" + tempoRemanescente + ")");
                    }
                });
            }
            if (tempoRemanescente <= 0) {
                DateTime dateTime = new DateTime();
                DateTime futuro = dateTime.plusSeconds(Math.toIntExact(rotacao));
//                System.out.println(Funcoes.currentTimeStringStamp(Funcoes.FORMATO_h_m_s) + ": " + "; próxima: " + Funcoes.dToC(futuro, "HH:mm:ss"));

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("weather").child("wunderground").child("actual");
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ObjWeatherUnderground objWeatherUnderground = dataSnapshot.getValue(ObjWeatherUnderground.class);
                        ImageView imageViewTempo = vboxdia.getImageViewTempo();
                        Label tempMax = vboxdia.getLabelTempMax();
                        Label tempMin = vboxdia.getLabelTempMin();
                        if (objWeatherUnderground != null) {
                            imageViewTempo.setManaged(true);
                            imageViewTempo.setVisible(true);
                            String urlImagemTempo = "http://icons-ak.wxug.com/i/c/" + ICONSET + "/" + objWeatherUnderground.getIcon() + ".gif";
                            imageViewTempo.setImage(new Image(urlImagemTempo, 32, 32, true, true));

                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    tempMax.setManaged(true);
                                    tempMax.setText(objWeatherUnderground.getTemp_c() + "º");
                                    tempMin.setManaged(true);
                                    tempMin.setText(objWeatherUnderground.getFeelslike_c() + "º");
                                }
                            });
                        } else {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    imageViewTempo.setManaged(false);
                                    imageViewTempo.setVisible(false);
                                    tempMax.setManaged(false);
                                    tempMin.setManaged(false);
                                }
                            });
                        }
                        vboxdia.tempoRemanescente = rotacao;
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        vboxdia.tempoRemanescente = rotacao;
                    }
                });
            }
        }

    }
}
