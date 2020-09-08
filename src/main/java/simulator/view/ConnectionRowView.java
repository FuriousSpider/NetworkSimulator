package simulator.view;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import simulator.Engine;
import simulator.element.Connection;
import simulator.element.device.additionalElements.Port;
import util.Values;

import java.util.ArrayList;
import java.util.List;

public class ConnectionRowView extends GridPane implements ColorPickerDialog.OnColorSelectedListener {
    private final Label colorLabel;
    private final Connection connection;
    private final int id;
    private OnDeleteClickListener onDeleteClickListener;

    public ConnectionRowView(Connection connection, Port otherPort, boolean showTitle) {
        this.colorLabel = new Label();
        this.connection = connection;
        this.id = connection.getId();
        Port thisPort = connection.getOtherPort(otherPort);
        Engine engine = Engine.getInstance();
        Label connectedWith = new Label(engine.getDeviceByPort(otherPort).getDeviceName());
        Button deleteConnection = new Button("Remove");

        ColumnConstraints column1 = new ColumnConstraints();
        ColumnConstraints column2 = new ColumnConstraints();
        ColumnConstraints column3 = new ColumnConstraints();
        ColumnConstraints column4 = new ColumnConstraints();
        ColumnConstraints column5 = new ColumnConstraints();
        ColumnConstraints column6 = new ColumnConstraints();

        if (thisPort.hasInterface() && thisPort.isPortTaken()) {
            IPTextField ipTextField = new IPTextField();
            ipTextField.setIpAddress(thisPort.getIpAddress());
            ipTextField.setOnSaveClickedListener(thisPort);
            ipTextField.show();
            if (showTitle) {
                Label label = new Label("IP Address:");
                label.getStyleClass().add("boldLabel");
                label.setTooltip(new Tooltip("x.x.x.x/x"));
                this.add(label, 0, 0);
            }
            this.add(ipTextField, 0, 1);
            column1.setMinWidth(110);
            column1.setMaxWidth(110);
        }

        column2.setMinWidth(40);
        column2.setMaxWidth(40);
        if (showTitle) {
            Label label = new Label("Port:");
            label.getStyleClass().add("boldLabel");
            this.add(label, 1, 0);
        }
        this.add(new Label(thisPort.getPortName()), 1, 1);

        //TODO: change -> allow only when switch or router on the other side
        if (thisPort.hasVLan()) {
            VLanTextField vLanTextField = new VLanTextField();
            vLanTextField.setVLanId(thisPort.getVLanId());
            vLanTextField.setTrunkMode(thisPort.isInTrunkMode());
            vLanTextField.setTrunkModeAllowedVLANS(thisPort.getTrunkModeAllowedIds());
            List<VLanTextField.OnChangeModeClickedListener> bothConnectionEnds = new ArrayList<>();
            bothConnectionEnds.add(thisPort);
            bothConnectionEnds.add(otherPort);
            List<TrunkModeAllowedVlan.OnAllowedVLanChangeListener> bothConnectionEnds2 = new ArrayList<>();
            bothConnectionEnds2.add(thisPort);
            bothConnectionEnds2.add(otherPort);
            vLanTextField.setOnChangeModeClickedListenerList(bothConnectionEnds);
            vLanTextField.setOnSaveClickedListener(thisPort);
            vLanTextField.setOnAllowedVlanChangeListener(bothConnectionEnds2);
            vLanTextField.show();
            if (showTitle) {
                Label label = new Label("VLAN:");
                label.getStyleClass().add("boldLabel");
                label.setTooltip(new Tooltip("1 - 1001"));
                this.add(label, 2, 0);
            }
            this.add(vLanTextField, 2, 1);
            column3.setMinWidth(140);
            column3.setMaxWidth(140);

        }

        colorLabel.setBackground(new Background(new BackgroundFill(connection.getColor(), CornerRadii.EMPTY, Insets.EMPTY)));
        colorLabel.setMinHeight(Values.LABEL_COLOR_MIN_HEIGHT);
        colorLabel.setMinWidth(Values.LABEL_COLOR_MIN_WIDTH);
        colorLabel.setOnMouseClicked(this::onColorClicked);
        colorLabel.setTooltip(new Tooltip("Click to change the connection color"));

        deleteConnection.setOnMouseClicked(this::onMouseClicked);

        //TODO: change to device name
        if (showTitle) {
            Label label = new Label("Connected with:");
            label.getStyleClass().add("boldLabel");
            this.add(label, 3, 0);
        }
        this.add(connectedWith, 3, 1);
        if (showTitle) {
            Label label = new Label("Connection:");
            label.getStyleClass().add("boldLabel");
            this.add(label, 4, 0);
        }
        this.add(colorLabel, 4, 1);
        this.add(deleteConnection, 5, 1);

        this.setHgap(10);
        column4.setMinWidth(100);
        column4.setMaxWidth(100);
        column5.setMinWidth(80);
        column5.setMaxWidth(80);
        column6.setMinWidth(60);
        column6.setMaxWidth(60);
        this.getColumnConstraints().addAll(column1, column2, column3, column4, column5, column6);

        Pane pane = new Pane();
        pane.setMinHeight(1);
        pane.setMaxHeight(1);
        pane.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
        this.add(pane, 0, 2, 5, 1);

        this.setVgap(2);
    }

    private void onColorClicked(MouseEvent mouseEvent) {
        Color color = (Color) colorLabel.getBackground().getFills().get(0).getFill();
        ColorPickerDialog dialog = new ColorPickerDialog();
        dialog.setColor(color);
        dialog.setOnColorSelectedListener(this);
        dialog.show();
    }

    public void setOnDeleteClickListener(OnDeleteClickListener onDeleteClickListener) {
        this.onDeleteClickListener = onDeleteClickListener;
    }

    private void onMouseClicked(MouseEvent mouseEvent) {
        onDeleteClickListener.onClick(id);
    }

    @Override
    public void onColorSelected(Color color) {
        connection.setColor(color);
        colorLabel.setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, null)));
    }

    public interface OnDeleteClickListener {
        void onClick(int id);
    }
}