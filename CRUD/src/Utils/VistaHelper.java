package Utils;

import java.util.List;
import javafx.scene.control.ListView;

public class VistaHelper {

    public static <T> void refrescarLista(ListView<T> listView, List<T> datos) {
        listView.getItems().setAll(datos);
    }
}
