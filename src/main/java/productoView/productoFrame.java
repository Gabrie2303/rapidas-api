package productoView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONObject;

public class productoFrame extends javax.swing.JFrame {

    private JTextField txtId, txtNombre, txtPrecio, txtImagenUrl;
    private JTextArea txtDescripcion;
    private JCheckBox chkDisponible;
    private JTable tablaProductos;
    private DefaultTableModel modeloTabla;
    private JButton btnGuardar, btnActualizar, btnEliminar, btnLimpiar;

    private final String API_URL = "http://localhost:8081/api/productos";

    public productoFrame() {
        initComponentsCustom();
        cargarProductos();
    }

    private void initComponentsCustom() {
        setTitle("Gestión de Productos - Comidas Rápidas");
        setSize(850, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // --- PANEL FORMULARIO (IZQUIERDA) ---
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBorder(BorderFactory.createTitledBorder("Datos del Producto"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtId = new JTextField();
        txtId.setEditable(false);
        txtNombre = new JTextField(15);
        txtPrecio = new JTextField(15);
        txtImagenUrl = new JTextField(15);
        txtDescripcion = new JTextArea(3, 15);
        chkDisponible = new JCheckBox("Disponible");
        chkDisponible.setSelected(true);

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; panelForm.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1; panelForm.add(txtId, gbc); row++;

        gbc.gridx = 0; gbc.gridy = row; panelForm.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1; panelForm.add(txtNombre, gbc); row++;

        gbc.gridx = 0; gbc.gridy = row; panelForm.add(new JLabel("Precio ($):"), gbc);
        gbc.gridx = 1; panelForm.add(txtPrecio, gbc); row++;

        gbc.gridx = 0; gbc.gridy = row; panelForm.add(new JLabel("Descripción:"), gbc);
        gbc.gridx = 1; panelForm.add(new JScrollPane(txtDescripcion), gbc); row++;

        gbc.gridx = 0; gbc.gridy = row; panelForm.add(new JLabel("URL Imagen:"), gbc);
        gbc.gridx = 1; panelForm.add(txtImagenUrl, gbc); row++;

        gbc.gridx = 1; gbc.gridy = row; panelForm.add(chkDisponible, gbc); row++;

        // --- BOTONES ---
        JPanel panelBotones = new JPanel(new GridLayout(2, 2, 5, 5));
        btnGuardar = new JButton("Guardar");
        btnActualizar = new JButton("Actualizar");
        btnEliminar = new JButton("Eliminar");
        btnLimpiar = new JButton("Limpiar");

        panelBotones.add(btnGuardar);
        panelBotones.add(btnActualizar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        panelForm.add(panelBotones, gbc);

        add(panelForm, BorderLayout.WEST);

        // --- TABLA DE PRODUCTOS (DERECHA) ---
        modeloTabla = new DefaultTableModel(new String[]{"ID", "Nombre", "Precio", "Disponible", "Descripción"}, 0);
        tablaProductos = new JTable(modeloTabla);
        add(new JScrollPane(tablaProductos), BorderLayout.CENTER);

        // --- EVENTOS ---
        btnGuardar.addActionListener(e -> guardarProducto());
        btnActualizar.addActionListener(e -> actualizarProducto());
        btnEliminar.addActionListener(e -> eliminarProducto());
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        tablaProductos.getSelectionModel().addListSelectionListener(e -> seleccionarFila());
    }

    // --- MÉTODOS HTTP API ---

    private void cargarProductos() {
        modeloTabla.setRowCount(0);
        try {
            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            if (conn.getResponseCode() == 200) {
                Scanner scanner = new Scanner(url.openStream());
                StringBuilder inline = new StringBuilder();
                while (scanner.hasNext()) {
                    inline.append(scanner.nextLine());
                }
                scanner.close();

                JSONArray jsonArray = new JSONArray(inline.toString());
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    modeloTabla.addRow(new Object[]{
                        obj.optLong("idProducto"),
                        obj.optString("nombre"),
                        obj.optDouble("precio"),
                        obj.optBoolean("disponible", true) ? "Sí" : "No",
                        obj.optString("descripcion")
                    });
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar productos: " + ex.getMessage());
        }
    }

    private void guardarProducto() {
        try {
            JSONObject json = new JSONObject();
            json.put("nombre", txtNombre.getText());
            json.put("precio", Double.parseDouble(txtPrecio.getText()));
            json.put("descripcion", txtDescripcion.getText());
            json.put("imagenUrl", txtImagenUrl.getText());
            json.put("disponible", chkDisponible.isSelected());

            enviarPeticion(API_URL, "POST", json.toString());
            limpiarFormulario();
            cargarProductos();
            JOptionPane.showMessageDialog(this, "Producto guardado correctamente");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar: " + ex.getMessage());
        }
    }

    private void actualizarProducto() {
        if (txtId.getText().isEmpty()) return;
        try {
            JSONObject json = new JSONObject();
            json.put("nombre", txtNombre.getText());
            json.put("precio", Double.parseDouble(txtPrecio.getText()));
            json.put("descripcion", txtDescripcion.getText());
            json.put("imagenUrl", txtImagenUrl.getText());
            json.put("disponible", chkDisponible.isSelected());

            enviarPeticion(API_URL + "/" + txtId.getText(), "PUT", json.toString());
            limpiarFormulario();
            cargarProductos();
            JOptionPane.showMessageDialog(this, "Producto actualizado correctamente");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al actualizar: " + ex.getMessage());
        }
    }

    private void eliminarProducto() {
        if (txtId.getText().isEmpty()) return;
        int confirm = JOptionPane.showConfirmDialog(this, "¿Eliminar este producto?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                enviarPeticion(API_URL + "/" + txtId.getText(), "DELETE", null);
                limpiarFormulario();
                cargarProductos();
                JOptionPane.showMessageDialog(this, "Producto eliminado");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al eliminar: " + ex.getMessage());
            }
        }
    }

    private void enviarPeticion(String urlStr, String metodo, String jsonBody) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(metodo);
        conn.setRequestProperty("Content-Type", "application/json");

        if (jsonBody != null) {
            conn.setDoOutput(true);
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonBody.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
        }
        conn.getResponseCode();
    }

    private void seleccionarFila() {
        int fila = tablaProductos.getSelectedRow();
        if (fila >= 0) {
            txtId.setText(modeloTabla.getValueAt(fila, 0).toString());
            txtNombre.setText(modeloTabla.getValueAt(fila, 1).toString());
            txtPrecio.setText(modeloTabla.getValueAt(fila, 2).toString());
            chkDisponible.setSelected("Sí".equals(modeloTabla.getValueAt(fila, 3)));
            txtDescripcion.setText(modeloTabla.getValueAt(fila, 4).toString());
        }
    }

    private void limpiarFormulario() {
        txtId.setText("");
        txtNombre.setText("");
        txtPrecio.setText("");
        txtDescripcion.setText("");
        txtImagenUrl.setText("");
        chkDisponible.setSelected(true);
        tablaProductos.clearSelection();
    }

    public static void main(String args[]) {
        SwingUtilities.invokeLater(() -> new productoFrame().setVisible(true));
    }
}