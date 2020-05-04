package simulator;

import util.Values;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("layout.fxml")));
        Scene scene = new Scene(root, Values.APPLICATION_MIN_WIDTH, Values.APPLICATION_MIN_HEIGHT);
        stage.setMinWidth(Values.APPLICATION_MIN_WIDTH);
        stage.setMinHeight(Values.APPLICATION_MIN_HEIGHT);

        stage.setTitle("Network Simulator");
        stage.setScene(scene);
        stage.show();
    }
}
