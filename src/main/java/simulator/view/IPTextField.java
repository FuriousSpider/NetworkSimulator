package simulator.view;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import simulator.Manager;
import util.Utils;
import util.Values;

public class IPTextField extends GridPane {
    private final TextField ipAddressTextField;
    private final Label ipAddressLabel;
    private final Button editButton;
    private final Button cancelButton;
    private final HBox buttonLine;

    private String ipAddressValue;
    private boolean isInEditMode;

    private OnSaveClickedListener listener;

    public IPTextField() {
        this.ipAddressTextField = new TextField();
        this.ipAddressLabel = new Label();
        this.editButton = new Button("Edit");
        this.cancelButton = new Button("Cancel");
        this.buttonLine = new HBox();

        this.add(ipAddressTextField, 0, 0);
        this.add(ipAddressLabel, 0, 0);

        this.buttonLine.getChildren().add(editButton);
        this.buttonLine.getChildren().add(cancelButton);

        this.add(buttonLine, 0, 1);

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
                Manager.getInstance().logError(Values.ERROR_INVALID_IP_ADDRESS);
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
