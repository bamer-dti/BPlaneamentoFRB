package objectos;

import com.google.firebase.database.*;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import pojos.SMS;
import utils.Funcoes;

import java.io.IOException;
import java.util.ArrayList;

import static utils.Constantes.SMS_MACHINA;

/**
 * by miguel.silva on 14-02-2017.
 */
public class ButaoSMS extends HBox {
    private static final String TAG = ButaoSMS.class.getSimpleName();
    private static final double RADIUS_CIRCULO = 11;
    private ArrayList<SMS> lista;
    private SimpleIntegerProperty naoLidaIntProp = new SimpleIntegerProperty(-1);

    public ButaoSMS(Machina machina, int tipoSMS, boolean lida) {
        Button botao = new Button("");
        botao.setAlignment(Pos.CENTER);
        botao.textProperty().bind(Bindings.concat("", naoLidaIntProp));
        botao.setFont(new Font(RADIUS_CIRCULO));

        Tooltip tooltip = new Tooltip();
        tooltip.textProperty().bind(Bindings.concat(lida ? "" : "não ", "concluidas: ", naoLidaIntProp));
        botao.setTooltip(tooltip);
        Insets inseto = new Insets(1);
        ButaoSMS.setMargin(botao, inseto);
        setAlignment(Pos.CENTER_RIGHT);

        double r = RADIUS_CIRCULO;
        botao.setShape(new Circle(r));
        botao.setMinSize(2 * r, 2 * r);
        botao.setMaxSize(2 * r, 2 * r);

        naoLidaIntProp.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (newValue.intValue() != 0) {
                    if (lida) {
                        Funcoes.colocarEstilo(botao, "botao_lidas");
                    } else {
                        Funcoes.colocarEstilo(botao, "botao_nao_lidas");
                    }
                } else {
                    Funcoes.colocarEstilo(botao, "botao_lidas_inactivo");
                }
            }
        });

        naoLidaIntProp.set(0);
        getChildren().add(botao);

        if (tipoSMS == SMS_MACHINA && machina != null) {
            String keyLida = lida ? "Lidas" : "NaoLidas";
            String machinaCode = machina.getCodigo();
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("sms").child(keyLida).child(machinaCode);
            ref.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    lista = new ArrayList<>();
                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                        SMS sms = d.getValue(SMS.class);
                        lista.add(sms);
                    }
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            naoLidaIntProp.set(lista.size());
                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        botao.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    new GridListaSMS(machina, lida, lista);
                } catch (IOException e) {
                    e.printStackTrace();
                    Funcoes.alertaException(e);
                }
            }
        });
    }
}
