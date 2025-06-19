package cliente;

import javax.swing.*;
import java.awt.*;
public class ClienteGUI extends JFrame {

    private final ClienteController controller;
    private final JTextField campoNombreArchivo;
    private final JButton botonSubir;
    private final JButton botonDescargar;

    public ClienteGUI() {
        super("Cliente - Sistema Distribuido");

        controller = new ClienteController(this);

        setLayout(new BorderLayout());

        JPanel panelCentro = new JPanel();
        panelCentro.setLayout(new GridLayout(3, 1, 10, 10));

        campoNombreArchivo = new JTextField();
        campoNombreArchivo.setBorder(BorderFactory.createTitledBorder("Nombre del archivo a descargar"));

        botonSubir = new JButton("Subir archivo");
        botonDescargar = new JButton("Descargar archivo");

        panelCentro.add(campoNombreArchivo);
        panelCentro.add(botonSubir);
        panelCentro.add(botonDescargar);

        add(panelCentro, BorderLayout.CENTER);

        botonSubir.addActionListener(e -> controller.subirArchivo());
        botonDescargar.addActionListener(e -> {
            String nombre = campoNombreArchivo.getText();
            if (!nombre.isEmpty()) {
                controller.descargarArchivo(nombre);
            } else {
                JOptionPane.showMessageDialog(this, "Ingrese el nombre del archivo");
            }
        });

        //ventana
        setSize(400, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ClienteGUI::new);
    }

}
