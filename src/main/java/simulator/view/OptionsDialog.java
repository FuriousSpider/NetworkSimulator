package simulator.view;

import javafx.beans.Observable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import util.Values;

public class OptionsDialog extends Dialog<String> {
    private final GridPane content;
    private final Label elementSizeTitle;
    private final Slider elementSizeSlider;
    private final Label elementSizeValue;
    private final Label simulationSpeedTitle;
    private final Slider simulationSpeedSlider;
    private final Label simulationSpeedValue;
    private final Label showDeviceNameTitle;
    private final CheckBox showDeviceNameCheckBox;
    private final Label showPortsTitle;
    private final CheckBox showPortsCheckBox;

    public OptionsDialog() {
        this.content = new GridPane();
        this.elementSizeTitle = new Label("Element size:");
        this.elementSizeSlider = new Slider();
        this.elementSizeValue = new Label(String.valueOf(Values.DEVICE_SIZE));
        this.simulationSpeedTitle = new Label("Simulation speed");
        this.simulationSpeedSlider = new Slider();
        this.simulationSpeedValue = new Label(String.valueOf(Values.MESSAGE_PROGRESS_STEP));
        this.showDeviceNameTitle = new Label("Show device name");
        this.showDeviceNameCheckBox = new CheckBox();
        this.showPortsTitle = new Label("Show ports");
        this.showPortsCheckBox = new CheckBox();

        this.content.add(elementSizeTitle, 0, 0);
        this.content.add(elementSizeSlider, 1, 0);
        this.content.add(elementSizeValue, 2, 0);

        this.content.add(simulationSpeedTitle, 0, 1);
        this.content.add(simulationSpeedSlider, 1, 1);
        this.content.add(simulationSpeedValue, 2, 1);

        this.content.add(showDeviceNameTitle, 0, 2);
        this.content.add(showDeviceNameCheckBox, 1, 2);

        this.content.add(showPortsTitle, 0, 3);
        this.content.add(showPortsCheckBox, 1, 3);

        this.content.setHgap(10);
        this.getDialogPane().setContent(content);

        this.elementSizeSlider.setMin(Values.DEVICE_MIN_SIZE);
        this.elementSizeSlider.setMax(Values.DEVICE_MAX_SIZE);
        this.elementSizeSlider.setValue(Values.DEVICE_SIZE);
        this.elementSizeSlider.valueProperty().addListener(this::onElementSizeChange);

        this.simulationSpeedSlider.setMin(Values.MESSAGE_PROGRESS_MIN_STEP);
        this.simulationSpeedSlider.setMax(Values.MESSAGE_PROGRESS_MAX_STEP);
        this.simulationSpeedSlider.setValue(Values.MESSAGE_PROGRESS_STEP);
        this.simulationSpeedSlider.valueProperty().addListener(this::onSimulationSpeedChange);

        this.showDeviceNameCheckBox.setSelected(Values.SHOW_DEVICE_NAME);
        this.showPortsCheckBox.setSelected(Values.SHOW_PORTS);

        this.showDeviceNameCheckBox.setOnMouseClicked(this::onShowDeviceNameClicked);
        this.showPortsCheckBox.setOnMouseClicked(this::onShowPortsClicked);

        this.getDialogPane().setMinWidth(Values.DIALOG_OPTIONS_MIN_WIDTH);
        this.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);

        this.setTitle("Options");
    }

    private void onElementSizeChange(Observable observable, Number oldValue, Number newValue) {
        Values.DEVICE_SIZE = newValue.intValue();
        elementSizeValue.setText(String.valueOf(newValue.intValue()));
    }

    private void onSimulationSpeedChange(Observable observable, Number oldValue, Number newValue) {
        Values.MESSAGE_PROGRESS_STEP = newValue.intValue();
        simulationSpeedValue.setText(String.valueOf(newValue.intValue()));
    }

    private void onShowDeviceNameClicked(MouseEvent mouseEvent) {
        Values.SHOW_DEVICE_NAME = showDeviceNameCheckBox.isSelected();
    }

    private void onShowPortsClicked(MouseEvent mouseEvent) {
        Values.SHOW_PORTS = showPortsCheckBox.isSelected();
    }
}
