package piltarija;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.AlphaComposite;
import java.awt.Font;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Toolkit;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Set;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.GrayFilter;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.JToggleButton;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
    
public class menu extends javax.swing.JFrame {
    private static final String EMISOR_FACTURA = "Pil Tarija S.A.";
    private static final Color NEGRO = new Color(16, 24, 32);
    private static final Color NEGRO_SUAVE = new Color(28, 40, 52);
    private static final Color AZUL_CLARO = new Color(220, 243, 255);
    private static final Color AZUL = new Color(116, 204, 243);
    private static final Color BLANCO = Color.WHITE;
    private final String nombreUsuario;
    private final String usuario;
    private final String correoUsuario;
    private final String rolUsuario;
    private JPanel panelProductos;
    private JScrollPane scrollProductos;
    private JPanel panelVistas;
    private JPanel panelControlesCabecera;
    private CardLayout layoutVistas;
    private CardLayout layoutCabecera;
    private final Set<String> codigosProductosSeleccionados = new LinkedHashSet<>();
    private final List<JToggleButton> tarjetasProductos = new ArrayList<>();
    private DefaultTableModel modeloProductos;
    private JTable tablaProductos;
    private JPanel contenidoPedidos;
    private JPanel panelPedidos;
    private JLabel resumenHistorialPedidos;
    private JPanel listaFacturasPerfil;
    private DefaultTableModel modeloPedido;
    private JLabel mensajePedidos;
    private JLabel totalPedido;
    private String codigoProductoSeleccionadoPedido;
    private final Map<String, ItemPedido> productosEnPedido = new LinkedHashMap<>();
    private final List<Factura> historialFacturas = new ArrayList<>();
    private int siguienteNumeroFactura = 1;

    public menu() {
        this(null, null, null, null);
    }

    public menu(String rolUsuario) {
        this(null, null, null, rolUsuario);
    }

    public menu(String nombreUsuario, String rolUsuario) {
        this(nombreUsuario, null, null, rolUsuario);
    }

    public menu(String nombreUsuario, String usuario, String correoUsuario, String rolUsuario) {
        this(nombreUsuario, usuario, correoUsuario, rolUsuario, null);
    }

    public menu(String nombreUsuario, String usuario, String correoUsuario, String rolUsuario,
                Set<String> productosSeleccionados) {
        this.nombreUsuario = nombreUsuario;
        this.usuario = usuario;
        this.correoUsuario = correoUsuario;
        this.rolUsuario = rolUsuario;
        initComponents();
        aplicarEstiloMenu();
        Image iconoPrograma = leerImagen("src/assets/logo_piltarija_original.png");
        if (iconoPrograma != null) {
            setIconImage(iconoPrograma);
        }
        configurarPanelProductos();
        configurarBusquedaProductos();
        botonCatalogo.addActionListener(evt -> mostrarBusquedaCatalogo(true));
        botonPedidos.addActionListener(this::botonPedidosActionPerformed);
        escalarImagen(logo, "src/assets/logo_piltarija_original.png", 48, 48);
        escalarImagenBoton(botonInicio, "src/assets/user.png");
        escalarImagenBoton(botonCatalogo, "src/assets/other.png");
        escalarImagenBoton(botonPedidos, "src/assets/buy.png");
        escalarImagenBoton(botonPerfil, "src/assets/user.png");
        escalarImagenBoton(botonCerrarSesion, "src/assets/exit.png");
        escalarImagenBoton(botonperfil, "src/assets/user.png");
        botonCerrarSesion.setVisible(rolUsuario != null);
        setLocationRelativeTo(null);
        panelProductos.add(new JLabel("Cargando productos...", SwingConstants.CENTER));
        if (productosSeleccionados != null) {
            codigosProductosSeleccionados.addAll(productosSeleccionados);
        }
        javax.swing.SwingUtilities.invokeLater(() -> {
            cargarProductos();
            if (productosSeleccionados != null && !productosSeleccionados.isEmpty()) {
                agregarProductosSeleccionados();
            }
        });
    }

    private void aplicarEstiloMenu() {
        getContentPane().setBackground(new Color(247, 251, 254));
        jPanel2.setBackground(NEGRO);
        jLabel1.setForeground(AZUL);
        jLabel1.setFont(new Font("Segoe UI", Font.BOLD, 18));
        jSeparator1.setForeground(new Color(65, 91, 112));
        jSeparator2.setForeground(AZUL);
        jSeparator3.setForeground(AZUL);
        estilizarBotonNavegacion(botonInicio);
        estilizarBotonNavegacion(botonCatalogo);
        estilizarBotonNavegacion(botonPedidos);
        estilizarBotonNavegacion(botonPerfil);
        estilizarBotonNavegacion(botonCerrarSesion);

        botonperfil.setBackground(AZUL_CLARO);
        botonperfil.setOpaque(true);
        botonperfil.setFocusPainted(false);
        botonperfil.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AZUL, 1),
                BorderFactory.createEmptyBorder(3, 5, 3, 5)));
        BuscarProductos.setBackground(BLANCO);
        BuscarProductos.setForeground(new Color(110, 120, 130));
        BuscarProductos.setCaretColor(NEGRO);
        BuscarProductos.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AZUL, 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));
    }

    private void estilizarBotonNavegacion(JButton boton) {
        boton.setBackground(NEGRO_SUAVE);
        boton.setForeground(BLANCO);
        boton.setOpaque(true);
        boton.setContentAreaFilled(true);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(55, 72, 88), 1),
                BorderFactory.createEmptyBorder(7, 10, 7, 10)));
        boton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evento) {
                boton.setBackground(AZUL);
                boton.setForeground(NEGRO);
            }

            @Override
            public void mouseExited(MouseEvent evento) {
                boton.setBackground(NEGRO_SUAVE);
                boton.setForeground(BLANCO);
            }
        });
    }


    private void configurarPanelProductos() {
        panelProductos = new JPanel(new GridLayout(0, 3, 12, 12));
        panelProductos.setBackground(Color.WHITE);
        panelProductos.setOpaque(false);
        scrollProductos = new JScrollPane(panelProductos);
        scrollProductos.setBorder(BorderFactory.createEmptyBorder());
        scrollProductos.setOpaque(false);
        scrollProductos.getViewport().setOpaque(false);

        JLabel tituloDescubrir = new JLabel("Descubrir");
        tituloDescubrir.setFont(new Font("Segoe UI", Font.BOLD, 32));
        tituloDescubrir.setForeground(NEGRO);

        JLabel saludo = new JLabel(crearTextoSaludo());
        saludo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        saludo.setForeground(NEGRO_SUAVE);

        JPanel panelInicio = new JPanel(new BorderLayout());
        panelInicio.setBackground(Color.WHITE);
        JPanel textoInicio = new JPanel(new BorderLayout());
        textoInicio.setBackground(Color.WHITE);
        textoInicio.setBorder(BorderFactory.createEmptyBorder(20, 16, 10, 16));
        textoInicio.add(saludo, BorderLayout.WEST);
        panelInicio.add(textoInicio, BorderLayout.NORTH);

        JPanel accesosDirectos = new JPanel(new GridLayout(1, 2, 18, 18));
        accesosDirectos.setBackground(Color.WHITE);
        accesosDirectos.setBorder(BorderFactory.createEmptyBorder(20, 16, 20, 16));
        accesosDirectos.add(crearAccesoDirecto(
                "Catálogo", "Explora nuestros productos", "src/assets/productosimage.jpeg",
                () -> mostrarBusquedaCatalogo(true)));
        accesosDirectos.add(crearAccesoDirecto(
                "Mis pedidos", "Consulta tus pedidos", "src/assets/carritodecompras.jpg",
                this::mostrarPedidos));
        panelInicio.add(accesosDirectos, BorderLayout.CENTER);

        Image imagenFondoCatalogo = leerImagen("src/assets/productosimage.jpeg");
        JPanel panelCatalogo = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics grafico) {
                super.paintComponent(grafico);
                if (imagenFondoCatalogo != null) {
                    Graphics2D g2 = (Graphics2D) grafico.create();
                    g2.setComposite(AlphaComposite.SrcOver.derive(0.20f));
                    g2.drawImage(imagenFondoCatalogo, 0, 0, getWidth(), getHeight(), this);
                    g2.dispose();
                }
            }
        };
        panelCatalogo.setBackground(Color.WHITE);
        panelCatalogo.add(scrollProductos, BorderLayout.CENTER);

        JButton botonCarrito = new JButton();
        botonCarrito.setToolTipText("Carrito de compras");
        botonCarrito.getAccessibleContext().setAccessibleName("Carrito de compras");
        botonCarrito.setPreferredSize(new java.awt.Dimension(44, 44));
        botonCarrito.setBorderPainted(false);
        botonCarrito.setContentAreaFilled(false);
        botonCarrito.setFocusPainted(false);
        botonCarrito.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        escalarImagenBoton(botonCarrito, "src/assets/carrito_de_compras.png");
        botonCarrito.setToolTipText("Añadir selección al carrito");
        botonCarrito.addActionListener(evento -> abrirCarrito());
        JPanel panelCarrito = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 6));
        panelCarrito.setBackground(AZUL_CLARO);
        panelCarrito.setOpaque(true);
        panelCarrito.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, AZUL));
        panelCarrito.add(botonCarrito);
        panelCatalogo.add(panelCarrito, BorderLayout.SOUTH);

        JPanel encabezadoInicio = new JPanel(new BorderLayout());
        encabezadoInicio.setBackground(getContentPane().getBackground());
        encabezadoInicio.add(tituloDescubrir, BorderLayout.CENTER);
        JPanel encabezadoCatalogo = new JPanel(new BorderLayout());
        encabezadoCatalogo.setBackground(getContentPane().getBackground());
        encabezadoCatalogo.add(BuscarProductos, BorderLayout.WEST);
        JPanel encabezadoPedidos = new JPanel(new BorderLayout());
        encabezadoPedidos.setBackground(getContentPane().getBackground());
        JLabel tituloPedidos = new JLabel("Mis pedidos");
        tituloPedidos.setFont(new Font("Segoe UI", Font.BOLD, 26));
        tituloPedidos.setForeground(NEGRO);
        encabezadoPedidos.add(tituloPedidos, BorderLayout.CENTER);
        encabezadoPedidos.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, AZUL));
        JPanel encabezadoPerfil = new JPanel(new BorderLayout());
        encabezadoPerfil.setBackground(getContentPane().getBackground());
        JLabel tituloPerfil = new JLabel("Mi perfil");
        tituloPerfil.setFont(new Font("Segoe UI", Font.BOLD, 26));
        tituloPerfil.setForeground(NEGRO);
        encabezadoPerfil.add(tituloPerfil, BorderLayout.CENTER);
        encabezadoPerfil.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, AZUL));
        layoutCabecera = new CardLayout();
        panelControlesCabecera = new JPanel(layoutCabecera);
        panelControlesCabecera.add(encabezadoInicio, "inicio");
        panelControlesCabecera.add(encabezadoCatalogo, "catalogo");
        panelControlesCabecera.add(encabezadoPedidos, "pedidos");
        panelControlesCabecera.add(encabezadoPerfil, "perfil");

        Image imagenFondoPedidos = leerImagen("src/assets/carritodecompras.jpg");
        panelPedidos = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics grafico) {
                super.paintComponent(grafico);
                if (productosEnPedido.isEmpty() && imagenFondoPedidos != null) {
                    Graphics2D g2 = (Graphics2D) grafico.create();
                    g2.setComposite(AlphaComposite.SrcOver.derive(0.16f));
                    g2.drawImage(imagenFondoPedidos, 0, 0, getWidth(), getHeight(), this);
                    g2.dispose();
                } else if (!productosEnPedido.isEmpty()) {
                    grafico.setColor(Color.WHITE);
                    grafico.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        panelPedidos.setBackground(Color.WHITE);
        JPanel centroPedidos = new JPanel(new BorderLayout());
        centroPedidos.setOpaque(false);
        centroPedidos.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        JPanel tarjetaMensaje = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics grafico) {
                Graphics2D g2 = (Graphics2D) grafico.create();
                g2.setColor(new Color(255, 255, 255, 225));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.dispose();
                super.paintComponent(grafico);
            }
        };
        tarjetaMensaje.setOpaque(false);
        tarjetaMensaje.setBorder(BorderFactory.createEmptyBorder(18, 24, 18, 24));
        mensajePedidos = new JLabel(mensajePedidosVacio(), SwingConstants.CENTER);
        mensajePedidos.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        mensajePedidos.setForeground(new Color(90, 90, 90));
        contenidoPedidos = new JPanel(new BorderLayout(8, 8));
        contenidoPedidos.setOpaque(false);
        contenidoPedidos.add(mensajePedidos, BorderLayout.CENTER);
        tarjetaMensaje.add(contenidoPedidos, BorderLayout.CENTER);
        centroPedidos.add(tarjetaMensaje, BorderLayout.CENTER);
        panelPedidos.add(centroPedidos, BorderLayout.CENTER);

        layoutVistas = new CardLayout();
        panelVistas = new JPanel(layoutVistas);
        panelVistas.add(panelInicio, "inicio");
        panelVistas.add(panelCatalogo, "catalogo");
        panelVistas.add(panelPedidos, "pedidos");
        panelVistas.add(crearPanelPerfil(), "perfil");

        JPanel cabecera = new JPanel(new BorderLayout(12, 0));
        cabecera.add(panelControlesCabecera, BorderLayout.CENTER);
        cabecera.add(botonperfil, BorderLayout.EAST);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cabecera, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSeparator3)
                    .addComponent(panelVistas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jSeparator2)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cabecera, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelVistas, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        pack();
        mostrarBusquedaCatalogo(false);
    }

    private String crearTextoSaludo() {
        if (nombreUsuario == null || nombreUsuario.trim().isEmpty()) {
            return "¡Hola, bienvenido!";
        }
        String tipoUsuario = "admin".equalsIgnoreCase(rolUsuario) ? "administrador" : "usuario";
        return "¡Hola, " + nombreUsuario + " (" + tipoUsuario + ")!";
    }

    private JButton crearAccesoDirecto(String titulo, String descripcion,
                                       String rutaFondo, Runnable accion) {
        Image imagenFondo = leerImagen(rutaFondo);
        JButton acceso = new JButton("<html><div style='text-align:center'><b>"
                + titulo + "</b><br><span style='font-size:small'>"
                + descripcion + "</span></div></html>") {
            @Override
            protected void paintComponent(Graphics grafico) {
                if (imagenFondo != null) {
                    Graphics2D g2 = (Graphics2D) grafico.create();
                    g2.setComposite(AlphaComposite.SrcOver.derive(0.24f));
                    g2.drawImage(imagenFondo, 0, 0, getWidth(), getHeight(), this);
                    g2.dispose();
                }
                super.paintComponent(grafico);
            }
        };
        acceso.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        acceso.setForeground(NEGRO);
        acceso.setBackground(Color.WHITE);
        acceso.setOpaque(false);
        acceso.setContentAreaFilled(false);
        acceso.setFocusPainted(false);
        acceso.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        acceso.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AZUL, 1),
                BorderFactory.createEmptyBorder(20, 18, 20, 18)));
        acceso.addActionListener(evento -> accion.run());
        return acceso;
    }

    private void estilizarBotonAzul(JButton boton) {
        boton.setBackground(AZUL_CLARO);
        boton.setForeground(NEGRO);
        boton.setOpaque(true);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AZUL, 1),
                BorderFactory.createEmptyBorder(7, 12, 7, 12)));
        boton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evento) {
                boton.setBackground(AZUL);
            }

            @Override
            public void mouseExited(MouseEvent evento) {
                boton.setBackground(AZUL_CLARO);
            }
        });
    }

    private void estilizarTabla(JTable tabla) {
        tabla.setBackground(BLANCO);
        tabla.setForeground(NEGRO);
        tabla.setGridColor(AZUL_CLARO);
        tabla.setRowHeight(32);
        tabla.setSelectionBackground(AZUL_CLARO);
        tabla.setSelectionForeground(NEGRO);
        tabla.getTableHeader().setBackground(NEGRO_SUAVE);
        tabla.getTableHeader().setForeground(AZUL_CLARO);
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabla.getTableHeader().setReorderingAllowed(false);
    }

    private JPanel crearPanelPerfil() {
        JPanel perfil = new JPanel(new BorderLayout(12, 12));
        perfil.setBackground(Color.WHITE);
        perfil.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JPanel informacion = new JPanel(new GridLayout(0, 2, 8, 8));
        informacion.setBackground(Color.WHITE);
        informacion.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Información de la cuenta"),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        agregarDatoPerfil(informacion, "Nombre completo:", nombreUsuario);
        agregarDatoPerfil(informacion, "Nombre de usuario:", usuario);
        agregarDatoPerfil(informacion, "Correo electrónico:", correoUsuario);
        agregarDatoPerfil(informacion, "Tipo de cuenta:",
                esAdministrador() ? "Administrador" : "Usuario");
        JPanel encabezadoPerfil = new JPanel(new BorderLayout(0, 10));
        encabezadoPerfil.setBackground(Color.WHITE);
        encabezadoPerfil.add(informacion, BorderLayout.NORTH);

        JPanel historial = new JPanel(new BorderLayout(12, 8));
        historial.setBackground(Color.WHITE);
        historial.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Historial de pedidos"),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        resumenHistorialPedidos = new JLabel("Aún no tienes pedidos.");
        resumenHistorialPedidos.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JButton verPedidos = new JButton("Ir a mis pedidos");
        estilizarBotonAzul(verPedidos);
        verPedidos.addActionListener(evento -> mostrarPedidos());
        JPanel encabezadoHistorial = new JPanel(new BorderLayout(8, 0));
        encabezadoHistorial.setBackground(Color.WHITE);
        encabezadoHistorial.add(resumenHistorialPedidos, BorderLayout.CENTER);
        encabezadoHistorial.add(verPedidos, BorderLayout.EAST);
        historial.add(encabezadoHistorial, BorderLayout.NORTH);
        listaFacturasPerfil = new JPanel(new GridLayout(0, 1, 4, 4));
        listaFacturasPerfil.setBackground(Color.WHITE);
        historial.add(new JScrollPane(listaFacturasPerfil), BorderLayout.CENTER);
        actualizarHistorialPerfil();
        encabezadoPerfil.add(historial, BorderLayout.SOUTH);
        perfil.add(encabezadoPerfil, BorderLayout.NORTH);

        if (esAdministrador()) {
            JPanel gestion = new JPanel(new BorderLayout(8, 8));
            gestion.setBackground(Color.WHITE);
            gestion.setBorder(BorderFactory.createTitledBorder("Administrar productos"));

            modeloProductos = new DefaultTableModel(
                    new Object[]{"Código", "Producto", "Precio", "Stock", "Imagen", "Estado"}, 0) {
                @Override
                public boolean isCellEditable(int fila, int columna) {
                    return false;
                }
            };
            tablaProductos = new JTable(modeloProductos);
            tablaProductos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            tablaProductos.getTableHeader().setReorderingAllowed(false);
            estilizarTabla(tablaProductos);
            gestion.add(new JScrollPane(tablaProductos), BorderLayout.CENTER);

            JButton agregar = new JButton("Añadir producto");
            JButton modificar = new JButton("Modificar");
            JButton eliminar = new JButton("Inhabilitar");
            JButton habilitar = new JButton("Habilitar");
            estilizarBotonAzul(agregar);
            estilizarBotonAzul(modificar);
            estilizarBotonAzul(eliminar);
            estilizarBotonAzul(habilitar);
            agregar.addActionListener(evento -> agregarProducto());
            modificar.addActionListener(evento -> modificarProducto());
            eliminar.addActionListener(evento -> eliminarProducto());
            habilitar.addActionListener(evento -> habilitarProducto());
            habilitar.setEnabled(false);
            tablaProductos.getSelectionModel().addListSelectionListener(evento -> {
                if (evento.getValueIsAdjusting()) {
                    return;
                }
                if (tablaProductos.getSelectedRow() < 0) {
                    habilitar.setEnabled(false);
                    eliminar.setEnabled(false);
                } else {
                    int filaSeleccionada = tablaProductos.convertRowIndexToModel(
                            tablaProductos.getSelectedRow());
                    boolean yaHabilitado = "Habilitado".equals(
                            String.valueOf(modeloProductos.getValueAt(filaSeleccionada, 5)));
                    habilitar.setEnabled(!yaHabilitado);
                    eliminar.setEnabled(yaHabilitado);
                }
            });
            JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT));
            acciones.setBackground(Color.WHITE);
            acciones.add(agregar);
            acciones.add(modificar);
            acciones.add(eliminar);
            acciones.add(habilitar);
            gestion.add(acciones, BorderLayout.SOUTH);
            perfil.add(gestion, BorderLayout.CENTER);
        }
        return perfil;
    }

    private void agregarDatoPerfil(JPanel panel, String etiqueta, String valor) {
        JLabel nombreDato = new JLabel(etiqueta);
        nombreDato.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JLabel valorDato = new JLabel(valor == null || valor.trim().isEmpty() ? "—" : valor);
        valorDato.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(nombreDato);
        panel.add(valorDato);
    }

    private void cargarProductosAdmin() {
        if (modeloProductos == null) {
            return;
        }
        modeloProductos.setRowCount(0);
        String sql = "SELECT codigo, nombre, precio, stock, imagen, habilitado FROM productos ORDER BY nombre";
        try (Connection conexion = Conexion.conectar();
             Statement consulta = conexion.createStatement();
             ResultSet productos = consulta.executeQuery(sql)) {
            while (productos.next()) {
                modeloProductos.addRow(new Object[]{
                    productos.getString("codigo"),
                    productos.getString("nombre"),
                    productos.getBigDecimal("precio"),
                    productos.getInt("stock"),
                    productos.getString("imagen"),
                    productos.getBoolean("habilitado") ? "Habilitado" : "Inhabilitado"
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "No se pudieron cargar los productos para su administración.\n" + e.getMessage(),
                    "Error de base de datos",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void agregarProducto() {
        String[] datos = solicitarDatosProducto(new String[]{
            siguienteCodigoProducto(), "", "", "0", ""
        }, "Añadir producto");
        if (datos == null) {
            return;
        }
        String sql = "INSERT INTO productos (codigo, nombre, precio, stock, imagen, habilitado) VALUES (?, ?, ?, ?, ?, 1)";
        try (Connection conexion = Conexion.conectar()) {
            if (codigoProductoExiste(conexion, datos[0], null)) {
                JOptionPane.showMessageDialog(this, "Ese código de producto ya existe.");
                return;
            }
            try (java.sql.PreparedStatement guardar = conexion.prepareStatement(sql)) {
                guardar.setString(1, datos[0]);
                guardar.setString(2, datos[1]);
                guardar.setBigDecimal(3, new BigDecimal(datos[2]));
                guardar.setInt(4, Integer.parseInt(datos[3]));
                guardar.setString(5, datos[4].isEmpty() ? null : datos[4]);
                guardar.executeUpdate();
            }
            actualizarVistasProductos();
        } catch (SQLException | NumberFormatException e) {
            mostrarErrorProducto(e);
        }
    }

    private void modificarProducto() {
        int fila = tablaProductos.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un producto para modificar.");
            return;
        }
        fila = tablaProductos.convertRowIndexToModel(fila);
        String codigoAnterior = String.valueOf(modeloProductos.getValueAt(fila, 0));
        Object imagenAnterior = modeloProductos.getValueAt(fila, 4);
        String[] datos = solicitarDatosProducto(new String[]{
            codigoAnterior,
            String.valueOf(modeloProductos.getValueAt(fila, 1)),
            String.valueOf(modeloProductos.getValueAt(fila, 2)),
            String.valueOf(modeloProductos.getValueAt(fila, 3)),
            imagenAnterior == null ? "" : String.valueOf(imagenAnterior)
        }, "Modificar producto");
        if (datos == null) {
            return;
        }

        String sql = "UPDATE productos SET codigo = ?, nombre = ?, precio = ?, stock = ?, imagen = ? WHERE codigo = ?";
        try (Connection conexion = Conexion.conectar()) {
            if (codigoProductoExiste(conexion, datos[0], codigoAnterior)) {
                JOptionPane.showMessageDialog(this, "Ese código de producto ya existe.");
                return;
            }
            try (java.sql.PreparedStatement actualizar = conexion.prepareStatement(sql)) {
                actualizar.setString(1, datos[0]);
                actualizar.setString(2, datos[1]);
                actualizar.setBigDecimal(3, new BigDecimal(datos[2]));
                actualizar.setInt(4, Integer.parseInt(datos[3]));
                actualizar.setString(5, datos[4].isEmpty() ? null : datos[4]);
                actualizar.setString(6, codigoAnterior);
                actualizar.executeUpdate();
            }
            if (!codigoAnterior.equals(datos[0]) && codigosProductosSeleccionados.remove(codigoAnterior)) {
                codigosProductosSeleccionados.add(datos[0]);
            }
            actualizarVistasProductos();
        } catch (SQLException | NumberFormatException e) {
            mostrarErrorProducto(e);
        }
    }

    private void eliminarProducto() {
        int fila = tablaProductos.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un producto para eliminar.");
            return;
        }
        fila = tablaProductos.convertRowIndexToModel(fila);
        String codigo = String.valueOf(modeloProductos.getValueAt(fila, 0));
        String nombre = String.valueOf(modeloProductos.getValueAt(fila, 1));
        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Deseas inhabilitar «" + nombre + "»? Se ocultará del catálogo y podrás habilitarlo luego.",
                "Inhabilitar producto",
                JOptionPane.YES_NO_OPTION);
        if (opcion != JOptionPane.YES_OPTION) {
            return;
        }
        try (Connection conexion = Conexion.conectar();
             java.sql.PreparedStatement deshabilitar = conexion.prepareStatement(
                     "UPDATE productos SET habilitado = 0 WHERE codigo = ?")) {
            deshabilitar.setString(1, codigo);
            deshabilitar.executeUpdate();
            codigosProductosSeleccionados.remove(codigo);
            productosEnPedido.remove(codigo);
            actualizarPanelPedidos();
            actualizarVistasProductos();
        } catch (SQLException e) {
            mostrarErrorProducto(e);
        }
    }

    private void habilitarProducto() {
        int fila = tablaProductos.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un producto para habilitar.");
            return;
        }
        fila = tablaProductos.convertRowIndexToModel(fila);
        String codigo = String.valueOf(modeloProductos.getValueAt(fila, 0));
        try (Connection conexion = Conexion.conectar();
             java.sql.PreparedStatement habilitar = conexion.prepareStatement(
                     "UPDATE productos SET habilitado = 1 WHERE codigo = ?")) {
            habilitar.setString(1, codigo);
            habilitar.executeUpdate();
            actualizarVistasProductos();
        } catch (SQLException e) {
            mostrarErrorProducto(e);
        }
    }

    private String[] solicitarDatosProducto(String[] iniciales, String titulo) {
        JTextField codigo = new JTextField(iniciales[0], 18);
        JTextField nombre = new JTextField(iniciales[1], 18);
        JTextField precio = new JTextField(iniciales[2], 18);
        JTextField stock = new JTextField(iniciales[3], 18);
        JTextField imagen = new JTextField(iniciales[4], 18);
        JButton seleccionarImagen = new JButton("Elegir...");
        seleccionarImagen.addActionListener(evento -> {
            JFileChooser selector = new JFileChooser(new File("src/assets/productos"));
            selector.setFileFilter(new FileNameExtensionFilter("Imágenes", "jpg", "jpeg", "png"));
            if (selector.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                imagen.setText(rutaImagenProducto(selector.getSelectedFile()));
            }
        });

        JPanel formulario = new JPanel(new GridLayout(0, 2, 8, 6));
        formulario.add(new JLabel("Código:"));
        formulario.add(codigo);
        formulario.add(new JLabel("Nombre:"));
        formulario.add(nombre);
        formulario.add(new JLabel("Precio:"));
        formulario.add(precio);
        formulario.add(new JLabel("Stock:"));
        formulario.add(stock);
        formulario.add(new JLabel("Ruta de imagen:"));
        JPanel campoImagen = new JPanel(new BorderLayout(4, 0));
        campoImagen.add(imagen, BorderLayout.CENTER);
        campoImagen.add(seleccionarImagen, BorderLayout.EAST);
        formulario.add(campoImagen);

        int opcion = JOptionPane.showConfirmDialog(this, formulario, titulo,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (opcion != JOptionPane.OK_OPTION) {
            return null;
        }
        String codigoTexto = codigo.getText().trim();
        String nombreTexto = nombre.getText().trim();
        String precioTexto = precio.getText().trim();
        String stockTexto = stock.getText().trim();
        String imagenTexto = imagen.getText().trim();
        if (codigoTexto.isEmpty() || nombreTexto.isEmpty() || precioTexto.isEmpty() || stockTexto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Completa código, nombre, precio y stock.");
            return null;
        }
        if (codigoTexto.length() > 20 || nombreTexto.length() > 100 || imagenTexto.length() > 500) {
            JOptionPane.showMessageDialog(this, "Uno de los campos supera el largo permitido.");
            return null;
        }
        try {
            BigDecimal precioNumero = new BigDecimal(precioTexto.replace(',', '.'));
            int stockNumero = Integer.parseInt(stockTexto);
            if (precioNumero.signum() < 0 || stockNumero < 0) {
                throw new NumberFormatException("negative");
            }
            return new String[]{codigoTexto, nombreTexto, precioNumero.toPlainString(),
                String.valueOf(stockNumero), imagenTexto};
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El precio o el stock no tienen un valor válido.");
            return null;
        }
    }

    private boolean codigoProductoExiste(Connection conexion, String codigo, String excluir)
            throws SQLException {
        String sql = excluir == null
                ? "SELECT 1 FROM productos WHERE codigo = ? LIMIT 1"
                : "SELECT 1 FROM productos WHERE codigo = ? AND codigo <> ? LIMIT 1";
        try (java.sql.PreparedStatement consulta = conexion.prepareStatement(sql)) {
            consulta.setString(1, codigo);
            if (excluir != null) {
                consulta.setString(2, excluir);
            }
            try (ResultSet resultado = consulta.executeQuery()) {
                return resultado.next();
            }
        }
    }

    private String siguienteCodigoProducto() {
        int siguiente = 1;
        try (Connection conexion = Conexion.conectar();
             Statement consulta = conexion.createStatement();
             ResultSet codigos = consulta.executeQuery("SELECT codigo FROM productos")) {
            while (codigos.next()) {
                String codigo = codigos.getString(1);
                if (codigo != null && codigo.matches("PIL\\d+")) {
                    siguiente = Math.max(siguiente, Integer.parseInt(codigo.substring(3)) + 1);
                }
            }
        } catch (SQLException | NumberFormatException e) {
            // El administrador puede corregir el código antes de guardar.
        }
        return String.format(Locale.ROOT, "PIL%03d", siguiente);
    }

    private String rutaImagenProducto(File archivo) {
        Path ruta = archivo.toPath().toAbsolutePath().normalize();
        Path proyecto = Paths.get(System.getProperty("user.dir")).toAbsolutePath().normalize();
        if (ruta.startsWith(proyecto)) {
            return proyecto.relativize(ruta).toString().replace(File.separatorChar, '/');
        }
        return ruta.toString();
    }

    private void actualizarVistasProductos() {
        cargarProductosAdmin();
        cargarProductos();
    }

    private void mostrarErrorProducto(Exception error) {
        JOptionPane.showMessageDialog(this,
                "No se pudo guardar el cambio del producto.\n" + error.getMessage(),
                "Error de productos",
                JOptionPane.ERROR_MESSAGE);
    }

    private void abrirCarrito() {
        if (codigosProductosSeleccionados.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Selecciona al menos un producto para continuar.",
                    "Sin productos seleccionados",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (rolUsuario == null) {
            int opcion = JOptionPane.showConfirmDialog(this,
                    "Debes iniciar sesión para comprar. ¿Quieres iniciar sesión ahora?",
                    "Iniciar sesión",
                    JOptionPane.YES_NO_OPTION);
            if (opcion == JOptionPane.YES_OPTION) {
                new Login(this, true).setVisible(true);
            }
            return;
        }
        agregarProductosSeleccionados();
    }

    private String mensajePedidosVacio() {
        return rolUsuario == null
                ? "<html><div style='text-align:center'>Sin pedidos aún...<br>Regístrate o inicia sesión en el programa.</div></html>"
                : "<html><div style='text-align:center'>Sin pedidos aún.<br>¡Realiza una compra!</div></html>";
    }

    private void agregarProductosSeleccionados() {
        if (codigosProductosSeleccionados.isEmpty()) {
            return;
        }
        String sql = "SELECT codigo, nombre, precio, stock, habilitado FROM productos WHERE codigo = ?";
        try (Connection conexion = Conexion.conectar();
             PreparedStatement consulta = conexion.prepareStatement(sql)) {
            for (String codigo : new ArrayList<>(codigosProductosSeleccionados)) {
                consulta.setString(1, codigo);
                try (ResultSet producto = consulta.executeQuery()) {
                    if (producto.next()) {
                        String nombre = producto.getString("nombre");
                        BigDecimal precio = producto.getBigDecimal("precio");
                        int stock = producto.getInt("stock");
                        boolean habilitado = producto.getBoolean("habilitado");
                        ItemPedido item = productosEnPedido.get(codigo);
                        if (!habilitado) {
                            productosEnPedido.remove(codigo);
                            JOptionPane.showMessageDialog(this,
                                    "«" + nombre + "» ya no está disponible en el catálogo.",
                                    "Producto no disponible", JOptionPane.INFORMATION_MESSAGE);
                        } else if (stock <= 0) {
                            productosEnPedido.remove(codigo);
                            JOptionPane.showMessageDialog(this,
                                    "«" + nombre + "» ya no tiene unidades disponibles.",
                                    "Producto agotado", JOptionPane.INFORMATION_MESSAGE);
                        } else if (item == null) {
                            productosEnPedido.put(codigo, new ItemPedido(nombre,
                                    precio == null ? BigDecimal.ZERO : precio, stock));
                        } else {
                            item.stockDisponible = stock;
                            if (item.cantidad >= stock) {
                                JOptionPane.showMessageDialog(this,
                                        "Solo quedan " + stock + " unidades de «" + nombre + "».",
                                        "Stock insuficiente", JOptionPane.INFORMATION_MESSAGE);
                            } else {
                                item.cantidad++;
                            }
                        }
                    }
                }
            }
            codigosProductosSeleccionados.clear();
            for (JToggleButton tarjeta : tarjetasProductos) {
                tarjeta.setSelected(false);
            }
            actualizarHistorialPerfil();
            actualizarPanelPedidos();
        } catch (SQLException error) {
            JOptionPane.showMessageDialog(this,
                    "No se pudieron añadir los productos al pedido.\n" + error.getMessage(),
                    "Error del carrito", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarPanelPedidos() {
        contenidoPedidos.removeAll();
        actualizarHistorialPerfil();
        if (productosEnPedido.isEmpty()) {
            mensajePedidos.setText(mensajePedidosVacio());
            contenidoPedidos.add(mensajePedidos, BorderLayout.CENTER);
        } else {
            modeloPedido = new DefaultTableModel(new Object[]{"Producto", "Cantidad", "Precio", "Subtotal"}, 0) {
                @Override
                public boolean isCellEditable(int fila, int columna) {
                    return columna == 1;
                }
            };
            BigDecimal total = BigDecimal.ZERO;
            for (ItemPedido item : productosEnPedido.values()) {
                BigDecimal subtotal = item.precio.multiply(BigDecimal.valueOf(item.cantidad));
                total = total.add(subtotal);
                modeloPedido.addRow(new Object[]{item.nombre, item.cantidad,
                    "Bs " + item.precio.toPlainString(), "Bs " + subtotal.toPlainString()});
            }
            modeloPedido.addTableModelListener(evento -> {
                if (evento.getType() != TableModelEvent.UPDATE || evento.getColumn() != 1) {
                    return;
                }
                int fila = evento.getFirstRow();
                List<String> codigos = new ArrayList<>(productosEnPedido.keySet());
                if (fila < 0 || fila >= codigos.size()) {
                    return;
                }
                String codigo = codigos.get(fila);
                Object valor = modeloPedido.getValueAt(fila, 1);
                try {
                    int cantidad = Integer.parseInt(String.valueOf(valor).trim());
                    ItemPedido item = productosEnPedido.get(codigo);
                    if (cantidad < 0 || (item != null && cantidad > item.stockDisponible)) {
                        throw new NumberFormatException("negative");
                    }
                    if (cantidad == 0) {
                        productosEnPedido.remove(codigo);
                        codigoProductoSeleccionadoPedido = null;
                    } else {
                        if (item != null) {
                            item.cantidad = cantidad;
                            codigoProductoSeleccionadoPedido = codigo;
                        }
                    }
                    javax.swing.SwingUtilities.invokeLater(this::actualizarPanelPedidos);
                } catch (NumberFormatException error) {
                    javax.swing.SwingUtilities.invokeLater(() -> {
                        actualizarPanelPedidos();
                        JOptionPane.showMessageDialog(this,
                                "Ingresa una cantidad entre 0 y el stock disponible.",
                                "Cantidad no válida", JOptionPane.INFORMATION_MESSAGE);
                    });
                }
            });
            JTable tablaPedido = new JTable(modeloPedido);
            tablaPedido.setRowHeight(34);
            tablaPedido.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            tablaPedido.setFillsViewportHeight(true);
            tablaPedido.setShowVerticalLines(false);
            tablaPedido.setSelectionBackground(new Color(225, 237, 249));
            estilizarTabla(tablaPedido);
            tablaPedido.getTableHeader().setReorderingAllowed(false);
            JScrollPane scrollPedido = new JScrollPane(tablaPedido);
            scrollPedido.setBorder(BorderFactory.createLineBorder(new Color(225, 225, 225)));
            contenidoPedidos.add(scrollPedido, BorderLayout.CENTER);

            JPanel resumen = new JPanel(new BorderLayout(12, 8));
            resumen.setOpaque(false);
            resumen.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)),
                    BorderFactory.createEmptyBorder(12, 4, 2, 4)));
            String totalTexto = total.toPlainString();
            BigDecimal totalPedidoActual = total;
            totalPedido = new JLabel("Total: Bs " + totalTexto);
            totalPedido.setFont(new Font("Segoe UI", Font.BOLD, 18));
            JButton botonMenosCantidad = new JButton("−");
            botonMenosCantidad.setToolTipText("Reducir una unidad del producto seleccionado");
            estilizarBotonAzul(botonMenosCantidad);
            JButton botonMasCantidad = new JButton("+");
            botonMasCantidad.setToolTipText("Añadir una unidad del producto seleccionado");
            estilizarBotonAzul(botonMasCantidad);
            botonMenosCantidad.setEnabled(false);
            botonMasCantidad.setEnabled(false);
            botonMenosCantidad.addActionListener(evento -> ajustarCantidadSeleccionada(tablaPedido, -1));
            botonMasCantidad.addActionListener(evento -> ajustarCantidadSeleccionada(tablaPedido, 1));
            tablaPedido.getSelectionModel().addListSelectionListener(evento -> {
                if (!evento.getValueIsAdjusting()) {
                    boolean seleccionado = tablaPedido.getSelectedRow() >= 0;
                    botonMenosCantidad.setEnabled(seleccionado);
                    botonMasCantidad.setEnabled(seleccionado);
                    if (seleccionado) {
                        int filaModelo = tablaPedido.convertRowIndexToModel(tablaPedido.getSelectedRow());
                        List<String> codigos = new ArrayList<>(productosEnPedido.keySet());
                        if (filaModelo < codigos.size()) {
                            codigoProductoSeleccionadoPedido = codigos.get(filaModelo);
                        }
                    }
                }
            });
            JPanel controlesCantidad = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
            controlesCantidad.setOpaque(false);
            controlesCantidad.add(new JLabel("Cantidad:"));
            controlesCantidad.add(botonMenosCantidad);
            controlesCantidad.add(botonMasCantidad);
            JButton botonPago = new JButton("Proceder al pago");
            estilizarBotonAzul(botonPago);
            botonPago.addActionListener(evento -> confirmarPago(totalPedidoActual));
            JPanel cierrePedido = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
            cierrePedido.setOpaque(false);
            cierrePedido.add(totalPedido);
            cierrePedido.add(botonPago);
            resumen.add(controlesCantidad, BorderLayout.WEST);
            resumen.add(cierrePedido, BorderLayout.EAST);
            contenidoPedidos.add(resumen, BorderLayout.SOUTH);
            if (tablaPedido.getRowCount() > 0) {
                int filaInicial = codigoProductoSeleccionadoPedido == null
                        ? -1 : new ArrayList<>(productosEnPedido.keySet())
                                .indexOf(codigoProductoSeleccionadoPedido);
                if (filaInicial < 0) {
                    filaInicial = 0;
                }
                tablaPedido.setRowSelectionInterval(filaInicial, filaInicial);
            }
        }
        contenidoPedidos.revalidate();
        contenidoPedidos.repaint();
    }

    private void ajustarCantidadSeleccionada(JTable tabla, int cambio) {
        int filaSeleccionada = tabla.getSelectedRow();
        if (filaSeleccionada < 0) {
            JOptionPane.showMessageDialog(this,
                    "Selecciona un producto de la lista para cambiar su cantidad.",
                    "Producto no seleccionado", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int filaModelo = tabla.convertRowIndexToModel(filaSeleccionada);
        List<String> codigos = new ArrayList<>(productosEnPedido.keySet());
        if (filaModelo >= codigos.size()) {
            return;
        }
        String codigo = codigos.get(filaModelo);
        ItemPedido item = productosEnPedido.get(codigo);
        if (item == null) {
            return;
        }
        if (cambio > 0 && item.cantidad >= item.stockDisponible) {
            JOptionPane.showMessageDialog(this,
                    "No puedes superar las " + item.stockDisponible + " unidades disponibles.",
                    "Stock insuficiente", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        item.cantidad += cambio;
        if (item.cantidad <= 0) {
            productosEnPedido.remove(codigo);
            codigoProductoSeleccionadoPedido = null;
        }
        actualizarPanelPedidos();
    }

    private void confirmarPago(BigDecimal total) {
        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Confirmas el pago por Bs " + total.toPlainString() + "?",
                "Confirmar pago", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (opcion != JOptionPane.YES_OPTION) {
            return;
        }

        if (!descontarStockDeBaseDatos()) {
            return;
        }

        List<LineaFactura> lineas = new ArrayList<>();
        for (ItemPedido item : productosEnPedido.values()) {
            BigDecimal subtotal = item.precio.multiply(BigDecimal.valueOf(item.cantidad));
            lineas.add(new LineaFactura(item.nombre, item.cantidad, item.precio, subtotal));
        }
        Factura factura = new Factura(siguienteNumeroFactura++, LocalDateTime.now(), lineas, total,
                nombreUsuario, correoUsuario, EMISOR_FACTURA);
        historialFacturas.add(factura);
        productosEnPedido.clear();
        codigoProductoSeleccionadoPedido = null;
        actualizarPanelPedidos();
        actualizarVistasProductos();
        JOptionPane.showMessageDialog(this,
                "Pago confirmado. La factura " + factura.numero
                + " está disponible en Perfil > Historial de pedidos.",
                "Pago confirmado", JOptionPane.INFORMATION_MESSAGE);
    }

    private boolean descontarStockDeBaseDatos() {
        String sql = "UPDATE productos SET stock = stock - ? WHERE codigo = ? AND stock >= ? AND habilitado = 1";
        try (Connection conexion = Conexion.conectar()) {
            conexion.setAutoCommit(false);
            try (PreparedStatement actualizar = conexion.prepareStatement(sql)) {
                for (Map.Entry<String, ItemPedido> entrada : productosEnPedido.entrySet()) {
                    ItemPedido item = entrada.getValue();
                    actualizar.setInt(1, item.cantidad);
                    actualizar.setString(2, entrada.getKey());
                    actualizar.setInt(3, item.cantidad);
                    if (actualizar.executeUpdate() != 1) {
                        conexion.rollback();
                        JOptionPane.showMessageDialog(this,
                                "El stock de «" + item.nombre + "» cambió y ya no alcanza para completar la compra.\n"
                                + "Revisa las cantidades e inténtalo de nuevo.",
                                "Stock insuficiente", JOptionPane.WARNING_MESSAGE);
                        actualizarVistasProductos();
                        return false;
                    }
                }
                conexion.commit();
                return true;
            } catch (SQLException error) {
                conexion.rollback();
                throw error;
            }
        } catch (SQLException error) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo actualizar el stock en la base de datos. La compra no se completó.\n"
                    + error.getMessage(),
                    "Error de inventario", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void actualizarHistorialPerfil() {
        if (listaFacturasPerfil == null) {
            return;
        }
        listaFacturasPerfil.removeAll();
        if (historialFacturas.isEmpty()) {
            listaFacturasPerfil.add(new JLabel("Aún no hay facturas.", SwingConstants.LEFT));
            resumenHistorialPedidos.setText(productosEnPedido.isEmpty()
                    ? "Aún no tienes pedidos."
                    : "Tienes un pedido pendiente.");
        } else {
            resumenHistorialPedidos.setText(historialFacturas.size()
                    + (historialFacturas.size() == 1 ? " factura" : " facturas")
                    + (productosEnPedido.isEmpty() ? " emitidas" : " · Hay un pedido pendiente"));
            DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            for (Factura factura : historialFacturas) {
                JButton botonFactura = new JButton("Factura #" + factura.numero + " · "
                        + factura.fecha.format(formato) + " · Bs " + factura.total.toPlainString());
                estilizarBotonAzul(botonFactura);
                botonFactura.setHorizontalAlignment(SwingConstants.LEFT);
                botonFactura.addActionListener(evento -> mostrarFactura(factura));
                listaFacturasPerfil.add(botonFactura);
            }
        }
        listaFacturasPerfil.revalidate();
        listaFacturasPerfil.repaint();
    }

    private void mostrarFactura(Factura factura) {
        DefaultTableModel modeloFactura = new DefaultTableModel(
                new Object[]{"Producto", "Cantidad", "Precio", "Subtotal"}, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        for (LineaFactura linea : factura.lineas) {
            modeloFactura.addRow(new Object[]{linea.nombre, linea.cantidad,
                "Bs " + linea.precio.toPlainString(), "Bs " + linea.subtotal.toPlainString()});
        }
        JTable tabla = new JTable(modeloFactura);
        tabla.setRowHeight(26);
        estilizarTabla(tabla);
        JPanel contenido = new JPanel(new BorderLayout(8, 8));
        JPanel datosFactura = new JPanel(new GridLayout(0, 1, 2, 2));
        datosFactura.setBackground(Color.WHITE);
        datosFactura.add(new JLabel("Emisor: " + factura.emisor));
        datosFactura.add(new JLabel("Cliente: " + textoFactura(factura.nombreCliente)));
        datosFactura.add(new JLabel("Correo: " + textoFactura(factura.correoCliente)));
        datosFactura.add(new JLabel("Factura #" + factura.numero + " · "
                + factura.fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));
        contenido.add(datosFactura, BorderLayout.NORTH);
        contenido.add(new JScrollPane(tabla), BorderLayout.CENTER);
        JLabel total = new JLabel("Total pagado: Bs " + factura.total.toPlainString());
        total.setFont(new Font("Segoe UI", Font.BOLD, 15));
        contenido.add(total, BorderLayout.SOUTH);
        contenido.setPreferredSize(new java.awt.Dimension(500, 250));
        JOptionPane.showMessageDialog(this, contenido, "Factura", JOptionPane.INFORMATION_MESSAGE);
    }

    private String textoFactura(String valor) {
        return valor == null || valor.trim().isEmpty() ? "No registrado" : valor;
    }

    private static final class LineaFactura {
        private final String nombre;
        private final int cantidad;
        private final BigDecimal precio;
        private final BigDecimal subtotal;

        private LineaFactura(String nombre, int cantidad, BigDecimal precio, BigDecimal subtotal) {
            this.nombre = nombre;
            this.cantidad = cantidad;
            this.precio = precio;
            this.subtotal = subtotal;
        }
    }

    private static final class Factura {
        private final int numero;
        private final LocalDateTime fecha;
        private final List<LineaFactura> lineas;
        private final BigDecimal total;
        private final String nombreCliente;
        private final String correoCliente;
        private final String emisor;

        private Factura(int numero, LocalDateTime fecha, List<LineaFactura> lineas, BigDecimal total,
                        String nombreCliente, String correoCliente, String emisor) {
            this.numero = numero;
            this.fecha = fecha;
            this.lineas = new ArrayList<>(lineas);
            this.total = total;
            this.nombreCliente = nombreCliente;
            this.correoCliente = correoCliente;
            this.emisor = emisor;
        }
    }

    private static final class ItemPedido {
        private final String nombre;
        private final BigDecimal precio;
        private int cantidad = 1;
        private int stockDisponible;

        private ItemPedido(String nombre, BigDecimal precio, int stockDisponible) {
            this.nombre = nombre;
            this.precio = precio;
            this.stockDisponible = stockDisponible;
        }
    }

    private void configurarBusquedaProductos() {
        BuscarProductos.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent evento) {
                filtrarProductos();
            }

            @Override
            public void removeUpdate(DocumentEvent evento) {
                filtrarProductos();
            }

            @Override
            public void changedUpdate(DocumentEvent evento) {
                filtrarProductos();
            }
        });
    }

    private void filtrarProductos() {
        if (panelProductos == null) {
            return;
        }
        String texto = BuscarProductos.getText();
        if (texto == null || texto.trim().equalsIgnoreCase("Buscar productos")) {
            texto = "";
        }
        String filtro = normalizarNombre(texto);
        panelProductos.removeAll();
        int cantidadVisible = 0;
        for (JToggleButton tarjeta : tarjetasProductos) {
            String nombre = String.valueOf(tarjeta.getClientProperty("nombreProducto"));
            if (filtro.isEmpty() || normalizarNombre(nombre).contains(filtro)) {
                panelProductos.add(tarjeta);
                cantidadVisible++;
            }
        }
        if (cantidadVisible == 0) {
            String mensaje = tarjetasProductos.isEmpty()
                    ? "No hay productos registrados."
                    : "No se encontraron productos.";
            panelProductos.add(new JLabel(mensaje, SwingConstants.CENTER));
        }
        panelProductos.revalidate();
        panelProductos.repaint();
    }

    private void mostrarAcceso() {
        mostrarPerfil();
    }

    private void mostrarPerfil() {
        if (rolUsuario == null) {
            new Login(this).setVisible(true);
            return;
        }
        layoutCabecera.show(panelControlesCabecera, "perfil");
        layoutVistas.show(panelVistas, "perfil");
        if (esAdministrador()) {
            cargarProductosAdmin();
        }
        getContentPane().revalidate();
        getContentPane().repaint();
    }

    private boolean esAdministrador() {
        return "admin".equalsIgnoreCase(rolUsuario)
                || "administrador".equalsIgnoreCase(rolUsuario);
    }

    private void mostrarBusquedaCatalogo(boolean visible) {
        if (panelControlesCabecera != null && panelVistas != null) {
            String vista = visible ? "catalogo" : "inicio";
            layoutCabecera.show(panelControlesCabecera, vista);
            layoutVistas.show(panelVistas, vista);
        }
        if (visible) {
            BuscarProductos.requestFocusInWindow();
        }
        getContentPane().revalidate();
        getContentPane().repaint();
    }

    private void mostrarPedidos() {
        layoutCabecera.show(panelControlesCabecera, "pedidos");
        layoutVistas.show(panelVistas, "pedidos");
        getContentPane().revalidate();
        getContentPane().repaint();
    }

    @SuppressWarnings("unchecked")
    
    private void cargarProductos() {
        panelProductos.removeAll();
        tarjetasProductos.clear();
        panelProductos.setLayout(new GridLayout(0, 3, 12, 12));

        try (Connection conexion = Conexion.conectar();
             Statement consulta = conexion.createStatement();
             ResultSet productos = consulta.executeQuery(
                     "SELECT * FROM productos WHERE habilitado = 1 ORDER BY nombre")) {
            ResultSetMetaData columnas = productos.getMetaData();
            int columnaNombre = buscarColumna(columnas,
                    "nombre", "nombre_producto", "producto", "name", "descripcion");
            int columnaCodigo = buscarColumna(columnas, "codigo", "id_producto", "id");
            int columnaPrecio = buscarColumna(columnas, "precio", "price", "costo");
            int columnaStock = buscarColumna(columnas, "stock", "existencia", "unidades");
            int columnaImagen = buscarColumna(columnas,
                    "imagen", "foto", "ruta_imagen", "imagen_url", "image", "photo");

            while (productos.next()) {
                String codigo = columnaCodigo < 0
                        ? "producto-" + (tarjetasProductos.size() + 1)
                        : productos.getString(columnaCodigo);
                String nombre = leerNombre(productos, columnas, columnaNombre, columnaImagen);
                String precio = columnaPrecio < 0 ? null : productos.getString(columnaPrecio);
                int stock = columnaStock < 0 ? 0 : productos.getInt(columnaStock);
                Object imagen = columnaImagen < 0 ? null : productos.getObject(columnaImagen);
                if (imagen == null || String.valueOf(imagen).trim().isEmpty()) {
                    imagen = buscarImagenPorNombre(nombre);
                }
                JToggleButton tarjeta = crearTarjetaProducto(codigo, nombre, precio, imagen, stock);
                tarjeta.putClientProperty("nombreProducto", nombre);
                if (stock <= 0) {
                    codigosProductosSeleccionados.remove(codigo);
                } else if (codigosProductosSeleccionados.contains(codigo)) {
                    tarjeta.setSelected(true);
                }
                tarjetasProductos.add(tarjeta);
            }

            filtrarProductos();
        } catch (SQLException e) {
            tarjetasProductos.clear();
            panelProductos.removeAll();
            panelProductos.add(new JLabel(
                    "No se pudo cargar el catálogo: " + e.getMessage(), SwingConstants.CENTER));
        }

        panelProductos.revalidate();
        panelProductos.repaint();
    }

    private int buscarColumna(ResultSetMetaData columnas, String... nombres) throws SQLException {
        for (int i = 1; i <= columnas.getColumnCount(); i++) {
            String etiqueta = columnas.getColumnLabel(i).toLowerCase(Locale.ROOT);
            for (String nombre : nombres) {
                if (etiqueta.equals(nombre)) {
                    return i;
                }
            }
        }
        return -1;
    }

    private String leerNombre(ResultSet fila, ResultSetMetaData columnas,
                              int columnaNombre, int columnaImagen) throws SQLException {
        if (columnaNombre > 0) {
            String nombre = fila.getString(columnaNombre);
            if (nombre != null && !nombre.trim().isEmpty()) {
                return nombre;
            }
        }

        for (int i = 1; i <= columnas.getColumnCount(); i++) {
            if (i == columnaImagen) {
                continue;
            }
            Object valor = fila.getObject(i);
            if (valor != null && !(valor instanceof byte[]) && !(valor instanceof Blob)) {
                return String.valueOf(valor);
            }
        }
        return "Producto sin nombre";
    }

    private JToggleButton crearTarjetaProducto(String codigo, String nombre, String precio,
                                               Object imagen, int stock) {
        boolean agotado = stock <= 0;
        JToggleButton botonProducto = new JToggleButton();
        botonProducto.setLayout(new BorderLayout());
        botonProducto.setBackground(Color.WHITE);
        botonProducto.setContentAreaFilled(false);
        botonProducto.setFocusPainted(false);
        botonProducto.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        botonProducto.setToolTipText(agotado ? nombre + " · Sin stock" : "Selecciona " + nombre);
        botonProducto.setEnabled(!agotado);

        JPanel tarjeta = new JPanel(new BorderLayout(6, 6));
        tarjeta.setBackground(agotado ? new Color(238, 240, 242) : Color.WHITE);
        tarjeta.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JLabel foto = new JLabel("Sin imagen asignada", SwingConstants.CENTER);
        foto.setPreferredSize(new java.awt.Dimension(150, 120));
        Image imagenCargada = leerImagen(imagen);
        if (imagenCargada != null) {
            foto.setText("");
            if (agotado) {
                imagenCargada = GrayFilter.createDisabledImage(imagenCargada);
            }
            foto.setIcon(new ImageIcon(imagenCargada.getScaledInstance(
                    150, 120, Image.SCALE_SMOOTH)));
        } else if (agotado) {
            foto.setForeground(new Color(145, 150, 155));
        }
        tarjeta.add(foto, BorderLayout.CENTER);

        JPanel datos = new JPanel(new GridLayout(0, 1, 2, 2));
        JLabel etiquetaNombre = new JLabel(nombre);
        etiquetaNombre.setForeground(agotado ? new Color(135, 140, 145) : NEGRO);
        datos.add(etiquetaNombre);
        if (precio != null && !precio.trim().isEmpty()) {
            JLabel etiquetaPrecio = new JLabel("Precio: " + precio);
            etiquetaPrecio.setForeground(agotado ? new Color(145, 150, 155) : NEGRO_SUAVE);
            datos.add(etiquetaPrecio);
        }
        JLabel etiquetaStock = new JLabel(agotado ? "SIN STOCK" : "Disponibles: " + stock);
        etiquetaStock.setFont(new Font("Segoe UI", Font.BOLD, 12));
        etiquetaStock.setForeground(agotado ? new Color(130, 135, 140) : new Color(38, 130, 90));
        datos.add(etiquetaStock);
        datos.setBackground(agotado ? new Color(238, 240, 242) : Color.WHITE);
        tarjeta.add(datos, BorderLayout.SOUTH);

        botonProducto.add(tarjeta, BorderLayout.CENTER);
        botonProducto.setBorder(crearBordeProducto(false));
        botonProducto.addItemListener(evento -> {
            if (!botonProducto.isEnabled()) {
                return;
            }
            if (botonProducto.isSelected()) {
                codigosProductosSeleccionados.add(codigo);
            } else {
                codigosProductosSeleccionados.remove(codigo);
            }
            botonProducto.setBorder(crearBordeProducto(botonProducto.isSelected()));
        });
        agregarClickEnContenido(tarjeta, botonProducto);
        return botonProducto;
    }

    private javax.swing.border.Border crearBordeProducto(boolean seleccionado) {
        Color color = seleccionado ? new Color(48, 158, 207) : new Color(205, 228, 240);
        int grosor = seleccionado ? 2 : 1;
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, grosor),
                BorderFactory.createEmptyBorder(2, 2, 2, 2));
    }

    private void agregarClickEnContenido(Component componente, JToggleButton botonProducto) {
        componente.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evento) {
                if (evento.getButton() == MouseEvent.BUTTON1 && botonProducto.isEnabled()) {
                    botonProducto.doClick();
                }
            }
        });
        if (componente instanceof Container) {
            for (Component hijo : ((Container) componente).getComponents()) {
                agregarClickEnContenido(hijo, botonProducto);
            }
        }
    }

    public Set<String> getCodigosProductosSeleccionados() {
        return Collections.unmodifiableSet(new LinkedHashSet<>(codigosProductosSeleccionados));
    }

    private Image leerImagen(Object valor) {
        if (valor == null) {
            return null;
        }
        try {
            if (valor instanceof byte[]) {
                return ImageIO.read(new ByteArrayInputStream((byte[]) valor));
            }
            if (valor instanceof Blob) {
                Blob blob = (Blob) valor;
                return ImageIO.read(new ByteArrayInputStream(blob.getBytes(1, (int) blob.length())));
            }
            String rutaTexto = String.valueOf(valor).trim();
            if (rutaTexto.isEmpty() || rutaTexto.startsWith("http://")
                    || rutaTexto.startsWith("https://")) {
                return null;
            }
            Path ruta = Paths.get(rutaTexto);
            if (!ruta.isAbsolute()) {
                Path raiz = Paths.get(System.getProperty("user.dir"));
                Path desdeProyecto = raiz.resolve(ruta);
                Path desdeAssets = raiz.resolve("src").resolve("assets").resolve(ruta);
                ruta = Files.exists(desdeProyecto) ? desdeProyecto : desdeAssets;
            }
            File archivo = ruta.toFile();
            return archivo.isFile() ? ImageIO.read(archivo) : null;
        } catch (Exception e) {
            return null;
        }
    }

    private String buscarImagenPorNombre(String nombreProducto) {
        String nombre = normalizarNombre(nombreProducto);
        String archivo = null;
        if (nombre.contains("bioyogurt") || nombre.contains("yogurtbio")) {
            archivo = "bioyogurt.jpg";
        } else if (nombre.contains("cremadeleche")) {
            archivo = "cremadeleche.jpg";
        } else if (nombre.contains("dulcedeleche")) {
            archivo = "dulcedeleche.jpg";
        } else if (nombre.contains("karpil") && nombre.contains("230")) {
            archivo = "karpil230ml.jpg";
        } else if (nombre.contains("karpil") && nombre.contains("900")) {
            archivo = "karpil900ml.jpg";
        } else if (nombre.contains("lecheenpolvo") || nombre.contains("lechepolvo")) {
            archivo = "lecheenpolvo.jpg";
        } else if (nombre.contains("lecheentera")) {
            archivo = "lecheentera.jpg";
        } else if (nombre.contains("mantequilla")) {
            archivo = "mantequilla.jpg";
        } else if (nombre.contains("nutrilac")) {
            archivo = "nutrilac.jpg";
        }
        return archivo == null ? null : "src/assets/productos/" + archivo;
    }

    private String normalizarNombre(String texto) {
        String sinAcentos = Normalizer.normalize(texto == null ? "" : texto,
                Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return sinAcentos.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "");
    }

    private void escalarImagen(javax.swing.JLabel label, String ruta, int alto, int ancho) {
        javax.swing.ImageIcon imageIcon = new javax.swing.ImageIcon(ruta);
        java.awt.Image image = imageIcon.getImage();
        java.awt.Image nuevaImagen = image.getScaledInstance(alto, ancho, java.awt.Image.SCALE_SMOOTH);
        label.setIcon(new javax.swing.ImageIcon(nuevaImagen));
    }
    
    private void escalarImagenBoton(javax.swing.JButton boton, String ruta) {
        javax.swing.ImageIcon imageIcon = new javax.swing.ImageIcon(ruta);
        java.awt.Image image = imageIcon.getImage();
        java.awt.Image nuevaImagen = image.getScaledInstance(32, 32, java.awt.Image.SCALE_SMOOTH);
        boton.setIcon(new javax.swing.ImageIcon(nuevaImagen));
    }
    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSeparator2 = new javax.swing.JSeparator();
        jPanel2 = new javax.swing.JPanel();
        logo = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        botonInicio = new javax.swing.JButton();
        botonCatalogo = new javax.swing.JButton();
        botonPedidos = new javax.swing.JButton();
        botonPerfil = new javax.swing.JButton();
        botonCerrarSesion = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JSeparator();
        BuscarProductos = new javax.swing.JTextField();
        botonperfil = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(800, 600));

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jLabel1.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        jLabel1.setText("Pil Tarija");

        botonInicio.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        botonInicio.setText("Inicio");
        botonInicio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonInicioActionPerformed(evt);
            }
        });

        botonCatalogo.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        botonCatalogo.setText("Catálogo");

        botonPedidos.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        botonPedidos.setText("Mis Pedidos");

        botonPerfil.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        botonPerfil.setText("Perfil");
        botonPerfil.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonPerfilActionPerformed(evt);
            }
        });

        botonCerrarSesion.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        botonCerrarSesion.setText("Cerrar Sesion");
        botonCerrarSesion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonCerrarSesionActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(logo, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel1))
                            .addComponent(botonInicio)
                            .addComponent(botonCatalogo)
                            .addComponent(botonPedidos)
                            .addComponent(botonPerfil)
                            .addComponent(botonCerrarSesion))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(logo, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addComponent(jLabel1)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(botonInicio)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(botonCatalogo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(botonPedidos)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(botonPerfil)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 233, Short.MAX_VALUE)
                .addComponent(botonCerrarSesion)
                .addGap(16, 16, 16))
        );

        BuscarProductos.setForeground(new java.awt.Color(204, 204, 204));
        BuscarProductos.setText("Buscar productos");
        BuscarProductos.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                BuscarProductosFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                BuscarProductosFocusLost(evt);
            }
        });
        BuscarProductos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BuscarProductosActionPerformed(evt);
            }
        });

        botonperfil.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        botonperfil.setBorderPainted(false);
        botonperfil.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonperfilActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator3)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(BuscarProductos, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 143, Short.MAX_VALUE)
                        .addComponent(botonperfil, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jSeparator2)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(BuscarProductos)
                    .addComponent(botonperfil, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BuscarProductosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BuscarProductosActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_BuscarProductosActionPerformed

    private void BuscarProductosFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_BuscarProductosFocusGained
        if (BuscarProductos.getText().equals("Buscar productos")) {
            BuscarProductos.setText("");
            BuscarProductos.setForeground(java.awt.Color.BLACK);
        }
    }//GEN-LAST:event_BuscarProductosFocusGained

    private void BuscarProductosFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_BuscarProductosFocusLost
        if (BuscarProductos.getText().trim().isEmpty()) {
            BuscarProductos.setText("Buscar productos");
            BuscarProductos.setForeground(java.awt.Color.GRAY);
        }
    }//GEN-LAST:event_BuscarProductosFocusLost

    private void botonInicioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonInicioActionPerformed
        mostrarBusquedaCatalogo(false);
    }//GEN-LAST:event_botonInicioActionPerformed


    private void botonPedidosActionPerformed(java.awt.event.ActionEvent evt) {
        mostrarPedidos();
    }

    private void botonPerfilActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonPerfilActionPerformed
        mostrarAcceso();
    }//GEN-LAST:event_botonPerfilActionPerformed

    private void botonCerrarSesionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonCerrarSesionActionPerformed
        int opcion = JOptionPane.showConfirmDialog(
                this,
                "¿Deseas cerrar la sesión?",
                "Cerrar sesión",
                JOptionPane.YES_NO_OPTION);
        if (opcion == JOptionPane.YES_OPTION) {
            new menu().setVisible(true);
            dispose();
        }
    }//GEN-LAST:event_botonCerrarSesionActionPerformed

    private void botonperfilActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonperfilActionPerformed
        mostrarAcceso();
    }//GEN-LAST:event_botonperfilActionPerformed

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
            java.util.logging.Logger.getLogger(menu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(menu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(menu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(menu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new menu().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField BuscarProductos;
    private javax.swing.JButton botonCatalogo;
    private javax.swing.JButton botonCerrarSesion;
    private javax.swing.JButton botonInicio;
    private javax.swing.JButton botonPedidos;
    private javax.swing.JButton botonPerfil;
    private javax.swing.JButton botonperfil;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JLabel logo;
    // End of variables declaration//GEN-END:variables
}
