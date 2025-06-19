package common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtils {

    public static byte[] leerArchivo(File archivo) throws IOException {
        try (FileInputStream fis = new FileInputStream(archivo)) {
            byte[] datos = new byte[(int) archivo.length()];
            fis.read(datos);
            return datos;
        }
    }

    public static void guardarArchivo(String nombre, byte[] datos) throws IOException {
        guardarEnCarpeta("descargas", nombre, datos);
    }


    public static void guardarFragmento(String nombre, byte[] datos) throws IOException {
        guardarEnCarpeta("fragmentos", nombre, datos);
    }

    public static byte[] leerFragmento(String nombre) throws IOException {
        File archivo = new File("fragmentos", nombre);
        return leerArchivo(archivo);
    }

    private static void guardarEnCarpeta(String nombreCarpeta, String nombreArchivo, byte[] datos) throws IOException {
        File carpeta = new File(nombreCarpeta);
        if (!carpeta.exists()) {
            carpeta.mkdir();
        }

        File destino = new File(carpeta, nombreArchivo);
        try (FileOutputStream fos = new FileOutputStream(destino)) {
            fos.write(datos);
        }

        System.out.println("Archivo guardado en: " + destino.getAbsolutePath());
    }
}
