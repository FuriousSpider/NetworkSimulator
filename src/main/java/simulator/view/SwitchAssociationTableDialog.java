package simulator.view;

import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import simulator.Engine;
import simulator.element.device.additionalElements.AssociationTableEntry;

import java.util.List;

public class SwitchAssociationTableDialog extends Dialog<String> {
    private final GridPane content;


    public SwitchAssociationTableDialog() {
        this.content = new GridPane();

        this.getDialogPane().setContent(content);

        this.setTitle("MAC Address Table");
        this.content.setHgap(10);

        this.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);
    }

    public void setData(List<AssociationTableEntry> associationTable) {
        content.add(new Label("Index"), 0, 0);
        content.add(new Label("vLanId"), 1, 0);
        content.add(new Label("MAC Address"), 2, 0);
        content.add(new Label("Port"), 3, 0);

        Pane pane = new Pane();
        pane.setMinHeight(1);
        pane.setMaxHeight(1);
        pane.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
        content.add(pane, 0, 1, 4, 1);

        for (AssociationTableEntry entry : associationTable) {
            int index = associationTable.indexOf(entry) + 2;
            content.add(new Label(String.valueOf(index - 1)), 0, index);
            if (entry.getvLanId() != 0) {
                content.add(new Label(String.valueOf(entry.getvLanId())), 1, index);
            }
            content.add(new Label(entry.getMacAddress()), 2, index);
            content.add(new Label(Engine.getInstance().getPortById(entry.getPortId()).getPortName()), 3,index);
        }
    }
}
