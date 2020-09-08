package simulator.view;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import simulator.Engine;
import simulator.element.device.additionalElements.Policy;
import util.Utils;
import util.Values;

import java.util.HashSet;
import java.util.Set;

public class FirewallPoliciesRow extends GridPane {
    private final Label priorityLabel;
    private final Label sourceIpAddressTitleLabel;
    private final Label destinationIpAddressTitleLabel;
    private final Label sourceIpAddressLabel;
    private final Label destinationIpAddressLabel;
    private final TextField sourceIpAddressTextField;
    private final TextField destinationIpAddressTextField;
    private final Label applicationTitleLabel;
    private final VBox applicationView;
    private final Label thenRuleTitleLabel;
    private final Label thenRuleValueLabel;
    private final Button changeRuleButton;
    private final HBox thenRuleLayout;
    private final HBox buttonLine;
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
        this.priorityLabel = new Label((priorityNumber + 1) + ".");
        this.priorityLabel.getStyleClass().add("boldLabel");
        this.sourceIpAddressTitleLabel = new Label("Source network: ");
        this.sourceIpAddressTitleLabel.getStyleClass().add("boldLabel");
        this.destinationIpAddressTitleLabel = new Label("Destination network: ");
        this.destinationIpAddressTitleLabel.getStyleClass().add("boldLabel");
        this.sourceIpAddressLabel = new Label();
        this.destinationIpAddressLabel = new Label();
        this.sourceIpAddressTextField = new TextField();
        this.destinationIpAddressTextField = new TextField();
        this.applicationTitleLabel = new Label("Applications:");
        this.applicationTitleLabel.getStyleClass().add("boldLabel");
        this.applicationView = new VBox();
        this.thenRuleTitleLabel = new Label("Then rule:");
        this.thenRuleTitleLabel.getStyleClass().add("boldLabel");
        this.thenRuleValueLabel = new Label();
        this.thenRuleLayout = new HBox();
        this.changeRuleButton = new Button("Change");
        this.buttonLine = new HBox();
        this.editButton = new Button("Edit");
        this.cancelButton = new Button("Cancel");

        this.add(priorityLabel, 0, 0, 1, 6);
        this.add(sourceIpAddressTitleLabel, 1, 1);
        this.add(sourceIpAddressLabel, 2, 1);
        this.add(sourceIpAddressTextField, 2, 1);
        this.add(destinationIpAddressTitleLabel, 1, 2);
        this.add(destinationIpAddressLabel, 2, 2);
        this.add(destinationIpAddressTextField, 2, 2);
        this.add(thenRuleTitleLabel, 1, 3);
        this.add(thenRuleLayout, 2, 3);
        this.add(applicationTitleLabel, 1, 4);
        this.add(applicationView, 2, 4, 2, 1);
        this.add(buttonLine, 1, 5, 2, 1);

        this.buttonLine.getChildren().add(editButton);
        this.buttonLine.getChildren().add(cancelButton);

        this.thenRuleLayout.getChildren().add(thenRuleValueLabel);
        this.thenRuleLayout.getChildren().add(changeRuleButton);
        this.thenRuleLayout.setSpacing(10);

        this.editButton.setOnMouseClicked(this::onEditButtonClicked);
        this.cancelButton.setOnMouseClicked(this::onCancelButtonClicked);
        this.changeRuleButton.setOnMouseClicked(this::onChangeRuleButtonClicked);

        this.setHgap(10);
        this.setVgap(4);

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
            thenRuleValueLabel.setText(thenRuleValue.name());
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
