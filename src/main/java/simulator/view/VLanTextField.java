package simulator.view;

import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import simulator.Engine;
import util.Utils;
import util.Values;

public class VLanTextField extends VBox {

    private final HBox firstLine;
    private final HBox secondLine;
    private final TextField vLanTextField;
    private final Label vLanLabel;
    private final CheckBox isInTrunkModeCheckBox;
    private final Button editButton;
    private final Button cancelButton;

    private String vLanValue;
    private boolean isInEditMode;

    private OnChangeModeClickedListener onChangeModeClickedListener;
    private OnSaveClickedListener onSaveClickedListener;

    public VLanTextField() {
        this.firstLine = new HBox();
        this.secondLine = new HBox();
        this.vLanTextField = new TextField();
        this.vLanLabel = new Label();
        this.isInTrunkModeCheckBox = new CheckBox();
        this.editButton = new Button("Edit");
        this.cancelButton = new Button("Cancel");

        this.getChildren().add(firstLine);
        this.getChildren().add(secondLine);

        this.firstLine.getChildren().add(vLanTextField);
        this.firstLine.getChildren().add(vLanLabel);
        this.firstLine.getChildren().add(isInTrunkModeCheckBox);

        this.secondLine.getChildren().add(editButton);
        this.secondLine.getChildren().add(cancelButton);

        editButton.setOnMouseClicked(this::onEditButtonClicked);
        cancelButton.setOnMouseClicked(this::onCancelButtonClicked);
        isInTrunkModeCheckBox.setOnMouseClicked(this::onChangeModeCheckBoxClicked);

        this.isInEditMode = true;
        changeState();
    }

    private void changeState() {
        if (isInEditMode) {
            vLanTextField.setVisible(false);
            vLanTextField.setManaged(false);

            vLanLabel.setVisible(true);
            vLanLabel.setManaged(true);

            editButton.setText("Edit");
            cancelButton.setVisible(false);
            cancelButton.setManaged(false);
        } else {
            vLanTextField.setVisible(true);
            vLanTextField.setManaged(true);

            vLanLabel.setVisible(false);
            vLanLabel.setManaged(false);

            editButton.setText("Save");
            cancelButton.setVisible(true);
            cancelButton.setManaged(true);
        }
        isInEditMode = !isInEditMode;
        loadVLanId();
    }

    private void loadVLanId() {
        if (isInEditMode) {
            vLanTextField.setText(vLanValue);
        } else {
            vLanLabel.setText(vLanValue);
        }
    }

    public void setVLanId(int vLanId) {
        this.vLanValue = String.valueOf(vLanId);
        loadVLanId();
    }

    public void setTrunkMode(boolean isInTrunkMode) {
        this.isInTrunkModeCheckBox.setSelected(isInTrunkMode);
    }

    private void onEditButtonClicked(MouseEvent mouseEvent) {
        if (isInEditMode) {
            if (!Utils.isVLanIdValid(vLanTextField.getText())) {
                Engine.getInstance().logError(Values.ERROR_INVALID_VLAN_ID);
                return;
            }
            vLanValue = vLanTextField.getText();
            if (onSaveClickedListener != null) {
                onSaveClickedListener.onSaveClicked(Integer.parseInt(vLanValue));
            }
        }
        changeState();
    }

    private void onCancelButtonClicked(MouseEvent mouseEvent) {
        changeState();
    }

    private void onChangeModeCheckBoxClicked(MouseEvent mouseEvent) {
        onChangeModeClickedListener.onChangeModeClicked(isInTrunkModeCheckBox.isSelected());
    }

    public void show() {
        this.setVisible(true);
        this.setManaged(true);
    }

    public void hide() {
        this.setVisible(false);
        this.setManaged(false);
    }

    public void setOnChangeModeClickedListener(OnChangeModeClickedListener listener) {
        this.onChangeModeClickedListener = listener;
    }

    public void setOnSaveClickedListener(OnSaveClickedListener listener) {
        this.onSaveClickedListener = listener;
    }

    public interface OnChangeModeClickedListener {
        void onChangeModeClicked(boolean isInTrunkMode);
    }

    public interface OnSaveClickedListener {
        void onSaveClicked(int vLanId);
    }
}
