package simulator.view;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import simulator.Engine;
import util.Utils;
import util.Values;

public class IPTextField extends VBox {
    private final HBox firstLine;
    private final HBox secondLine;
    private final TextField ipAddressTextField;
    private final Label ipAddressLabel;
    private final Button editButton;
    private final Button cancelButton;

    private String ipAddressValue;
    private boolean isInEditMode;

    private OnSaveClickedListener listener;

    public IPTextField() {
        this.firstLine = new HBox();
        this.secondLine = new HBox();
        this.ipAddressTextField = new TextField();
        this.ipAddressLabel = new Label();
        this.editButton = new Button("Edit");
        this.cancelButton = new Button("Cancel");

        this.getChildren().add(firstLine);
        this.getChildren().add(secondLine);

        this.firstLine.getChildren().add(ipAddressTextField);
        this.firstLine.getChildren().add(ipAddressLabel);

        this.secondLine.getChildren().add(editButton);
        this.secondLine.getChildren().add(cancelButton);

        this.editButton.setOnMouseClicked(this::onEditButtonClicked);
        this.cancelButton.setOnMouseClicked(this::onCancelButtonClicked);

        this.isInEditMode = true;
        changeState();
    }

    private void changeState() {
        if (isInEditMode) {
            ipAddressTextField.setVisible(false);
            ipAddressTextField.setManaged(false);

            ipAddressLabel.setVisible(true);
            ipAddressLabel.setManaged(true);

            editButton.setText("Edit");
            cancelButton.setVisible(false);
            cancelButton.setManaged(false);
        } else {
            ipAddressTextField.setVisible(true);
            ipAddressTextField.setManaged(true);

            ipAddressLabel.setVisible(false);
            ipAddressLabel.setManaged(false);

            editButton.setText("Save");
            cancelButton.setVisible(true);
            cancelButton.setManaged(true);
        }
        isInEditMode = !isInEditMode;
        loadIpAddress();
    }

    private void loadIpAddress() {
        if (isInEditMode) {
            ipAddressTextField.setText(ipAddressValue);
        } else {
            ipAddressLabel.setText(ipAddressValue);
        }
    }

    private void onEditButtonClicked(MouseEvent mouseEvent) {
        if (isInEditMode) {
            if (!Utils.isHostAddress(ipAddressTextField.getText())) {
                Engine.getInstance().logError(Values.ERROR_INVALID_IP_ADDRESS);
                return;
            }
            ipAddressValue = ipAddressTextField.getText();
            if (listener != null) {
                listener.onSaveClicked(ipAddressValue);
            }
        }
        changeState();
    }

    private void onCancelButtonClicked(MouseEvent mouseEvent) {
        changeState();
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddressValue = ipAddress;
        loadIpAddress();
    }

    public void show() {
        this.setVisible(true);
        this.setManaged(true);
    }

    public void hide() {
        this.setVisible(false);
        this.setManaged(false);
    }

    public void setOnSaveClickedListener(OnSaveClickedListener listener) {
        this.listener = listener;
    }

    public interface OnSaveClickedListener {
        void onSaveClicked(String ipAddress);
    }
}
