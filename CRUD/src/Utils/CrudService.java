package Utils;

import java.util.List;

public interface CrudService<T, K> {

    void crear(T entidad) throws Exception;

    List<T> leerTodos();

    T leerPorId(K id);

    boolean actualizar(K id, T entidadNueva);

    boolean eliminar(K id);
}
