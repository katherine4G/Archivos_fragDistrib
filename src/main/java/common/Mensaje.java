/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package common;
import java.io.Serializable;

public class Mensaje implements Serializable {

    private static final long serialVersionUID = 1L;

    private String cabecera;
    private String nombreArchivo;
    private byte[] datos;
    private int longitud;

    public Mensaje(String cabecera, String nombreArchivo, byte[] datos, int longitud) {
        this.cabecera = cabecera;
        this.nombreArchivo = nombreArchivo;
        this.datos = datos;
        this.longitud = longitud;
    }

    public String getCabecera() {
        return cabecera;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public byte[] getDatos() {
        return datos;
    }

    public int getLongitud() {
        return longitud;
    }
}
