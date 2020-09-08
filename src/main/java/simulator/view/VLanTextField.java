package simulator.view;

import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import simulator.Engine;
import util.Utils;
import util.Values;

import java.util.List;

public class VLanTextField extends GridPane {

    private final Label vLanIdTitleLabel;
    private final TextField vLanTextField;
    private final Label vLanLabel;
    private final Label trunkModeLabel;
    private final CheckBox isInTrunkModeCheckBox;
    private final TrunkModeAllowedVlan trunkModeAllowedVLANS;
    private final Button editButton;
    private final Button cancelButton;
    private final HBox buttonLine;

    private String vLanValue;
    private boolean isInEditMode;

    private List<OnChangeModeClickedListener> onChangeModeClickedListenerList;
    private OnSaveClickedListener onSaveClickedListener;

    public VLanTextField() {
        this.vLanTextField = new TextField();
        this.vLanIdTitleLabel = new Label("ID:");
        this.vLanLabel = new Label();
        this.trunkModeLabel = new Label("TRUNK");
        this.isInTrunkModeCheckBox = new CheckBox();
        this.trunkModeAllowedVLANS = new TrunkModeAllowedVlan();
        this.editButton = new Button("Edit");
        this.cancelButton = new Button("Cancel");
        this.buttonLine = new HBox();

        this.vLanTextField.setMaxWidth(50);
        this.vLanLabel.setMaxWidth(50);

        this.add(vLanIdTitleLabel, 0, 0);
        this.add(vLanTextField, 1, 0);
        this.add(vLanLabel, 1, 0);

        this.add(trunkModeLabel, 0, 0, 2, 1);

        this.add(new Label("Trunk mode:"), 0, 1);
        this.add(isInTrunkModeCheckBox, 1, 1);

        this.add(trunkModeAllowedVLANS, 0, 2, 2, 1);

        this.buttonLine.getChildren().add(editButton);
        this.buttonLine.getChildren().add(cancelButton);

        this.add(buttonLine, 0, 3, 2, 1);

        editButton.setOnMouseClicked(this::onEditButtonClicked);
        cancelButton.setOnMouseClicked(this::onCancelButtonClicked);
        isInTrunkModeCheckBox.setOnMouseClicked(this::onChangeModeCheckBoxClicked);

        this.setHgap(5);

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
        if (isInTrunkMode) {
            trunkModeAllowedVLANS.show();
        } else {
            trunkModeAllowedVLANS.hide();
        }
        showTrunkId();
    }

    public void setTrunkModeAllowedVLANS(List<Integer> vLanList) {
        this.trunkModeAllowedVLANS.setAllowedVlanList(vLanList);
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
        for (OnChangeModeClickedListener listener : onChangeModeClickedListenerList) {
            listener.onChangeModeClicked(isInTrunkModeCheckBox.isSelected());
        }
        if (isInTrunkModeCheckBox.isSelected()) {
            trunkModeAllowedVLANS.show();
        } else {
            trunkModeAllowedVLANS.hide();
        }
        showTrunkId();
    }

    private void showTrunkId() {
        if (isInTrunkModeCheckBox.isSelected()) {
            trunkModeLabel.setVisible(true);
            trunkModeLabel.setManaged(true);

            vLanLabel.setVisible(false);
            vLanLabel.setManaged(false);
            vLanTextField.setVisible(false);
            vLanTextField.setManaged(false);
            vLanIdTitleLabel.setVisible(false);
            vLanIdTitleLabel.setManaged(false);
        } else {
            trunkModeLabel.setVisible(false);
            trunkModeLabel.setManaged(false);

            vLanIdTitleLabel.setVisible(true);
            vLanIdTitleLabel.setManaged(true);
            if (isInEditMode) {
                vLanTextField.setVisible(true);
                vLanTextField.setManaged(true);
            } else {
                vLanLabel.setVisible(true);
                vLanLabel.setManaged(true);
            }
        }
    }

    public void show() {
        this.setVisible(true);
        this.setManaged(true);
    }

    public void hide() {
        this.setVisible(false);
        this.setManaged(false);
    }

    public void setOnChangeModeClickedListenerList(List<OnChangeModeClickedListener> listenerList) {
        this.onChangeModeClickedListenerList = listenerList;
    }

    public void setOnSaveClickedListener(OnSaveClickedListener listener) {
        this.onSaveClickedListener = listener;
    }

    public void setOnAllowedVlanChangeListener(List<TrunkModeAllowedVlan.OnAllowedVLanChangeListener> listenerList) {
        trunkModeAllowedVLANS.setOnAllowedVLanChangeListener(listenerList);
    }

    public interface OnChangeModeClickedListener {
        void onChangeModeClicked(boolean isInTrunkMode);
    }

    public interface OnSaveClickedListener {
        void onSaveClicked(int vLanId);
    }
}
