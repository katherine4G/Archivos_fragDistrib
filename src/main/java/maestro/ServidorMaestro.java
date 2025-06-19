package maestro;

import common.Constantes;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorMaestro {

    public static void main(String[] args) {
        System.out.println("Servidor Maestro escuchando en puerto " + Constantes.PUERTO_MAESTRO + "...");
        System.out.println("Ahora, ejecute los nodos. package:secundario > ServidorSecundario.java");
        try (ServerSocket serverSocket = new ServerSocket(Constantes.PUERTO_MAESTRO)) {
            while (true) {
                Socket socketCliente = serverSocket.accept();
                System.out.println("Nueva conexión de cliente: " + socketCliente.getInetAddress());

                // Hilo que se encarga de manejar ese cliente
                ConexionClienteHandler handler = new ConexionClienteHandler(socketCliente);
                handler.start();
            }

        } catch (IOException e) {
            System.err.println("Error al iniciar el servidor maestro: " + e.getMessage());
        }
    }
}
