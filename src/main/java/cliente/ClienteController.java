package cliente;

import common.FileUtils;
import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ClienteController {

    private final ClienteGUI vista;
    private final ClienteSocket clienteSocket;

    public ClienteController(ClienteGUI vista) {
        this.vista = vista;
        ClienteSocket temp = null;
        try {
            temp = new ClienteSocket();
        } catch (Exception e) {
            mostrarMensaje("❌ Error al conectar con el servidor maestro: " + e.getMessage());
        }
        clienteSocket = temp;
    }

    // Subir archivo desde el sistema local al servidor maestro
    public void subirArchivo() {
        JFileChooser fileChooser = new JFileChooser();
        int opcion = fileChooser.showOpenDialog(vista);
        if (opcion == JFileChooser.APPROVE_OPTION) {
            File archivo = fileChooser.getSelectedFile();
            try {
                byte[] datos = FileUtils.leerArchivo(archivo);
                clienteSocket.enviarArchivo(archivo.getName(), datos);
                mostrarMensaje("📤 Archivo subido exitosamente.");
                actualizarListaCombinada();
            } catch (Exception e) {
                mostrarMensaje("❌ Error al subir archivo: " + e.getMessage());
            }
        }
    }

    // Descargar archivo del sistema distribuido y guardarlo en /descargas
    public void descargarArchivo(String nombreArchivo) {
        try {
            byte[] datos = clienteSocket.descargarArchivo(nombreArchivo);
            FileUtils.guardarArchivo(nombreArchivo, datos);
            mostrarMensaje("📥 Archivo descargado con éxito.");
            actualizarListaCombinada();
        } catch (Exception e) {
            mostrarMensaje("❌ Error al descargar: " + e.getMessage());
        }
    }

    // Mostrar mensaje en la interfaz
    public void mostrarMensaje(String msg) {
        vista.mostrarMensaje(msg);
    }

    // Leer archivos locales desde /descargas y mostrarlos en la tabla
    public void actualizarListaCombinada() {
        Set<String> archivosLocales = new HashSet<>();
        Set<String> archivosRemotos = new HashSet<>();

        // Leer locales
        File carpeta = new File("descargas");
        if (!carpeta.exists()) carpeta.mkdir();

        String[] locales = carpeta.list();
        if (locales != null) archivosLocales.addAll(Arrays.asList(locales));

        // Leer remotos desde el sistema distribuido
        try {
            archivosRemotos.addAll(clienteSocket.listarArchivos());
        } catch (Exception e) {
            mostrarMensaje("⚠️ No se pudo obtener lista remota: " + e.getMessage());
        }

        // Evitar duplicados
        List<String[]> archivosFinal = new ArrayList<>();
        for (String archivo : archivosLocales) {
            archivosFinal.add(new String[]{archivo, "local"});
        }
        for (String archivo : archivosRemotos) {
            if (!archivosLocales.contains(archivo)) {
                archivosFinal.add(new String[]{archivo, "remoto"});
            }
        }

        // Actualizar tabla
        vista.mostrarArchivosConEstado(archivosFinal);
    }

    // Eliminar archivo local
    public void eliminarArchivo(String nombreArchivo) {
        File archivo = new File("descargas", nombreArchivo);
        if (archivo.exists() && archivo.delete()) {
            mostrarMensaje("🗑️ Archivo eliminado localmente.");
        } else {
            mostrarMensaje("❌ No se pudo eliminar.");
        }
        actualizarListaCombinada();
    }

    // Renombrar archivo local
    public void renombrarArchivo(String viejo, String nuevo) {
        File archivoViejo = new File("descargas", viejo);
        File archivoNuevo = new File("descargas", nuevo);

        if (archivoViejo.exists() && archivoViejo.renameTo(archivoNuevo)) {
            mostrarMensaje("✏️ Archivo renombrado localmente.");
        } else {
            mostrarMensaje("❌ Error al renombrar.");
        }
        actualizarListaCombinada();
    }
}