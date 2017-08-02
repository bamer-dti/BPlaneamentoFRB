package utils;

import pojos.ArtigoOSBO;
import sqlite.DBSQLite;
import webservices.WSWorker;

import java.util.Timer;
import java.util.TimerTask;

public class StackWorker {
    private final Timer timer;
    private final StackWorker context;
    private boolean isBusy;

    public StackWorker() {
        this.isBusy = false;
        timer = new Timer();
        timer.schedule(new IniciarTarefa(), Constantes.INTERVALO_UPDATE_WS, Constantes.INTERVALO_UPDATE_WS);
        context = this;
    }

    public Timer getTimer() {
        return timer;
    }

    public void setBusy(boolean busy) {
        isBusy = busy;
    }

    private class IniciarTarefa extends TimerTask {
        @Override
        public void run() {
            if (isBusy)
                return;
            isBusy = true;
            ArtigoOSBO artigo = DBSQLite.getInstancia().select_Top1_Stack();
            if (artigo == null) {
                isBusy = false;
                return;
            }
            WSWorker.actualizarOSBO(context, artigo);
        }

    }
}
