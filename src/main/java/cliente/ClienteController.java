package cliente;

import common.FileUtils;
import javax.swing.*;
import java.io.File;
public class ClienteController {

    private final ClienteGUI vista;
    private final ClienteSocket clienteSocket;

    public ClienteController(ClienteGUI vista) {
        this.vista = vista;
        ClienteSocket temp = null;
        try {
            temp = new ClienteSocket();
        } catch (Exception e) {
            mostrarMensaje("Error al conectar con el servidor maestro: " + e.getMessage());
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
                mostrarMensaje("Archivo subido exitosamente.");
            } catch (Exception e) {
                mostrarMensaje("Error al subir archivo: " + e.getMessage());
            }
        }
    }

    public void descargarArchivo(String nombreArchivo) {
        try {
            byte[] datos = clienteSocket.descargarArchivo(nombreArchivo);
            FileUtils.guardarArchivo(nombreArchivo, datos);
            mostrarMensaje("Archivo descargado con éxito.");
        } catch (Exception e) {
            mostrarMensaje("Error al descargar: " + e.getMessage());
        }
    }

    public void cerrarConexion() {
        clienteSocket.cerrarConexion();
    }

    private void mostrarMensaje(String msg) {
        JOptionPane.showMessageDialog(vista, msg);
    }
}
