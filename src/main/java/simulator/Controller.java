package simulator;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import simulator.element.*;
import simulator.view.ConnectionRowView;
import util.Values;
import view.CanvasPane;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    private BorderPane root;
    @FXML
    private Pane canvasPane;
    @FXML
    private GridPane elementInfo;
    @FXML
    private VBox connectionsInfo;
    @FXML
    private Label elementInfoDeviceType;
    private Engine engine;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        CanvasPane canvas = new CanvasPane(canvasPane.getWidth(), canvasPane.getHeight());
        canvasPane.getChildren().add(canvas);
        engine = new Engine(this, canvas.getCanvas().getGraphicsContext2D());

        canvas.getCanvas().setOnMouseDragged(this::canvasMouseDragged);
        canvas.getCanvas().setOnMousePressed(this::canvasMousePressed);
        canvas.getCanvas().setOnMouseReleased(this::canvasMouseReleased);
        root.setOnKeyPressed(this::keyPressed);

        hideElementInfo();
    }

    @FXML
    private void handleEndDeviceButtonClick() {
        engine.addDevice(new EndDevice(Values.ELEMENT_DEFAULT_POSITION, Values.ELEMENT_DEFAULT_POSITION));
    }

    @FXML
    private void handleHubButtonClick() {
        engine.addDevice(new Hub(Values.ELEMENT_DEFAULT_POSITION, Values.ELEMENT_DEFAULT_POSITION));
    }

    @FXML
    private void handleSwitchButtonClick() {
        engine.addDevice(new Switch(Values.ELEMENT_DEFAULT_POSITION, Values.ELEMENT_DEFAULT_POSITION));
    }

    @FXML
    private void handleRouterButtonClick() {
        engine.addDevice(new Router(Values.ELEMENT_DEFAULT_POSITION, Values.ELEMENT_DEFAULT_POSITION));
    }

    @FXML
    private void handleFirewallButtonClick() {
        engine.addDevice(new Firewall(Values.ELEMENT_DEFAULT_POSITION, Values.ELEMENT_DEFAULT_POSITION));
    }

    @FXML
    private void handleRemoveElementButtonClick() {
        engine.removeSelectedElement();
    }

    @FXML
    private void handleConnectElementsButtonClick() {
        engine.onConnectClicked();
    }

    private void canvasMousePressed(MouseEvent mouseEvent) {
        engine.onMousePressed((int) mouseEvent.getX(), (int) mouseEvent.getY());
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            engine.selectElement((int) mouseEvent.getX(), (int) mouseEvent.getY());
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
            engine.deselectElement();
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

    public void showElementInfo(Element selectedElement) {
        elementInfo.setVisible(true);
        elementInfoDeviceType.setText(selectedElement.getDeviceType());
    }

    public void hideElementInfo() {
        elementInfo.setVisible(false);
    }

    public void showConnectionList(int selectedId, List<Connection> connectionList) {
        connectionsInfo.getChildren().clear();
        for (Connection connection : connectionList) {
            int id;
            if (selectedId != connection.getFirstId()) {
                id = connection.getFirstId();
            } else {
                id = connection.getSecondId();
            }
            ConnectionRowView connectionRowView = new ConnectionRowView(id, engine.getElementById(id).getDeviceType(), connection.getColor());
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
}
