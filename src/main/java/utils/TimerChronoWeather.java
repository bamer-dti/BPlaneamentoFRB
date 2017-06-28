package utils;

import objectos.VBoxDia;

/**
 * by miguel.silva on 22-02-2017.
 */
public class TimerChronoWeather extends java.util.Timer {
    private final VBoxDia vboxdia;

    public TimerChronoWeather(VBoxDia vboxdia) {
        this.vboxdia = vboxdia;
        System.out.println("Criado o relógio de " + vboxdia.getId());
    }


    @Override
    public int purge() {
        System.out.println("Parado relógio de " + vboxdia.getId());
        return super.purge();
    }
}
