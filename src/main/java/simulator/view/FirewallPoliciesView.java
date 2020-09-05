package simulator.view;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import simulator.element.device.additionalElements.Policy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FirewallPoliciesView extends VBox implements FirewallPoliciesRow.OnSaveClickedListener {
    private final HBox firstLine;
    private final HBox secondLine;
    private final HBox thirdLine;
    private final VBox firewallPoliciesView;
    private final Button addNewPolicyButton;
    private final Label defaultRuleLabel;
    private final Button changeDefaultRuleButton;

    private List<Policy> policyList;
    private Policy.Rule defaultRuleValue;
    private OnPoliciesListUpdatedListener onPoliciesListUpdatedListener;

    public FirewallPoliciesView() {
        this.firstLine = new HBox();
        this.secondLine = new HBox();
        this.thirdLine = new HBox();
        this.firewallPoliciesView = new VBox();
        this.addNewPolicyButton = new Button("Add new policy");
        this.defaultRuleLabel = new Label("Default rule: ");
        this.changeDefaultRuleButton = new Button("Change");

        this.policyList = new ArrayList<>();

        this.getChildren().add(firstLine);
        this.getChildren().add(secondLine);
        this.getChildren().add(thirdLine);
        this.firstLine.getChildren().add(firewallPoliciesView);
        this.secondLine.getChildren().add(defaultRuleLabel);
        this.secondLine.getChildren().add(changeDefaultRuleButton);
        this.thirdLine.getChildren().add(addNewPolicyButton);

        this.addNewPolicyButton.setOnMouseClicked(this::onAddNewPolicyButtonClicked);
        this.changeDefaultRuleButton.setOnMouseClicked(this::onChangeDefaultRuleButtonClicked);
    }

    public void setPolicies(List<Policy> policyList) {
        this.policyList.clear();
        this.policyList.addAll(policyList);

        refreshPolicies();
    }

    public void setDefaultRule(Policy.Rule rule) {
        this.defaultRuleValue = rule;
        this.defaultRuleLabel.setText("Default rule: " + rule.name());
    }

    private void refreshPolicies() {
        this.firewallPoliciesView.getChildren().clear();
        for (Policy policy : policyList) {
            HBox row = new HBox();

            int priorityNumber = policyList.indexOf(policy);
            FirewallPoliciesRow firewallPoliciesRow = new FirewallPoliciesRow(priorityNumber);
            firewallPoliciesRow.setPolicy(policy);
            firewallPoliciesRow.setOnSaveClickedListener(this);
            row.getChildren().add(firewallPoliciesRow);

            if (priorityNumber != 0) {
                Button upButton = new Button("Up");
                upButton.setId(String.valueOf(priorityNumber));
                upButton.setOnMouseClicked(this::onUpButtonClicked);
                row.getChildren().add(upButton);
            }

            if (priorityNumber != policyList.size() - 1) {
                Button downButton = new Button("Down");
                downButton.setId(String.valueOf(priorityNumber));
                downButton.setOnMouseClicked(this::onDownButtonClicked);
                row.getChildren().add(downButton);
            }

            Button removeButton = new Button("Remove");
            removeButton.setId(String.valueOf(policy.getId()));
            removeButton.setOnMouseClicked(this::onRemoveButtonClicked);

            row.getChildren().add(removeButton);
            firewallPoliciesView.getChildren().add(row);
        }
        if (defaultRuleValue != null) {
            defaultRuleLabel.setText("Default rule: " + defaultRuleValue.name());
        }
    }



    private void onAddNewPolicyButtonClicked(MouseEvent mouseEvent) {
        policyList.add(new Policy());
        refreshPolicies();
    }

    private void onChangeDefaultRuleButtonClicked(MouseEvent mouseEvent) {
        if (defaultRuleValue == Policy.Rule.DENY) {
            defaultRuleValue = Policy.Rule.PERMIT;
        } else {
            defaultRuleValue = Policy.Rule.DENY;
        }
        policyListChanged();
    }

    private void onUpButtonClicked(MouseEvent mouseEvent) {
        int id = Integer.parseInt(((Button)mouseEvent.getSource()).getId());
        Collections.swap(policyList, id - 1, id);
        policyListChanged();
    }

    private void onDownButtonClicked(MouseEvent mouseEvent) {
        int id = Integer.parseInt(((Button)mouseEvent.getSource()).getId());
        Collections.swap(policyList, id, id + 1);
        policyListChanged();
    }

    private void onRemoveButtonClicked(MouseEvent mouseEvent) {
        int id = Integer.parseInt(((Button) mouseEvent.getSource()).getId());
        removePolicy(id);
        policyListChanged();
    }

    private void removePolicy(int policyId) {
        for (int i = 0; i < policyList.size(); i++) {
            if (policyList.get(i).getId() == policyId) {
                policyList.remove(i);
                break;
            }
        }
    }

    private void policyListChanged() {
        if (onPoliciesListUpdatedListener != null) {
            onPoliciesListUpdatedListener.onPoliciesListUpdated(policyList, defaultRuleValue);
        }
        refreshPolicies();
    }

    public void setOnPoliciesListUpdatedListener(OnPoliciesListUpdatedListener listener) {
        this.onPoliciesListUpdatedListener = listener;
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
    public void onSaveClicked(Policy updatedPolicy) {
        for (Policy value : policyList) {
            if (value.getId() == updatedPolicy.getId()) {
                value.setSourceNetworkAddress(updatedPolicy.getSourceNetworkAddress());
                value.setDestinationNetworkAddress(updatedPolicy.getDestinationNetworkAddress());
                value.setApplicationSet(updatedPolicy.getApplicationSet());
                value.setRule(updatedPolicy.getRule());
                break;
            }
        }
        policyListChanged();
    }

    public interface OnPoliciesListUpdatedListener {
        void onPoliciesListUpdated(List<Policy> policyList, Policy.Rule defaultRule);
    }
}
