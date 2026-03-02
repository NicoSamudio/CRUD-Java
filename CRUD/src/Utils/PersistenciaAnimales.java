//Se puede simplificar

package Utils;

import Constantes.EstadoRegistro;
import Constantes.FormatoDatos;
import Models.Animal;
import Models.Gato;
import Models.Perro;
import Models.Pez;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import javax.swing.filechooser.FileSystemView;
import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class PersistenciaAnimales {

    private final String nombreArchivo;

    private final Path carpetaDatos = FileSystemView.getFileSystemView()
            .getHomeDirectory().toPath().resolve("Datos Exportados");

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    // DTO simple para poder reconstruir el subtipo (Perro/Gato/Pez) -> redundante, podria mejorar
    private static class RegistroAnimal {

        String tipo;            // perro, gato, pez
        int id;
        String nombre;
        String fechaNacimiento; // 2026-02-22
        String estado;          // activo o inactivo
        String dato1;           // raza/pelaje/especie
        Boolean dato2;          // castrado/arenero/cardumen
    }

    public PersistenciaAnimales(String rutaArchivo, FormatoDatos formato) {
        this.nombreArchivo = new File(rutaArchivo).getName();

        if (formato != FormatoDatos.JSON) {
            throw new IllegalArgumentException("Esta versión simple soporta solo JSON.");
        }

        carpetaDatos.toFile().mkdirs();
    }

    private String rutaJson() {
        return carpetaDatos.resolve(nombreArchivo).toString();
    }

    //=== JSON: guardar / cargar ===
    public void guardar(List<Animal> animales) {
        if (animales == null) {
            animales = Collections.emptyList();
        }

        List<RegistroAnimal> registros = new ArrayList<>();
        for (Animal animal : animales) {
            RegistroAnimal registro = convertirARegistro(animal);
            if (registro != null) {
                registros.add(registro);
            }
        }

        try (Writer writer = new OutputStreamWriter(new FileOutputStream(rutaJson()), StandardCharsets.UTF_8)) {
            gson.toJson(registros, writer);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo guardar JSON en: " + rutaJson(), e);
        }
    }

    public List<Animal> cargar() {
        File archivo = new File(rutaJson());
        if (!archivo.exists() || archivo.length() == 0) {
            return new ArrayList<>();
        }

        try (Reader reader = new InputStreamReader(new FileInputStream(archivo), StandardCharsets.UTF_8)) {
            Type tipoLista = new TypeToken<List<RegistroAnimal>>() {
            }.getType();
            List<RegistroAnimal> registros = gson.fromJson(reader, tipoLista);
            if (registros == null) {
                return new ArrayList<>();
            }

            List<Animal> out = new ArrayList<>();
            for (RegistroAnimal registro : registros) {
                Animal animal = convertirAAnimal(registro);
                if (animal != null) {
                    out.add(animal);
                }
            }
            return out;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private RegistroAnimal convertirARegistro(Animal animal) {
        if (animal == null) {
            return null;
        }

        RegistroAnimal registro = new RegistroAnimal(); // Puedo usar un toString
        registro.id = animal.getId();
        registro.nombre = animal.getNombre();
        registro.fechaNacimiento = (animal.getFecha_nacimiento() == null) ? null : animal.getFecha_nacimiento().toString();
        registro.estado = (animal.getEstado() == null) ? EstadoRegistro.ACTIVO.name() : animal.getEstado().name();

        if (animal instanceof Perro perro) {
            registro.tipo = "perro";
            registro.dato1 = perro.getRaza();
            registro.dato2 = perro.getCastrado();
            return registro;
        }
        if (animal instanceof Gato gato) {
            registro.tipo = "gato";
            registro.dato1 = gato.getColorPelaje();
            registro.dato2 = gato.getUsaArenero();
            return registro;
        }
        if (animal instanceof Pez pez) {
            registro.tipo = "pez";
            registro.dato1 = pez.getEspecie();
            registro.dato2 = pez.getEsDeCardumen();
            return registro;
        }

        return null;
    }

    private Animal convertirAAnimal(RegistroAnimal registrar) {
        if (registrar == null || registrar.tipo == null) {
            return null;
        }

        LocalDate fecha = (registrar.fechaNacimiento == null || registrar.fechaNacimiento.isBlank())
                ? null
                : LocalDate.parse(registrar.fechaNacimiento);

        EstadoRegistro estado = (registrar.estado == null || registrar.estado.isBlank())
                ? EstadoRegistro.ACTIVO
                : EstadoRegistro.valueOf(registrar.estado);

        String dato1 = (registrar.dato1 == null) ? "" : registrar.dato1;

        return switch (registrar.tipo.toLowerCase()) {
            case "perro" ->
                new Perro(registrar.id, registrar.nombre, fecha, estado, dato1, registrar.dato2);
            case "gato" ->
                new Gato(registrar.id, registrar.nombre, fecha, estado, dato1, registrar.dato2);
            case "pez" ->
                new Pez(registrar.id, registrar.nombre, fecha, estado, dato1, registrar.dato2);
            default ->
                null;
        };
    }

    // === TXT: exportar listado (salida) ===
    public String exportarTxtLegible(List<Animal> animales, String nombreArchivoTxt, String titulo, String filtro) {
        if (animales == null) {
            animales = Collections.emptyList();
        }
        if (nombreArchivoTxt == null || nombreArchivoTxt.isBlank()) {
            nombreArchivoTxt = "reporte_animales.txt";
        }
        if (!nombreArchivoTxt.toLowerCase().endsWith(".txt")) {
            nombreArchivoTxt += ".txt";
        }
        if (titulo == null || titulo.isBlank()) {
            titulo = "Listado de Animales";
        }
        if (filtro == null || filtro.isBlank()) {
            filtro = "Sin filtro";
        }

        File carpeta = carpetaDatos.toFile();
        carpeta.mkdirs();

        File archivo = carpetaDatos.resolve(nombreArchivoTxt).toFile();

        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(archivo), StandardCharsets.UTF_8))) {

            String fechaHora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

            bw.write("==================================================");
            bw.newLine();
            bw.write(titulo.toUpperCase());
            bw.newLine();
            bw.write("Generado: " + fechaHora);
            bw.newLine();
            bw.write("Filtro: " + filtro);
            bw.newLine();
            bw.write("Total: " + animales.size());
            bw.newLine();
            bw.write("==================================================");
            bw.newLine();
            bw.newLine();

            for (Animal animal : animales) {
                bw.write(linea(animal));
                bw.newLine();
            }

        } catch (IOException e) {
            throw new RuntimeException("No se pudo exportar TXT en: " + archivo.getAbsolutePath(), e);
        }

        return archivo.getAbsolutePath();
    }

    private String linea(Animal animal) {
        if (animal == null) {
            return "-";
        }

        String tipo = tipoDe(animal);
        String nombre = (animal.getNombre() == null || animal.getNombre().isBlank()) ? "-" : animal.getNombre();
        String fecha = (animal.getFecha_nacimiento() == null) ? "-" : animal.getFecha_nacimiento().toString();
        String estado = (animal.getEstado() == null) ? EstadoRegistro.ACTIVO.name() : animal.getEstado().name();

        String detalle = detalleDe(animal);
        return "ID: " + animal.getId() + " \n| Tipo: " + tipo + " \n| Nombre: " + nombre
                + " \n| Nacimiento: " + fecha +  detalle + " \n| Estado: " + estado + "\n\n";
    }

    private String tipoDe(Animal animal) {
        if (animal instanceof Perro) {
            return "Perro";
        }
        if (animal instanceof Gato) {
            return "Gato";
        }
        if (animal instanceof Pez) {
            return "Pez";
        }
        return "Otro";
    }

    private String detalleDe(Animal animal) {
        if (animal instanceof Perro perro) {
            return "\n| Raza: " + texto(perro.getRaza()) + " \n| Castrado: " + bool(perro.getCastrado());
        }
        if (animal instanceof Gato gato) {
            return "\n| Pelaje: " + texto(gato.getColorPelaje()) + " \n| Arenero: " + bool(gato.getUsaArenero());
        }
        if (animal instanceof Pez pez) {
            return "\n| Especie: " + texto(pez.getEspecie()) + " \n| Cardumen: " + bool(pez.getEsDeCardumen());
        }
        return "Sin detalle";
    }

    private String texto(String s) {
        return (s == null || s.isBlank()) ? "-" : s;
    }

    private String bool(Boolean b) {
        return (b == null) ? "-" : String.valueOf(b);
    }
}
