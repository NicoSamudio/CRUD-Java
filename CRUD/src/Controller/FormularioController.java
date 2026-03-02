
package Controller;

import Constantes.AnimalTipo;
import Constantes.EstadoRegistro;
import Constantes.TextosUI;
import Exceptions.ExceptionHelper;
import Models.Animal;
import Utils.AnimalService;
import Utils.FormularioMapper;
import Utils.Sorter;
import Utils.ValidadorAnimal;
import Utils.VistaHelper;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Predicate;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.util.StringConverter;

public class FormularioController implements Initializable {

    private final AnimalService animalService = new AnimalService();

    private boolean enEdicion = false;
    private int idEnEdicion = -1;

    @FXML private TextField txtId;
    @FXML private TextField txtNombre;
    @FXML private DatePicker datePicker;

    @FXML private ChoiceBox<AnimalTipo> cbTipo;
    @FXML private TextField txtDato1;

    @FXML private RadioButton btnSi;
    @FXML private RadioButton btnNo;

    // Estado del animal (CRUD)
    @FXML private Label lblEstado;
    @FXML private RadioButton btnSi2; // Activo
    @FXML private RadioButton btnNo2; // Inactivo

    // Orden
    @FXML private ChoiceBox<String> cbOrden;
    @FXML private RadioButton btnA;
    @FXML private RadioButton btnZ;

    @FXML private Label lblDato1;
    @FXML private Label lblDato2;

    @FXML private ListView<Animal> listViewDatos;

    @FXML private Button btnAgregar;
    @FXML private Button btnEliminar;
    @FXML private Button btnModificar;
    @FXML private Button btnExportar;

    // Filtros
    @FXML private ChoiceBox<AnimalTipo> cbAnimalTipo;       
    @FXML private ChoiceBox<EstadoRegistro> cbEstado;       
    @FXML private Button btnAplicar;
    @FXML private Button btnLimpiar;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // === Tipo (alta/modificación) ===
        cbTipo.getItems().setAll(AnimalTipo.values());
        cbTipo.setConverter(new StringConverter<>() {
            @Override public String toString(AnimalTipo t) { return (t == null) ? "" : t.texto(); }
            @Override public AnimalTipo fromString(String s) { return null; }
        });
        cbTipo.setValue(AnimalTipo.PERRO);
        actualizarEtiquetas();
        cbTipo.setOnAction(e -> actualizarEtiquetas());


         // === Orden ===
        cbOrden.getItems().addAll(TextosUI.ORDEN_ID, TextosUI.ORDEN_NOMBRE, TextosUI.ORDEN_FECHA_NACIMIENTO);
        cbOrden.setValue(TextosUI.ORDEN_PLACEHOLDER);

        ToggleGroup grupoAZ = new ToggleGroup();
        btnA.setToggleGroup(grupoAZ);
        btnZ.setToggleGroup(grupoAZ);
        grupoAZ.selectToggle(null);

        cbOrden.setOnAction(e -> ordenarSegunControles());
        btnA.setOnAction(e -> ordenarSegunControles());
        btnZ.setOnAction(e -> ordenarSegunControles());

 
        // ===Grupo de botones Sí/No dato2 ===
        ToggleGroup grupoSiNo = new ToggleGroup();
        btnSi.setToggleGroup(grupoSiNo);
        btnNo.setToggleGroup(grupoSiNo);
        grupoSiNo.selectToggle(null);


        // === Estado CRUD (Activo/Inactivo) ===
        ToggleGroup grupoEstadoCrud = new ToggleGroup();
        btnSi2.setToggleGroup(grupoEstadoCrud);
        btnNo2.setToggleGroup(grupoEstadoCrud);
        grupoEstadoCrud.selectToggle(null);
        
        if (lblEstado != null) lblEstado.setText("Activo");

        btnSi2.setOnAction(e -> { if (lblEstado != null) lblEstado.setText("Activo"); });
        btnNo2.setOnAction(e -> { if (lblEstado != null) lblEstado.setText("Inactivo"); });

        // === Filtros (enums + placeholder) ===

        cbAnimalTipo.getItems().setAll(AnimalTipo.values());
        cbAnimalTipo.setConverter(new StringConverter<>() {
            @Override public String toString(AnimalTipo t) {
                return (t == null) ? TextosUI.CB_TIPO : t.texto();
            }
            @Override public AnimalTipo fromString(String s) { return null; }
        });
        cbAnimalTipo.setValue(null); 

        cbEstado.getItems().setAll(EstadoRegistro.values());
        cbEstado.setConverter(new StringConverter<>() {
            @Override public String toString(EstadoRegistro e) {
                if (e == null) return TextosUI.CB_ESTADO;
                return (e == EstadoRegistro.ACTIVO) ? "Activo" : "Inactivo";
            }
            @Override public EstadoRegistro fromString(String s) { return null; }
        });
        cbEstado.setValue(null); 

        btnAplicar.setOnAction(e -> aplicarFiltro());
        btnLimpiar.setOnAction(e -> limpiarFiltros());

        refrescarLista();
    }

        // === Acciones: Agregar, modificar, eliminar, exportar ===
    @FXML
    void agregar(ActionEvent e) {
        ExceptionHelper.runGuarded(() -> {

            Animal nuevo = construirAnimalDesdeFormulario();
            nuevo.setEstado(leerEstadoCrud());
            ValidadorAnimal.validar(nuevo);
            

            if (enEdicion) {
                boolean ok = animalService.actualizar(idEnEdicion, nuevo);
                if (!ok) {
                    mostrarError(TextosUI.MSG_NO_SE_PUDO_ACTUALIZAR);
                    return;
                }
                postAccionUi(true, TextosUI.MSG_GUARDADO_OK);
            } else {
                animalService.crear(nuevo);
                postAccionUi(true, TextosUI.MSG_AGREGADO_OK);
            }
        });
    }

    @FXML
    void modificar(ActionEvent e) {
        Animal seleccionado = listViewDatos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarError(TextosUI.MSG_SELECCIONA_PRIMERO);
            return;
        }

        cargarFormularioDesde(seleccionado);

        enEdicion = true;
        idEnEdicion = seleccionado.getId();

        txtId.setDisable(true);
        cbTipo.setDisable(true);

        mostrarInfo(TextosUI.MSG_MODO_EDICION);
    }

    @FXML
    void eliminar(ActionEvent e) {
        ExceptionHelper.runGuarded(() -> {
            Animal seleccionado = listViewDatos.getSelectionModel().getSelectedItem();
            if (seleccionado == null) {
                mostrarError(TextosUI.MSG_SELECCIONA_PARA_ELIMINAR);
                return;
            }

            if (!confirmarEliminacion(seleccionado)) return;

            boolean ok = animalService.eliminar(seleccionado.getId());
            if (!ok) {
                mostrarError("No se pudo eliminar. Puede que el registro ya no exista.");
                return;
            }

            postAccionUi(true, TextosUI.MSG_ELIMINADO_OK);
        });
    }

    @FXML
    void exportarTxt(ActionEvent e) {
        ExceptionHelper.runGuarded(() -> {
            animalService.exportarListadoATxt(a -> true, "Todos los animales");
            mostrarInfo("TXT exportado correctamente.\nGuardado en: Escritorio → Datos Exportados");
        });
    }

    //=== FILTRO DOBLE ===
    private void aplicarFiltro() {
        AnimalTipo tipoFiltro = cbAnimalTipo.getValue();        
        EstadoRegistro estadoFiltro = cbEstado.getValue();      

        Predicate<Animal> filtro = a -> true;

        if (tipoFiltro != null) {
            filtro = filtro.and(a -> a.getTipo() == tipoFiltro);
        }
        if (estadoFiltro != null) {
            filtro = filtro.and(a -> a.getEstado() == estadoFiltro);
        }

        List<Animal> filtrados = animalService.filtrarActual(filtro);
        VistaHelper.refrescarLista(listViewDatos, filtrados);

        ordenarSegunControles();
    }

    private void limpiarFiltros() {
        cbAnimalTipo.setValue(null);
        cbEstado.setValue(null);
        refrescarLista();
    }


    // === ORDEN ===
    private void ordenarSegunControles() {
        String criterioTexto = cbOrden.getValue();

        if (criterioTexto == null || criterioTexto.isBlank() || TextosUI.ORDEN_PLACEHOLDER.equals(criterioTexto)) {
            return;
        }

        List<Animal> listaActual = new ArrayList<>(listViewDatos.getItems());

        Sorter.Criterio criterio = mapearCriterio(criterioTexto);
        Sorter.Direccion direccion = btnA.isSelected() ? Sorter.Direccion.ASC : Sorter.Direccion.DESC;

        Sorter.ordenar(listaActual, criterio, direccion);
        VistaHelper.refrescarLista(listViewDatos, listaActual);
    }

    private Sorter.Criterio mapearCriterio(String criterioTexto) {
        if (TextosUI.ORDEN_ID.equals(criterioTexto)) return Sorter.Criterio.ID;
        if (TextosUI.ORDEN_NOMBRE.equals(criterioTexto)) return Sorter.Criterio.NOMBRE;
        if (TextosUI.ORDEN_FECHA_NACIMIENTO.equals(criterioTexto)) return Sorter.Criterio.FECHA;
        return Sorter.Criterio.ID;
    }


    //=== LISTA ===
    private void refrescarLista() {
        List<Animal> base = new ArrayList<>(animalService.leerTodos());
        VistaHelper.refrescarLista(listViewDatos, base);
    }

    private void postAccionUi(boolean salirEdicion, String infoMsg) {
        limpiarFormulario();
        listViewDatos.getSelectionModel().clearSelection();
        refrescarLista();

        if (salirEdicion) salirModoEdicion();

        if (infoMsg != null && !infoMsg.isBlank()) {
            mostrarInfo(infoMsg);
        }
    }

    private void salirModoEdicion() {
        enEdicion = false;
        idEnEdicion = -1;
        txtId.setDisable(false);
        cbTipo.setDisable(false);
    }

    
    // === CONFIRMACIONES ===

    private boolean confirmar(String titulo, String cuerpo) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(cuerpo);
        Optional<ButtonType> r = alerta.showAndWait();
        return r.isPresent() && r.get() == ButtonType.OK;
    }

    private boolean confirmarEliminacion(Animal animal) {
        return confirmar(TextosUI.TIT_CONFIRMAR_ELIMINACION,
                "¿Eliminar el animal ID " + animal.getId() + " (" + animal.getNombre() + ")?");
    }


    // === ETIQUETAS ===
    private void actualizarEtiquetas() {
        AnimalTipo tipo = cbTipo.getValue();

        if (tipo == null) {
            lblDato1.setText(TextosUI.LBL_DEFAULT);
            lblDato2.setText(TextosUI.LBL_DEFAULT);
            return;
        }

        switch (tipo) {
            case PERRO -> {
                lblDato1.setText(TextosUI.LBL_RAZA);
                lblDato2.setText(TextosUI.LBL_CASTRADO);
            }
            case GATO -> {
                lblDato1.setText(TextosUI.LBL_COLOR_PELAJE);
                lblDato2.setText(TextosUI.LBL_USA_ARENERO); 
            }
            case PEZ -> {
                lblDato1.setText(TextosUI.LBL_ESPECIE);
                lblDato2.setText(TextosUI.LBL_ES_CARDUMEN);
            }
        }
    }

    
    //=== Mapper para crear el animal ===
    private Animal construirAnimalDesdeFormulario() {
        return FormularioMapper.construirAnimal(
                txtId.getText(),
                txtNombre.getText(),
                datePicker.getValue(),
                cbTipo.getValue(),     
                txtDato1.getText(),
                leerSiNo()
        );
    }

    private void cargarFormularioDesde(Animal animal) {
        FormularioMapper.FormularioData data = FormularioMapper.aFormularioData(animal);

        txtId.setText(data.idTexto);
        txtNombre.setText(data.nombreTexto);
        datePicker.setValue(data.fechaNacimiento);

        cbTipo.setValue(animal.getTipo());
        actualizarEtiquetas();

        txtDato1.setText(data.dato1Texto);
        setSiNo(data.dato2SiNo);

        EstadoRegistro est = animal.getEstado();
        if (est == EstadoRegistro.INACTIVO) {
            btnNo2.setSelected(true);
            if (lblEstado != null) lblEstado.setText("Inactivo");
        } else {
            btnSi2.setSelected(true);
            if (lblEstado != null) lblEstado.setText("Activo");
        }
    }

    private void limpiarFormulario() {
        txtId.clear();
        txtNombre.clear();
        datePicker.setValue(null);
        txtDato1.clear();
        resetSiNo();

        cbTipo.setValue(AnimalTipo.PERRO);
        actualizarEtiquetas();

        cbOrden.setValue(TextosUI.ORDEN_PLACEHOLDER);
        btnA.setSelected(false);
        btnZ.setSelected(false);

        btnSi2.setSelected(false);
        btnNo2.setSelected(false);
        if (lblEstado != null) {
            lblEstado.setText("Activo");
        }
    }


    // === Mensajes ===

    private void mostrarError(String mensaje) {
        Alert a = new Alert(Alert.AlertType.ERROR, mensaje, ButtonType.OK);
        a.setHeaderText(TextosUI.HDR_VALIDACION);
        a.showAndWait();
    }

    private void mostrarInfo(String mensaje) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, mensaje, ButtonType.OK);
        a.setHeaderText(null);
        a.showAndWait();
    }

     //===  Sí/No (dato2) ===
    private Boolean leerSiNo() {
        if (btnSi.isSelected()) return true;
        if (btnNo.isSelected()) return false;
        return null;
    }

    private void resetSiNo() {
        btnSi.setSelected(false);
        btnNo.setSelected(false);
        btnSi.setDisable(false);
        btnNo.setDisable(false);
    }

    private void setSiNo(Boolean b) {
        resetSiNo();
        if (b == null) return;
        if (b) btnSi.setSelected(true);
        else btnNo.setSelected(true);
    }


    // === Estado CRUD (btnSi2/btnNo2) ===
private EstadoRegistro leerEstadoCrud() {
    if (btnSi2 != null && btnSi2.isSelected()) return EstadoRegistro.ACTIVO;
    if (btnNo2 != null && btnNo2.isSelected()) return EstadoRegistro.INACTIVO;

    throw new IllegalArgumentException("Seleccioná el estado del registro (Activo/Inactivo).");
}
}