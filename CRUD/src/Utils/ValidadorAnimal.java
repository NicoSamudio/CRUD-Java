package Utils;

import java.time.LocalDate;

import Models.Animal;
import Models.Gato;
import Models.Perro;
import Models.Pez;

public final class ValidadorAnimal {

    private ValidadorAnimal() {
    }

    public static void validar(Animal animal) {
        Validador.validarNoNulo(animal, "No se recibió ningún animal para validar.");
        Validador.validarNoNulo(animal.getTipo(), "Seleccioná el tipo de animal.");
        Validador.validarTextoNoVacio(animal.getNombre(), "El nombre no puede estar vacío");
        Validador.validarNoNulo(animal.getEstado(), "Seleccioná el estado del registro.");
        Validador.validarNoNulo(animal.getFecha_nacimiento(), "Seleccioná la fecha de nacimiento.");

        if (animal.getFecha_nacimiento() != null && animal.getFecha_nacimiento().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de nacimiento no puede ser futura");
        }

        switch (animal) {
            case Perro perro -> {
                Validador.validarTextoNoVacio(perro.getRaza(), "La raza no puede estar vacía");
                Validador.validarNoNulo(perro.getCastrado(), "Seleccioná si está castrado (Sí/No).");
            }
            case Gato gato -> {
                Validador.validarTextoNoVacio(gato.getColorPelaje(), "El color de pelaje no puede estar vacío");
                Validador.validarNoNulo(gato.getUsaArenero(), "Indicá si usa arenero (Sí/No).");
            }
            case Pez pez -> {
                Validador.validarTextoNoVacio(pez.getEspecie(), "La especie no puede estar vacía");
                Validador.validarNoNulo(pez.getEsDeCardumen(), "Indicá si es de cardumen (Sí/No).");
            }
            default -> {
            }
        }
    }
}
