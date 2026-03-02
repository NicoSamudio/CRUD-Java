package Utils;

import Constantes.FormatoDatos;
import Exceptions.ObjetoRepetidoException;
import Models.Animal;
import Models.Gato;
import Models.Perro;
import Models.Pez;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.swing.filechooser.FileSystemView;

public class AnimalService implements Iterable<Animal>, CrudService<Animal, Integer> {

    private static final List<Animal> animales = new ArrayList<>();

    private final PersistenciaAnimales persistencia = new PersistenciaAnimales("data/animales.json", FormatoDatos.JSON);

    private final Path carpetaExportTxt = FileSystemView.getFileSystemView()
            .getHomeDirectory()
            .toPath()
            .resolve("Datos Exportados");

    public AnimalService() {
        if (animales.isEmpty()) {
            List<Animal> cargados = persistencia.cargar();
            animales.clear();
            animales.addAll(cargados);
        }
    }

    private void guardarCambios() {
        persistencia.guardar(animales);
    }

    // === CRUD UNICO (interfaz) ===
    @Override
    public void crear(Animal animal) throws Exception {
        validarCampos(animal);
        if (existeId(animal.getId())) {
            throw ObjetoRepetidoException.para(animal);
        }

        animales.add(animal);
        guardarCambios();
    }

    @Override
    public List<Animal> leerTodos() {
        return Collections.unmodifiableList(animales);
    }

    @Override
    public Animal leerPorId(Integer id) {
        if (id == null) {
            return null;
        }
        return buscarPorId(id);
    }

    @Override
    public boolean actualizar(Integer id, Animal animalNuevo) {
        try {
            if (id == null || animalNuevo == null) {
                return false;
            }
            if (animalNuevo.getId() != id) {
                throw new IllegalArgumentException("No se puede cambiar el ID.");
            }

            validarCampos(animalNuevo);

            int index = indiceDeId(id);
            if (index == -1) {
                return false;
            }

            animales.set(index, animalNuevo);
            guardarCambios();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean eliminar(Integer id) {
        if (id == null) {
            return false;
        }

        Iterator<Animal> it = animales.iterator();
        while (it.hasNext()) {
            if (it.next().getId() == id) {
                it.remove();
                guardarCambios();
                return true;
            }
        }
        return false;
    }

    // Extras útiles (NO redundan CRUD)
    public void agregarAnimales(List<? extends Animal> lista) throws Exception {
        if (lista == null || lista.isEmpty()) {
            return;
        }
        for (Animal animal : lista) {
            crear(animal);
        }
    }

    public void copiarA(Collection<? super Animal> destino) {
        if (destino == null) {
            return;
        }
        destino.addAll(animales);
    }

    public List<Animal> filtrar(List<? extends Animal> origen, Predicate<? super Animal> criterio) {
        if (origen == null) {
            return List.of();
        }
        if (criterio == null) {
            return new ArrayList<>(origen);
        }

        List<Animal> salida = new ArrayList<>();
        for (Animal animal : origen) {
            if (criterio.test(animal)) {
                salida.add(animal);
            }
        }
        return salida;
    }

    public List<Animal> filtrarActual(Predicate<? super Animal> criterio) {
        return filtrar(leerTodos(), criterio);
    }

    public String exportarListadoATxt(Predicate<? super Animal> criterio, String descripcionFiltro) {
        List<Animal> filtrados = filtrarActual(criterio);

        try {
            Files.createDirectories(carpetaExportTxt);
        } catch (Exception e) {
            throw new RuntimeException("No se pudo crear la carpeta de exportación: " + carpetaExportTxt.toAbsolutePath(), e);
        }

        Path archivo = carpetaExportTxt.resolve("reporte_animales.txt");  /// estar dentro de una excepcion (se puede mejorar) cacht general'''

        persistencia.exportarTxtLegible(
                filtrados,
                archivo.toString(),
                "Listado filtrado de animales",
                descripcionFiltro
        );

        return archivo.toAbsolutePath().toString();
    }

    // == Modificaciones funcionales ===
    public boolean modificarBasico(int id, String nuevoNombre, LocalDate nuevaFechaNacimiento) {
        Animal animal = buscarPorId(id);
        if (animal == null) {
            return false;
        }

        ModificadorAnimal.modificarBasico(animal, nuevoNombre, nuevaFechaNacimiento);
        guardarCambios();
        return true;
    }

    private <T extends Animal> boolean modificarSiEs(int id, Class<T> tipoEsperado, Consumer<T> cambios) {
        Animal animal = buscarPorId(id);
        if (animal == null || !tipoEsperado.isInstance(animal)) {
            return false;
        }

        cambios.accept(tipoEsperado.cast(animal));
        guardarCambios();
        return true;
    }

    public boolean modificarPerro(int id, String raza, Boolean castrado) {
        return modificarSiEs(id, Perro.class, ModificadorAnimal.cambiosPerro(raza, castrado));
    }

    public boolean modificarGato(int id, String colorPelaje, Boolean usaArenero) {
        return modificarSiEs(id, Gato.class, ModificadorAnimal.cambiosGato(colorPelaje, usaArenero));
    }

    public boolean modificarPez(int id, String especie, Boolean esDeCardumen) {
        return modificarSiEs(id, Pez.class, ModificadorAnimal.cambiosPez(especie, esDeCardumen));
    }

    // == = Internos ===
    private Animal buscarPorId(int id) {
        for (Animal animal : animales) {
            if (animal.getId() == id) {
                return animal;
            }
        }
        return null;
    }

    private boolean existeId(int id) {
        return buscarPorId(id) != null;
    }

    private int indiceDeId(int id) {
        for (int i = 0; i < animales.size(); i++) {
            if (animales.get(i).getId() == id) {
                return i;
            }
        }
        return -1;
    }

    private void validarCampos(Animal animal) {
        ValidadorAnimal.validar(animal);
    }

    //=== Iterator propio ===
    @Override
    public Iterator<Animal> iterator() {
        return new IteradorAnimales();
    }

    private class IteradorAnimales implements Iterator<Animal> {

        private int indice = 0;

        @Override
        public boolean hasNext() {
            return indice < animales.size();
        }

        @Override
        public Animal next() {
            if (!hasNext()) {
                throw new NoSuchElementException("No hay más elementos");
            }
            return animales.get(indice++);
        }

        @Override
        public void remove() {
            if (indice == 0) {
                throw new IllegalStateException("Llamá next() antes de remove().");
            }
            animales.remove(indice - 1);
            indice--;
            guardarCambios();
        }
    }
}
