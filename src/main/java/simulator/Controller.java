package simulator;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import simulator.element.*;
import util.Values;
import view.CanvasPane;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    private Pane canvasPane;

    private Engine engine;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        CanvasPane canvas = new CanvasPane(canvasPane.getWidth(), canvasPane.getHeight());
        canvasPane.getChildren().add(canvas);
        engine = new Engine(canvas.getCanvas().getGraphicsContext2D());

        canvas.getCanvas().setOnMouseDragged(this::canvasMouseDragged);
        canvas.getCanvas().setOnMousePressed(this::canvasMousePressed);
        canvas.getCanvas().setOnMouseReleased(this::canvasMouseReleased);
    }

    @FXML
    private void handleEndDeviceButtonClick(ActionEvent event) {
        engine.addDevice(new EndDevice(Values.ELEMENT_DEFAULT_POSITION, Values.ELEMENT_DEFAULT_POSITION));
    }

    @FXML
    private void handleHubButtonClick(ActionEvent event) {
        engine.addDevice(new Hub(Values.ELEMENT_DEFAULT_POSITION, Values.ELEMENT_DEFAULT_POSITION));
    }

    @FXML
    private void handleSwitchButtonClick(ActionEvent event) {
        engine.addDevice(new Switch(Values.ELEMENT_DEFAULT_POSITION, Values.ELEMENT_DEFAULT_POSITION));
    }

    @FXML
    private void handleRouterButtonClick(ActionEvent event) {
        engine.addDevice(new Router(Values.ELEMENT_DEFAULT_POSITION, Values.ELEMENT_DEFAULT_POSITION));
    }

    @FXML
    private void handleFirewallButtonClick(ActionEvent event) {
        engine.addDevice(new Firewall(Values.ELEMENT_DEFAULT_POSITION, Values.ELEMENT_DEFAULT_POSITION));
    }

    private void canvasMousePressed(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            engine.selectElement((int) mouseEvent.getX(), (int) mouseEvent.getY());
        }
    }

    private void canvasMouseDragged(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            engine.moveElement((int) mouseEvent.getX(), (int) mouseEvent.getY());
        }
    }

    private void canvasMouseReleased(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            engine.deselectElement();
        }
    }
}
