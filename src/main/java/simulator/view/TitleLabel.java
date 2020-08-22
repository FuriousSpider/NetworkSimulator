package simulator.view;

import javafx.beans.NamedArg;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class TitleLabel extends HBox {

    private Label titleLabel;
    private Label valueLabel;

    public TitleLabel() {
        this.titleLabel = new Label();
        this.valueLabel = new Label();

        this.getChildren().add(titleLabel);
        this.getChildren().add(valueLabel);
    }

    public TitleLabel(@NamedArg("title") String title, @NamedArg("value") String value) {
        this();
        this.titleLabel.setText(title);
        this.valueLabel.setText(value);
    }

    public void setValue(String value) {
        this.valueLabel.setText(value);
    }
}
