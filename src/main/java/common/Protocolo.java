package common;

/**
 *
 * @author kathe
 */

public class Protocolo {
    public static final String UPLOAD = "UPLOAD";    // subir archivo           
    public static final String DOWNLOAD = "DOWNLOAD";        
    public static final String STORE = "STORE"; // guardar fragmento de Maestro a nodo
    public static final String FRAG_REQUEST = "FRAG_REQUEST"; // solicitar ese fragmento
    // faltan:
    public static final String DELETE = "DELETE";  // eliminar archivo
    public static final String RENAME = "RENAME";   // renombrar archivo
    public static final String LIST = "LIST"; // pedir lista de archivos disponibles
}
