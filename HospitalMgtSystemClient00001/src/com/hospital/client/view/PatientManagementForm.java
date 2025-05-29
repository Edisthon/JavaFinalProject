package com.hospital.client.view;

import com.hospital.client.util.RmiClientUtil;
import com.hospital.common.model.Patient;
import com.hospital.common.service.IPatientService;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.regex.Pattern;

public class PatientManagementForm extends JFrame {

    private JTextField patientIdField;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField dobField;
    private JTextField genderField;
    private JTextField contactNumberField;
    private JTextArea addressArea;
    private JTextField emailField;

    private JButton addPatientButton;
    private JButton updatePatientButton;
    private JButton deletePatientButton;
    private JButton searchButton;
    private JButton clearFieldsButton;
    private JButton loadAllButton;
    private JButton exportCsvButton; // New button

    private JTable patientTable;
    private DefaultTableModel tableModel;

    private IPatientService patientService;
    private String sessionToken;

    // Regex for basic email validation
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");
    // Regex for contact number: allows digits, +, (), -, and spaces, length 7-15
    private static final Pattern CONTACT_PATTERN = Pattern.compile("^[\\d\\s+()-]{7,15}$");


    public PatientManagementForm(String sessionToken) {
        this.sessionToken = sessionToken;
        setTitle("Manage Patients - Hospital Management System");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        try {
            patientService = RmiClientUtil.getPatientService();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error connecting to patient service: " + e.getMessage(), "Service Error", JOptionPane.ERROR_MESSAGE);
        }

        initComponents();
        layoutComponents();
        addEventListeners();

        loadPatients();
    }

    private void initComponents() {
        patientIdField = new JTextField(10);
        patientIdField.setEditable(false);
        firstNameField = new JTextField(15);
        lastNameField = new JTextField(15);
        dobField = new JTextField(10);
        genderField = new JTextField(10);
        contactNumberField = new JTextField(15);
        addressArea = new JTextArea(3, 20);
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);
        emailField = new JTextField(20);

        addPatientButton = new JButton("Add Patient");
        updatePatientButton = new JButton("Update Patient");
        deletePatientButton = new JButton("Delete Patient");
        searchButton = new JButton("Search");
        clearFieldsButton = new JButton("Clear Fields");
        loadAllButton = new JButton("Load All");
        exportCsvButton = new JButton("Export to CSV"); // Initialize new button


        String[] columnNames = {"ID", "First Name", "Last Name", "DOB", "Gender", "Contact", "Email", "Address"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        patientTable = new JTable(tableModel);
        patientTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void layoutComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("Patient ID:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; formPanel.add(patientIdField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("First Name:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; formPanel.add(firstNameField, gbc);

        gbc.gridx = 2; gbc.gridy = 1; formPanel.add(new JLabel("Last Name:"), gbc);
        gbc.gridx = 3; gbc.gridy = 1; formPanel.add(lastNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(new JLabel("Date of Birth (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; formPanel.add(dobField, gbc);

        gbc.gridx = 2; gbc.gridy = 2; formPanel.add(new JLabel("Gender:"), gbc);
        gbc.gridx = 3; gbc.gridy = 2; formPanel.add(genderField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; formPanel.add(new JLabel("Contact Number:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; formPanel.add(contactNumberField, gbc);

        gbc.gridx = 2; gbc.gridy = 3; formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 3; gbc.gridy = 3; formPanel.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = 4; formPanel.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1; gbc.gridy = 4; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(new JScrollPane(addressArea), gbc);
        gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(addPatientButton);
        buttonPanel.add(updatePatientButton);
        buttonPanel.add(deletePatientButton);
        buttonPanel.add(searchButton);
        buttonPanel.add(clearFieldsButton);
        buttonPanel.add(loadAllButton);
        buttonPanel.add(exportCsvButton); // Add new button to panel

        mainPanel.add(formPanel, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(patientTable), BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void addEventListeners() {
        addPatientButton.addActionListener(e -> addPatient());
        updatePatientButton.addActionListener(e -> updatePatient());
        deletePatientButton.addActionListener(e -> deletePatient());
        searchButton.addActionListener(e -> searchPatients());
        clearFieldsButton.addActionListener(e -> clearFields());
        loadAllButton.addActionListener(e -> loadPatients());
        exportCsvButton.addActionListener(e -> exportDataToCsv()); // Add listener

        patientTable.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting() && patientTable.getSelectedRow() != -1) {
                populateFieldsFromSelectedRow();
            }
        });
    }

    private void populateFieldsFromSelectedRow() {
        int selectedRow = patientTable.getSelectedRow();
        if (selectedRow >= 0) {
            patientIdField.setText(tableModel.getValueAt(selectedRow, 0) != null ? tableModel.getValueAt(selectedRow, 0).toString() : "");
            firstNameField.setText(tableModel.getValueAt(selectedRow, 1) != null ? tableModel.getValueAt(selectedRow, 1).toString() : "");
            lastNameField.setText(tableModel.getValueAt(selectedRow, 2) != null ? tableModel.getValueAt(selectedRow, 2).toString() : "");
            dobField.setText(tableModel.getValueAt(selectedRow, 3) != null ? tableModel.getValueAt(selectedRow, 3).toString() : "");
            genderField.setText(tableModel.getValueAt(selectedRow, 4) != null ? tableModel.getValueAt(selectedRow, 4).toString() : "");
            contactNumberField.setText(tableModel.getValueAt(selectedRow, 5) != null ? tableModel.getValueAt(selectedRow, 5).toString() : "");
            emailField.setText(tableModel.getValueAt(selectedRow, 6) != null ? tableModel.getValueAt(selectedRow, 6).toString() : "");
            addressArea.setText(tableModel.getValueAt(selectedRow, 7) != null ? tableModel.getValueAt(selectedRow, 7).toString() : "");
        }
    }
    
    private SimpleDateFormat getDateFormat() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false); // Make date parsing strict
        return sdf;
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
        if (dobField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Date of Birth is required.", "Input Error", JOptionPane.WARNING_MESSAGE);
            dobField.requestFocus();
            return false;
        }
         // Gender is optional in schema, but if you want to make it required:
        // if (genderField.getText().trim().isEmpty()) {
        //     JOptionPane.showMessageDialog(this, "Gender is required.", "Input Error", JOptionPane.WARNING_MESSAGE);
        //     genderField.requestFocus();
        //     return false;
        // }


        // 2. Valid Email Format (if email is provided)
        String email = emailField.getText().trim();
        if (!email.isEmpty() && !EMAIL_PATTERN.matcher(email).matches()) {
            JOptionPane.showMessageDialog(this, "Invalid email format.", "Input Error", JOptionPane.WARNING_MESSAGE);
            emailField.requestFocus();
            return false;
        }

        // 3. Date Format/Validity (YYYY-MM-DD and not in the future)
        try {
            Date dob = getDateFormat().parse(dobField.getText().trim());
            if (dob.after(new Date())) { // Check if DOB is in the future
                JOptionPane.showMessageDialog(this, "Date of Birth cannot be in the future.", "Input Error", JOptionPane.WARNING_MESSAGE);
                dobField.requestFocus();
                return false;
            }
        } catch (ParseException pe) {
            JOptionPane.showMessageDialog(this, "Invalid date format for Date of Birth. Please use YYYY-MM-DD.", "Input Error", JOptionPane.WARNING_MESSAGE);
            dobField.requestFocus();
            return false;
        }

        // 4. Contact Number Format (if provided)
        String contact = contactNumberField.getText().trim();
        if (!contact.isEmpty() && !CONTACT_PATTERN.matcher(contact).matches()) {
            JOptionPane.showMessageDialog(this, "Invalid contact number format. Use 7-15 digits, optionally with +,-,() and spaces.", "Input Error", JOptionPane.WARNING_MESSAGE);
            contactNumberField.requestFocus();
            return false;
        }
        
        // 5. Numeric ID for update
        if (isUpdate) {
            if (patientIdField.getText().trim().isEmpty()) {
                 JOptionPane.showMessageDialog(this, "Patient ID is required for update. Please select a patient.", "Input Error", JOptionPane.WARNING_MESSAGE);
                 return false;
            }
            try {
                Long.parseLong(patientIdField.getText().trim());
            } catch (NumberFormatException e) {
                 JOptionPane.showMessageDialog(this, "Patient ID must be a valid number for update.", "Input Error", JOptionPane.WARNING_MESSAGE);
                 return false;
            }
        }


        return true; // All validations passed
    }


    private void loadPatients() {
        if (patientService == null) {
            JOptionPane.showMessageDialog(this, "Patient service not available.", "Error", JOptionPane.ERROR_MESSAGE);
            addDummyDataToTable();
            return;
        }
        try {
            List<Patient> patients = patientService.getAllPatients(sessionToken);
            tableModel.setRowCount(0);
            if (patients != null) {
                for (Patient patient : patients) {
                    Vector<Object> row = new Vector<>();
                    row.add(patient.getPatientId());
                    row.add(patient.getFirstName());
                    row.add(patient.getLastName());
                    row.add(patient.getDateOfBirth() != null ? getDateFormat().format(patient.getDateOfBirth()) : "");
                    row.add(patient.getGender());
                    row.add(patient.getContactNumber());
                    row.add(patient.getEmail());
                    row.add(patient.getAddress());
                    tableModel.addRow(row);
                }
            } else {
                 JOptionPane.showMessageDialog(this, "No patients found or error loading patients.", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading patients: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            addDummyDataToTable();
        }
    }
    
    private void addDummyDataToTable() {
        tableModel.setRowCount(0);
        tableModel.addRow(new Object[]{1L, "John", "Doe", "1990-01-01", "Male", "1234567890", "john.doe@example.com", "123 Main St"});
        tableModel.addRow(new Object[]{2L, "Jane", "Smith", "1985-05-15", "Female", "0987654321", "jane.smith@example.com", "456 Oak Ave"});
    }


    private void addPatient() {
        if (!validateInputs(false)) { // Pass false for 'isUpdate'
            return;
        }
        try {
            if (patientService == null) {
                JOptionPane.showMessageDialog(this, "Patient service not available.", "Service Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Patient patient = new Patient();
            patient.setFirstName(firstNameField.getText().trim());
            patient.setLastName(lastNameField.getText().trim());
            patient.setDateOfBirth(getDateFormat().parse(dobField.getText().trim())); // Already validated
            patient.setGender(genderField.getText().trim());
            patient.setContactNumber(contactNumberField.getText().trim());
            patient.setEmail(emailField.getText().trim());
            patient.setAddress(addressArea.getText().trim());

            String response = patientService.registerPatient(patient, sessionToken);
            JOptionPane.showMessageDialog(this, response, "Add Patient", JOptionPane.INFORMATION_MESSAGE);
            if (response.toLowerCase().contains("success")) {
                loadPatients();
                clearFields();
            }
        } catch (Exception e) { // Catch ParseException from date again, though validateInputs should catch it.
            JOptionPane.showMessageDialog(this, "Error adding patient: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updatePatient() {
        if (!validateInputs(true)) { // Pass true for 'isUpdate'
            return;
        }
         try {
            if (patientService == null) {
                JOptionPane.showMessageDialog(this, "Patient service not available.", "Service Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Patient patient = new Patient();
            patient.setPatientId(Long.parseLong(patientIdField.getText().trim())); // Validated
            patient.setFirstName(firstNameField.getText().trim());
            patient.setLastName(lastNameField.getText().trim());
            patient.setDateOfBirth(getDateFormat().parse(dobField.getText().trim())); // Validated
            patient.setGender(genderField.getText().trim());
            patient.setContactNumber(contactNumberField.getText().trim());
            patient.setEmail(emailField.getText().trim());
            patient.setAddress(addressArea.getText().trim());

            String response = patientService.updatePatient(patient, sessionToken);
            JOptionPane.showMessageDialog(this, response, "Update Patient", JOptionPane.INFORMATION_MESSAGE);
             if (response.toLowerCase().contains("success")) {
                loadPatients();
                clearFields();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error updating patient: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deletePatient() {
        try {
            if (patientService == null) {
                JOptionPane.showMessageDialog(this, "Patient service not available.", "Service Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (patientIdField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select a patient to delete or enter Patient ID.", "Delete Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Long patientId = Long.parseLong(patientIdField.getText().trim());
            int confirmation = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete patient ID: " + patientId + "?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
            if (confirmation == JOptionPane.YES_OPTION) {
                String response = patientService.deletePatient(patientId, sessionToken);
                JOptionPane.showMessageDialog(this, response, "Delete Patient", JOptionPane.INFORMATION_MESSAGE);
                if (response.toLowerCase().contains("success")) {
                    loadPatients();
                    clearFields();
                }
            }
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Invalid Patient ID format.", "Input Error", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error deleting patient: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchPatients() {
        String searchTerm = JOptionPane.showInputDialog(this, "Enter search term (name):");
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            if (patientService == null) {
                JOptionPane.showMessageDialog(this, "Patient service not available.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                List<Patient> patients = patientService.searchPatients(searchTerm.trim(), sessionToken);
                tableModel.setRowCount(0);
                if (patients != null && !patients.isEmpty()) {
                    for (Patient patient : patients) {
                        Vector<Object> row = new Vector<>();
                        row.add(patient.getPatientId());
                        row.add(patient.getFirstName());
                        row.add(patient.getLastName());
                        row.add(patient.getDateOfBirth() != null ? getDateFormat().format(patient.getDateOfBirth()) : "");
                        row.add(patient.getGender());
                        row.add(patient.getContactNumber());
                        row.add(patient.getEmail());
                        row.add(patient.getAddress());
                        tableModel.addRow(row);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "No patients found matching the search term.", "Search Result", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error searching patients: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (searchTerm != null) {
             JOptionPane.showMessageDialog(this, "Search term cannot be empty.", "Search Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void clearFields() {
        patientIdField.setText("");
        firstNameField.setText("");
        lastNameField.setText("");
        dobField.setText("");
        genderField.setText("");
        contactNumberField.setText("");
        addressArea.setText("");
        emailField.setText("");
        patientTable.clearSelection();
    }
    
    private void exportDataToCsv() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Specify a file to save");
        fileChooser.setSelectedFile(new File("patients_report.csv"));
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV Files", "csv"));

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            // Ensure the file has a .csv extension
            if (!fileToSave.getName().toLowerCase().endsWith(".csv")) {
                fileToSave = new File(fileToSave.getParentFile(), fileToSave.getName() + ".csv");
            }

            try (FileWriter fw = new FileWriter(fileToSave);
                 BufferedWriter bw = new BufferedWriter(fw)) {

                // Write headers
                for (int i = 0; i < tableModel.getColumnCount(); i++) {
                    bw.write("\"" + tableModel.getColumnName(i).replace("\"", "\"\"") + "\"" + (i == tableModel.getColumnCount() - 1 ? "" : ","));
                }
                bw.newLine();

                // Write data
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    for (int j = 0; j < tableModel.getColumnCount(); j++) {
                        Object value = tableModel.getValueAt(i, j);
                        String cellValue = (value == null) ? "" : value.toString();
                        // Basic CSV escaping for quotes
                        cellValue = cellValue.replace("\"", "\"\"");
                        bw.write("\"" + cellValue + "\"" + (j == tableModel.getColumnCount() - 1 ? "" : ","));
                    }
                    bw.newLine();
                }
                JOptionPane.showMessageDialog(this, "Data exported successfully to " + fileToSave.getAbsolutePath(), "Export Successful", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error exporting data: " + ex.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
                // ex.printStackTrace();
            }
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new PatientManagementForm("dummy-session-token-for-pmf-test").setVisible(true);
        });
    }
}
