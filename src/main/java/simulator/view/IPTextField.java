package simulator.view;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class IPTextField extends VBox {
    private final HBox firstLine;
    private final HBox secondLine;
    private final HBox thirdLine;
    private final TextField ipAddressTextField;
    private final Label ipAddressLabel;
    private final Label errorLabel;
    private final Button editButton;
    private final Button cancelButton;

    private String ipAddressValue;
    private boolean isInEditMode;

    private OnSaveClickedListener listener;

    public IPTextField() {
        this.firstLine = new HBox();
        this.secondLine = new HBox();
        this.thirdLine = new HBox();
        this.ipAddressTextField = new TextField();
        this.ipAddressLabel = new Label();
        //TODO: handle error message
        this.errorLabel = new Label("IP address doesn't match the pattern: xxx.xxx.xxx.xxx/xx");
        this.editButton = new Button("Edit");
        this.cancelButton = new Button("Cancel");

        this.getChildren().add(firstLine);
        this.getChildren().add(secondLine);
        this.getChildren().add(thirdLine);

        this.firstLine.getChildren().add(ipAddressTextField);
        this.firstLine.getChildren().add(ipAddressLabel);

        this.secondLine.getChildren().add(errorLabel);

        this.thirdLine.getChildren().add(editButton);
        this.thirdLine.getChildren().add(cancelButton);

        editButton.setOnMouseClicked(this::onEditButtonClicked);
        cancelButton.setOnMouseClicked(this::onCancelButtonClicked);

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
            //TODO: validate if ipAddress is correct
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
