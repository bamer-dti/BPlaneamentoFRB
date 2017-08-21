package objectos;

import bamer.AppMain;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import ecras.EnviarSMS;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import pojos.Machina;
import pojos.MaquinaOS;
import pojos.MaquinaStatus;
import pojos.TokenMachina;
import utils.Campos;
import utils.Constantes;
import utils.Funcoes;
import utils.Procedimentos;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import static utils.Constantes.SMS_MACHINA;
import static utils.Constantes.SMS_OPERADOR;

/**
 * by miguel.silva on 09-02-2017.
 */
public class ButaoMachina extends GridPane {
    private static final String TAG = ButaoMachina.class.getSimpleName();
    private final ButaoMachina context;
    public SimpleObjectProperty<TokenMachina> tokenMachinaProp = new SimpleObjectProperty<>();
    public SimpleStringProperty maquinaCodigoProp = new SimpleStringProperty();
    public SimpleStringProperty operadorProp = new SimpleStringProperty();
    public SimpleStringProperty tokenProp = new SimpleStringProperty();
    private SimpleStringProperty seccaoProp = new SimpleStringProperty();
    private SimpleStringProperty funcaoProp = new SimpleStringProperty();
    private SimpleStringProperty nomeProp = new SimpleStringProperty();
    private SimpleIntegerProperty ordemProp = new SimpleIntegerProperty();
    private SimpleObjectProperty<Machina> machinaProp = new SimpleObjectProperty<>();
    private SimpleStringProperty dataTokenProp = new SimpleStringProperty();
    private SimpleLongProperty timestampProp = new SimpleLongProperty();
    private SimpleStringProperty osProp = new SimpleStringProperty("");
    private SimpleBooleanProperty machinaOnlineProp = new SimpleBooleanProperty();
    private SimpleStringProperty tempoMaquinaProp = new SimpleStringProperty();
    private Timer cronometroMaquina;

    public ButaoMachina(Machina machina) {
        context = this;
        setMinWidth(200);
        setMaxWidth(200);
        setupBinders(machina);
        colocarObjectos();
        eventos();
//        firebase();
        firebaseListeners();
        objectoSMS(machina);
    }

    private void firebaseListeners() {
        ValueEventListener listener_OS = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        osProp.setValue("");
                    }
                });

                MaquinaOS maquinaOs = dataSnapshot.getValue(MaquinaOS.class);
                if (maquinaOs == null)
                    return;
                String texto = maquinaOs.getObrano() == 0 ? "" : "OP " + maquinaOs.getObrano();
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        osProp.setValue(texto);
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        ValueEventListener listenerStatus = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        machinaOnlineProp.setValue(false);
                        operadorProp.setValue("");
                    }
                });

                MaquinaStatus maquinaStatus = dataSnapshot.getValue(MaquinaStatus.class);

                if (maquinaStatus == null) {
                    return;
                }

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        machinaOnlineProp.setValue(maquinaStatus.isLigado());
                        operadorProp.setValue(maquinaStatus.getOperador().toUpperCase());
                    }
                });
                actualizarCronometroDaMaquina(maquinaStatus);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        FirebaseDatabase.getInstance()
                .getReference(Campos.SECCAO)
                .child(machinaProp.get().getSeccao())
                .child(Campos.MAQUINAS)
                .child(machinaProp.get().getCodigo())
                .child(Campos.OS)
                .addValueEventListener(listener_OS);

        FirebaseDatabase.getInstance()
                .getReference(Campos.SECCAO)
                .child(machinaProp.get().getSeccao())
                .child(Campos.MAQUINAS)
                .child(machinaProp.get().getCodigo())
                .child(Campos.STATUS)
                .addValueEventListener(listenerStatus);
    }

    private void actualizarCronometroDaMaquina(MaquinaStatus maquinaStatus) {
        boolean isOnline = maquinaStatus.isLigado();
        if (cronometroMaquina != null) {
            cronometroMaquina.cancel();
            cronometroMaquina.purge();
            cronometroMaquina = null;
        }
        if (!isOnline) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    long tempo = (maquinaStatus.getUnixtime());
                    tempoMaquinaProp.set(Funcoes.millis_em_dd_MM_HH_mm_ss(tempo * 1000));
                }
            });
        } else {
            cronometroMaquina = new Timer();
            cronometroMaquina.schedule(new CronometroMaquina(maquinaStatus), 0, 1000);
        }
    }

    private void objectoSMS(Machina machina) {
        ButaoSMS smsMachinaNaoLidas = new ButaoSMS(machina, SMS_MACHINA, false);
        ButaoSMS smsMachinaLidas = new ButaoSMS(machina, SMS_MACHINA, true);
//        this.smsOperador= new ButaoSMS(machina, SMS_OPERADOR);
        ButaoMachina.setHgrow(smsMachinaNaoLidas, Priority.ALWAYS);
        this.add(smsMachinaNaoLidas, 1, 0);
        this.add(smsMachinaLidas, 2, 0);
//        this.add(smsOperador,1, 1);
    }

    private void colocarObjectos() {
        String seccao = (String) AppMain.getInstancia().getComboSeccao().getValue();
        if (seccao.equals(seccaoProp.get())) {
            setVisible(false);
            setManaged(false);
//            return;
        } else {
            setVisible(true);
            setManaged(true);
        }

        double margemObjectos = 4;

        int col = 0;
        int row = 0;
        Label labelCodigo = new Label();
        labelCodigo.setFont(new Font(20.0f));
        labelCodigo.textProperty().bind(maquinaCodigoProp);
        setMargin(labelCodigo, new Insets(0, 0, 0, margemObjectos));
        this.add(labelCodigo, col, row);

        col = 0;
        row = 1;
        HBox hbox01 = new HBox();

        Label labelOperador = new Label();
        labelOperador.textProperty().bind(operadorProp);
        setMargin(labelOperador, new Insets(0, 0, 0, margemObjectos));

        Label labelTempoMaquina = new Label("00:00:00");
        labelTempoMaquina.textProperty().bind(tempoMaquinaProp);
        HBox.setMargin(labelTempoMaquina, new Insets(0, 0, 0, margemObjectos * 2));
        hbox01.getChildren().addAll(labelOperador, labelTempoMaquina);

        this.add(hbox01, col, row);

        col = 0;
        row = 2;
        Label labelOP = new Label("");
        labelOP.textProperty().bind(osProp);
        setMargin(labelOP, new Insets(0, 0, 0, margemObjectos));
        this.add(labelOP, col, row);
//        col = 1;
//        row = 0;

//        Label labelFuncao = new Label();
//        labelFuncao.textProperty().bind(funcaoProp);
//        setMargin(labelFuncao, new Insets(0, margemObjectos, 0, margemObjectos));
//        this.add(labelFuncao, col, row);

        col = 0;
        row = 3;
        Region region = new Region();
        GridPane.setHgrow(region, Priority.ALWAYS);
        GridPane.setConstraints(region, col, row, 2, 1, HPos.RIGHT, VPos.CENTER);
        this.add(region, col, row);

    }

//    private void firebase() {
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("tokens").child("maquinas").child(maquinaCodigoProp.get());
//        ref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                TokenMachina token = dataSnapshot.getValue(TokenMachina.class);
//                if (token != null) {
//                    token.setCodigo(dataSnapshot.getKey());
//                    tokenMachinaProp.set(token);
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }


    private void eventos() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem menuSmsMachina = new MenuItem("Novo SMS " + maquinaCodigoProp.get());
        menuSmsMachina.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                enviarSMS(SMS_MACHINA);
            }
        });

        MenuItem menuSmsOperador = new MenuItem();
//        menuSmsOperador.visibleProperty().bind(onlineProp);
        menuSmsOperador.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                enviarSMS(SMS_OPERADOR);
            }
        });

        contextMenu.getItems().addAll(menuSmsMachina, menuSmsOperador);

        setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                contextMenu.hide();
                if (event.getClickCount() > 1) {
                    event.consume();
                }
                if (event.getClickCount() == 1) {
                    if (event.isSecondaryButtonDown()) {
                        menuSmsOperador.setText("Novo SMS " + operadorProp.get());
                        contextMenu.show(context, event.getScreenX(), event.getScreenY());
                        event.consume();
                        System.out.println(TAG + ": Chamar menu paraLabel a máquina " + nomeProp.get());
                    }
                }
            }
        });
    }

    private void enviarSMS(int tipoSMS) {
        try {
            new EnviarSMS(tipoSMS, this);
        } catch (IOException e) {
            e.printStackTrace();
            Procedimentos.alertaException(e);
        }
    }

    private void setupBinders(Machina machina) {
        machinaProp.addListener(new ChangeListener<Machina>() {
            @Override
            public void changed(ObservableValue<? extends Machina> observable, Machina oldValue, Machina newValue) {
                seccaoProp.set(newValue.getSeccao());
                maquinaCodigoProp.set(newValue.getCodigo());
                funcaoProp.set(newValue.getEstado());
                nomeProp.set(newValue.getNome());
                ordemProp.set(newValue.getOrdem());
                Procedimentos.colocarEstilo(context, "machine_off");
            }
        });

        machinaProp.set(machina);

//        tokenMachinaProp.addListener(new ChangeListener<TokenMachina>() {
//            @Override
//            public void changed(ObservableValue<? extends TokenMachina> observable, TokenMachina oldValue, TokenMachina newValue) {
//                dataTokenProp.set(newValue.getData());
//                onlineProp.set(newValue.isOnline());
//                Platform.runLater(new Runnable() {
//                    @Override
//                    public void run() {
//                        operadorProp.set(newValue.getOperador());
//                    }
//                });
//                timestampProp.set(newValue.getTimestamp());
//                tokenProp.set(newValue.getToken());
//            }
//        });

        machinaOnlineProp.addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    Procedimentos.colocarEstilo(context, "machine_on");
                } else {
                    Procedimentos.colocarEstilo(context, "machine_off");
                }
            }
        });
    }

    public Machina getMachinaProp() {
        return machinaProp.get();
    }

    private class CronometroMaquina extends TimerTask {
        private final MaquinaStatus maquinaStatus;

        public CronometroMaquina(MaquinaStatus maquinaStatus) {
            this.maquinaStatus = maquinaStatus;
        }

        @Override
        public void run() {
            long tempo = (Funcoes.agoraUnixSegundos() - maquinaStatus.getUnixtime());
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    tempoMaquinaProp.setValue(Funcoes.unix_HH_mm_ss(tempo - Constantes.OFFSET_TEMPO));
                }
            });
        }
    }
}
