package piltarija;

import java.awt.BorderLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.awt.Color;
import java.awt.Font;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.FlowLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JOptionPane;

public class Login extends javax.swing.JFrame {
    private static final Color NEGRO = new Color(16, 24, 32);
    private static final Color AZUL_CLARO = new Color(220, 243, 255);
    private static final Color AZUL = new Color(116, 204, 243);
    private static final String TEXTO_USUARIO = "Usuario o Correo";
    private static final String TEXTO_CONTRASENA = "Contraseña";
    private final menu menuPublico;
    private final boolean continuarCompra;
    private boolean usuarioEsFantasma = true;
    private boolean contrasenaEsFantasma = true;
    private char caracterEcoContrasena;

    public Login() {
        this(null, false);
    }

    public Login(menu menuPublico) {
        this(menuPublico, false);
    }

    public Login(menu menuPublico, boolean continuarCompra) {
        this.menuPublico = menuPublico;
        this.continuarCompra = continuarCompra;
        initComponents();
        configurarCamposFantasma();
        botonRegistrarse.addActionListener(evento -> new Registro().setVisible(true));
        aplicarEstiloVisual();
        setLocationRelativeTo(menuPublico);
    }

    private void aplicarEstiloVisual() {
        setTitle("Iniciar sesión · Pil Tarija");
        JPanel fondo = new JPanel(new GridBagLayout());
        fondo.setBackground(AZUL_CLARO);
        JPanel tarjeta = new JPanel(new GridBagLayout());
        tarjeta.setBackground(Color.WHITE);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AZUL, 1),
                BorderFactory.createEmptyBorder(24, 32, 26, 32)));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 0, 5, 0);

        Image imagenLogo = cargarLogo();
        if (imagenLogo != null) {
            setIconImage(imagenLogo);
            JLabel logo = new JLabel(new ImageIcon(
                    imagenLogo.getScaledInstance(86, 86, Image.SCALE_SMOOTH)));
            logo.setHorizontalAlignment(JLabel.CENTER);
            c.gridy = 0;
            c.insets = new Insets(0, 0, 8, 0);
            tarjeta.add(logo, c);
        }

        JLabel titulo = new JLabel("Bienvenido", JLabel.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 25));
        titulo.setForeground(NEGRO);
        c.gridy = 1;
        c.insets = new Insets(0, 0, 3, 0);
        tarjeta.add(titulo, c);

        JLabel subtitulo = new JLabel("Inicia sesión para continuar", JLabel.CENTER);
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitulo.setForeground(new Color(75, 105, 125));
        c.gridy = 2;
        c.insets = new Insets(0, 0, 14, 0);
        tarjeta.add(subtitulo, c);

        estilizarCampo(Usuario_Correo);
        estilizarCampo(Contraseña);
        agregarCampo(tarjeta, c, 3, "Usuario o correo", Usuario_Correo);
        agregarCampo(tarjeta, c, 5, "Contraseña", Contraseña);

        botonAcceder.setFont(new Font("Segoe UI", Font.BOLD, 14));
        botonAcceder.setForeground(NEGRO);
        botonAcceder.setBackground(AZUL);
        botonAcceder.setOpaque(true);
        botonAcceder.setFocusPainted(false);
        botonAcceder.setBorder(BorderFactory.createEmptyBorder(9, 20, 9, 20));
        botonRegistrarse.setFont(new Font("Segoe UI", Font.BOLD, 13));
        botonRegistrarse.setForeground(NEGRO);
        botonRegistrarse.setBackground(Color.WHITE);
        botonRegistrarse.setOpaque(true);
        botonRegistrarse.setFocusPainted(false);
        botonRegistrarse.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AZUL, 1),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)));
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        acciones.setOpaque(false);
        acciones.add(botonAcceder);
        acciones.add(botonRegistrarse);
        c.gridy = 7;
        c.insets = new Insets(18, 0, 0, 0);
        tarjeta.add(acciones, c);

        GridBagConstraints centro = new GridBagConstraints();
        centro.gridx = 0;
        centro.gridy = 0;
        centro.insets = new Insets(18, 18, 18, 18);
        fondo.add(tarjeta, centro);
        setContentPane(fondo);
        getRootPane().setDefaultButton(botonAcceder);
        pack();
        setResizable(false);
    }

    private void agregarCampo(JPanel panel, GridBagConstraints c, int fila,
                              String texto, javax.swing.JTextField campo) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(new Font("Segoe UI", Font.BOLD, 13));
        etiqueta.setForeground(NEGRO);
        c.gridy = fila;
        c.insets = new Insets(5, 0, 2, 0);
        panel.add(etiqueta, c);
        c.gridy = fila + 1;
        c.insets = new Insets(0, 0, 5, 0);
        panel.add(campo, c);
    }

    private void estilizarCampo(javax.swing.JTextField campo) {
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        campo.setPreferredSize(new Dimension(320, 40));
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AZUL, 1),
                BorderFactory.createEmptyBorder(8, 9, 8, 9)));
    }

    private Image cargarLogo() {
        try {
            return ImageIO.read(new File("src/assets/logo_piltarija_original.png"));
        } catch (Exception error) {
            return null;
        }
    }

    private void configurarCamposFantasma() {
        caracterEcoContrasena = Contraseña.getEchoChar();
        Usuario_Correo.setForeground(Color.GRAY);
        Usuario_Correo.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent evento) {
                if (usuarioEsFantasma) {
                    Usuario_Correo.setText("");
                    Usuario_Correo.setForeground(Color.BLACK);
                    usuarioEsFantasma = false;
                }
            }

            @Override
            public void focusLost(FocusEvent evento) {
                if (Usuario_Correo.getText().trim().isEmpty()) {
                    Usuario_Correo.setText(TEXTO_USUARIO);
                    Usuario_Correo.setForeground(Color.GRAY);
                    usuarioEsFantasma = true;
                }
            }
        });

        Contraseña.setForeground(Color.GRAY);
        Contraseña.setEchoChar((char) 0);
        Contraseña.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent evento) {
                if (contrasenaEsFantasma) {
                    Contraseña.setText("");
                    Contraseña.setEchoChar(caracterEcoContrasena);
                    Contraseña.setForeground(Color.BLACK);
                    contrasenaEsFantasma = false;
                }
            }

            @Override
            public void focusLost(FocusEvent evento) {
                if (Contraseña.getPassword().length == 0) {
                    Contraseña.setText(TEXTO_CONTRASENA);
                    Contraseña.setEchoChar((char) 0);
                    Contraseña.setForeground(Color.GRAY);
                    contrasenaEsFantasma = true;
                }
            }
        });
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Usuario_Correo = new javax.swing.JTextField();
        botonAcceder = new javax.swing.JButton();
        botonRegistrarse = new javax.swing.JButton();
        Contraseña = new javax.swing.JPasswordField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        Usuario_Correo.setText("Usuario o Correo");

        botonAcceder.setText("Acceder");
        botonAcceder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonAccederActionPerformed(evt);
            }
        });

        botonRegistrarse.setText("Registrarse");

        Contraseña.setText("Contraseña");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(49, 49, 49)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(Usuario_Correo)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(botonAcceder)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 164, Short.MAX_VALUE)
                        .addComponent(botonRegistrarse))
                    .addComponent(Contraseña))
                .addContainerGap(58, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(131, 131, 131)
                .addComponent(Usuario_Correo, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(52, 52, 52)
                .addComponent(Contraseña, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(42, 42, 42)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(botonAcceder)
                    .addComponent(botonRegistrarse))
                .addContainerGap(128, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void botonAccederActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonAccederActionPerformed
        iniciarSesion();
    }//GEN-LAST:event_botonAccederActionPerformed

    private void iniciarSesion() {
        String cuenta = Usuario_Correo.getText().trim();
        String contrasena = new String(Contraseña.getPassword());

        if (cuenta.isEmpty() || cuenta.equals(TEXTO_USUARIO)
                || contrasena.isEmpty() || contrasena.equals(TEXTO_CONTRASENA)) {
            JOptionPane.showMessageDialog(this, "Ingresa tu usuario o correo y tu contraseña.");
            return;
        }

        String sql = "SELECT nombre, usuario, correo, 'usuario' AS rol FROM usuarios "
                + "WHERE (usuario = ? OR correo = ?) AND contraseña = ? "
                + "UNION ALL "
                + "SELECT nombre, usuario, correo, 'admin' AS rol FROM administrador "
                + "WHERE (usuario = ? OR correo = ?) AND contraseña = ?";

        try (Connection cn = Conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, cuenta);
            ps.setString(2, cuenta);
            ps.setString(3, contrasena);
            ps.setString(4, cuenta);
            ps.setString(5, cuenta);
            ps.setString(6, contrasena);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    JOptionPane.showMessageDialog(this, "Usuario, correo o contraseña incorrectos.");
                    return;
                }

                String nombre = rs.getString("nombre");
                String usuario = rs.getString("usuario");
                String correo = rs.getString("correo");
                String rol = rs.getString("rol");
                JOptionPane.showMessageDialog(this, "¡Bienvenido " + nombre + " (" + rol + ")!");
                java.util.Set<String> seleccion = continuarCompra && menuPublico != null
                        ? menuPublico.getCodigosProductosSeleccionados() : null;
                new menu(nombre, usuario, correo, rol, seleccion).setVisible(true);
                if (menuPublico != null) {
                    menuPublico.dispose();
                }
                dispose();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo validar el acceso. Revisa la conexión con la base de datos.\n" + e.getMessage(),
                    "Error de inicio de sesión",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Login().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPasswordField Contraseña;
    private javax.swing.JTextField Usuario_Correo;
    private javax.swing.JButton botonAcceder;
    private javax.swing.JButton botonRegistrarse;
    // End of variables declaration//GEN-END:variables
}
