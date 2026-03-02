package Utils;

import java.time.LocalDate;

import Constantes.AnimalTipo;
import Constantes.EstadoRegistro;

import Models.Animal;
import Models.Gato;
import Models.Perro;
import Models.Pez;

public final class FormularioMapper {

    private FormularioMapper() {
    }

    // === Animal, solo mapeo, convierto los datos del formulario en una instancia de perro, gato o pez ===
    public static Animal construirAnimal(
            String idTexto,
            String nombreTexto,
            LocalDate fechaNacimiento,
            AnimalTipo tipo,
            String dato1Texto,
            Boolean dato2SiNo
    ) {
        int id = parsearId(idTexto);

        String nombre = trimOrEmpty(nombreTexto);
        String dato1 = trimOrEmpty(dato1Texto);

        if (tipo == null) {
            throw new IllegalArgumentException("Seleccioná el tipo de animal.");
        }

        //=== Estado por defecto (el controller lo pisa con leerEstadoCrud) ===
        EstadoRegistro estado = EstadoRegistro.ACTIVO;

        return switch (tipo) {
            case PERRO ->
                new Perro(id, nombre, fechaNacimiento, estado, dato1, dato2SiNo);
            case GATO ->
                new Gato(id, nombre, fechaNacimiento, estado, dato1, dato2SiNo);
            case PEZ ->
                new Pez(id, nombre, fechaNacimiento, estado, dato1, dato2SiNo);
        };
    }

    //=== Animal -> para cargar formulario ===
    public static FormularioData aFormularioData(Animal animal) {
        if (animal == null) {
            return FormularioData.vacio();
        }

        FormularioData data = new FormularioData();
        data.idTexto = String.valueOf(animal.getId());
        data.nombreTexto = nullToEmpty(animal.getNombre());
        data.fechaNacimiento = animal.getFecha_nacimiento();

        switch (animal) {
            case Perro perro -> {
                data.dato1Texto = nullToEmpty(perro.getRaza());
                data.dato2SiNo = perro.getCastrado();
            }
            case Gato gato -> {
                data.dato1Texto = nullToEmpty(gato.getColorPelaje());
                data.dato2SiNo = gato.getUsaArenero();
            }
            case Pez pez -> {
                data.dato1Texto = nullToEmpty(pez.getEspecie());
                data.dato2SiNo = pez.getEsDeCardumen();
            }
            default -> {
                data.dato1Texto = "";
                data.dato2SiNo = null;
            }
        }

        return data;
    }

    //=== Helpers ===
    private static int parsearId(String idTexto) {
        String limpio = trimOrEmpty(idTexto);
        if (limpio.isBlank()) {
            throw new IllegalArgumentException("El ID no puede estar vacío");
        }
        return Integer.parseInt(limpio);
    }

    private static String trimOrEmpty(String s) {
        return (s == null) ? "" : s.trim();
    }

    private static String nullToEmpty(String s) {
        return (s == null) ? "" : s;
    }

    //===  DTO ===
    public static class FormularioData {

        public String idTexto;
        public String nombreTexto;
        public LocalDate fechaNacimiento;
        public String dato1Texto;
        public Boolean dato2SiNo;

        public static FormularioData vacio() {
            FormularioData data = new FormularioData();
            data.idTexto = "";
            data.nombreTexto = "";
            data.fechaNacimiento = null;
            data.dato1Texto = "";
            data.dato2SiNo = null;
            return data;
        }
    }
}
