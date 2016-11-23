package utils;

import java.time.LocalDate;

///**
// * Created by miguel.silva on 08-06-2016.
// */
public class Singleton {
    private static Singleton singleton;
    public LocalDate dataInicioAgenda;

    public static Singleton getInstancia() {
        if (singleton == null) {
            singleton = new Singleton();
        }
        return singleton;
    }

}
