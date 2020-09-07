package simulator.view;

import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;
import simulator.Engine;

import java.util.ArrayList;
import java.util.List;


public class SwitchButtonsView extends GridPane {
    private final Button showMacTableButton;
    private final Button clearMacTableButton;

    private List<Pair<String, Integer>> associationTable;

    private OnClearMacTableClickedListener onClearMacTableClickedListener;

    public SwitchButtonsView() {
        this.showMacTableButton = new Button("Show MAC Address Table");
        this.clearMacTableButton = new Button("Clear MAC Address Table");

        this.associationTable = new ArrayList<>();

        this.showMacTableButton.setOnMouseClicked(this::showMacTableButtonClicked);
        this.clearMacTableButton.setOnMouseClicked(this::clearMacTableButtonClicked);

        this.add(showMacTableButton, 0, 0);
        this.add(clearMacTableButton, 1, 0);
    }

    public void setMacTable(List<Pair<String, Integer>> associationTable) {
        this.associationTable.clear();
        this.associationTable.addAll(associationTable);
    }

    private void showMacTableButtonClicked(MouseEvent mouseEvent) {
        if (!associationTable.isEmpty()) {
            SwitchAssociationTableDialog dialog = new SwitchAssociationTableDialog();
            dialog.setData(associationTable);
            dialog.show();
        } else {
            Engine.getInstance().logError("Nothing to show - MAC Address Table is empty");
        }

    }

    private void clearMacTableButtonClicked(MouseEvent mouseEvent) {
        associationTable.clear();
        if (onClearMacTableClickedListener != null) {
            onClearMacTableClickedListener.onClearMacTableClicked();
            Engine.getInstance().log("MAC Address Table has been cleared");
        }
    }

    public void setOnClearMacTableClickedListener(OnClearMacTableClickedListener listener) {
        this.onClearMacTableClickedListener = listener;
    }

    public void show() {
        this.setVisible(true);
        this.setManaged(true);
    }

    public void hide() {
        this.setVisible(false);
        this.setManaged(false);
    }

    public interface OnClearMacTableClickedListener {
        void onClearMacTableClicked();
    }
}
