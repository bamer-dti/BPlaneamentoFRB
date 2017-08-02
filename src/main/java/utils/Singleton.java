package utils;

import bamer.AppMain;
import javafx.collections.ObservableList;

import java.time.LocalDate;

///**
// * Created by miguel.silva on 08-06-2016.
// */
public class Singleton {
    private static Singleton singleton;
    public LocalDate dataInicioAgenda;
    public boolean loginComSucesso = false;
    public String email_utilizador = "";
    public AppMain appMain;
    private ObservableList<String> lista_de_estados;

    public static Singleton getInstancia() {
        if (singleton == null) {
            singleton = new Singleton();
        }
        return singleton;
    }

    public ObservableList<String> getLista_de_estados() {
        return lista_de_estados;
    }

    public void setLista_de_estados(ObservableList<String> lista_de_estados) {
        this.lista_de_estados = lista_de_estados;
    }
}
