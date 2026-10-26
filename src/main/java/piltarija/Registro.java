package piltarija;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class Registro extends JFrame {
    private static final Color NEGRO = new Color(16, 24, 32);
    private static final Color AZUL_CLARO = new Color(220, 243, 255);
    private static final Color AZUL = new Color(116, 204, 243);
    private final JTextField campoNombre = new JTextField(22);
    private final JTextField campoUsuario = new JTextField(22);
    private final JTextField campoCorreo = new JTextField(22);
    private final JPasswordField campoContrasena = new JPasswordField(22);
    private final JPasswordField campoConfirmacion = new JPasswordField(22);

    public Registro() {
        setTitle("Crear cuenta");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        Image logo = cargarLogo();
        if (logo != null) {
            setIconImage(logo);
        }
        construirVentana();
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
    }

    private void construirVentana() {
        JPanel fondo = new JPanel(new GridBagLayout());
        fondo.setBackground(AZUL_CLARO);
        JPanel contenido = new JPanel(new GridBagLayout());
        contenido.setBackground(Color.WHITE);
        contenido.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AZUL, 1),
                BorderFactory.createEmptyBorder(20, 28, 24, 28)));
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 0, 5, 0);

        Image logo = cargarLogo();
        if (logo != null) {
            JLabel imagenLogo = new JLabel(new ImageIcon(
                    logo.getScaledInstance(76, 76, Image.SCALE_SMOOTH)));
            imagenLogo.setHorizontalAlignment(JLabel.CENTER);
            c.gridy = 0;
            c.insets = new Insets(0, 0, 5, 0);
            contenido.add(imagenLogo, c);
        }

        JLabel titulo = new JLabel("Crear cuenta", JLabel.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(NEGRO);
        c.gridy = 1;
        c.insets = new Insets(0, 0, 2, 0);
        contenido.add(titulo, c);

        JLabel subtitulo = new JLabel("Únete a Pil Tarija", JLabel.CENTER);
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitulo.setForeground(new Color(75, 105, 125));
        c.gridy = 2;
        c.insets = new Insets(0, 0, 10, 0);
        contenido.add(subtitulo, c);

        agregarCampo(contenido, c, 3, "Nombre completo", campoNombre);
        agregarCampo(contenido, c, 5, "Nombre de usuario", campoUsuario);
        agregarCampo(contenido, c, 7, "Correo electrónico", campoCorreo);
        agregarCampo(contenido, c, 9, "Contraseña", campoContrasena);
        agregarCampo(contenido, c, 11, "Confirmar contraseña", campoConfirmacion);

        JButton botonCrearCuenta = new JButton("Crear cuenta");
        botonCrearCuenta.setFont(new Font("Segoe UI", Font.BOLD, 14));
        botonCrearCuenta.setForeground(NEGRO);
        botonCrearCuenta.setBackground(AZUL);
        botonCrearCuenta.setOpaque(true);
        botonCrearCuenta.setFocusPainted(false);
        botonCrearCuenta.setBorder(BorderFactory.createEmptyBorder(9, 20, 9, 20));
        botonCrearCuenta.addActionListener(evento -> registrarUsuario());
        c.gridy = 13;
        c.insets = new Insets(18, 0, 0, 0);
        contenido.add(botonCrearCuenta, c);

        GridBagConstraints centro = new GridBagConstraints();
        centro.gridx = 0;
        centro.gridy = 0;
        centro.insets = new Insets(16, 16, 16, 16);
        fondo.add(contenido, centro);
        setContentPane(fondo);
        getRootPane().setDefaultButton(botonCrearCuenta);
    }

    private void agregarCampo(JPanel panel, GridBagConstraints c, int fila,
                              String texto, JTextField campo) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(new Font("Segoe UI", Font.BOLD, 13));
        etiqueta.setForeground(NEGRO);
        c.gridy = fila;
        c.insets = new Insets(5, 0, 2, 0);
        panel.add(etiqueta, c);
        c.gridy = fila + 1;
        c.insets = new Insets(0, 0, 5, 0);
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AZUL, 1),
                BorderFactory.createEmptyBorder(7, 8, 7, 8)));
        panel.add(campo, c);
    }

    private Image cargarLogo() {
        try {
            return ImageIO.read(new File("src/assets/logo_piltarija_original.png"));
        } catch (Exception error) {
            return null;
        }
    }

    private void registrarUsuario() {
        String nombre = campoNombre.getText().trim();
        String usuario = campoUsuario.getText().trim();
        String correo = campoCorreo.getText().trim();
        String contrasena = new String(campoContrasena.getPassword());
        String confirmacion = new String(campoConfirmacion.getPassword());

        if (nombre.isEmpty() || usuario.isEmpty() || correo.isEmpty()
                || contrasena.isEmpty() || confirmacion.isEmpty()) {
            mostrarMensaje("Completa todos los campos.", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (nombre.length() > 100 || usuario.length() > 100 || correo.length() > 200) {
            mostrarMensaje("Uno de los campos supera el largo permitido.", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!correo.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            mostrarMensaje("Ingresa un correo electrónico válido.", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (contrasena.length() < 6 || contrasena.length() > 50) {
            mostrarMensaje("La contraseña debe tener entre 6 y 50 caracteres.", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!contrasena.equals(confirmacion)) {
            mostrarMensaje("Las contraseñas no coinciden.", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String buscarDuplicados = "SELECT 1 FROM usuarios WHERE usuario = ? OR correo = ? "
                + "UNION ALL SELECT 1 FROM administrador WHERE usuario = ? OR correo = ? LIMIT 1";
        String insertar = "INSERT INTO usuarios (nombre, usuario, correo, contraseña) VALUES (?, ?, ?, ?)";

        try (Connection conexion = Conexion.conectar();
             PreparedStatement buscar = conexion.prepareStatement(buscarDuplicados)) {
            buscar.setString(1, usuario);
            buscar.setString(2, correo);
            buscar.setString(3, usuario);
            buscar.setString(4, correo);
            try (ResultSet existente = buscar.executeQuery()) {
                if (existente.next()) {
                    mostrarMensaje("Ese usuario o correo ya está registrado.", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            try (PreparedStatement guardar = conexion.prepareStatement(insertar)) {
                guardar.setString(1, nombre);
                guardar.setString(2, usuario);
                guardar.setString(3, correo);
                guardar.setString(4, contrasena);
                guardar.executeUpdate();
            }

            mostrarMensaje("Tu cuenta se creó correctamente. Ya puedes iniciar sesión.",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (SQLException e) {
            mostrarMensaje("No se pudo crear la cuenta. Revisa la conexión con la base de datos.\n"
                    + e.getMessage(), JOptionPane.ERROR_MESSAGE);
        }
    }

    private void mostrarMensaje(String mensaje, int tipo) {
        JOptionPane.showMessageDialog(this, mensaje, "Registro", tipo);
    }
}
