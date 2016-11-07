package utils;

import javafx.scene.control.ProgressIndicator;
import sqlite.PreferenciasEmSQLite;

import java.time.LocalDate;

///**
// * Created by miguel.silva on 08-06-2016.
// */
public class Singleton {
    private static Singleton singleton;
    public LocalDate dataInicioAgenda;
    private ProgressIndicator progressIndicator;

    public static Singleton getInstancia() {
        if (singleton == null) {
            singleton = new Singleton();
            PreferenciasEmSQLite prefs = PreferenciasEmSQLite.getInstancia();
        }
        return singleton;
    }

    public ProgressIndicator getProgressIndicator() {
        return progressIndicator;
    }

    public void setProgressIndicator(ProgressIndicator progressIndicator) {
        progressIndicator.setProgress(100d);
        progressIndicator.setPrefWidth(30);
        this.progressIndicator = progressIndicator;
    }
}
