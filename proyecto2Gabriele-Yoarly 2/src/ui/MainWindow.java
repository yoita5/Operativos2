/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;
import java.nio.file.*;
import javax.swing.table.DefaultTableModel;
import org.json.JSONException;
import proyecto2.Cafarelli.Castillo.Archivo;
import proyecto2.Cafarelli.Castillo.Bloque;
import proyecto2.Cafarelli.Castillo.SistemaArchivos;
import proyecto2.Cafarelli.Castillo.Util;


/**
 *
 */
public class MainWindow extends javax.swing.JFrame {
public SistemaArchivos sistemaArchivos; // Instancia de SistemaArchivos
private Util util;
 private PanelDisco panelDisco;
private DefaultTableModel modeloTabla;
    /**
     * Creates new form MainWindow
     */
    public MainWindow() {
                  
        this.sistemaArchivos = SistemaArchivos.cargarEstado(); // Inicializar sistema de archivos
        initComponents();
         modeloTabla = (DefaultTableModel) tablaArchivos.getModel();
        cargarTablaDesdeCSV();
       

        this.setLocationRelativeTo(null); 
        jTree1.setCellRenderer(new CustomTreeRenderer());
        this.util = new Util(); 
        

        this.panelDisco = new PanelDisco(sistemaArchivos); // Pasar el sistema de archivos

    panelDisco.setPreferredSize(new Dimension(500, 500));

    jPanelDisco.setLayout(new BorderLayout()); // Usa un layout adecuado
    jPanelDisco.add(panelDisco, BorderLayout.CENTER); // Agregar al centro

    jPanelDisco.revalidate(); // Refrescar la interfaz
    jPanelDisco.repaint(); // Forzar que se vuelva a dibujar
    }

    private void guardarTablaEnCSV() {
    File archivo = new File("tabla_archivos.csv");

    // Si la tabla está vacía, eliminar el archivo CSV si existe
    if (modeloTabla.getRowCount() == 0) {
        if (archivo.exists()) {
            archivo.delete();
            System.out.println("📂 No hay datos en la tabla. Archivo CSV eliminado.");
        }
        return;
    }

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivo))) {
        // Escribir encabezados
        writer.write("Nombre,Longitud,PrimerBloque,CadenaEnlaces");
        writer.newLine();

        // Recorrer filas de la tabla y escribir en el archivo
        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            String nombre = modeloTabla.getValueAt(i, 0).toString();
            String longitud = modeloTabla.getValueAt(i, 1).toString();
            String primerBloque = modeloTabla.getValueAt(i, 2).toString();
            String cadenaEnlaces = modeloTabla.getValueAt(i, 3).toString()
                    .replace("<html>", "").replace("</html>", "").replace("<br>", " / ");

            // Escribir la línea en formato CSV
            writer.write(nombre + "," + longitud + "," + primerBloque + "," + cadenaEnlaces);
            writer.newLine();
        }

        System.out.println("✅ Tabla guardada en tabla_archivos.csv");
    } catch (IOException e) {
        System.err.println("❌ Error al guardar la tabla: " + e.getMessage());
    }
}

    private void cargarTablaDesdeCSV() {
    File archivo = new File("tabla_archivos.csv");

    // Si el archivo no existe o está vacío, no hacer nada
    if (!archivo.exists() || archivo.length() == 0) {
        System.out.println("📂 No hay archivo CSV guardado o está vacío. Iniciando con una tabla vacía.");
        return;
    }

    try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
        String linea;
        reader.readLine(); // Saltar la primera línea (encabezado)

        while ((linea = reader.readLine()) != null) {
            String[] datos = linea.split(",", 4); // Dividir en 4 columnas

            if (datos.length < 4) continue; // Si hay datos incompletos, ignorarlos

            String nombre = datos[0];
            int longitud = Integer.parseInt(datos[1]);
            String primerBloque = datos[2];
            String cadenaEnlaces = "<html>" + datos[3].replace(" / ", "<br>") + "</html>";

            // Agregar fila a la tabla
            modeloTabla.addRow(new Object[]{nombre, longitud, primerBloque, cadenaEnlaces});
        }

        System.out.println("✅ Tabla cargada desde tabla_archivos.csv");
    } catch (IOException | NumberFormatException e) {
        System.err.println("❌ Error al cargar la tabla: " + e.getMessage());
    }
}

    
private void actualizarTablaArchivos() {
    // Limpiar la tabla antes de actualizar
    modeloTabla.setRowCount(0);

    Archivo archivoActual = sistemaArchivos.getListaArchivos().getCabeza();

    while (archivoActual != null) {
        String nombre = archivoActual.nombre;
        int bloquesAsignados = archivoActual.tamano;
        
        // Obtener el primer bloque
        Bloque primerBloque = archivoActual.listaBloques.getPrimerBloque();
        String direccionPrimerBloque = (primerBloque != null) ? String.valueOf(primerBloque.id) : "N/A";

        // 📌 **Corrección del ciclo infinito en la cadena de enlaces**
        StringBuilder cadenaEnlaces = new StringBuilder("<html>");  
        Bloque bloqueActual = primerBloque;
        int contador = 0;

        while (bloqueActual != null) {
            cadenaEnlaces.append(bloqueActual.id);
            contador++;  

            // ✅ Solo agregar la flecha si hay un siguiente bloque **y no es el último**
            if (bloqueActual.siguiente != null) {
                if (contador % 5 == 0) {  
                    cadenaEnlaces.append("<br>");  
                } else {
                    cadenaEnlaces.append(" → ");
                }
            }
            
            // Mover al siguiente bloque
            bloqueActual = bloqueActual.siguiente;

            // 🔥 Protección contra ciclos infinitos
            if (contador > bloquesAsignados-1) {  // Asegurar que no sobrepase la cantidad de bloques asignados
                break;
            }
        }

        cadenaEnlaces.append("</html>"); // ✅ Cerrar correctamente el HTML

        // Agregar fila a la tabla
        modeloTabla.addRow(new Object[]{nombre, bloquesAsignados, direccionPrimerBloque, cadenaEnlaces.toString()});
        
        archivoActual = archivoActual.siguiente; // Pasar al siguiente archivo
    }
}






    private void cargarLogDesdeArchivo() {
    File archivo = new File("log.txt");
    if (!archivo.exists()) {
        return; // Si el archivo no existe, no se carga nada
    }

    try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
        StringBuilder contenido = new StringBuilder();
        String linea;
        while ((linea = reader.readLine()) != null) {
            contenido.append(linea).append("\n");
        }
        taLog.setText(contenido.toString()); // Cargar el contenido en el JTextArea
    } catch (IOException e) {
        JOptionPane.showMessageDialog(this, "Error al cargar el log: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}

    private void guardarLogEnArchivo() {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter("log.txt"))) {
        writer.write(taLog.getText()); // Guarda todo el contenido del JTextArea
    } catch (IOException e) {
        JOptionPane.showMessageDialog(this, "Error al guardar el log: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}

    private void registrarLog(String accion, String nombre) {
    // Obtener el usuario actual
    String usuario = lbUsuact.getText();

    // Obtener la fecha y hora actual
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    String fechaHora = sdf.format(new Date());

    // Construir el mensaje del log
    String mensaje = "[" + fechaHora + "] " + usuario + " " + accion + ": " + nombre + "\n";

    // Agregar el mensaje al JTextArea (taLog)
    taLog.append(mensaje);
}

    private void cargarUsuarioDesdeArchivo() {
    File archivo = new File("usuario.txt");
    if (!archivo.exists()) {
        return; // Si el archivo no existe, no hace nada
    }

    try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
        String usuarioGuardado = reader.readLine();
        if (usuarioGuardado != null && !usuarioGuardado.trim().isEmpty()) {
            lbUsuact.setText(usuarioGuardado);
        }
    } catch (IOException e) {
        JOptionPane.showMessageDialog(this, "Error al cargar el usuario: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}

    private void guardarUsuarioEnArchivo(String usuario) {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter("usuario.txt"))) {
        writer.write(usuario);
    } catch (IOException e) {
        JOptionPane.showMessageDialog(this, "Error al guardar el usuario: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
    private void guardarArbolEnJSON(DefaultMutableTreeNode root) {
    JSONObject jsonTree = guardarNodosEnJSON(root);
    try (FileWriter file = new FileWriter("treeData.json")) {
        file.write(jsonTree.toString(4)); // Indentación para hacer el JSON más legible
    } catch (IOException e) {
        JOptionPane.showMessageDialog(this, "Error al guardar el árbol: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}

private JSONObject guardarNodosEnJSON(DefaultMutableTreeNode node) {
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("nombre", node.getUserObject().toString());
    
    // **Solo guardar hijos si el nodo lo permite (para evitar errores con archivos)**
    JSONArray hijosArray = new JSONArray();
    if (node.getAllowsChildren()) {
        for (int i = 0; i < node.getChildCount(); i++) {
            hijosArray.put(guardarNodosEnJSON((DefaultMutableTreeNode) node.getChildAt(i)));
        }
    }
    
    jsonObject.put("hijos", hijosArray);
    return jsonObject;
}

private void cargarArbolDesdeJSON() {
    File archivo = new File("treeData.json");
    if (!archivo.exists()) {
        return; // Si el archivo no existe, no se carga nada
    }

    try {
        String contenido = new String(Files.readAllBytes(Paths.get("treeData.json")));
        JSONObject jsonTree = new JSONObject(contenido);
        
        DefaultMutableTreeNode root = cargarNodosDesdeJSON(jsonTree);
        jTree1.setModel(new DefaultTreeModel(root));
        
        // **Aplicar el renderizador después de cargar el árbol**
        jTree1.setCellRenderer(new CustomTreeRenderer());

    } catch (IOException | JSONException e) {
        JOptionPane.showMessageDialog(this, "Error al cargar el árbol: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}

private DefaultMutableTreeNode cargarNodosDesdeJSON(JSONObject jsonObject) {
    String nombreNodo = jsonObject.getString("nombre");
    DefaultMutableTreeNode node = new DefaultMutableTreeNode(nombreNodo);

    // **Si el nombre tiene "[Tamaño:", es un archivo y NO debe tener hijos**
    if (nombreNodo.contains("[Tamaño:")) {
        node.setAllowsChildren(false);
    }

    JSONArray hijosArray = jsonObject.getJSONArray("hijos");
    for (int i = 0; i < hijosArray.length(); i++) {
        node.add(cargarNodosDesdeJSON(hijosArray.getJSONObject(i)));
    }
    
    return node;
}
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jPanelDisco = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaArchivos = new javax.swing.JTable();
        jLabel15 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        taLog = new javax.swing.JTextArea();
        jLabel10 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        lbModoact = new javax.swing.JLabel();
        btCambiarmodo = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        lbUsuact = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        btCambiarusu = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        jPanel4 = new javax.swing.JPanel();
        tfNombre = new javax.swing.JTextField();
        tfLongitud = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel5 = new javax.swing.JLabel();
        cbSelectortipo = new javax.swing.JComboBox<>();
        btCrear = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        tfSelectednode = new javax.swing.JTextField();
        btEditar = new javax.swing.JButton();
        btEliminar = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTree1 = new javax.swing.JTree();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jPanel2.setBackground(new java.awt.Color(0, 153, 153));

        javax.swing.GroupLayout jPanelDiscoLayout = new javax.swing.GroupLayout(jPanelDisco);
        jPanelDisco.setLayout(jPanelDiscoLayout);
        jPanelDiscoLayout.setHorizontalGroup(
            jPanelDiscoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 312, Short.MAX_VALUE)
        );
        jPanelDiscoLayout.setVerticalGroup(
            jPanelDiscoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 256, Short.MAX_VALUE)
        );

        jLabel11.setBackground(new java.awt.Color(34, 40, 49));
        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(34, 40, 49));
        jLabel11.setText("SIMULACIÓN");

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(34, 40, 49));
        jLabel12.setText("DE DISCO");

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(34, 40, 49));
        jLabel13.setText("------>");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(90, 90, 90)
                        .addComponent(jLabel11))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(109, 109, 109)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel13)
                            .addComponent(jLabel12))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 247, Short.MAX_VALUE)
                .addComponent(jPanelDisco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(139, 139, 139))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(45, 45, 45)
                        .addComponent(jPanelDisco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(170, 170, 170)
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel13)))
                .addContainerGap(231, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("SD", jPanel2);

        jPanel8.setBackground(new java.awt.Color(0, 153, 153));
        jPanel8.setForeground(new java.awt.Color(34, 40, 49));

        tablaArchivos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nombre del Archivo", "Longitud", "Primer Bloque", "Cadena de Enlaces"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tablaArchivos.setRowHeight(150);
        jScrollPane1.setViewportView(tablaArchivos);

        jLabel15.setBackground(new java.awt.Color(34, 40, 49));
        jLabel15.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel15.setText("TABLA DE ASIGNACIÓN DE ARCHIVOS");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addContainerGap(156, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 643, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(137, 137, 137))
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(250, 250, 250)
                .addComponent(jLabel15)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addContainerGap(10, Short.MAX_VALUE)
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(51, 51, 51))
        );

        jTabbedPane1.addTab("Tabla", jPanel8);

        jPanel5.setBackground(new java.awt.Color(0, 153, 153));

        taLog.setColumns(20);
        taLog.setRows(5);
        jScrollPane3.setViewportView(taLog);

        jLabel10.setBackground(new java.awt.Color(204, 204, 204));
        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel10.setText("REGISTRO DE AUDITORIA");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(249, 249, 249)
                        .addComponent(jLabel10))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(56, 56, 56)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 825, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(55, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 393, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(73, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Log", jPanel5);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jPanel3.setBackground(new java.awt.Color(0, 153, 153));

        jLabel1.setBackground(new java.awt.Color(0, 0, 0));
        jLabel1.setText("Actualmente te encuentras en modo:");

        lbModoact.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lbModoact.setText("Usuario");

        btCambiarmodo.setText("Cambiar a modo Admin");
        btCambiarmodo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btCambiarmodoActionPerformed(evt);
            }
        });

        jLabel6.setBackground(new java.awt.Color(0, 0, 0));
        jLabel6.setText("Usuario actual:");

        lbUsuact.setBackground(new java.awt.Color(0, 0, 0));
        lbUsuact.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lbUsuact.setText("lbUsuact");

        jPanel7.setBackground(new java.awt.Color(95, 101, 110));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(238, 238, 238));
        jLabel7.setText("Cambiar usuario");

        jLabel8.setForeground(new java.awt.Color(238, 238, 238));
        jLabel8.setText("Ingresa tu nombre:");

        btCambiarusu.setText("Cambiar");
        btCambiarusu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btCambiarusuActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btCambiarusu))))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(158, 158, 158)
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(144, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btCambiarusu))
                .addGap(20, 20, 20))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lbUsuact, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jSeparator2)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                                    .addComponent(jLabel1)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(lbModoact, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(btCambiarmodo, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addContainerGap(21, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btCambiarmodo)
                    .addComponent(lbModoact)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(lbUsuact))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(38, Short.MAX_VALUE))
        );

        jPanel4.setBackground(new java.awt.Color(0, 153, 153));
        jPanel4.setForeground(new java.awt.Color(238, 238, 238));

        jLabel3.setForeground(new java.awt.Color(238, 238, 238));
        jLabel3.setText("Nombre:");

        jLabel4.setForeground(new java.awt.Color(238, 238, 238));
        jLabel4.setText("Longitud:");

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jLabel5.setForeground(new java.awt.Color(238, 238, 238));
        jLabel5.setText("Tipo:");

        cbSelectortipo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Archivo", "Directorio" }));
        cbSelectortipo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbSelectortipoActionPerformed(evt);
            }
        });

        btCrear.setText("Crear");
        btCrear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btCrearActionPerformed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel9.setText("Crear Directorios y Archivos");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel3)
                            .addComponent(tfNombre)
                            .addComponent(jLabel4)
                            .addComponent(tfLongitud, javax.swing.GroupLayout.DEFAULT_SIZE, 167, Short.MAX_VALUE)))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(jLabel9)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(40, 40, 40)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel5)
                    .addComponent(cbSelectortipo, 0, 175, Short.MAX_VALUE)
                    .addComponent(btCrear, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(21, 21, 21))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(jLabel5))
                        .addGap(5, 5, 5)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(tfNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tfLongitud, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(cbSelectortipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(28, 28, 28)
                                .addComponent(btCrear))))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        jPanel6.setBackground(new java.awt.Color(0, 153, 153));

        tfSelectednode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfSelectednodeActionPerformed(evt);
            }
        });

        btEditar.setText("Editar");
        btEditar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btEditarActionPerformed(evt);
            }
        });

        btEliminar.setText("Eliminar");
        btEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btEliminarActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel2.setText("Editar/Eliminar Directorios y Archivos");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(tfSelectednode, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(61, 61, 61)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btEditar, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(49, 49, 49))
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(tfSelectednode, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(37, 37, 37)
                        .addComponent(btEditar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btEliminar)))
                .addGap(17, 23, Short.MAX_VALUE))
        );

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("JTree");
        jTree1.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        jTree1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTree1MouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jTree1);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 57, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 339, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(40, 40, 40))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 506, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(12, 12, 12))
        );

        jTabbedPane1.addTab("JTree", jPanel1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btCambiarmodoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btCambiarmodoActionPerformed
       if (lbModoact.getText().equals("Usuario")) {
        lbModoact.setText("Admin");
        btCambiarmodo.setText("Cambiar a Usuario");
    } else {
        lbModoact.setText("Usuario");
        btCambiarmodo.setText("Cambiar a Admin");
    }
        
    }//GEN-LAST:event_btCambiarmodoActionPerformed

    private void tfSelectednodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfSelectednodeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfSelectednodeActionPerformed

    private void btEditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btEditarActionPerformed
 if (lbModoact.getText().equals("Usuario")) {
        JOptionPane.showMessageDialog(this, "No tienes permisos para editar en modo Usuario.", "Acceso Denegado", JOptionPane.WARNING_MESSAGE);
        return;
    }
         
    DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) jTree1.getSelectionPath().getLastPathComponent();
    
    if (selectedNode == null) {
        JOptionPane.showMessageDialog(this, "Selecciona un nodo para editar.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // variable que guarda el nuevo nombre que se le va a dar al archivo/directorio del textfield
    String nuevoNombre = tfSelectednode.getText().trim();
    if (nuevoNombre.isEmpty()) {
        JOptionPane.showMessageDialog(this, "El nombre no puede estar vacío.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    
    // variable que tiene el string del texto del nodo seleccionado archivo o directorio
    String nodoTexto = selectedNode.getUserObject().toString();
    
    //obtenemos detalles del archivo
    int index = nodoTexto.indexOf("[");
    String detalles = (index != -1) ? nodoTexto.substring(index) : ""; // Mantener todo lo demás
    
    //debemos obtener el nombre ORIGINAL del archivo antes de modificarlo para cambiarlo tambien en la ll de archivos si es que es un archivo, si no pues no pasa nada
    String nombreOg = util.extraerNombreArchivo(nodoTexto);
    
    //antes de modificar el nombre en el JTREE, si se trata de un archivo, lo hacemos en el objeto arhcivo correpondiente al archivo seleccionado:
    //LOGICA TOCHA Y MANIPULACION DE LAS EDDS PARA MODIFICAR EFICIENTEMENTE EL NOMBRE DE UN ARCHIVO
    //verificar si es un archivo o es un directorio
    if (nodoTexto.contains("[Tamaño:")) {
       Archivo archivoActual = sistemaArchivos.getListaArchivos().getCabeza();
        while (archivoActual != null) {
            if (!archivoActual.nombre.equals(nombreOg) && archivoActual.nombre.equals(nuevoNombre)) {
                JOptionPane.showMessageDialog(this, "Ya existe un archivo con el mismo nombre.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            archivoActual = archivoActual.siguiente; // Avanzar en la lista
        }
       //buscamos el archivo con la funcion de buscar archivos de la clase sistemaarchivos con su nombre original antes de  er modificado
       Archivo archivoModif = sistemaArchivos.buscarArchivo(nombreOg);
     
       //ahora modificamos el nombre del archivo seleccionado 
       sistemaArchivos.renombrarArchivo(archivoModif, nuevoNombre);  
    }
    
    else {
    for (int i = 0; i < selectedNode.getParent().getChildCount(); i++) {
        DefaultMutableTreeNode siblingNode = (DefaultMutableTreeNode) selectedNode.getParent().getChildAt(i);
        String siblingName = siblingNode.toString();

        if (!siblingNode.equals(selectedNode) && siblingName.startsWith(nuevoNombre + " [")) {
            JOptionPane.showMessageDialog(this, "Ya existe un directorio con el mismo nombre en este nivel.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    }
}
    // Actualizar el nodo con el nuevo nombre sin perder datos EN EL JTREE
    selectedNode.setUserObject(nuevoNombre + " " + detalles);
    
    

    DefaultTreeModel model = (DefaultTreeModel) jTree1.getModel();
    model.reload();
    
    registrarLog("modificó", nuevoNombre);
    actualizarTablaArchivos();
    
    }//GEN-LAST:event_btEditarActionPerformed

    private void btEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btEliminarActionPerformed
    if (lbModoact.getText().equals("Usuario")) {
        JOptionPane.showMessageDialog(this, "No tienes permisos para eliminar en modo Usuario.", "Acceso Denegado", JOptionPane.WARNING_MESSAGE);
        return;
    }

    DefaultTreeModel model = (DefaultTreeModel) jTree1.getModel();
    DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) jTree1.getSelectionPath().getLastPathComponent();

    if (selectedNode == null) {
        JOptionPane.showMessageDialog(this, "Por favor, selecciona un nodo para eliminar.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Evitar eliminar la raíz
    DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) selectedNode.getParent();
    if (parentNode == null) {
        JOptionPane.showMessageDialog(this, "No se puede eliminar la raíz del árbol.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Confirmación antes de eliminar
    int confirm = JOptionPane.showConfirmDialog(this, "¿Estás seguro de que quieres eliminar '" + selectedNode.toString() + "'?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
    if (confirm != JOptionPane.YES_OPTION) {
        return;
    }
    
    
    //LOGICA Y MANIPULACION DE LAS EDDS PARA ELIMINAR EFICIENTEMENTE UN ARCHIVO
    //verificar si es un archivo o es un directorio
    if (selectedNode.getUserObject().toString().contains("[Tamaño:")) {
        
       //dado el string completo que se muestra en el jtree, usamos la funcion que esta en la clase util que nos permite extraer solo el nombre del archivo 
       String nomArchivoElim = util.extraerNombreArchivo(selectedNode.getUserObject().toString());
       
       //buscamos el archivo con la funcion de buscar archivos de la clase sistemaarchivos
       Archivo archivoElim = sistemaArchivos.buscarArchivo(nomArchivoElim);
       
       //con el archivo encontrado, liberamos sus bloques asignados
       sistemaArchivos.liberarBloquesArchivo(archivoElim);
       
       //ahora eliminamos el archivo seleccionado de la lista enlazada de archivos
       sistemaArchivos.eliminarArchivo(archivoElim); 
       
       registrarLog("eliminó", nomArchivoElim);
    }
    // 🔹 Si es un directorio, eliminar todos sus archivos y subdirectorios recursivamente
    else {
        eliminarDirectorio(selectedNode, model);
         String nombreEliminado = selectedNode.toString();
         registrarLog("eliminó", nombreEliminado);
    }
   
    // Eliminar el nodo padre
    parentNode.remove(selectedNode);
    model.reload();
    
    actualizarTablaArchivos();

    
    }//GEN-LAST:event_btEliminarActionPerformed
private void eliminarDirectorio(DefaultMutableTreeNode nodoDirectorio, DefaultTreeModel treeModel) {
    if (nodoDirectorio == null) return;

    // 🔹 Recorrer los hijos desde el final para evitar problemas al eliminar
    for (int i = nodoDirectorio.getChildCount() - 1; i >= 0; i--) {
        DefaultMutableTreeNode hijo = (DefaultMutableTreeNode) nodoDirectorio.getChildAt(i);

        // 🔹 Si el hijo es un directorio, eliminarlo recursivamente
        if (!hijo.getUserObject().toString().contains("[Tamaño:")) {
            eliminarDirectorio(hijo, treeModel);
        } 
        // 🔹 Si el hijo es un archivo, eliminarlo correctamente
        else {
            String nomArchivoElim = util.extraerNombreArchivo(hijo.getUserObject().toString());
            Archivo archivoElim = sistemaArchivos.buscarArchivo(nomArchivoElim);
            sistemaArchivos.liberarBloquesArchivo(archivoElim);
            sistemaArchivos.eliminarArchivo(archivoElim);
        }

        // 🔹 Eliminar el nodo del árbol
        treeModel.removeNodeFromParent(hijo);
    }
}

    private void jTree1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTree1MouseClicked
         // Mostrar solo el nombre del nodo seleccionado en el JTextField
    TreeSelectionModel smd = jTree1.getSelectionModel();
    if (smd.getSelectionCount() > 0) {
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) jTree1.getSelectionPath().getLastPathComponent();
        String nodoTexto = selectedNode.getUserObject().toString();
        
        // Extraer solo el nombre antes del primer "["
        int index = nodoTexto.indexOf("[");
        if (index != -1) {
            tfSelectednode.setText(nodoTexto.substring(0, index).trim()); 
        } else {
            tfSelectednode.setText(nodoTexto.trim()); // Si no hay "[", es solo el nombre
        }
    }
    }//GEN-LAST:event_jTree1MouseClicked

    private void cbSelectortipoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbSelectortipoActionPerformed
                                                 
    if (cbSelectortipo.getSelectedItem().equals("Directorio")) {
        tfLongitud.setEnabled(false); // Desactivar campo de longitud
        tfLongitud.setText(""); // Limpiar el campo
    } else {
        tfLongitud.setEnabled(true); // Activar campo si es Archivo
    }

    }//GEN-LAST:event_cbSelectortipoActionPerformed

    private void btCrearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btCrearActionPerformed
                                     
                                   
    // Verificar si el usuario está en modo "Usuario"
    if (lbModoact.getText().equals("Usuario")) {
        JOptionPane.showMessageDialog(this, "No tienes permisos para crear elementos en modo Usuario.", "Acceso Denegado", JOptionPane.WARNING_MESSAGE);
        return;
    }

    String nombre = tfNombre.getText().trim();
    String tipo = cbSelectortipo.getSelectedItem().toString(); 
    String longitudStr = tfLongitud.getText().trim();

    if (nombre.isEmpty()) {
        JOptionPane.showMessageDialog(this, "El nombre no puede estar vacío.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }
    
    if (jTree1.getSelectionPath() == null) {
    JOptionPane.showMessageDialog(this, "Selecciona un nodo donde agregar el nuevo elemento.", "Error", JOptionPane.ERROR_MESSAGE);
    return;
}


    DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) jTree1.getSelectionPath().getLastPathComponent();

    if (selectedNode == null) {
        JOptionPane.showMessageDialog(this, "Selecciona un nodo donde agregar el nuevo elemento.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    
    if (!selectedNode.getAllowsChildren()) {
        JOptionPane.showMessageDialog(this, "No puedes agregar hijos a un archivo.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Obtener la fecha de creación
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    String fechaCreacion = sdf.format(new Date());

    // Definir permisos básicos
    String permisos = "rwx";  // (lectura, escritura, ejecución)

    DefaultMutableTreeNode newNode;
    if (tipo.equals("Archivo")) {
        
         Archivo archivoActual = sistemaArchivos.getListaArchivos().getCabeza();
    while (archivoActual != null) {
        if (archivoActual.nombre.equals(nombre)) {
            JOptionPane.showMessageDialog(this, "Ya existe un archivo con el mismo nombre.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        archivoActual = archivoActual.siguiente; // Avanzar en la lista de archivos
    }
        
        
        if (longitudStr.isEmpty() || !longitudStr.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "La longitud debe ser un número entero mayor a 0.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
//
        int longitud = Integer.parseInt(longitudStr);
        if (longitud <= 0) {
            JOptionPane.showMessageDialog(this, "La longitud debe ser mayor a 0.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (longitud > sistemaArchivos.bloquesLibres) {
            JOptionPane.showMessageDialog(this, "No hay suficiente espacio para dicha longitud", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Formato del archivo con su información
        String infoArchivo = nombre + " [Tamaño: " + longitud + " | Permisos: " + permisos + " | Creado: " + fechaCreacion + "]";
        newNode = new DefaultMutableTreeNode(infoArchivo);
        
        //crear archivo objeto tipo archivo
        Archivo nuevoArchivo = new Archivo(nombre, longitud);
        sistemaArchivos.asignarBloquesArchivo(nuevoArchivo, longitud);
        sistemaArchivos.agregarArchivo(nuevoArchivo);

        //  **Evitar que el archivo tenga hijos**
        newNode.setAllowsChildren(false);
    } else {
        
         for (int i = 0; i < selectedNode.getChildCount(); i++) {
        DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) selectedNode.getChildAt(i);
        String childName = childNode.toString();
        
        // Verificar si el nombre coincide (sin los detalles entre "[ ]")
        if (childName.startsWith(nombre + " [Directorio")) {
            JOptionPane.showMessageDialog(this, "Ya existe un directorio con el mismo nombre en este nivel.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    }

        // Formato del directorio con su información
        String infoDirectorio = nombre + " [Directorio | Permisos: " + permisos + " | Creado: " + fechaCreacion + "]";
        newNode = new DefaultMutableTreeNode(infoDirectorio);
    }

    selectedNode.add(newNode);
    DefaultTreeModel model = (DefaultTreeModel) jTree1.getModel();
    model.reload();

    tfNombre.setText("");
    tfLongitud.setText("");
    
    registrarLog("creó", nombre);
panelDisco.actualizarDisco();
actualizarTablaArchivos();

    }//GEN-LAST:event_btCrearActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
         cargarArbolDesdeJSON();
         cargarUsuarioDesdeArchivo();
         cargarLogDesdeArchivo();
    }//GEN-LAST:event_formWindowOpened

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
     DefaultMutableTreeNode root = (DefaultMutableTreeNode) jTree1.getModel().getRoot();
     guardarArbolEnJSON(root);
     guardarUsuarioEnArchivo(lbUsuact.getText());
     
     sistemaArchivos.guardarEstado();
     guardarLogEnArchivo();
     guardarTablaEnCSV(); 
    }//GEN-LAST:event_formWindowClosing

    private void btCambiarusuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btCambiarusuActionPerformed
        String nuevoUsuario = jTextField1.getText().trim();

    if (nuevoUsuario.isEmpty()) {
        JOptionPane.showMessageDialog(this, "El nombre de usuario no puede estar vacío.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Cambiar el usuario en la etiqueta
    lbUsuact.setText(nuevoUsuario);

    // Guardar usuario en el archivo
    guardarUsuarioEnArchivo(nuevoUsuario);
    }//GEN-LAST:event_btCambiarusuActionPerformed

    
 
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
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainWindow().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btCambiarmodo;
    private javax.swing.JButton btCambiarusu;
    private javax.swing.JButton btCrear;
    private javax.swing.JButton btEditar;
    private javax.swing.JButton btEliminar;
    private javax.swing.JComboBox<String> cbSelectortipo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanelDisco;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTree jTree1;
    private javax.swing.JLabel lbModoact;
    private javax.swing.JLabel lbUsuact;
    private javax.swing.JTextArea taLog;
    private javax.swing.JTable tablaArchivos;
    private javax.swing.JTextField tfLongitud;
    private javax.swing.JTextField tfNombre;
    private javax.swing.JTextField tfSelectednode;
    // End of variables declaration//GEN-END:variables
}
