package Exceptions;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

@FunctionalInterface
public interface ExceptionHelper {

    void run() throws Exception;

    //=== Metodo para ejecutar con try/catch centralizado ===
    static void runGuarded(ExceptionHelper task) {
        try {
            task.run();
        } catch (NumberFormatException ex) {
            showError("Revisá los números: ID debe ser entero");
        } catch (Exceptions.ObjetoRepetidoException ex) {
            showError("Ya existe un animal con ese ID.");
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        } catch (Exception ex) {
            showError("Error inesperado: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    //=== Metodo helper para mostrar errores ===
    private static void showError(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR, mensaje, ButtonType.OK);
        alerta.setHeaderText("Validación");
        alerta.showAndWait();
    }
}
