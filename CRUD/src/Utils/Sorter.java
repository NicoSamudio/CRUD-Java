package Utils;

import java.util.Comparator;
import java.util.List;

import Models.Animal;

public class Sorter {

    private Sorter() {
    }

    public enum Criterio {
        ID, NOMBRE, FECHA
    }

    public enum Direccion {
        ASC, DESC
    }

    public static <T extends Animal> void ordenar(
            List<T> lista,
            Criterio criterio,
            Direccion direccion
    ) {
        if (lista == null) {
            throw new IllegalArgumentException("La lista no puede ser null");
        }
        if (criterio == null) {
            throw new IllegalArgumentException("El criterio no puede ser null");
        }
        if (direccion == null) {
            throw new IllegalArgumentException("La direccion no puede ser null");
        }

        Comparator<T> comparator = switch (criterio) {
            case ID ->
                Comparator.comparingInt(animal -> animal.getId());

            case NOMBRE ->
                Comparator.comparing(
                animal -> animal.getNombre(),
                String.CASE_INSENSITIVE_ORDER
                );

            case FECHA ->
                Comparator.comparing(
                animal -> animal.getFecha_nacimiento(),
                Comparator.nullsLast(Comparator.naturalOrder())
                );
        };

        if (direccion == Direccion.DESC) {
            comparator = comparator.reversed();
        }

        lista.sort(comparator);
    }
}
