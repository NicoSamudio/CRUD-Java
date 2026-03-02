package Constantes;

public enum AnimalTipo {
    PERRO, GATO, PEZ;

    public String texto() {
        return switch (this) {
            case PERRO ->
                "Perro";
            case GATO ->
                "Gato";
            case PEZ ->
                "Pez";
        };
    }
}
