package simulator.view;

import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import simulator.Engine;
import simulator.element.Port;
import util.Values;

import java.util.ArrayList;
import java.util.List;

public class PortListDialog extends Dialog<String> {
    private final List<Port> portList;
    private OnPortSelectedListener onPortSelectedListener;

    public PortListDialog() {
        super();

        this.portList = new ArrayList<>();
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
                Engine.getInstance().logError(Values.ERROR_NO_FREE_PORT_AVAILABLE);
                break;
            case 1:
                if (onPortSelectedListener != null) {
                    onPortSelectedListener.onPortSelected(emptyPortList.get(0).getId());
                }
                break;
            default:
                VBox pane = new VBox();
                pane.getChildren().add(new Label("Select port"));
                for (Port port : portList) {
                    HBox line = new HBox();
                    line.getChildren().add(new Label(String.valueOf(port.getPortName())));
                    line.getChildren().add(new Label(String.valueOf(port.isPortTaken())));
                    if (!port.isPortTaken()) {
                        Button button = new Button("Select");
                        button.setId(String.valueOf(port.getId()));
                        button.setOnMouseClicked(this::onSelectButtonClicked);
                        line.getChildren().add(button);
                    }
                    pane.getChildren().add(line);
                }

                this.getDialogPane().setContent(pane);
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
