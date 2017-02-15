package objectos;

import bamer.AppMain;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.internal.Log;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import sqlite.DBSQLite;
import utils.Campos;

/**
 * by miguel.silva on 09-02-2017.
 */
public class HBoxMachinas extends HBox {
    private static final String TAG = HBoxMachinas.class.getSimpleName();
    private final HBoxMachinas self;

    public HBoxMachinas() {
        this.self = this;
        FirebaseDatabase.getInstance().getReference(Campos.KEY_SECCAO).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    String seccao = d.getKey();
                    FirebaseDatabase.getInstance().getReference(Campos.KEY_SECCAO).child(seccao).child("maquinas").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot d2 : dataSnapshot.getChildren()) {
                                Machina machina = d2.getValue(Machina.class);
                                machina.setSeccao(seccao);
                                machina.setCodigo(d2.getKey());
//                                System.out.println(machina.toString());
                                int i = DBSQLite.getInstancia().guardarMachina(machina);
                                if (i == 0) {
                                    Log.w(TAG, "Não foi guardado em SQL o objecto " + machina.toString());
                                }
                                CriarBotaoMachina(machina);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void CriarBotaoMachina(Machina machina) {
        GridButtonMachina botao = new GridButtonMachina(machina);
        setMargin(botao, new Insets(0, 10, 0, 10));
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                self.getChildren().add(botao);
                AppMain.getInstancia().actualizarMostradorMaquinas();
            }
        });
    }
}
