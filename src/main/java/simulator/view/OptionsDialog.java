package simulator.view;

import javafx.beans.Observable;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import util.Values;

public class OptionsDialog extends Dialog<String> {
    private final VBox content;
    private final HBox firstLine;
    private final HBox secondLine;
    private final Label elementSizeTitle;
    private final Label simulationSpeedTitle;
    private final Slider elementSizeSlider;
    private final Slider simulationSpeedSlider;
    private final Label elementSizeValue;
    private final Label simulationSpeedValue;

    public OptionsDialog() {
        this.content = new VBox();
        this.firstLine = new HBox();
        this.secondLine = new HBox();
        this.elementSizeTitle = new Label("Element size:");
        this.simulationSpeedTitle = new Label("Simulation speed");
        this.elementSizeSlider = new Slider();
        this.simulationSpeedSlider = new Slider();
        this.elementSizeValue = new Label(String.valueOf(Values.DEVICE_SIZE));
        this.simulationSpeedValue = new Label(String.valueOf(Values.MESSAGE_PROGRESS_STEP));

        this.content.getChildren().add(firstLine);
        this.content.getChildren().add(secondLine);

        this.firstLine.getChildren().add(elementSizeTitle);
        this.firstLine.getChildren().add(elementSizeSlider);
        this.firstLine.getChildren().add(elementSizeValue);

        this.secondLine.getChildren().add(simulationSpeedTitle);
        this.secondLine.getChildren().add(simulationSpeedSlider);
        this.secondLine.getChildren().add(simulationSpeedValue);

        this.getDialogPane().setContent(content);

        this.elementSizeSlider.setMin(Values.DEVICE_MIN_SIZE);
        this.elementSizeSlider.setMax(Values.DEVICE_MAX_SIZE);
        this.elementSizeSlider.setValue(Values.DEVICE_SIZE);
        this.elementSizeSlider.valueProperty().addListener(this::onElementSizeChange);

        this.simulationSpeedSlider.setMin(Values.MESSAGE_PROGRESS_MIN_STEP);
        this.simulationSpeedSlider.setMax(Values.MESSAGE_PROGRESS_MAX_STEP);
        this.simulationSpeedSlider.setValue(Values.MESSAGE_PROGRESS_STEP);
        this.simulationSpeedSlider.valueProperty().addListener(this::onSimulationSpeedChange);

        this.getDialogPane().setMinWidth(Values.DIALOG_OPTIONS_MIN_WIDTH);
        this.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);
    }

    private void onElementSizeChange(Observable observable, Number oldValue, Number newValue) {
        Values.DEVICE_SIZE = newValue.intValue();
        elementSizeValue.setText(String.valueOf(newValue.intValue()));
    }

    private void onSimulationSpeedChange(Observable observable, Number oldValue, Number newValue) {
        Values.MESSAGE_PROGRESS_STEP = newValue.intValue();
        simulationSpeedValue.setText(String.valueOf(newValue.intValue()));
    }
}
