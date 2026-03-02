package Exceptions;

public class ObjetoRepetidoException extends Exception {

    public ObjetoRepetidoException(String mensaje) {
        super(mensaje);
    }

    public static ObjetoRepetidoException para(Object obj) {
        return new ObjetoRepetidoException("El objeto ya existe: " + obj.toString());
    }
}
