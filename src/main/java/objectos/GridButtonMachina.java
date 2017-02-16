package objectos;

import bamer.AppMain;
import com.google.firebase.database.*;
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
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import pojos.TokenMachina;
import utils.Funcoes;

import java.io.IOException;

import static utils.Constantes.SMS_MACHINA;
import static utils.Constantes.SMS_OPERADOR;

/**
 * by miguel.silva on 09-02-2017.
 */
public class GridButtonMachina extends GridPane {
    private static final String TAG = GridButtonMachina.class.getSimpleName();
    private final GridButtonMachina context;
    public SimpleObjectProperty<TokenMachina> tokenMachinaProp = new SimpleObjectProperty<>();
    public SimpleStringProperty codigoProp = new SimpleStringProperty();
    public SimpleStringProperty operadorProp = new SimpleStringProperty();
    public SimpleStringProperty tokenProp = new SimpleStringProperty();
    private SimpleStringProperty seccaoProp = new SimpleStringProperty();
    private SimpleStringProperty funcaoProp = new SimpleStringProperty();
    private SimpleStringProperty nomeProp = new SimpleStringProperty();
    private SimpleIntegerProperty ordemProp = new SimpleIntegerProperty();
    private SimpleObjectProperty<Machina> machinaProp = new SimpleObjectProperty<>();
    private SimpleStringProperty dataTokenProp = new SimpleStringProperty();
    private SimpleBooleanProperty onlineProp = new SimpleBooleanProperty();
    private SimpleLongProperty timestampProp = new SimpleLongProperty();

    public GridButtonMachina(Machina machina) {
        context = this;
        setMinWidth(200);
        setMaxWidth(200);
        setupBinders(machina);
        colocarObjectos();
        eventos();
        firebase();
        listeners();
        objectoSMS(machina);
    }

    private void objectoSMS(Machina machina) {
        ButaoSMS smsMachinaNaoLidas = new ButaoSMS(machina, SMS_MACHINA, false);
        ButaoSMS smsMachinaLidas = new ButaoSMS(machina, SMS_MACHINA, true);
//        this.smsOperador= new ButaoSMS(machina, SMS_OPERADOR);
        GridButtonMachina.setHgrow(smsMachinaNaoLidas, Priority.ALWAYS);
        this.add(smsMachinaNaoLidas, 1, 0);
        this.add(smsMachinaLidas, 2, 0);
//        this.add(smsOperador,1, 1);
    }

    private void listeners() {
        operadorProp.addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                System.out.println(TAG + ": operador passou paraLabel '" + newValue + "' na máquina " + codigoProp.get());
            }
        });
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
        labelCodigo.textProperty().bind(codigoProp);
        setMargin(labelCodigo, new Insets(0, 0, 0, margemObjectos));
        this.add(labelCodigo, col, row);

        col = 0;
        row = 1;
        Label labelOperador = new Label();
        labelOperador.textProperty().bind(operadorProp);
        setMargin(labelOperador, new Insets(0, 0, 0, margemObjectos));
        this.add(labelOperador, col, row);

//        col = 1;
//        row = 0;

//        Label labelFuncao = new Label();
//        labelFuncao.textProperty().bind(funcaoProp);
//        setMargin(labelFuncao, new Insets(0, margemObjectos, 0, margemObjectos));
//        this.add(labelFuncao, col, row);

        col = 0;
        row = 2;
        Region region = new Region();
        GridPane.setHgrow(region, Priority.ALWAYS);
        GridPane.setConstraints(region, col, row, 2, 1, HPos.RIGHT, VPos.CENTER);
        this.add(region, col, row);

    }

    private void firebase() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("tokens").child("maquinas").child(codigoProp.get());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                TokenMachina token = dataSnapshot.getValue(TokenMachina.class);
                if (token != null) {
                    token.setCodigo(dataSnapshot.getKey());
                    tokenMachinaProp.set(token);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void eventos() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem menuSmsMachina = new MenuItem("Novo SMS " + codigoProp.get());
        menuSmsMachina.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                enviarSMS(SMS_MACHINA);
            }
        });

        MenuItem menuSmsOperador = new MenuItem();
        menuSmsOperador.visibleProperty().bind(onlineProp);
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
            Funcoes.alertaException(e);
        }
    }

    private void setupBinders(Machina machina) {
        machinaProp.addListener(new ChangeListener<Machina>() {
            @Override
            public void changed(ObservableValue<? extends Machina> observable, Machina oldValue, Machina newValue) {
                seccaoProp.set(newValue.getSeccao());
                codigoProp.set(newValue.getCodigo());
                funcaoProp.set(newValue.getFuncao());
                nomeProp.set(newValue.getNome());
                ordemProp.set(newValue.getOrdem());
                Funcoes.colocarEstilo(context, "machine_off");
            }
        });

        machinaProp.set(machina);

        tokenMachinaProp.addListener(new ChangeListener<TokenMachina>() {
            @Override
            public void changed(ObservableValue<? extends TokenMachina> observable, TokenMachina oldValue, TokenMachina newValue) {
                dataTokenProp.set(newValue.getData());
                onlineProp.set(newValue.isOnline());
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        operadorProp.set(newValue.getOperador());
                    }
                });
                timestampProp.set(newValue.getTimestamp());
                tokenProp.set(newValue.getToken());
            }
        });

        onlineProp.addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    Funcoes.colocarEstilo(context, "machine_on");
                } else {
                    Funcoes.colocarEstilo(context, "machine_off");
                }
            }
        });
    }

    public Machina getMachinaProp() {
        return machinaProp.get();
    }
}
