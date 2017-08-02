package utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.io.InputStreamReader;

public class FirebaseAuthCustom {

    private static final String BASE_URL = "https://www.googleapis.com/identitytoolkit/v3/relyingparty/";
    private static final String OPERATION_AUTH = "verifyPassword";
    private static FirebaseAuthCustom instance = null;
    private String firebaseKey;

    protected FirebaseAuthCustom() {
        firebaseKey = Constantes.Firebase.FIREBASE_SECRET_KEY;
    }

    public static FirebaseAuthCustom getInstance() {
        if (instance == null) {
            instance = new FirebaseAuthCustom();
        }
        return instance;
    }

    public JsonObject auth2(String username, String password) throws UnirestException {
        HttpResponse<String> response = Unirest.post(BASE_URL + OPERATION_AUTH + "?key=" + firebaseKey)
                .header("content-type", "application/json")
                .header("cache-control", "no-cache")
//                .header("postman-token", "fa81cd52-8355-37a4-b802-fcb5e3963eee")
                .body("{\"email\":\"" + username + "\",\"password\":\"" + password + "\",\"returnSecureToken\":true}")
                .asString();

        InputStreamReader parsar = new InputStreamReader(response.getRawBody());
        JsonParser jp = new JsonParser();
        JsonElement root = jp.parse(parsar);
        JsonObject rootobj = root.getAsJsonObject();

        return rootobj;
    }
}