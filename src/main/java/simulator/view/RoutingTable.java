package simulator.view;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import simulator.Engine;
import util.Utils;
import util.Values;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class RoutingTable extends VBox {

    private Label title;
    private VBox entryList;
    private HBox newEntryBox;
    private TextField newEntryNetworkTextField;
    private TextField newEntryNextHopTextField;
    private Button addNewEntry;

    private Map<String, String> routingTable;

    private OnRoutingTableChangeListener listener;

    public RoutingTable() {
        this.title = new Label("Routing table");
        this.entryList = new VBox();
        this.newEntryBox = new HBox();
        this.newEntryNetworkTextField = new TextField();
        this.newEntryNextHopTextField = new TextField();
        this.addNewEntry = new Button("Add");

        this.routingTable = new HashMap<>();

        this.getChildren().add(title);
        this.getChildren().add(entryList);
        this.getChildren().add(newEntryBox);
        this.getChildren().add(addNewEntry);

        this.newEntryBox.getChildren().add(newEntryNetworkTextField);
        this.newEntryBox.getChildren().add(newEntryNextHopTextField);

        this.addNewEntry.setOnMouseClicked(this::onAddButtonClicked);

        this.entryList.getChildren().clear();
    }

    private void onAddButtonClicked(MouseEvent mouseEvent) {
        Pattern ipv4Pattern = Pattern.compile(Values.REGEX_IP_ADDRESS_WITH_MASK);
        boolean areEntriesValid = true;
        if (!ipv4Pattern.matcher(newEntryNetworkTextField.getText()).matches()) {
            areEntriesValid = false;
            Engine.getInstance().logError(Values.ERROR_INVALID_NETWORK_IP_ADDRESS);
        } else if (!Utils.isNetworkAddress(newEntryNetworkTextField.getText())) {
            areEntriesValid = false;
            Engine.getInstance().logError(Values.ERROR_ADDRESS_IS_NOT_A_NETWORK_ADDRESS);
        }
        if (!ipv4Pattern.matcher(newEntryNextHopTextField.getText()).matches()) {
            areEntriesValid = false;
            Engine.getInstance().logError(Values.ERROR_INVALID_IP_ADDRESS);
        } else if (Utils.isNetworkAddress(newEntryNextHopTextField.getText())) {
            areEntriesValid = false;
            Engine.getInstance().logError(Values.ERROR_ADDRESS_IS_NOT_A_HOST_ADDRESS);
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
            Label label = new Label(key + ", next hop: " + routingTable.get(key));
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
