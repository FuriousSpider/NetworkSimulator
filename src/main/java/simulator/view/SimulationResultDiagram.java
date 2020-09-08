package simulator.view;

import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import simulator.element.device.additionalElements.History;

import java.util.ArrayList;
import java.util.List;

public class SimulationResultDiagram extends Dialog<String> {
    private final GridPane contentView;
    private List<History> historyList;

    public SimulationResultDiagram() {
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
        Label indexTitleLabel = new Label("Index");
        indexTitleLabel.getStyleClass().add("boldLabel");
        contentView.add(indexTitleLabel, 0, 0);

        Label deviceNameTitleLabel = new Label("Device name");
        deviceNameTitleLabel.getStyleClass().add("boldLabel");
        contentView.add(deviceNameTitleLabel, 1, 0);

        Label deviceTypeTitleLabel = new Label("Device type");
        deviceTypeTitleLabel.getStyleClass().add("boldLabel");
        contentView.add(deviceTypeTitleLabel, 2, 0);

        Label fromPortTitleLabel = new Label("From port");
        fromPortTitleLabel.getStyleClass().add("boldLabel");
        contentView.add(fromPortTitleLabel, 3, 0);

        Label toPortTitleLabel = new Label("To port");
        toPortTitleLabel.getStyleClass().add("boldLabel");
        contentView.add(toPortTitleLabel, 4, 0);

        Label confirmationTitleLabel = new Label("Confirmation");
        confirmationTitleLabel.getStyleClass().add("boldLabel");
        contentView.add(confirmationTitleLabel, 5, 0);

        Label actionTitleLabel = new Label("Action");
        actionTitleLabel.getStyleClass().add("boldLabel");
        contentView.add(actionTitleLabel, 6, 0);

        Pane pane = new Pane();
        pane.setMinHeight(1);
        pane.setMaxHeight(1);
        pane.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
        contentView.add(pane, 0, 1, 7, 1);

        for (History history : historyList) {
            int index = historyList.indexOf(history) + 2;
            Label indexLabel = new Label(String.valueOf(index - 1));
            Label deviceNameLabel = new Label(history.getDeviceName());
            Label deviceTypeLabel = new Label(history.getDeviceType());
            Label portFromLabel = new Label(history.getFromPort());
            Label portToLabel = new Label(history.getToPort());
            Label confirmationLabel = new Label(getConfirmationText(history.isConfirmationMessage()));
            Label decisionLabel = new Label(history.getDecisionValue());
            contentView.add(indexLabel, 0, index);
            contentView.add(deviceNameLabel, 1, index);
            contentView.add(deviceTypeLabel, 2, index);
            contentView.add(portFromLabel, 3, index);
            contentView.add(portToLabel, 4, index);
            contentView.add(confirmationLabel, 5, index);
            contentView.add(decisionLabel, 6, index);
        }
        super.show();
    }

    private String getConfirmationText(boolean isConfirmationMessage) {
        if (isConfirmationMessage) {
            return "yes";
        } else {
            return "no";
        }
    }
}
