package bamer;

import com.google.firebase.database.*;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.json.JSONObject;
import pojos.TokenMachina;
import webservices.WSWorker;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static utils.Constantes.SMS_MACHINA;
import static utils.Constantes.SMS_OPERADOR;

/**
 * by miguel.silva on 13-02-2017.
 */
@SuppressWarnings({"unchecked", "SuspiciousMethodCalls"})
public class ControllerEnviarSMS implements Initializable {
    private static final String TAG = ControllerEnviarSMS.class.getSimpleName();
    public Label paraLabel;
    public Label tokenLabel;
    public TextArea mensagemTextArea;
    public TextField assuntoTextField;
    public HBox hboxparam;
    public ToggleGroup toogle_para;
    public RadioButton radio_para_maquina;
    public RadioButton radio_para_operador;
    public ComboBox<TokenMachina> combo_para;

    public int tipoSMS;
    private ArrayList listaDeTokensMachina;
    private Background corverde;
    private Background corvermelho;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tokenLabel.setManaged(false);
        corverde = new Background(new BackgroundFill(Color.FORESTGREEN, CornerRadii.EMPTY, Insets.EMPTY));
        corvermelho = new Background(new BackgroundFill(Color.ORANGERED, CornerRadii.EMPTY, Insets.EMPTY));
    }

    public void configVazio() {
        paraLabel.setText("");
        tokenLabel.setText("");
        combo_para.setVisible(false);
        toogle_para.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        combo_para.setVisible(false);
                        paraLabel.setText("");
                        tokenLabel.setText("");
                    }
                });
                if (listaDeTokensMachina != null) {
                    ObservableList<TokenMachina> lista = combo_para.getItems();
                    combo_para.getItems().removeAll(lista);
                }
                listaDeTokensMachina = new ArrayList<>();
                if (toogle_para.getSelectedToggle() != null) {
                    RadioButton radio = (RadioButton) toogle_para.getSelectedToggle();
                    if (radio == radio_para_maquina) {
                        tipoSMS = SMS_MACHINA;
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("tokens").child("maquinas");
                        popularCombo(ref);

                    }
                    if (radio == radio_para_operador) {
                        tipoSMS = SMS_OPERADOR;
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("tokens").child("operadores");
                        popularCombo(ref);
                    }
                }
            }

            private void popularCombo(DatabaseReference ref) {
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot d : dataSnapshot.getChildren()) {
                            TokenMachina tokenMachina = d.getValue(TokenMachina.class);
                            tokenMachina.setCodigo(d.getKey());
                            if (tipoSMS == SMS_OPERADOR) {
                                if (!tokenMachina.isOnline()) {
                                    continue;
                                }
                            }
                            listaDeTokensMachina.add(tokenMachina);
                        }

                        if (listaDeTokensMachina == null) {
                            return;
                        }
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                combo_para.setButtonCell(new TokenMachinaListCell());
                                combo_para.setCellFactory(new Callback<ListView<TokenMachina>, ListCell<TokenMachina>>() {
                                    @Override
                                    public ListCell<TokenMachina> call(ListView<TokenMachina> p) {
                                        return new TokenMachinaListCell();
                                    }
                                });
                                combo_para.getItems().addAll(listaDeTokensMachina);
                                System.out.println(TAG + ": seleccionado o  radio de máquinas com " + listaDeTokensMachina.size() + " registos. Comobo: " + combo_para.getItems().size());
                                combo_para.setVisible(true);
                                combo_para.selectionModelProperty().addListener(new ChangeListener<SingleSelectionModel<TokenMachina>>() {
                                    @Override
                                    public void changed(ObservableValue<? extends SingleSelectionModel<TokenMachina>> observable, SingleSelectionModel<TokenMachina> oldValue, SingleSelectionModel<TokenMachina> newValue) {

                                    }
                                });
                                combo_para.valueProperty().addListener(new ChangeListener<TokenMachina>() {
                                    @Override
                                    public void changed(ObservableValue<? extends TokenMachina> observable, TokenMachina oldValue, TokenMachina tokenMachina) {
                                        if (tokenMachina == null) {
                                            paraLabel.setText("");
                                            tokenLabel.setText("");
                                            return;
                                        }
                                        paraLabel.setText(tokenMachina.getCodigo());
                                        tokenLabel.setText(tokenMachina.getToken());
                                    }
                                });
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    public void gravar(ActionEvent actionEvent) {
        if (tokenLabel.getText().equals("")) {
            alertaDadosEmFalta("O token de envio está vazio!");
            return;
        }

        if (paraLabel.getText().equals("")) {
            alertaDadosEmFalta("O campo PARA não está preenchido");
            return;
        }

        if (mensagemTextArea.getText().equals("")) {
            System.out.println("O SMS está vazio!");
            alertaDadosEmFalta("Não pode enviar um SMS sem mensagem!");
            return;
        }
        System.out.println("A enviar o texto " + mensagemTextArea.getText() + " paraLabel " + paraLabel.getText());

        Node source = (Node) actionEvent.getSource();
        Stage stage = (Stage) source.getScene().getWindow();

        int time_to_live;
        switch (tipoSMS) {
            case SMS_OPERADOR:
                time_to_live = 0;
                break;
            default:
                time_to_live = 3600;
                break;
        }

        JSONObject json = new JSONObject();
        json.put("de", "planeamento");
        json.put("para", paraLabel.getText());
        json.put("token", tokenLabel.getText());
        json.put("assunto", assuntoTextField.getText());
        json.put("mensagem", mensagemTextArea.getText());
        json.put("bostamp", "");
        json.put("time_to_live", time_to_live);
        new WSWorker().enviarSMS(stage, json);
    }

    private void alertaDadosEmFalta(String textoMensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Dados em falta");
        alert.setHeaderText(null);
        alert.setContentText(textoMensagem);

        alert.showAndWait();
    }

    public void cancelar(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    class TokenMachinaListCell extends ListCell<TokenMachina> {
        @Override
        protected void updateItem(TokenMachina item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null) {
                setText(item.getCodigo() + " (" + (item.isOnline() ? "online" : "offline") + ")");
                if (tipoSMS == SMS_MACHINA)
                    setBackground(item.isOnline() ? corverde : corvermelho);
            }
        }
    }
}
