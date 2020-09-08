package simulator;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import simulator.element.Connection;
import simulator.element.device.*;
import simulator.element.device.additionalElements.History;
import simulator.element.device.additionalElements.Port;
import simulator.view.*;
import util.Values;
import view.CanvasPane;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable, PortListDialog.OnPortSelectedListener, SimulationView.OnStartSimulationButtonClickedListener, SimulationView.OnStopSimulationButtonClickedListener {
    @FXML
    private BorderPane root;
    @FXML
    private Pane canvasPane;
    @FXML
    private VBox elementInfo;
    @FXML
    private DeviceNameLabel elementInfoDeviceName;
    @FXML
    private VBox connectionsInfo;
    @FXML
    private DeviceTypeLabel elementInfoDeviceType;
    @FXML
    private Label elementInfoMacAddress;
    @FXML
    private VBox elementInfoIpAddressLayout;
    @FXML
    private Label elementInfoIpAddressTitleLabel;
    @FXML
    private IPTextField elementInfoIpAddress;
    @FXML
    private DefaultGatewayView elementDefaultGateway;
    @FXML
    private RoutingTable routingTableView;
    @FXML
    private FirewallPoliciesView firewallPoliciesView;
    @FXML
    private SwitchButtonsView elementSwitchButtonsView;
    @FXML
    private SimulationView simulationView;
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

        simulationView.setOnStartSimulationButtonClickedListener(this);
        simulationView.setOnStopSimulationButtonClickedListener(this);

        Tooltip.install(elementInfoIpAddressTitleLabel, new Tooltip("x.x.x.x/x"));

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
        Device selectedDevice = engine.getSelectedDevice();
        if (selectedDevice != null) {
            PortListDialog dialog = new PortListDialog();
            dialog.setPortList(selectedDevice.getPortList());
            dialog.setOnPortSelectedListener(this);
            dialog.start();
        }
    }

    @FXML
    private void handleElementInfoMacAddressClick() {
        engine.copyToClipboard(elementInfoMacAddress.getText());
    }

    @FXML
    private void handleMenuNewClick() {
        YesCancelDialog dialog = new YesCancelDialog(Alert.AlertType.CONFIRMATION);
        dialog.setTitle(Values.DIALOG_NEW_TITLE);
        dialog.setContentText(Values.DIALOG_NEW_CONTENT);
        dialog.showAndWait().ifPresent(type -> {
            if (type.getText().equals(ButtonType.YES.getText())) {
                DataManager.resetFile();
                logPanel.clear();
                engine.startNewProject();
            }
        });
    }

    @FXML
    private void handleMenuOpenClick() {
        YesCancelDialog dialog = new YesCancelDialog(Alert.AlertType.CONFIRMATION);
        dialog.setTitle(Values.DIALOG_OPEN_TITLE);
        dialog.setContentText(Values.DIALOG_OPEN_CONTENT);
        dialog.showAndWait().ifPresent(type -> {
            if (type.getText().equals(ButtonType.YES.getText())) {
                DataManager.open();
            }
        });
        logPanel.clear();
    }

    @FXML
    private void handleMenuSaveClick() {
        engine.saveData();
    }

    @FXML
    private void handleMenuExitClick() {
        Engine.stopEngine();
        Platform.exit();
    }

    @FXML
    private void handleMenuOptionsClick() {
        new OptionsDialog().show();
    }

    private void canvasMousePressed(MouseEvent mouseEvent) {
        releaseFocus();
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
            handleConnectElementsButtonClick();
        }
    }

    public void showDeviceInfo(Device selectedDevice) {
        elementInfo.setVisible(true);
        elementInfo.setManaged(true);
        elementInfoDeviceName.setDeviceName(selectedDevice.getDeviceName());
        elementInfoDeviceName.setOnSaveDeviceNameClickedListener(selectedDevice);
        elementInfoDeviceType.setValue(selectedDevice.getDeviceType());
        elementInfoMacAddress.setText(selectedDevice.getMacAddress());

        if (selectedDevice instanceof EndDevice) {
            elementInfoIpAddressLayout.setVisible(true);
            elementInfoIpAddressLayout.setManaged(true);
            elementInfoIpAddress.show();
            elementInfoIpAddress.setIpAddress(((EndDevice) selectedDevice).getIpAddress());
            elementInfoIpAddress.setOnSaveClickedListener(((EndDevice) selectedDevice));

            elementDefaultGateway.show();
            elementDefaultGateway.setDefaultGateway(((EndDevice) selectedDevice).getGateway());
            elementDefaultGateway.setOnDefaultGatewayChangeListener((EndDevice) selectedDevice);
        } else {
            elementInfoIpAddressLayout.setVisible(false);
            elementInfoIpAddressLayout.setManaged(false);
            elementInfoIpAddress.hide();
            elementDefaultGateway.hide();
        }

        if (selectedDevice instanceof Router) {
            Router router = (Router) selectedDevice;
            routingTableView.show();
            routingTableView.setEntryList(router.getRoutingTableCopy());
            routingTableView.setOnRoutingTableChangeListener(router);
        } else {
            routingTableView.hide();
        }

        if (selectedDevice instanceof Firewall) {
            Firewall firewall = (Firewall) selectedDevice;
            firewallPoliciesView.show();
            firewallPoliciesView.setPolicies(firewall.getPolicyList());
            firewallPoliciesView.setDefaultRule(firewall.getDefaultRule());
            firewallPoliciesView.setOnPoliciesListUpdatedListener(firewall);
        } else {
            firewallPoliciesView.hide();
        }

        if (selectedDevice instanceof Switch) {
            Switch sw = (Switch) selectedDevice;
            elementSwitchButtonsView.show();
            elementSwitchButtonsView.setMacTable(sw.getAssociationTable());
            elementSwitchButtonsView.setOnClearMacTableClickedListener(sw);
        } else {
            elementSwitchButtonsView.hide();
        }
    }

    public void hideElementInfo() {
        elementInfo.setVisible(false);
        elementInfo.setManaged(false);
        connectionsInfo.setVisible(false);
        routingTableView.hide();
        firewallPoliciesView.hide();
    }

    public void showConnectionList(Device selectedDevice, List<Connection> connectionList) {
        connectionsInfo.getChildren().clear();

        Label titleLabel = new Label("Connections");
        titleLabel.getStyleClass().add("titleLabel");
        connectionsInfo.getChildren().add(titleLabel);

        for (Connection connection : connectionList) {
            Port other = connection.getOtherPort(selectedDevice.getPortList());
            ConnectionRowView connectionRowView = new ConnectionRowView(connection, other, connectionList.indexOf(connection) == 0);
            connectionRowView.setOnDeleteClickListener(this::onDeleteConnection);
            connectionsInfo.getChildren().add(connectionRowView);
        }
        connectionsInfo.setVisible(true);
    }

    public void showDevicePortListDialog(Device device, PortListDialog.OnPortSelectedListener listener) {
        PortListDialog portListDialog = new PortListDialog();
        portListDialog.setPortList(device.getPortList());
        portListDialog.setOnPortSelectedListener(listener);
        portListDialog.start();
    }

    public void showResultDiagram(List<History> historyList) {
        SimulationResultDiagram simulationResultDiagram = new SimulationResultDiagram();
        simulationResultDiagram.setContent(historyList);
        simulationResultDiagram.start();
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

    private void releaseFocus() {
        canvasPane.requestFocus();
    }

    public void lockSimulationButton() {
        simulationView.lockSimulationButton();
    }

    public void unlockSimulationButton() {
        simulationView.unlockSimulationButton();
    }

    @Override
    public void onPortSelected(int portId) {
        engine.onConnectClicked(portId);
    }

    @Override
    public void onStartSimulationClicked() {
        engine.startSimulation(
                simulationView.getSourceIpAddress(),
                simulationView.getDestinationIpAddress(),
                simulationView.getApplication()
        );
    }

    @Override
    public void onStopSimulationClicked() {
        engine.stopSimulation();
    }
}
