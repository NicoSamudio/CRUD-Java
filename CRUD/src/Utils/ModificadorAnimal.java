package Utils;

import java.time.LocalDate;
import java.util.function.Consumer;

import Models.Animal;
import Models.Perro;
import Models.Gato;
import Models.Pez;

public final class ModificadorAnimal {

    private ModificadorAnimal() {
    }

    public static void modificarBasico(Animal a, String nuevoNombre, LocalDate nuevaFechaNacimiento) {
        if (nuevoNombre != null) {
            Validador.validarTextoNoVacio(nuevoNombre, "El nombre no puede estar vacío");
            a.setNombre(nuevoNombre);
        }
        if (nuevaFechaNacimiento != null) {
            if (nuevaFechaNacimiento.isAfter(LocalDate.now())) {
                throw new IllegalArgumentException("La fecha de nacimiento no puede ser futura");
            }
            a.setFecha_nacimiento(nuevaFechaNacimiento);
        }
    }

    public static Consumer<Perro> cambiosPerro(String raza, Boolean castrado) {
        return perro -> {
            if (raza != null) {
                Validador.validarTextoNoVacio(raza, "La raza no puede estar vacía");
                perro.setRaza(raza);
            }
            if (castrado != null) {
                perro.setCastrado(castrado);
            }
        };
    }

    public static Consumer<Gato> cambiosGato(String colorPelaje, Boolean usaArenero) {
        return gato -> {
            if (colorPelaje != null) {
                Validador.validarTextoNoVacio(colorPelaje, "El color de pelaje no puede estar vacío");
                gato.setColorPelaje(colorPelaje);
            }
            if (usaArenero != null) {
                gato.setUsaArenero(usaArenero);
            }
        };
    }

    public static Consumer<Pez> cambiosPez(String especie, Boolean esDeCardumen) {
        return pez -> {
            if (especie != null) {
                Validador.validarTextoNoVacio(especie, "La especie no puede estar vacía");
                pez.setEspecie(especie);
            }
            if (esDeCardumen != null) {
                pez.setEsDeCardumen(esDeCardumen);
            }
        };
    }
}
