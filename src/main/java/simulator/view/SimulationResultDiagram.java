package simulator.view;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import simulator.element.device.additionalElements.History;

import java.util.ArrayList;
import java.util.List;

public class SimulationResultDiagram extends Dialog<String> {
    private final GridPane contentView;
    private List<History> historyList;

    public SimulationResultDiagram () {
        this.contentView = new GridPane();
        this.historyList = new ArrayList<>();

        this.getDialogPane().setContent(contentView);
        this.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);

        this.contentView.setHgap(10);
        this.setTitle("Result table");
    }

    public void setContent(List<History> historyList) {
        this.historyList.clear();
        this.historyList.addAll(historyList);
    }

    public void start() {
        contentView.add(new Label("Index"), 0, 0);
        contentView.add(new Label("Device name"), 1, 0);
        contentView.add(new Label("Device type"), 2, 0);
        contentView.add(new Label("From port"), 3, 0);
        contentView.add(new Label("To port"), 4, 0);
        contentView.add(new Label("Action"), 5, 0);

        for (History history : historyList) {
            int index = historyList.indexOf(history) + 1;
            Label indexLabel = new Label(String.valueOf(index));
            Label deviceNameLabel = new Label(history.getDeviceName());
            Label deviceTypeLabel = new Label(history.getDeviceType());
            Label portFromLabel = new Label(history.getFromPort());
            Label portToLabel = new Label(history.getToPort());
            Label decisionLabel = new Label(history.getDecisionValue());
            contentView.add(indexLabel, 0, index);
            contentView.add(deviceNameLabel, 1, index);
            contentView.add(deviceTypeLabel, 2, index);
            contentView.add(portFromLabel, 3, index);
            contentView.add(portToLabel, 4, index);
            contentView.add(decisionLabel, 5, index);
        }
        super.show();
    }
}
