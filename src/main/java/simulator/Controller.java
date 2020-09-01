package simulator;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import simulator.element.Connection;
import simulator.element.Port;
import simulator.element.device.*;
import simulator.view.*;
import util.Values;
import view.CanvasPane;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    private BorderPane root;
    @FXML
    private Pane canvasPane;
    @FXML
    private VBox elementInfo;
    @FXML
    private EditableLabel elementInfoDeviceName;
    @FXML
    private VBox connectionsInfo;
    @FXML
    private TitleLabel elementInfoDeviceType;
    @FXML
    private Label elementInfoMacAddress;
    @FXML
    private IPTextField elementInfoIpAddress;
    @FXML
    private RoutingTable routingTableView;
    @FXML
    private TextField simulationSourceIPAddress;
    @FXML
    private TextField simulationDestinationIPAddress;
    @FXML
    private LogPanel logPanel;
    private Engine engine;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        CanvasPane canvas = new CanvasPane(canvasPane.getWidth(), canvasPane.getHeight());
        canvasPane.getChildren().add(canvas);
        engine = Engine.getInstance();
        engine.setController(this);
        engine.setGraphicsContext(canvas.getCanvas().getGraphicsContext2D());

        canvas.getCanvas().setOnMouseDragged(this::canvasMouseDragged);
        canvas.getCanvas().setOnMousePressed(this::canvasMousePressed);
        canvas.getCanvas().setOnMouseReleased(this::canvasMouseReleased);
        canvas.getCanvas().setOnMouseMoved(this::canvasMouseMoved);
        root.setOnKeyPressed(this::keyPressed);

        hideElementInfo();
    }

    @FXML
    private void handleEndDeviceButtonClick() {
        engine.addDevice(new EndDevice(Values.DEVICE_DEFAULT_POSITION, Values.DEVICE_DEFAULT_POSITION));
    }

    @FXML
    private void handleHubButtonClick() {
        engine.addDevice(new Hub(Values.DEVICE_DEFAULT_POSITION, Values.DEVICE_DEFAULT_POSITION));
    }

    @FXML
    private void handleSwitchButtonClick() {
        engine.addDevice(new Switch(Values.DEVICE_DEFAULT_POSITION, Values.DEVICE_DEFAULT_POSITION));
    }

    @FXML
    private void handleRouterButtonClick() {
        engine.addDevice(new Router(Values.DEVICE_DEFAULT_POSITION, Values.DEVICE_DEFAULT_POSITION));
    }

    @FXML
    private void handleFirewallButtonClick() {
        engine.addDevice(new Firewall(Values.DEVICE_DEFAULT_POSITION, Values.DEVICE_DEFAULT_POSITION));
    }

    @FXML
    private void handleRemoveElementButtonClick() {
        engine.removeSelectedElement();
    }

    @FXML
    private void handleConnectElementsButtonClick() {
        engine.onConnectClicked();
    }

    @FXML
    private void handleElementInfoMacAddressClick() {
        engine.copyToClipboard(elementInfoMacAddress.getText());
    }

    @FXML
    private void handleStartSimulationButtonClick() {
        //TODO: when sourceMac TextField focused you cannot use key shorcuts on canvas
        engine.startSimulation(simulationSourceIPAddress.getText(), simulationDestinationIPAddress.getText());
    }

    @FXML
    private void handleStopSimulationButtonClick() {
        engine.stopSimulation();
    }

    private void canvasMousePressed(MouseEvent mouseEvent) {
        engine.onMousePressed((int) mouseEvent.getX(), (int) mouseEvent.getY());
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            engine.selectDevice((int) mouseEvent.getX(), (int) mouseEvent.getY());
        }
    }

    private void canvasMouseDragged(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            engine.moveElement((int) mouseEvent.getX(), (int) mouseEvent.getY());
        } else if (mouseEvent.getButton() == MouseButton.MIDDLE) {
            engine.moveAll((int) mouseEvent.getX(), (int) mouseEvent.getY());
        }
    }

    private void canvasMouseReleased(MouseEvent mouseEvent) {
        engine.onMouseReleased();
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            engine.deselectDevice();
        }
    }

    private void canvasMouseMoved(MouseEvent mouseEvent) {
        if (engine.isInConnectionMode()) {
            engine.changeConnectMousePosition((int) mouseEvent.getX(), (int) mouseEvent.getY());
        }
    }

    private void keyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.DELETE) {
            engine.removeSelectedElement();
        } else if (keyEvent.getCode() == KeyCode.ESCAPE) {
            engine.dropSelection();
        } else if (keyEvent.getCode() == KeyCode.C) {
            engine.onConnectClicked();
        }
    }

    public void showDeviceInfo(Device selectedDevice) {
        elementInfo.setVisible(true);
        elementInfoDeviceType.setValue(selectedDevice.getDeviceType());
        elementInfoMacAddress.setText(selectedDevice.getMacAddress());
        if (selectedDevice instanceof EndDevice) {
            elementInfoIpAddress.show();
            elementInfoIpAddress.setIpAddress(((EndDevice) selectedDevice).getIpAddress());
            elementInfoIpAddress.setOnSaveClickedListener(((EndDevice) selectedDevice));
        } else {
            elementInfoIpAddress.hide();
        }
        if (selectedDevice instanceof Router) {
            Router router = (Router) selectedDevice;
            routingTableView.show();
            routingTableView.setEntryList(router.getRoutingTableCopy());
            routingTableView.setOnRoutingTableChangeListener(router);
        } else {
            routingTableView.hide();
        }
    }

    public void hideElementInfo() {
        elementInfo.setVisible(false);
    }

    public void showConnectionList(Device selectedDevice, List<Connection> connectionList) {
        connectionsInfo.getChildren().clear();
        for (Connection connection : connectionList) {
            Port other = connection.getOtherPort(selectedDevice.getPortList());
            ConnectionRowView connectionRowView = new ConnectionRowView(connection, other);
            connectionRowView.setOnDeleteClickListener(this::onDeleteConnection);
            connectionsInfo.getChildren().add(connectionRowView);
        }
        connectionsInfo.setVisible(true);
    }

    public void hideConnectionList() {
        connectionsInfo.setVisible(false);
    }

    private void onDeleteConnection(int id) {
        engine.removeConnection(id);
    }

    public void log(String text) {
        logPanel.log(text);
    }

    public void logError(String errorMessage) {
        logPanel.logError(errorMessage);
    }
}
