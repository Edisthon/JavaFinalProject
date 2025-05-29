package com.hospital.client.view;

import com.hospital.client.util.RmiClientUtil;
import com.hospital.common.model.Doctor;
import com.hospital.common.service.IDoctorService;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Vector;
import java.util.regex.Pattern;

public class DoctorManagementForm extends JFrame {

    private JTextField doctorIdField;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField specializationField;
    private JTextField contactNumberField;
    private JTextField emailField;

    private JButton addDoctorButton;
    private JButton updateDoctorButton;
    private JButton deleteDoctorButton;
    private JButton searchButton;
    private JButton clearFieldsButton;
    private JButton loadAllButton;
    private JButton exportCsvButton; // New button

    private JTable doctorTable;
    private DefaultTableModel tableModel;

    private IDoctorService doctorService;
    private String sessionToken;

    // Regex for basic email validation (same as PatientManagementForm)
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");
    // Regex for contact number (same as PatientManagementForm)
    private static final Pattern CONTACT_PATTERN = Pattern.compile("^[\\d\\s+()-]{7,15}$");


    public DoctorManagementForm(String sessionToken) {
        this.sessionToken = sessionToken;
        setTitle("Manage Doctors - Hospital Management System");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        try {
            doctorService = RmiClientUtil.getDoctorService();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error connecting to doctor service: " + e.getMessage(), "Service Error", JOptionPane.ERROR_MESSAGE);
        }

        initComponents();
        layoutComponents();
        addEventListeners();

        loadDoctors();
    }

    private void initComponents() {
        doctorIdField = new JTextField(10);
        doctorIdField.setEditable(false);
        firstNameField = new JTextField(15);
        lastNameField = new JTextField(15);
        specializationField = new JTextField(15);
        contactNumberField = new JTextField(15);
        emailField = new JTextField(20);

        addDoctorButton = new JButton("Add Doctor");
        updateDoctorButton = new JButton("Update Doctor");
        deleteDoctorButton = new JButton("Delete Doctor");
        searchButton = new JButton("Search");
        clearFieldsButton = new JButton("Clear Fields");
        loadAllButton = new JButton("Load All");
        exportCsvButton = new JButton("Export to CSV");

        String[] columnNames = {"ID", "First Name", "Last Name", "Specialization", "Contact", "Email"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        doctorTable = new JTable(tableModel);
        doctorTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void layoutComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("Doctor ID:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; formPanel.add(doctorIdField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("First Name:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; formPanel.add(firstNameField, gbc);

        gbc.gridx = 2; gbc.gridy = 1; formPanel.add(new JLabel("Last Name:"), gbc);
        gbc.gridx = 3; gbc.gridy = 1; formPanel.add(lastNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(new JLabel("Specialization:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; formPanel.add(specializationField, gbc);

        gbc.gridx = 2; gbc.gridy = 2; formPanel.add(new JLabel("Contact Number:"), gbc);
        gbc.gridx = 3; gbc.gridy = 2; formPanel.add(contactNumberField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3; formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(emailField, gbc);
        gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(addDoctorButton);
        buttonPanel.add(updateDoctorButton);
        buttonPanel.add(deleteDoctorButton);
        buttonPanel.add(searchButton);
        buttonPanel.add(clearFieldsButton);
        buttonPanel.add(loadAllButton);
        buttonPanel.add(exportCsvButton);

        mainPanel.add(formPanel, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(doctorTable), BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void addEventListeners() {
        addDoctorButton.addActionListener(e -> addDoctor());
        updateDoctorButton.addActionListener(e -> updateDoctor());
        deleteDoctorButton.addActionListener(e -> deleteDoctor());
        searchButton.addActionListener(e -> searchDoctors());
        clearFieldsButton.addActionListener(e -> clearFields());
        loadAllButton.addActionListener(e -> loadDoctors());
        exportCsvButton.addActionListener(e -> exportDataToCsv());

        doctorTable.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting() && doctorTable.getSelectedRow() != -1) {
                populateFieldsFromSelectedRow();
            }
        });
    }

    private void populateFieldsFromSelectedRow() {
        int selectedRow = doctorTable.getSelectedRow();
        if (selectedRow >= 0) {
            doctorIdField.setText(tableModel.getValueAt(selectedRow, 0) != null ? tableModel.getValueAt(selectedRow, 0).toString() : "");
            firstNameField.setText(tableModel.getValueAt(selectedRow, 1) != null ? tableModel.getValueAt(selectedRow, 1).toString() : "");
            lastNameField.setText(tableModel.getValueAt(selectedRow, 2) != null ? tableModel.getValueAt(selectedRow, 2).toString() : "");
            specializationField.setText(tableModel.getValueAt(selectedRow, 3) != null ? tableModel.getValueAt(selectedRow, 3).toString() : "");
            contactNumberField.setText(tableModel.getValueAt(selectedRow, 4) != null ? tableModel.getValueAt(selectedRow, 4).toString() : "");
            emailField.setText(tableModel.getValueAt(selectedRow, 5) != null ? tableModel.getValueAt(selectedRow, 5).toString() : "");
        }
    }
    
    private boolean validateInputs(boolean isUpdate) {
        // 1. Non-Empty Fields
        if (firstNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "First name is required.", "Input Error", JOptionPane.WARNING_MESSAGE);
            firstNameField.requestFocus();
            return false;
        }
        if (lastNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Last name is required.", "Input Error", JOptionPane.WARNING_MESSAGE);
            lastNameField.requestFocus();
            return false;
        }
        if (specializationField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Specialization is required.", "Input Error", JOptionPane.WARNING_MESSAGE);
            specializationField.requestFocus();
            return false;
        }

        // 2. Valid Email Format (if email is provided)
        String email = emailField.getText().trim();
        if (!email.isEmpty() && !EMAIL_PATTERN.matcher(email).matches()) {
            JOptionPane.showMessageDialog(this, "Invalid email format.", "Input Error", JOptionPane.WARNING_MESSAGE);
            emailField.requestFocus();
            return false;
        }
        
        // 3. Contact Number Format (if provided)
        String contact = contactNumberField.getText().trim();
        if (!contact.isEmpty() && !CONTACT_PATTERN.matcher(contact).matches()) {
            JOptionPane.showMessageDialog(this, "Invalid contact number format. Use 7-15 digits, optionally with +,-,() and spaces.", "Input Error", JOptionPane.WARNING_MESSAGE);
            contactNumberField.requestFocus();
            return false;
        }
        
        // 4. Numeric ID for update
        if (isUpdate) {
            if (doctorIdField.getText().trim().isEmpty()) {
                 JOptionPane.showMessageDialog(this, "Doctor ID is required for update. Please select a doctor.", "Input Error", JOptionPane.WARNING_MESSAGE);
                 return false;
            }
            try {
                Long.parseLong(doctorIdField.getText().trim());
            } catch (NumberFormatException e) {
                 JOptionPane.showMessageDialog(this, "Doctor ID must be a valid number for update.", "Input Error", JOptionPane.WARNING_MESSAGE);
                 return false;
            }
        }
        return true; // All validations passed
    }


    private void loadDoctors() {
        if (doctorService == null) {
            JOptionPane.showMessageDialog(this, "Doctor service not available.", "Error", JOptionPane.ERROR_MESSAGE);
            addDummyDataToTable();
            return;
        }
        try {
            List<Doctor> doctors = doctorService.getAllDoctors(sessionToken);
            tableModel.setRowCount(0);
            if (doctors != null) {
                for (Doctor doctor : doctors) {
                    Vector<Object> row = new Vector<>();
                    row.add(doctor.getDoctorId());
                    row.add(doctor.getFirstName());
                    row.add(doctor.getLastName());
                    row.add(doctor.getSpecialization());
                    row.add(doctor.getContactNumber());
                    row.add(doctor.getEmail());
                    tableModel.addRow(row);
                }
            } else {
                 JOptionPane.showMessageDialog(this, "No doctors found or error loading doctors.", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading doctors: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            addDummyDataToTable();
        }
    }
    
    private void addDummyDataToTable() {
        tableModel.setRowCount(0);
        tableModel.addRow(new Object[]{1L, "Alice", "Wonder", "Cardiology", "555-0101", "alice.wonder@example.com"});
        tableModel.addRow(new Object[]{2L, "Bob", "Builder", "Pediatrics", "555-0202", "bob.builder@example.com"});
    }

    private void addDoctor() {
        if (!validateInputs(false)) { // Pass false for 'isUpdate'
            return;
        }
        try {
            if (doctorService == null) {
                JOptionPane.showMessageDialog(this, "Doctor service not available.", "Service Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Doctor doctor = new Doctor();
            doctor.setFirstName(firstNameField.getText().trim());
            doctor.setLastName(lastNameField.getText().trim());
            doctor.setSpecialization(specializationField.getText().trim());
            doctor.setContactNumber(contactNumberField.getText().trim());
            doctor.setEmail(emailField.getText().trim());

            String response = doctorService.addDoctor(doctor, sessionToken);
            JOptionPane.showMessageDialog(this, response, "Add Doctor", JOptionPane.INFORMATION_MESSAGE);
            if (response.toLowerCase().contains("success")) {
                loadDoctors();
                clearFields();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error adding doctor: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateDoctor() {
        if (!validateInputs(true)) { // Pass true for 'isUpdate'
            return;
        }
        try {
            if (doctorService == null) {
                JOptionPane.showMessageDialog(this, "Doctor service not available.", "Service Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Doctor doctor = new Doctor();
            doctor.setDoctorId(Long.parseLong(doctorIdField.getText().trim())); // Validated
            doctor.setFirstName(firstNameField.getText().trim());
            doctor.setLastName(lastNameField.getText().trim());
            doctor.setSpecialization(specializationField.getText().trim());
            doctor.setContactNumber(contactNumberField.getText().trim());
            doctor.setEmail(emailField.getText().trim());

            String response = doctorService.updateDoctor(doctor, sessionToken);
            JOptionPane.showMessageDialog(this, response, "Update Doctor", JOptionPane.INFORMATION_MESSAGE);
            if (response.toLowerCase().contains("success")) {
                loadDoctors();
                clearFields();
            }
        } catch (Exception e) { // Catch NumberFormatException for ID again, though validateInputs should handle it
            JOptionPane.showMessageDialog(this, "Error updating doctor: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteDoctor() {
        try {
            if (doctorService == null) {
                JOptionPane.showMessageDialog(this, "Doctor service not available.", "Service Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (doctorIdField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select a doctor to delete or enter Doctor ID.", "Delete Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Long doctorId = Long.parseLong(doctorIdField.getText().trim());
            int confirmation = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete doctor ID: " + doctorId + "?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
            if (confirmation == JOptionPane.YES_OPTION) {
                String response = doctorService.deleteDoctor(doctorId, sessionToken);
                JOptionPane.showMessageDialog(this, response, "Delete Doctor", JOptionPane.INFORMATION_MESSAGE);
                if (response.toLowerCase().contains("success")) {
                    loadDoctors();
                    clearFields();
                }
            }
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Invalid Doctor ID format.", "Input Error", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error deleting doctor: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchDoctors() {
        String searchTerm = JOptionPane.showInputDialog(this, "Enter search term (name or specialization):");
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            if (doctorService == null) {
                JOptionPane.showMessageDialog(this, "Doctor service not available.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                List<Doctor> doctors = doctorService.searchDoctors(searchTerm.trim(), sessionToken);
                tableModel.setRowCount(0);
                if (doctors != null && !doctors.isEmpty()) {
                    for (Doctor doctor : doctors) {
                        Vector<Object> row = new Vector<>();
                        row.add(doctor.getDoctorId());
                        row.add(doctor.getFirstName());
                        row.add(doctor.getLastName());
                        row.add(doctor.getSpecialization());
                        row.add(doctor.getContactNumber());
                        row.add(doctor.getEmail());
                        tableModel.addRow(row);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "No doctors found matching the search term.", "Search Result", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error searching doctors: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (searchTerm != null) {
            JOptionPane.showMessageDialog(this, "Search term cannot be empty.", "Search Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void clearFields() {
        doctorIdField.setText("");
        firstNameField.setText("");
        lastNameField.setText("");
        specializationField.setText("");
        contactNumberField.setText("");
        emailField.setText("");
        doctorTable.clearSelection();
    }
    
    private void exportDataToCsv() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Specify a file to save");
        fileChooser.setSelectedFile(new File("doctors_report.csv"));
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV Files", "csv"));

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getName().toLowerCase().endsWith(".csv")) {
                fileToSave = new File(fileToSave.getParentFile(), fileToSave.getName() + ".csv");
            }

            try (FileWriter fw = new FileWriter(fileToSave);
                 BufferedWriter bw = new BufferedWriter(fw)) {

                for (int i = 0; i < tableModel.getColumnCount(); i++) {
                    bw.write("\"" + tableModel.getColumnName(i).replace("\"", "\"\"") + "\"" + (i == tableModel.getColumnCount() - 1 ? "" : ","));
                }
                bw.newLine();

                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    for (int j = 0; j < tableModel.getColumnCount(); j++) {
                        Object value = tableModel.getValueAt(i, j);
                        String cellValue = (value == null) ? "" : value.toString();
                        cellValue = cellValue.replace("\"", "\"\"");
                        bw.write("\"" + cellValue + "\"" + (j == tableModel.getColumnCount() - 1 ? "" : ","));
                    }
                    bw.newLine();
                }
                JOptionPane.showMessageDialog(this, "Data exported successfully to " + fileToSave.getAbsolutePath(), "Export Successful", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error exporting data: " + ex.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new DoctorManagementForm("dummy-session-token-for-dmf-test").setVisible(true);
        });
    }
}
