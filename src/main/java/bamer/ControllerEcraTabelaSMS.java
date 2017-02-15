package bamer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import pojos.SMS;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * by miguel.silva on 14-02-2017.
 */
public class ControllerEcraTabelaSMS implements Initializable {
    public Button butfechar;
    public BorderPane borderpane;
    private TableView tableView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void populate(ArrayList<SMS> lista) {
        tableView = new TableView<>();
        preencherTabela(lista);
    }

    @SuppressWarnings("unchecked")
    private void preencherTabela(ArrayList<SMS> lista) {
//        tableView.setPlaceholder(new Label("A obter dados..."));

        ObservableList<SMS> observableList_LinhasXLS = FXCollections.observableArrayList(lista);

        TableColumn colData = new TableColumn("data");
        colData.setCellValueFactory(new PropertyValueFactory<SMS, String>("dataTxt"));
        colData.setEditable(false);


        TableColumn colAssunto = new TableColumn("assunto");
        colAssunto.setCellValueFactory(new PropertyValueFactory<SMS, String>("assunto"));
        colAssunto.setEditable(false);

        TableColumn colMensagem = new TableColumn("mensagem");
        colMensagem.setCellValueFactory(new PropertyValueFactory<SMS, String>("mensagem"));
        colMensagem.setEditable(false);

        TableColumn colPara = new TableColumn("para");
        colPara.setCellValueFactory(new PropertyValueFactory<SMS, String>("para"));
        colPara.setEditable(false);

        TableColumn<SMS, Boolean> colLida = new TableColumn("lida");
        colLida.setCellFactory(CheckBoxTableCell.forTableColumn(colLida));
        colLida.setCellValueFactory(new PropertyValueFactory<>("lida"));
        colLida.setEditable(false);

        TableColumn colQuemLeu = new TableColumn("quem leu");
        colQuemLeu.setCellValueFactory(new PropertyValueFactory<SMS, String>("lidaQuem"));
        colQuemLeu.setEditable(false);

        TableColumn<SMS, Boolean> colArquivar = new TableColumn("arquivar");
        colArquivar.setCellValueFactory(new PropertyValueFactory<>("arquivada"));
        colArquivar.setCellFactory(CheckBoxTableCell.forTableColumn(colArquivar));


        tableView.getColumns().addAll(colData, colAssunto, colMensagem, colPara, colLida, colQuemLeu, colArquivar);
        tableView.setItems(observableList_LinhasXLS);
        tableView.setEditable(true);
        borderpane.setCenter(tableView);

    }

    public void fechar(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.hide();
    }
}
