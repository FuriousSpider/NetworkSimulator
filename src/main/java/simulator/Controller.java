package simulator;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import util.Values;
import view.CanvasPane;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    private Pane canvasPane;
    private GraphicsContext ctx;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        CanvasPane canvas = new CanvasPane(canvasPane.getWidth(), canvasPane.getHeight());
        canvasPane.getChildren().add(canvas);
        ctx = canvas.getCanvas().getGraphicsContext2D();
    }

    @FXML
    private void handleEndDeviceButtonClick(ActionEvent event) {
        Image image = new Image("/endDevice.png");
        ctx.drawImage(image, 10, 10, Values.DEVICE_SIZE, Values.DEVICE_SIZE);
    }

    @FXML
    private void handleHubButtonClick(ActionEvent event) {
        Image image = new Image("/hub.png");
        ctx.drawImage(image, 10, 10, Values.DEVICE_SIZE, Values.DEVICE_SIZE);
    }

    @FXML
    private void handleSwitchButtonClick(ActionEvent event) {
        Image image = new Image("/switch.png");
        ctx.drawImage(image, 10, 10, Values.DEVICE_SIZE, Values.DEVICE_SIZE);
    }

    @FXML
    private void handleRouterButtonClick(ActionEvent event) {
        Image image = new Image("/router.png");
        ctx.drawImage(image, 10, 10, Values.DEVICE_SIZE, Values.DEVICE_SIZE);
    }

    @FXML
    private void handleFirewallButtonClick(ActionEvent event) {
        Image image = new Image("/firewall.png");
        ctx.drawImage(image, 10, 10, Values.DEVICE_SIZE, Values.DEVICE_SIZE);
    }
}
