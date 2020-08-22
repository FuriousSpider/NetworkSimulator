package simulator.view;

import javafx.scene.control.ScrollPane;
import javafx.scene.text.Text;

import java.util.Date;

public class LogPanel extends ScrollPane {

    private final Text textField;

    public LogPanel() {
        this.textField = new Text("Log pannel:\n");
        textField.wrappingWidthProperty().bind(this.widthProperty());
        this.setContent(textField);
    }

    public void log(String text) {
        String time = (new Date()).toString().split(" ")[3];
        this.textField.setText(textField.getText() + "\n" + time + ": " + text);
        this.setVvalue(1.0);
    }
}
