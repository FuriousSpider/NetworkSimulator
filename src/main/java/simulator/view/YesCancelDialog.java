package simulator.view;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

public class YesCancelDialog extends Alert {

    public YesCancelDialog(AlertType alertType) {
        super(alertType);

        ButtonType yesButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        getButtonTypes().setAll(yesButton, cancelButton);
    }
}
