package utils;

public class Constantes {
    public static final int SMS_MACHINA = 1;
    public static final int SMS_OPERADOR = 2;

    public static final int STARTED = 1;


    public static final String NOME_BASE_DADOS_SQL = "dadossql.db";

    //ESTADOS
    public static final String ESTADO_01_CORTE = "01 - CORTE";
    public static final long INTERVALO_UPDATE_WS = 1;

    public enum Operacao {
        ;
        public static final int ADICIONAR = 1;
        public static final int ACTUALIZAR = 2;
        public static final int REMOVER = 3;
    }

    public enum Preferencias {
        ;
        public static final String PREF_AGENDA_NUMCOLS = "pref_agenda_numcols";
        public static final String PREF_SECCAO = "pref_seccao";
        public static final String PREF_COMPRIMENTO_MINIMO = "pref_col_comp_min";
        public static final String PREF_ESTADO = "pref_estado";
    }

    public enum Firebase {
        ;
        public static final String FICHEIRO_CREDENCIAIS_GOOGLE = "firebase_auth.json";
        public static final String FIREBASE_SECRET_KEY = "AIzaSyBw1bgujTodKFVwNf5uGzhgifbehACm0Wk";
        public static final String FIREBASE_PROJECT_URL = "https://bamer-black-ops.firebaseio.com/";
    }
}
