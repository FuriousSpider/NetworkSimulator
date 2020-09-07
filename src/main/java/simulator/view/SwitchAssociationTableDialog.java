package simulator.view;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;
import simulator.Engine;

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

    public void setData(List<Pair<String, Integer>> associationTable) {
        content.add(new Label("Index"), 0, 0);
        content.add(new Label("MAC Address"), 1, 0);
        content.add(new Label("Port"), 2, 0);

        for (Pair<String, Integer> entry : associationTable) {
            int index = associationTable.indexOf(entry) + 1;
            content.add(new Label(String.valueOf(index)), 0, index);
            content.add(new Label(entry.getKey()), 1, index);
            content.add(new Label(Engine.getInstance().getPortById(entry.getValue()).getPortName()), 2,index);
        }
    }
}
