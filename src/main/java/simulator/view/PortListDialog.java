package simulator.view;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import simulator.Manager;
import simulator.element.device.additionalElements.Port;
import util.Values;

import java.util.ArrayList;
import java.util.List;

public class PortListDialog extends Dialog<String> {
    private final List<Port> portList;
    private OnPortSelectedListener onPortSelectedListener;

    public PortListDialog() {
        super();

        this.portList = new ArrayList<>();

        this.setTitle("Select port");
    }

    public void setPortList(List<Port> portList) {
        this.portList.addAll(portList);
    }

    public void setOnPortSelectedListener(OnPortSelectedListener listener) {
        this.onPortSelectedListener = listener;
    }

    public void start() {
        List<Port> emptyPortList = new ArrayList<>();
        for (Port port : portList) {
            if (!port.isPortTaken()) {
                emptyPortList.add(port);
            }
        }
        switch (emptyPortList.size()) {
            case 0:
                Manager.getInstance().logError(Values.ERROR_NO_FREE_PORT_AVAILABLE);
                break;
            case 1:
                if (onPortSelectedListener != null) {
                    onPortSelectedListener.onPortSelected(emptyPortList.get(0).getId());
                }
                break;
            default:
                GridPane layout = new GridPane();
                layout.setHgap(10);
                layout.setVgap(4);

                Label portNameLabel = new Label("Port name");
                portNameLabel.getStyleClass().add("boldLabel");
                layout.add(portNameLabel, 0, 0);

                Label isPortTakenLabel = new Label("Is port taken");
                isPortTakenLabel.getStyleClass().add("boldLabel");
                layout.add(isPortTakenLabel, 1, 0);

                Pane pane = new Pane();
                pane.setMinHeight(1);
                pane.setMaxHeight(1);
                pane.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
                layout.add(pane, 0, 1, 3, 1);

                for (Port port : portList) {
                    int index = portList.indexOf(port) + 2;
                    layout.add(new Label(String.valueOf(port.getPortName())), 0, index);
                    layout.add(new Label(String.valueOf(port.isPortTaken())), 1, index);
                    if (!port.isPortTaken()) {
                        Button button = new Button("Select");
                        button.setId(String.valueOf(port.getId()));
                        button.setOnMouseClicked(this::onSelectButtonClicked);
                        layout.add(button, 2, index);
                    }
                }

                this.getDialogPane().setContent(layout);
                this.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL);
                super.show();
                break;
        }
    }


    private void onSelectButtonClicked(MouseEvent mouseEvent) {
        if (onPortSelectedListener != null) {
            onPortSelectedListener.onPortSelected(Integer.parseInt(((Button) mouseEvent.getSource()).getId()));
        }
        super.close();
    }

    public interface OnPortSelectedListener {
        void onPortSelected(int portId);
    }
}
