package Main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private static final String FXML_FORMULARIO = "/ViewController/formulario_controler.fxml";

    @Override
    public void start(Stage stage) throws Exception {
        var resource = getClass().getResource(FXML_FORMULARIO);
        if (resource == null) {
            throw new IllegalStateException("No se encontró el FXML: " + FXML_FORMULARIO);
        }

        FXMLLoader loader = new FXMLLoader(resource);
        Scene scene = new Scene(loader.load());

        stage.setTitle("CRUD Animales");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
