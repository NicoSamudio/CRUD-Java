package Models;

import Constantes.AnimalTipo;
import Constantes.EstadoRegistro;

import java.time.LocalDate;

public class Pez extends Animal {

    private String especie;
    private Boolean esDeCardumen;

    //=== Contructor con todos los atributos ===
    public Pez(int id, String nombre, LocalDate fecha_nacimiento, EstadoRegistro estado, String especie, Boolean esDeCardumen) {
        super(AnimalTipo.PEZ, id, nombre, fecha_nacimiento, estado);
        this.especie = especie;
        this.esDeCardumen = esDeCardumen;
    }

    public Pez(int id, String nombre, EstadoRegistro estado, String especie, Boolean esDeCardumen) {
        this(id, nombre, null, estado, especie, esDeCardumen);
    }

    public Pez(int id, String nombre, LocalDate fecha_nacimiento, String especie, Boolean esDeCardumen) {
        this(id, nombre, fecha_nacimiento, EstadoRegistro.ACTIVO, especie, esDeCardumen);
    }

    public String getEspecie() {
        return especie;
    }

    public void setEspecie(String especie) {
        this.especie = especie;
    }

    public Boolean getEsDeCardumen() {
        return esDeCardumen;
    }

    public void setEsDeCardumen(Boolean esDeCardumen) {
        this.esDeCardumen = esDeCardumen;
    }

    @Override
    public String toString() {
        return super.toString()
                + " · Especie: " + especie
                + " · Es de cardumen: " + esDeCardumen;
    }
}
