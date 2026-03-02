package Models;

import Constantes.AnimalTipo;
import Constantes.EstadoRegistro;
import java.time.LocalDate;

public abstract class Animal implements Comparable<Animal> {

    protected AnimalTipo tipo;
    protected int id;
    protected String nombre;
    protected LocalDate fecha_nacimiento;
    protected EstadoRegistro estado;

    //=== Contructor con todos los atributos ===
    public Animal(AnimalTipo tipo, int id, String nombre, LocalDate fecha_nacimiento, EstadoRegistro estado) {
        this.tipo = tipo;
        this.id = id;
        this.nombre = nombre;
        this.fecha_nacimiento = fecha_nacimiento;
        this.estado = estado;
    }

    //===  Sobrecarga sin fecha ===
    public Animal(AnimalTipo tipo, int id, String nombre, EstadoRegistro estado) {
        this(tipo, id, nombre, null, estado);
    }

    // === Sobrecarga sin estado -> ACTIVO ===
    public Animal(AnimalTipo tipo, int id, String nombre, LocalDate fecha_nacimiento) {
        this(tipo, id, nombre, fecha_nacimiento, EstadoRegistro.ACTIVO);
    }

    public Animal() {
        this.estado = EstadoRegistro.ACTIVO;
    }

    public AnimalTipo getTipo() {
        return tipo;
    }

    public void setTipo(AnimalTipo tipo) {
        this.tipo = tipo;
    }

    // Helper para UI
    public String getTipoTexto() {
        return (tipo == null) ? "" : tipo.texto();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public LocalDate getFecha_nacimiento() {
        return fecha_nacimiento;
    }

    public void setFecha_nacimiento(LocalDate fecha_nacimiento) {
        this.fecha_nacimiento = fecha_nacimiento;
    }

    public EstadoRegistro getEstado() {
        return estado;
    }

    public void setEstado(EstadoRegistro estado) {
        this.estado = estado;
    }

    @Override
    public int compareTo(Animal other) {
        if (other == null) {
            return 1;
        }
        return Integer.compare(this.id, other.id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Animal other)) {
            return false;
        }
        return this.id == other.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    @Override
    public String toString() {
        return "Animal -> "
                + " Tipo: " + getTipoTexto()
                + " · ID: " + id
                + " · Nombre: " + nombre
                + " · Fecha nacimiento: " + fecha_nacimiento
                + " · Estado: " + estado;
    }
}
