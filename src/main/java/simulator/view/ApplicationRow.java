package simulator.view;

import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class ApplicationRow extends HBox {
    private final Label applicationName;
    private final CheckBox selected;

    private boolean isInEditMode;

    public ApplicationRow(String name, boolean isSelected) {
        this.applicationName = new Label(name);
        this.selected = new CheckBox();
        this.selected.setSelected(isSelected);

        this.getChildren().add(applicationName);
        this.getChildren().add(selected);

        this.isInEditMode = true;
        changeState();
    }

    public void changeState() {
        selected.setDisable(isInEditMode);
        isInEditMode = !isInEditMode;
    }

    public String getApplicationName() {
        return applicationName.getText();
    }

    public boolean isSelected() {
        return selected.isSelected();
    }
}
