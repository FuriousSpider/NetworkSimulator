package simulator.view;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import simulator.Engine;
import simulator.element.device.additionalElements.Policy;
import util.Utils;
import util.Values;

import java.util.HashSet;
import java.util.Set;

public class FirewallPoliciesRow extends VBox {
    private final HBox firstLine;
    private final HBox secondLine;
    private final HBox thirdLine;
    private final HBox lastLine;
    private final Label sourceIpAddressTitleLabel;
    private final Label destinationIpAddressTitleLabel;
    private final Label sourceIpAddressLabel;
    private final Label destinationIpAddressLabel;
    private final TextField sourceIpAddressTextField;
    private final TextField destinationIpAddressTextField;
    private final VBox applicationView;
    private final Label thenRuleLabel;
    private final Button changeRuleButton;
    private final Button editButton;
    private final Button cancelButton;

    private String sourceIpAddressValue;
    private String destinationIpAddressValue;
    private Policy.Rule thenRuleValue;
    private int policyId;
    private int priorityNumber;

    private boolean isInEditMode;
    private OnSaveClickedListener onSaveClickedListener;

    public FirewallPoliciesRow(int priorityNumber) {
        this.firstLine = new HBox();
        this.secondLine = new HBox();
        this.thirdLine = new HBox();
        this.applicationView = new VBox();
        this.lastLine = new HBox();
        this.sourceIpAddressTitleLabel = new Label("Source network: ");
        this.destinationIpAddressTitleLabel = new Label("Destination network: ");
        this.sourceIpAddressLabel = new Label();
        this.destinationIpAddressLabel = new Label();
        this.sourceIpAddressTextField = new TextField();
        this.destinationIpAddressTextField = new TextField();
        this.thenRuleLabel = new Label();
        this.changeRuleButton = new Button("Change");
        this.editButton = new Button("Edit");
        this.cancelButton = new Button("Cancel");

        this.getChildren().add(firstLine);
        this.getChildren().add(secondLine);
        this.getChildren().add(thirdLine);
        this.getChildren().add(applicationView);
        this.getChildren().add(lastLine);

        this.firstLine.getChildren().add(sourceIpAddressTitleLabel);
        this.firstLine.getChildren().add(sourceIpAddressLabel);
        this.firstLine.getChildren().add(sourceIpAddressTextField);
        this.secondLine.getChildren().add(destinationIpAddressTitleLabel);
        this.secondLine.getChildren().add(destinationIpAddressLabel);
        this.secondLine.getChildren().add(destinationIpAddressTextField);
        this.thirdLine.getChildren().add(thenRuleLabel);
        this.thirdLine.getChildren().add(changeRuleButton);
        this.lastLine.getChildren().add(editButton);
        this.lastLine.getChildren().add(cancelButton);

        this.editButton.setOnMouseClicked(this::onEditButtonClicked);
        this.cancelButton.setOnMouseClicked(this::onCancelButtonClicked);
        this.changeRuleButton.setOnMouseClicked(this::onChangeRuleButtonClicked);

        this.priorityNumber = priorityNumber;
        this.isInEditMode = true;
        changeState();
    }

    private void changeState() {
        if (isInEditMode) {
            sourceIpAddressTextField.setVisible(false);
            sourceIpAddressTextField.setManaged(false);
            destinationIpAddressTextField.setVisible(false);
            destinationIpAddressTextField.setManaged(false);

            sourceIpAddressLabel.setVisible(true);
            sourceIpAddressLabel.setManaged(true);
            destinationIpAddressLabel.setVisible(true);
            destinationIpAddressLabel.setManaged(true);

            editButton.setText("Edit");
            cancelButton.setVisible(false);
            cancelButton.setManaged(false);
            changeRuleButton.setVisible(false);
            changeRuleButton.setManaged(false);
        } else {
            sourceIpAddressTextField.setVisible(true);
            sourceIpAddressTextField.setManaged(true);
            destinationIpAddressTextField.setVisible(true);
            destinationIpAddressTextField.setManaged(true);

            sourceIpAddressLabel.setVisible(false);
            sourceIpAddressLabel.setManaged(false);
            destinationIpAddressLabel.setVisible(false);
            destinationIpAddressLabel.setManaged(false);

            editButton.setText("Save");
            cancelButton.setVisible(true);
            cancelButton.setManaged(true);
            changeRuleButton.setVisible(true);
            changeRuleButton.setManaged(true);
        }
        for (Node node : applicationView.getChildren()) {
            if (node instanceof ApplicationRow) {
                ApplicationRow applicationRow = (ApplicationRow) node;
                applicationRow.changeState();
            }
        }
        isInEditMode = !isInEditMode;
        reloadData();
    }

    private void reloadData() {
        if (isInEditMode) {
            sourceIpAddressTextField.setText(sourceIpAddressValue);
            destinationIpAddressTextField.setText(destinationIpAddressValue);
        } else {
            sourceIpAddressLabel.setText(sourceIpAddressValue);
            destinationIpAddressLabel.setText(destinationIpAddressValue);
        }
        if (thenRuleValue != null) {
            thenRuleLabel.setText("Then rule: " + thenRuleValue.name());
        }
    }

    private void onEditButtonClicked(MouseEvent mouseEvent) {
        if (isInEditMode) {
            String source = sourceIpAddressTextField.getText();
            String destination = destinationIpAddressTextField.getText();

            if (source != null && !source.isBlank() && !Utils.isNetworkAddress(source)) {
                Engine.getInstance().logError(Values.ERROR_INVALID_NETWORK_IP_ADDRESS);
                return;
            }
            if (destination != null && !destination.isBlank() && !Utils.isNetworkAddress(destination)) {
                Engine.getInstance().logError(Values.ERROR_INVALID_NETWORK_IP_ADDRESS);
                return;
            }
            sourceIpAddressValue = source;
            destinationIpAddressValue = destination;

            if (onSaveClickedListener != null) {
                Policy policy = new Policy();
                policy.setId(policyId);
                if (source != null && !source.isBlank()) {
                    policy.setSourceNetworkAddress(source);
                }
                if (destination != null && !destination.isBlank()) {
                    policy.setDestinationNetworkAddress(destination);
                }

                Set<Policy.Application> applicationSet = new HashSet<>();
                for (Node node : applicationView.getChildren()) {
                    if (node instanceof ApplicationRow) {
                        ApplicationRow applicationRow = (ApplicationRow) node;
                        if (applicationRow.isSelected()) {
                            applicationSet.add(Policy.Application.valueOf(applicationRow.getApplicationName()));
                        }
                    }
                }
                policy.setApplicationSet(applicationSet);
                policy.setRule(thenRuleValue);

                onSaveClickedListener.onSaveClicked(policy);
            }
        }
        changeState();
    }

    private void onCancelButtonClicked(MouseEvent mouseEvent) {
        changeState();
    }

    private void onChangeRuleButtonClicked(MouseEvent mouseEvent) {
        if (thenRuleValue == Policy.Rule.DENY) {
            thenRuleValue = Policy.Rule.PERMIT;
        } else {
            thenRuleValue = Policy.Rule.DENY;
        }
        reloadData();
    }

    public void setPolicy(Policy policy) {
        this.policyId = policy.getId();

        if (policy.getSourceNetworkAddress() != null) {
            sourceIpAddressValue = policy.getSourceNetworkAddress();
        }
        if (policy.getDestinationNetworkAddress() != null) {
            destinationIpAddressValue = policy.getDestinationNetworkAddress();
        }

        for (Policy.Application application : Policy.Application.values()) {
            this.applicationView.getChildren().add(new ApplicationRow(application.name(), policy.getApplicationSet().contains(application)));
        }

        this.thenRuleValue = policy.getRule();
        reloadData();
    }

    public void setOnSaveClickedListener(OnSaveClickedListener listener) {
        this.onSaveClickedListener = listener;
    }

    public interface OnSaveClickedListener {
        void onSaveClicked(Policy updatedPolicy);
    }
}
