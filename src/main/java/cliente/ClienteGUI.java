package cliente;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ClienteGUI extends JFrame {

    private final ClienteController controller;
    private final JTable tablaArchivos;
    private final DefaultTableModel modeloTabla;
    private final JButton botonSubir;
    private final JButton botonDescargar;
    private final JButton botonEliminar;
    private final JButton botonRenombrar;
    private final JButton botonRefrescar ;
    private final JTextField campoNuevoNombre;
    private final JProgressBar barraProgreso;
    private final JTextArea areaMensajes;

    public ClienteGUI() {
        super("Cliente - Sistema Distribuido");

        controller = new ClienteController(this);

        setLayout(new BorderLayout(10, 10));

        // Tabla
        modeloTabla = new DefaultTableModel(new String[]{"Archivo", "Estado"}, 0);
        tablaArchivos = new JTable(modeloTabla);
        JScrollPane scrollTabla = new JScrollPane(tablaArchivos);

        // Panel botones
        JPanel panelBotones = new JPanel(new GridLayout(2, 2, 5, 5));
        botonSubir = new JButton("\uD83D\uDCE4 Subir archivo");
        botonDescargar = new JButton("\uD83D\uDCE5 Descargar");
        botonEliminar = new JButton("\u274C Eliminar");
        botonRenombrar = new JButton("\u2014 Renombrar");
        botonRefrescar = new JButton("🔄");
        panelBotones.add(botonSubir);
        panelBotones.add(botonDescargar);
        panelBotones.add(botonEliminar);
        panelBotones.add(botonRenombrar);
        panelBotones.add(botonRefrescar);

        // Campo nuevo nombre
        campoNuevoNombre = new JTextField();
        campoNuevoNombre.setBorder(BorderFactory.createTitledBorder("Nuevo nombre"));

        // Barra progreso
        barraProgreso = new JProgressBar();
        barraProgreso.setStringPainted(true);
        barraProgreso.setValue(0);

        // área mensajes
        areaMensajes = new JTextArea(5, 20);
        areaMensajes.setEditable(false);
        JScrollPane scrollMensajes = new JScrollPane(areaMensajes);
        scrollMensajes.setBorder(BorderFactory.createTitledBorder("Mensajes"));

        // Panel inferior
        JPanel panelInferior = new JPanel(new BorderLayout(5, 5));
        panelInferior.add(campoNuevoNombre, BorderLayout.NORTH);
        panelInferior.add(barraProgreso, BorderLayout.CENTER);
        panelInferior.add(scrollMensajes, BorderLayout.SOUTH);

        // Agregar al frame
        add(scrollTabla, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.NORTH);
        add(panelInferior, BorderLayout.SOUTH);

        // Listeners
        botonSubir.addActionListener(e -> controller.subirArchivo());

        botonDescargar.addActionListener(e -> {
            String nombre = obtenerArchivoSeleccionado();
            if (nombre == null || nombre.isEmpty()) {
                nombre = campoNuevoNombre.getText();
            }
            if (nombre != null && !nombre.isEmpty()) {
                controller.descargarArchivo(nombre);
            } else {
                controller.mostrarMensaje("Seleccione un archivo o escriba su nombre para descargar.");
            }
        });

        botonEliminar.addActionListener(e -> {
            String nombre = obtenerArchivoSeleccionado();
            if (nombre != null) {
                controller.eliminarArchivo(nombre);
            } else {
                controller.mostrarMensaje("Seleccione un archivo local para eliminar.");
            }
        });

        botonRenombrar.addActionListener(e -> {
            String seleccionado = obtenerArchivoSeleccionado();
            String nuevoNombre = campoNuevoNombre.getText();
            if (seleccionado != null && !nuevoNombre.isEmpty()) {
                controller.renombrarArchivo(seleccionado, nuevoNombre);
            } else {
                controller.mostrarMensaje("Seleccione un archivo local y escriba el nuevo nombre.");
            }
        });
        
        botonRefrescar.addActionListener(e -> controller.actualizarListaCombinada());

        setSize(550, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);

        controller.actualizarListaCombinada();
    }

    public void mostrarArchivosConEstado(List<String[]> archivos) {
        modeloTabla.setRowCount(0);
        for (String[] fila : archivos) {
            modeloTabla.addRow(fila);
        }
    }

    public String obtenerArchivoSeleccionado() {
        int fila = tablaArchivos.getSelectedRow();
        return fila >= 0 ? modeloTabla.getValueAt(fila, 0).toString() : null;
    }

    public void mostrarMensaje(String msg) {
        areaMensajes.append(msg + "\n");
    }

    public void actualizarProgreso(int valor) {
        barraProgreso.setValue(valor);
    }

    public void setProgreso(int porcentaje) {
        barraProgreso.setValue(porcentaje);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ClienteGUI::new);
    }
}