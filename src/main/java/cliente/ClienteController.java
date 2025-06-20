package cliente;

import common.FileUtils;
import javax.swing.*;
import java.io.File;
import java.util.*;

import java.text.SimpleDateFormat;


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

    public void subirArchivo() {
        JFileChooser fileChooser = new JFileChooser();
        int opcion = fileChooser.showOpenDialog(vista);
        if (opcion == JFileChooser.APPROVE_OPTION) {
            File archivo = fileChooser.getSelectedFile();
            try {
                byte[] datos = FileUtils.leerArchivo(archivo);
                clienteSocket.enviarArchivo(archivo.getName(), datos);
                mostrarMensaje("📤 Archivo subido exitosamente.");
                animarProgreso();
                actualizarListaCombinada();
            } catch (Exception e) {
                mostrarMensaje("❌ Error al subir archivo: " + e.getMessage());
            }
        }
    }

    public void descargarArchivo(String nombreArchivo) {
        try {
            byte[] datos = clienteSocket.descargarArchivo(nombreArchivo);
            FileUtils.guardarArchivo(nombreArchivo, datos);
            mostrarMensaje("📥 Archivo descargado con éxito.");
            animarProgreso();
            actualizarListaCombinada();
        } catch (Exception e) {
            mostrarMensaje("❌ Error al descargar: " + e.getMessage());
        }
    }

    public void eliminarArchivo(String nombreArchivo) {
        File archivo = new File("descargas", nombreArchivo);
        if (archivo.exists() && archivo.delete()) {
            mostrarMensaje("🗑️ Archivo eliminado localmente.");
        } else {
            mostrarMensaje("❌ No se pudo eliminar.");
        }
        actualizarListaCombinada();
    }

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

    public void actualizarListaCombinada() {
        Set<String> archivosLocales = new HashSet<>();
        Set<String> archivosRemotos = new HashSet<>();

        File carpeta = new File("descargas");
        if (!carpeta.exists()) carpeta.mkdir();

        String[] locales = carpeta.list();
        if (locales != null) archivosLocales.addAll(Arrays.asList(locales));

        try {
            archivosRemotos.addAll(clienteSocket.listarArchivos());
        } catch (Exception e) {
            mostrarMensaje("⚠️ No se pudo obtener lista remota: " + e.getMessage());
        }

        List<String[]> archivosFinal = new ArrayList<>();
        for (String archivo : archivosLocales) {
            File f = new File("descargas", archivo);
            long tamaño = f.length(); // en bytes
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a"); //formato hora
            String hora = sdf.format(new Date(f.lastModified()));
            archivosFinal.add(new String[]{archivo, "local", String.valueOf(tamaño), hora});
        }

        for (String dato : archivosRemotos) {
            String[] partes = dato.split(";", 3);
            String nombre = partes.length > 0 ? partes[0] : "-";
            String tamaño = partes.length > 1 ? partes[1] : "-";
            String fecha = partes.length > 2 ? partes[2] : "-";

            if (!archivosLocales.contains(nombre)) {
                archivosFinal.add(new String[]{nombre, "remoto", tamaño, fecha});
            }
        }
        
        vista.mostrarArchivosConEstado(archivosFinal);
    }

    public void mostrarMensaje(String msg) {
        vista.mostrarMensaje(msg);
    }

    // barra de progreso
    private void animarProgreso() {
        new Thread(() -> {
            for (int i = 0; i <= 100; i += 5) {
                vista.setProgreso(i);
                try {
                    Thread.sleep(25);
                } catch (InterruptedException ignored) {}
            }

            try {
                Thread.sleep(300);
            } catch (InterruptedException ignored) {}

            vista.setProgreso(0);
        }).start();
    }

}
