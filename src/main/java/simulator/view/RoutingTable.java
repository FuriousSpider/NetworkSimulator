package simulator.view;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import simulator.Engine;
import util.Utils;
import util.Values;

import java.util.HashMap;
import java.util.Map;

public class RoutingTable extends GridPane {
    private final Label title;
    private final Label destinationNetworkLabel;
    private final Label nextHopLabel;
    private final VBox entryList;
    private final TextField newEntryNetworkTextField;
    private final TextField newEntryNextHopTextField;
    private final Button addNewEntry;

    private Map<String, String> routingTable;

    private OnRoutingTableChangeListener listener;

    public RoutingTable() {
        this.title = new Label("Routing table (static)");
        this.title.getStyleClass().add("titleLabel");
        this.destinationNetworkLabel = new Label("Destination network:");
        this.destinationNetworkLabel.getStyleClass().add("boldLabel");
        this.nextHopLabel = new Label("Next hop:");
        this.nextHopLabel.getStyleClass().add("boldLabel");
        this.entryList = new VBox();
        this.newEntryNetworkTextField = new TextField();
        this.newEntryNextHopTextField = new TextField();
        this.addNewEntry = new Button("Add");

        this.routingTable = new HashMap<>();

        this.add(title, 0, 0);
        this.add(entryList, 0, 1, 3, 1);
        this.add(destinationNetworkLabel, 0, 2);
        this.add(nextHopLabel, 1, 2);
        this.add(newEntryNetworkTextField, 0, 3);
        this.add(newEntryNextHopTextField, 1, 3);
        this.add(addNewEntry, 2, 3);

        this.addNewEntry.setOnMouseClicked(this::onAddButtonClicked);

        this.entryList.getChildren().clear();
        this.setVgap(4);
    }

    private void onAddButtonClicked(MouseEvent mouseEvent) {
        boolean areEntriesValid = true;
        if (Utils.isIpAddressWithoutMask(newEntryNetworkTextField.getText())) {
            areEntriesValid = false;
            Engine.getInstance().logError(Values.ERROR_INVALID_NETWORK_IP_ADDRESS);
        } else if (!Utils.isNetworkAddress(newEntryNetworkTextField.getText())) {
            areEntriesValid = false;
            Engine.getInstance().logError(Values.ERROR_ADDRESS_IS_NOT_A_NETWORK_ADDRESS);
        }
        if (!Utils.isIpAddressWithoutMask(newEntryNextHopTextField.getText())) {
            areEntriesValid = false;
            Engine.getInstance().logError(Values.ERROR_INVALID_IP_ADDRESS);
        }

        if (areEntriesValid) {
            this.routingTable.put(newEntryNetworkTextField.getText(), newEntryNextHopTextField.getText());
            this.listener.onRoutingTableChange(routingTable);
            setEntryList(routingTable);
            Engine.getInstance().log(Values.MESSAGE_RECORD_ADDED);
        }
    }

    public void setEntryList(Map<String, String> routingTable) {
        this.routingTable = routingTable;
        this.entryList.getChildren().clear();
        for (String key : routingTable.keySet()) {
            HBox line = new HBox();
            line.setSpacing(10);
            Label label = new Label(key + " - via: " + routingTable.get(key));
            Button button = new Button("Remove");
            button.setOnMouseClicked(this::onRemoveEntryClicked);
            button.setId(key);
            line.getChildren().add(label);
            line.getChildren().add(button);
            entryList.getChildren().add(line);
        }
    }

    public void setOnRoutingTableChangeListener(OnRoutingTableChangeListener listener) {
        this.listener = listener;
    }

    public void show() {
        this.setVisible(true);
        this.setManaged(true);
    }

    public void hide() {
        this.setVisible(false);
        this.setManaged(false);
    }

    private void onRemoveEntryClicked(MouseEvent mouseEvent) {
        routingTable.remove(((Button) mouseEvent.getSource()).getId());
        listener.onRoutingTableChange(routingTable);
        setEntryList(routingTable);
    }

    public interface OnRoutingTableChangeListener {
        void onRoutingTableChange(Map<String, String> routingTable);
    }
}
