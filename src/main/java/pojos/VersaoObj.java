package pojos;


import java.util.Base64;

public class VersaoObj {
    public String versao;
    public String history;

    public String getHistory(){
        byte[] deco = Base64.getDecoder().decode(history);
        return new String(deco);
    }
}
