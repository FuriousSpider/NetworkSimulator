package simulator.view;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import simulator.Engine;
import util.Utils;
import util.Values;


public class DefaultGatewayView extends GridPane {
    private final Label defaultGatewayTitleLabel;
    private final Label defaultGatewayLabel;
    private final TextField defaultGatewayTextField;
    private final Button editButton;
    private final Button cancelButton;

    private String defaultGateway;
    private boolean isInEditMode;

    private OnDefaultGatewayChangeListener onDefaultGatewayChangeListener;

    public DefaultGatewayView() {
        this.defaultGatewayTitleLabel = new Label("Gateway: ");
        this.defaultGatewayLabel = new Label();
        this.defaultGatewayTextField = new TextField();
        this.editButton = new Button("Change");
        this.cancelButton = new Button("Cancel");

        this.editButton.setOnMouseClicked(this::onEditButtonClicked);
        this.cancelButton.setOnMouseClicked(this::onCancelButtonClicked);

        this.setHgap(10);

        this.add(defaultGatewayTitleLabel, 0, 0);
        this.add(defaultGatewayLabel, 1, 0);
        this.add(defaultGatewayTextField, 1, 0);

        this.add(editButton, 0, 1);
        this.add(cancelButton, 1, 1);

        this.defaultGatewayTitleLabel.getStyleClass().add("boldLabel");

        this.defaultGatewayTitleLabel.setTooltip(new Tooltip("x.x.x.x"));

        this.isInEditMode = true;
        changeState();
    }

    private void changeState() {
        if (isInEditMode) {
            defaultGatewayLabel.setVisible(true);
            defaultGatewayLabel.setManaged(true);

            defaultGatewayTextField.setVisible(false);
            defaultGatewayTextField.setManaged(false);

            cancelButton.setVisible(false);
            editButton.setText("Edit");
        } else {
            defaultGatewayLabel.setVisible(false);
            defaultGatewayLabel.setManaged(false);

            defaultGatewayTextField.setVisible(true);
            defaultGatewayTextField.setManaged(true);

            cancelButton.setVisible(true);
            editButton.setText("Save");
        }
        isInEditMode = !isInEditMode;
    }

    private void reloadData() {
        if (isInEditMode) {
            defaultGatewayTextField.setText(defaultGateway);
        } else {
            defaultGatewayLabel.setText(defaultGateway);
        }
    }

    private void onEditButtonClicked(MouseEvent mouseEvent) {
        if (isInEditMode) {
            if (defaultGatewayTextField.getText().isEmpty() || Utils.isIpAddressWithoutMask(defaultGatewayTextField.getText())) {
                defaultGateway = defaultGatewayTextField.getText();
                if (onDefaultGatewayChangeListener != null) {
                    onDefaultGatewayChangeListener.onDefaultGatewayChanged(defaultGateway);
                }
                changeState();
                reloadData();
            } else {
                Engine.getInstance().logError(Values.ERROR_INVALID_IP_ADDRESS);
            }
        } else {
            changeState();
            reloadData();
        }
    }

    private void onCancelButtonClicked(MouseEvent mouseEvent) {
        changeState();
    }

    public void setDefaultGateway(String defaultGateway) {
        this.defaultGateway = defaultGateway;
        reloadData();
    }

    public void setOnDefaultGatewayChangeListener(OnDefaultGatewayChangeListener listener) {
        this.onDefaultGatewayChangeListener = listener;
    }

    public void show() {
        this.setVisible(true);
        this.setManaged(true);
    }

    public void hide() {
        this.setVisible(false);
        this.setManaged(false);
    }

    public interface OnDefaultGatewayChangeListener {
        void onDefaultGatewayChanged(String defaultGateway);
    }

}
