package simulator.view;

import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.Date;

public class LogPanel extends ScrollPane {

    private final VBox logField;

    public LogPanel() {
        logField = new VBox();
        this.setContent(logField);
        logField.getChildren().add(new Label("Log panel:"));
    }

    public void log(String text) {
        String time = (new Date()).toString().split(" ")[3];
        logField.getChildren().add(new Label(time + ": " + text));
        this.setVvalue(1.0);
    }

    public void logError(String errorMessage) {
        String time = (new Date()).toString().split(" ")[3];
        Label label = new Label(time + ": " + errorMessage);
        label.setTextFill(Color.RED);
        logField.getChildren().add(label);
        this.setVvalue(1.0);
    }

    public void clear() {
        logField.getChildren().clear();
        logField.getChildren().add(new Label("Log panel:"));
    }
}
