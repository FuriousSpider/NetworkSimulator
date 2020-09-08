package simulator.view;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import util.Utils;

import java.util.ArrayList;
import java.util.List;

public class TrunkModeAllowedVlan extends GridPane implements TrunkModeAllowedVlanRow.OnRemoveButtonClickedListener {
    private final Label allowedVlanTitleLabel;
    private final VBox allowedVlanLayout;
    private final TextField allowedVlanTextField;
    private final Button addNewAllowedVlanButton;

    private final List<Integer> allowedVLanList;
    private List<OnAllowedVLanChangeListener> onAllowedVLanChangeListenerList;

    public TrunkModeAllowedVlan() {
        this.allowedVlanTitleLabel = new Label("Handle vlan:");
        this.allowedVlanTitleLabel.getStyleClass().add("boldLabel");
        this.allowedVlanTitleLabel.setTooltip(new Tooltip("1 - 1001"));
        this.allowedVlanLayout = new VBox();
        this.allowedVlanTextField = new TextField();
        this.allowedVlanTextField.setMaxWidth(50);
        this.addNewAllowedVlanButton = new Button("Add");

        this.add(allowedVlanTitleLabel, 0, 0);
        this.add(allowedVlanLayout, 0, 1);
        this.add(allowedVlanTextField, 0, 2);
        this.add(addNewAllowedVlanButton, 1, 2);

        this.allowedVLanList = new ArrayList<>();

        this.setHgap(10);
        this.setVgap(4);

        this.addNewAllowedVlanButton.setOnMouseClicked(this::onAddButtonClicked);
    }

    public void setAllowedVlanList(List<Integer> allowedVLanList) {
        this.allowedVLanList.clear();
        this.allowedVLanList.addAll(allowedVLanList);
        reloadVlanList();
    }

    private void reloadVlanList() {
        allowedVlanLayout.getChildren().clear();
        for (Integer id : allowedVLanList) {
            TrunkModeAllowedVlanRow row = new TrunkModeAllowedVlanRow();
            row.setVLanId(id);
            row.setOnRemoveButtonClickedListener(this);
            allowedVlanLayout.getChildren().add(row);
        }
    }

    private void onAddButtonClicked(MouseEvent mouseEvent) {
        if (Utils.isVLanIdValid(allowedVlanTextField.getText()) && !allowedVLanList.contains(Integer.parseInt(allowedVlanTextField.getText()))) {
            allowedVLanList.add(Integer.parseInt(allowedVlanTextField.getText()));
            for (OnAllowedVLanChangeListener listener : onAllowedVLanChangeListenerList) {
                listener.onAllowedVLanChange(allowedVLanList);
            }
            reloadVlanList();
        }
    }

    public void setOnAllowedVLanChangeListener(List<OnAllowedVLanChangeListener> listenerList) {
        this.onAllowedVLanChangeListenerList = listenerList;
    }

    public void show() {
        this.setVisible(true);
        this.setManaged(true);
    }

    public void hide() {
        this.setVisible(false);
        this.setManaged(false);
    }

    @Override
    public void onRemoveClicked(Integer vLanId) {
        allowedVLanList.remove(vLanId);
        for (OnAllowedVLanChangeListener listener : onAllowedVLanChangeListenerList) {
            listener.onAllowedVLanChange(allowedVLanList);
        }
        reloadVlanList();
    }

    public interface OnAllowedVLanChangeListener {
        void onAllowedVLanChange(List<Integer> allowedVLanList);
    }
}
