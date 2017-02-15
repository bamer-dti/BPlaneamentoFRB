package pojos;

/**
 * by miguel.silva on 13-02-2017.
 */
public class TokenMachina {
    private String codigo;
    private String data;
    private String machina;
    private boolean online;
    private String operador;
    private long timestamp;
    private String token;

    public TokenMachina() {

    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getMachina() {
        return machina;
    }

    public void setMachina(String machina) {
        this.machina = machina;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public String getOperador() {
        return operador;
    }

    public void setOperador(String operador) {
        this.operador = operador;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
