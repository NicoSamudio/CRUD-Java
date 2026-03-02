package Models;

import Constantes.AnimalTipo;
import Constantes.EstadoRegistro;

import java.time.LocalDate;

public class Perro extends Animal {

    private String raza;
    private Boolean castrado;

    //=== Contructor con todos los atributos ===
    public Perro(int id, String nombre, LocalDate fecha_nacimiento, EstadoRegistro estado, String raza, Boolean castrado) {
        super(AnimalTipo.PERRO, id, nombre, fecha_nacimiento, estado);
        this.raza = raza;
        this.castrado = castrado;
    }

    public Perro(int id, String nombre, EstadoRegistro estado, String raza, Boolean castrado) {
        this(id, nombre, null, estado, raza, castrado);
    }

    public Perro(int id, String nombre, LocalDate fecha_nacimiento, String raza, Boolean castrado) {
        this(id, nombre, fecha_nacimiento, EstadoRegistro.ACTIVO, raza, castrado);
    }

    public String getRaza() {
        return raza;
    }

    public void setRaza(String raza) {
        this.raza = raza;
    }

    public Boolean getCastrado() {
        return castrado;
    }

    public void setCastrado(Boolean castrado) {
        this.castrado = castrado;
    }

    @Override
    public String toString() {
        return super.toString()
                + " · Raza: " + raza
                + " · Castrado: " + castrado;
    }
}
