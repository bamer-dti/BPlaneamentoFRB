package objectos;

import com.jfoenix.controls.JFXButton;
import javafx.event.EventHandler;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.paint.Color;
import pojos.ArtigoOSBO;
import utils.Procedimentos;

public class JFXButtonPlanear extends JFXButton {

    public JFXButtonPlanear(String s) {
        setText(s);
        configurarEventos();
    }

    private void configurarEventos() {
        setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Object object = event.getDragboard().getContent(DataFormat.RTF);
                if (object instanceof VBoxOSBO) {
                    Dragboard dragboard = event.getDragboard();
                    textFillProperty().set(Color.GREENYELLOW);
                    event.acceptTransferModes(TransferMode.MOVE);
                    event.consume();
                }
            }
        });

        setOnDragExited(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                textFillProperty().set(Color.WHITE);
                event.acceptTransferModes(TransferMode.NONE);
                event.consume();
            }
        });

        setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Object object = event.getDragboard().getContent(DataFormat.RTF);
                if (object instanceof VBoxOSBO) {
                    Dragboard dragboard = event.getDragboard();
                    VBoxOSBO vboxEmDRAG = (VBoxOSBO) dragboard.getContent(DataFormat.RTF);
                    ArtigoOSBO artigoOSBOemDRAG = vboxEmDRAG.getArtigoOSBOProp();
                    if (artigoOSBOemDRAG.getBostamp().equals(getId())) {
                        event.consume();
                        return;
                    }
                    int ordem = vboxEmDRAG.getOrdemProp();
                    Procedimentos.actualizar_OSBO_colocar_em_planeamento(vboxEmDRAG);
                    Procedimentos.actualizar_restantes_OSBOs(vboxEmDRAG.getColuna(), ordem);
                    event.consume();
                }
            }
        });
    }
}
