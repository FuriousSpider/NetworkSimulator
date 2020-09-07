package simulator.view;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import simulator.element.device.additionalElements.Policy;

public class SimulationView extends GridPane {
    private final Label simulationTitleLabel;
    private final Label sourceIpAddressTitleLabel;
    private final TextField sourceIpAddressTextField;
    private final Label destinationIpAddressTitleLabel;
    private final TextField destinationIpAddressTextField;
    private final Label applicationTitleLabel;
    private final ComboBox<String> applicationComboBox;
    private final Button startSimulationButton;
    private final Button stopSimulationButton;

    private OnStartSimulationButtonClickedListener onStartSimulationButtonClickedListener;
    private OnStopSimulationButtonClickedListener onStopSimulationButtonClickedListener;

    public SimulationView() {
        this.simulationTitleLabel = new Label("Simulation");
        this.sourceIpAddressTitleLabel = new Label("Source IP Address:");
        this.sourceIpAddressTextField = new TextField();
        this.destinationIpAddressTitleLabel = new Label("Destination IP Address:");
        this.destinationIpAddressTextField = new TextField();
        this.applicationTitleLabel = new Label("Application:");
        this.applicationComboBox = new ComboBox<>();

        this.startSimulationButton = new Button("Simulate");
        this.stopSimulationButton = new Button("Stop simulation");

        this.add(simulationTitleLabel, 0, 0);

        this.add(sourceIpAddressTitleLabel, 0, 1);
        this.add(sourceIpAddressTextField, 1, 1);

        this.add(destinationIpAddressTitleLabel, 0, 2);
        this.add(destinationIpAddressTextField, 1, 2);

        this.add(applicationTitleLabel, 0, 3);
        this.add(applicationComboBox, 1, 3);

        this.add(startSimulationButton, 0, 4);
        this.add(stopSimulationButton, 1, 4);

        this.startSimulationButton.setOnMouseClicked(this::onStartSimulationClicked);
        this.stopSimulationButton.setOnMouseClicked(this::onStopSimulationClicked);

        for (Policy.Application application : Policy.Application.values()) {
            applicationComboBox.getItems().add(application.name());
        }

        this.setHgap(10);
        this.setPadding(new Insets(10));
    }

    public String getSourceIpAddress() {
        return sourceIpAddressTextField.getText();
    }

    public String getDestinationIpAddress() {
        return destinationIpAddressTextField.getText();
    }

    public Policy.Application getApplication() {
        try {
            return Policy.Application.valueOf(applicationComboBox.getValue());
        } catch (Exception e) {
            return null;
        }
    }

    private void onStartSimulationClicked(MouseEvent mouseEvent) {
        if (onStartSimulationButtonClickedListener != null) {
            onStartSimulationButtonClickedListener.onStartSimulationClicked();
        }
    }

    private void onStopSimulationClicked(MouseEvent mouseEvent) {
        if (onStopSimulationButtonClickedListener != null) {
            onStopSimulationButtonClickedListener.onStopSimulationClicked();
        }
    }

    public void lockSimulationButton() {
        startSimulationButton.setDisable(true);
    }

    public void unlockSimulationButton() {
        startSimulationButton.setDisable(false);
    }

    public void setOnStartSimulationButtonClickedListener(OnStartSimulationButtonClickedListener listener) {
        this.onStartSimulationButtonClickedListener = listener;
    }

    public void setOnStopSimulationButtonClickedListener(OnStopSimulationButtonClickedListener listener) {
        this.onStopSimulationButtonClickedListener = listener;
    }

    public interface OnStartSimulationButtonClickedListener {
        void onStartSimulationClicked();
    }

    public interface OnStopSimulationButtonClickedListener {
        void onStopSimulationClicked();
    }
}
