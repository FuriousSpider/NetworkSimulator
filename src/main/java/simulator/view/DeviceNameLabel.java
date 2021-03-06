package simulator.view;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

public class DeviceNameLabel extends HBox {
    private String value;

    private final Label titleLabel;
    private final Label label;
    private final TextField textField;
    private final Button editButton;

    private boolean isInEditMode;

    private OnSaveDeviceNameClickedListener onSaveDeviceNameClickedListener;

    public DeviceNameLabel() {
        this.titleLabel = new Label("Device name: ");
        this.label = new Label();
        this.textField = new TextField();
        this.editButton = new Button("Edit");

        this.getChildren().add(titleLabel);
        this.getChildren().add(label);
        this.getChildren().add(textField);
        this.getChildren().add(editButton);

        this.textField.setVisible(false);

        this.editButton.setOnMouseClicked(this::onEditButtonClicked);

        this.setSpacing(10);

        this.titleLabel.getStyleClass().add("boldLabel");

        this.isInEditMode = true;
        changeState();
    }

    private void changeState() {
        if (isInEditMode) {
            label.setText(value);
            label.setVisible(true);
            label.setManaged(true);
            textField.setVisible(false);
            textField.setManaged(false);
            editButton.setText("Edit");
        } else {
            textField.setText(value);
            textField.setVisible(true);
            textField.setManaged(true);
            label.setVisible(false);
            label.setManaged(false);
            editButton.setText("Save");
        }
        isInEditMode = !isInEditMode;
    }

    private void reloadData() {
        if (isInEditMode) {
            textField.setText(value);
        } else {
            label.setText(value);
        }
    }

    private void onEditButtonClicked(MouseEvent mouseEvent) {
        if (isInEditMode) {
            value = textField.getText();
            if (this.onSaveDeviceNameClickedListener != null) {
                onSaveDeviceNameClickedListener.onSaveDeviceNameClicked(value);
            }
        }
        changeState();
    }

    public void setDeviceName(String name) {
        this.value = name;
        reloadData();
    }

    public void setOnSaveDeviceNameClickedListener(OnSaveDeviceNameClickedListener listener) {
        this.onSaveDeviceNameClickedListener = listener;
    }

    public interface OnSaveDeviceNameClickedListener {
        void onSaveDeviceNameClicked(String name);
    }
}
