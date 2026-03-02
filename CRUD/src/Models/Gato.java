package Models;

import Constantes.AnimalTipo;
import Constantes.EstadoRegistro;

import java.time.LocalDate;

public class Gato extends Animal {

    private String colorPelaje;
    private Boolean usaArenero;

    //=== Contructor con todos los atributos ===
    public Gato(int id, String nombre, LocalDate fecha_nacimiento, EstadoRegistro estado, String colorPelaje, Boolean usaArenero) {
        super(AnimalTipo.GATO, id, nombre, fecha_nacimiento, estado);
        this.colorPelaje = colorPelaje;
        this.usaArenero = usaArenero;
    }

    public Gato(int id, String nombre, EstadoRegistro estado, String colorPelaje, Boolean usaArenero) {
        this(id, nombre, null, estado, colorPelaje, usaArenero);
    }

    public Gato(int id, String nombre, LocalDate fecha_nacimiento, String colorPelaje, Boolean usaArenero) {
        this(id, nombre, fecha_nacimiento, EstadoRegistro.ACTIVO, colorPelaje, usaArenero);
    }

    public String getColorPelaje() {
        return colorPelaje;
    }

    public void setColorPelaje(String colorPelaje) {
        this.colorPelaje = colorPelaje;
    }

    public Boolean getUsaArenero() {
        return usaArenero;
    }

    public void setUsaArenero(Boolean usaArenero) {
        this.usaArenero = usaArenero;
    }

    @Override
    public String toString() {
        return super.toString()
                + " · Color de pelaje: " + colorPelaje
                + " · Usa arenero: " + usaArenero;
    }
}
