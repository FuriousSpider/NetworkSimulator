package simulator.view;

import javafx.beans.NamedArg;
import javafx.beans.Observable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import simulator.Engine;

public class EditableLabel extends VBox {
    private String value;

    private final HBox firstLine;
    private final HBox secondLine;
    private final Label titleLabel;
    private final Label label;
    private final TextField textField;
    private final Button editButton;
    private final Button copyButton;

    public EditableLabel() {
        this.firstLine = new HBox();
        this.secondLine = new HBox();
        this.titleLabel = new Label();
        this.label = new Label();
        this.textField = new TextField();
        this.editButton = new Button("Edit");
        this.copyButton = new Button("Copy");

        this.getChildren().add(firstLine);
        this.getChildren().add(secondLine);
        this.firstLine.getChildren().add(titleLabel);
        this.firstLine.getChildren().add(label);
        this.firstLine.getChildren().add(textField);
        this.secondLine.getChildren().add(editButton);
        this.secondLine.getChildren().add(copyButton);

        this.textField.setVisible(false);

        this.editButton.setOnMouseClicked(this::onEditButtonClicked);
        this.copyButton.setOnMouseClicked(this::onCopyButtonClicked);
        this.textField.textProperty().addListener(this::onTextChangedListener);
    }

    public EditableLabel(@NamedArg("title") String title, @NamedArg("defaultValue") String defaultValue) {
        this();
        if (title != null) {
            this.titleLabel.setText(title);
        } else {
            this.titleLabel.setVisible(false);
            this.titleLabel.setManaged(false);
        }
        this.value = defaultValue;
        this.label.setText(defaultValue);
        this.textField.setManaged(false);
    }

    private void onEditButtonClicked(MouseEvent mouseEvent) {
        //TODO: when label is not empty it takes additional space
        if (label.isVisible()) {
            textField.setText(value);
            textField.setVisible(true);
            textField.setManaged(true);
            label.setVisible(false);
            label.setManaged(false);
            editButton.setText("Save");
        } else {
            label.setText(value);
            label.setVisible(true);
            label.setManaged(true);
            textField.setVisible(false);
            textField.setManaged(false);
            editButton.setText("Edit");
        }
    }

    private void onCopyButtonClicked(MouseEvent mouseEvent) {
        Engine.getInstance().copyToClipboard(value);
    }

    private void onTextChangedListener(Observable observable) {
        this.value = textField.getText();
    }

}
