package simulator.view;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import simulator.element.device.additionalElements.Policy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FirewallPoliciesView extends GridPane implements FirewallPoliciesRow.OnSaveClickedListener {
    private final Label titleLabel;
    private final VBox firewallPoliciesView;
    private final Button addNewPolicyButton;
    private final Label defaultRuleTitleLabel;
    private final Label defaultRuleValueLabel;
    private final Button changeDefaultRuleButton;

    private List<Policy> policyList;
    private Policy.Rule defaultRuleValue;
    private OnPoliciesListUpdatedListener onPoliciesListUpdatedListener;

    public FirewallPoliciesView() {
        this.titleLabel = new Label("Policies");
        this.titleLabel.getStyleClass().add("titleLabel");
        this.firewallPoliciesView = new VBox();
        this.addNewPolicyButton = new Button("Add new policy");
        this.defaultRuleTitleLabel = new Label("Default rule: ");
        this.defaultRuleTitleLabel.getStyleClass().add("boldLabel");
        this.defaultRuleValueLabel = new Label();
        this.changeDefaultRuleButton = new Button("Change");

        this.policyList = new ArrayList<>();

        this.add(titleLabel, 0, 0);
        this.add(firewallPoliciesView, 0, 1, 3, 1);
        this.add(defaultRuleTitleLabel, 0, 2);
        this.add(defaultRuleValueLabel, 1, 2);
        this.add(changeDefaultRuleButton, 2, 2);
        this.add(addNewPolicyButton, 0, 3);

        this.addNewPolicyButton.setOnMouseClicked(this::onAddNewPolicyButtonClicked);
        this.changeDefaultRuleButton.setOnMouseClicked(this::onChangeDefaultRuleButtonClicked);

        firewallPoliciesView.setSpacing(4);

        this.setVgap(4);
        this.setHgap(10);
    }

    public void setPolicies(List<Policy> policyList) {
        this.policyList.clear();
        this.policyList.addAll(policyList);

        refreshPolicies();
    }

    public void setDefaultRule(Policy.Rule rule) {
        this.defaultRuleValue = rule;
        this.defaultRuleValueLabel.setText(rule.name());
    }

    private void refreshPolicies() {
        this.firewallPoliciesView.getChildren().clear();
        for (Policy policy : policyList) {
            GridPane row = new GridPane();

            int priorityNumber = policyList.indexOf(policy);
            FirewallPoliciesRow firewallPoliciesRow = new FirewallPoliciesRow(priorityNumber);
            firewallPoliciesRow.setPolicy(policy);
            firewallPoliciesRow.setOnSaveClickedListener(this);
            row.add(firewallPoliciesRow, 0, 0);

            if (priorityNumber != 0) {
                Button upButton = new Button("Up");
                upButton.setId(String.valueOf(priorityNumber));
                upButton.setOnMouseClicked(this::onUpButtonClicked);
                row.add(upButton, 1, 0);
            }

            if (priorityNumber != policyList.size() - 1) {
                Button downButton = new Button("Down");
                downButton.setId(String.valueOf(priorityNumber));
                downButton.setOnMouseClicked(this::onDownButtonClicked);
                row.add(downButton, 2, 0);
            }

            Button removeButton = new Button("Remove");
            removeButton.setId(String.valueOf(policy.getId()));
            removeButton.setOnMouseClicked(this::onRemoveButtonClicked);

            row.add(removeButton, 3, 0);
            firewallPoliciesView.getChildren().add(row);

            Pane pane = new Pane();
            pane.setMinHeight(1);
            pane.setMaxHeight(1);
            pane.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
            firewallPoliciesView.getChildren().add(pane);
        }
        if (defaultRuleValue != null) {
            defaultRuleValueLabel.setText(defaultRuleValue.name());
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
