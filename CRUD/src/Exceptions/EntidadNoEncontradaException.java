package Exceptions;

public class EntidadNoEncontradaException extends Exception {

    public EntidadNoEncontradaException(String mensaje) {
        super(mensaje);
    }

    public static EntidadNoEncontradaException paraId(int id) {
        return new EntidadNoEncontradaException("No se encontró la entidad con ID: " + id);
    }
}
