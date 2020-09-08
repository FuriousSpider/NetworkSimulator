package simulator.view;

import javafx.beans.NamedArg;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class DeviceTypeLabel extends HBox {

    private Label titleLabel;
    private Label valueLabel;

    public DeviceTypeLabel() {
        this.titleLabel = new Label();
        this.valueLabel = new Label();

        this.getChildren().add(titleLabel);
        this.getChildren().add(valueLabel);

        titleLabel.getStyleClass().add("boldLabel");

        this.setSpacing(10);
    }

    public DeviceTypeLabel(@NamedArg("title") String title, @NamedArg("value") String value) {
        this();
        this.titleLabel.setText(title);
        this.valueLabel.setText(value);
    }

    public void setValue(String value) {
        this.valueLabel.setText(value);
    }
}
