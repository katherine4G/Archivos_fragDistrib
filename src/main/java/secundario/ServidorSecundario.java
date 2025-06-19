package secundario;

import common.Constantes;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class ServidorSecundario {

    public static void main(String[] args) {
        String[] argumentos = {"2"};  // cambiar nodo 1, 2 o 3
        args = argumentos;

        int puerto = obtenerPuertoDesdeArgs(args);
        System.out.println("Nodo: " + Arrays.toString(argumentos));
        System.out.println("Servidor Secundario escuchando en puerto " + puerto);


        try (ServerSocket servidor = new ServerSocket(puerto)) {
            while (true) {
                Socket cliente = servidor.accept();
                new ManejadorSecundario(cliente).start();
            }
        } catch (IOException e) {
            System.err.println("Error en servidor secundario: " + e.getMessage());
        }
    }

    private static int obtenerPuertoDesdeArgs(String[] args) {
        if (args.length == 0) return Constantes.PUERTO_SEC1;
        return switch (args[0]) {
            case "1" -> Constantes.PUERTO_SEC1;
            case "2" -> Constantes.PUERTO_SEC2;
            case "3" -> Constantes.PUERTO_SEC3;
            default -> Constantes.PUERTO_SEC1;
        };
    }
}
