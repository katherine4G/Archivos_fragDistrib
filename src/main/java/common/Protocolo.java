/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package common;

/**
 *
 * @author kathe
 */

public class Protocolo {
    public static final String UPLOAD = "UPLOAD";    // Subir archivo           
    public static final String DOWNLOAD = "DOWNLOAD";        
    public static final String STORE = "STORE"; // Guardar fragmento de Maestro a nodo.
    public static final String FRAG_REQUEST = "FRAG_REQUEST"; // Solicitar fragmento (Maestro → nodo)
    // faltan:
    public static final String DELETE = "DELETE";  // Eliminar archivo
    public static final String RENAME = "RENAME";   // Renombrar archivo
    public static final String LIST = "LIST"; // Solicitar lista de archivos disponibles
}
