package Utils;

public class Validador {

    public static void validarNoNulo(Object obj, String mensaje) throws IllegalArgumentException {
        if (obj == null) {
            throw new IllegalArgumentException(mensaje);
        }
    }

    public static void validarTextoNoVacio(String texto, String mensaje) throws IllegalArgumentException {
        if (texto == null || texto.trim().isEmpty()) {
            throw new IllegalArgumentException(mensaje);
        }
    }

    public static void validarNumeroPositivo(double valor, String mensaje) throws IllegalArgumentException {
        if (valor <= 0) {
            throw new IllegalArgumentException(mensaje);
        }
    }
}
