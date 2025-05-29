package com.hospital.client.view;

import com.hospital.client.util.RmiClientUtil;
import com.hospital.common.model.Appointment;
import com.hospital.common.model.Patient;
import com.hospital.common.model.Doctor;
import com.hospital.common.service.IAppointmentService;
import com.hospital.common.service.IPatientService;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;

public class AppointmentManagementForm extends JFrame {

    private JTextField appointmentIdField;
    private JComboBox<PatientWrapper> patientComboBox;
    private JComboBox<DoctorWrapper> doctorComboBox;
    private JTextField appointmentDateField;
    private JComboBox<String> statusComboBox;
    private JTextArea reasonArea;

    private JButton scheduleAppointmentButton;
    private JButton updateAppointmentButton;
    private JButton cancelAppointmentButton;
    private JButton searchButton;
    private JButton clearFieldsButton;
    private JButton loadAllButton;
    private JButton exportCsvButton; // New button

    private JTable appointmentTable;
    private DefaultTableModel tableModel;

    private IAppointmentService appointmentService;
    private IPatientService patientService;
    private IDoctorService doctorService;
    private String sessionToken;
    
    private static final SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public AppointmentManagementForm(String sessionToken) {
        this.sessionToken = sessionToken;
        setTitle("Manage Appointments - Hospital Management System");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        try {
            appointmentService = RmiClientUtil.getAppointmentService();
            patientService = RmiClientUtil.getPatientService();
            doctorService = RmiClientUtil.getDoctorService();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error connecting to services: " + e.getMessage(), "Service Error", JOptionPane.ERROR_MESSAGE);
        }

        initComponents();
        layoutComponents();
        addEventListeners();

        loadPatientsForCombobox();
        loadDoctorsForCombobox();
        loadAppointments();
    }

    private void initComponents() {
        appointmentIdField = new JTextField(10);
        appointmentIdField.setEditable(false);

        patientComboBox = new JComboBox<>();
        doctorComboBox = new JComboBox<>();
        appointmentDateField = new JTextField(16); 
        
        String[] statuses = {"Scheduled", "Completed", "Cancelled", "Pending"};
        statusComboBox = new JComboBox<>(statuses);
        
        reasonArea = new JTextArea(3, 20);
        reasonArea.setLineWrap(true);
        reasonArea.setWrapStyleWord(true);

        scheduleAppointmentButton = new JButton("Schedule Appointment");
        updateAppointmentButton = new JButton("Update Appointment");
        cancelAppointmentButton = new JButton("Cancel Appointment");
        searchButton = new JButton("Search (by Patient ID)");
        clearFieldsButton = new JButton("Clear Fields");
        loadAllButton = new JButton("Load All");
        exportCsvButton = new JButton("Export to CSV");

        // Added Patient ID and Doctor ID as hidden columns for easier data retrieval
        String[] columnNames = {"Appt ID", "Patient Name", "Doctor Name", "Date", "Status", "Reason", "Patient ID", "Doctor ID"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        appointmentTable = new JTable(tableModel);
        appointmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Hide Patient ID and Doctor ID columns from view, but keep data in model
        appointmentTable.getColumnModel().getColumn(6).setMinWidth(0);
        appointmentTable.getColumnModel().getColumn(6).setMaxWidth(0);
        appointmentTable.getColumnModel().getColumn(6).setWidth(0);
        appointmentTable.getColumnModel().getColumn(7).setMinWidth(0);
        appointmentTable.getColumnModel().getColumn(7).setMaxWidth(0);
        appointmentTable.getColumnModel().getColumn(7).setWidth(0);
    }

    private void layoutComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("Appt ID:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; formPanel.add(appointmentIdField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Patient:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; formPanel.add(patientComboBox, gbc);

        gbc.gridx = 2; gbc.gridy = 1; formPanel.add(new JLabel("Doctor:"), gbc);
        gbc.gridx = 3; gbc.gridy = 1; formPanel.add(doctorComboBox, gbc);

        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(new JLabel("Date (YYYY-MM-DD HH:MM):"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; formPanel.add(appointmentDateField, gbc);

        gbc.gridx = 2; gbc.gridy = 2; formPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 3; gbc.gridy = 2; formPanel.add(statusComboBox, gbc);

        gbc.gridx = 0; gbc.gridy = 3; formPanel.add(new JLabel("Reason:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; gbc.gridwidth = 3; 
        formPanel.add(new JScrollPane(reasonArea), gbc);
        gbc.gridwidth = 1;

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(scheduleAppointmentButton);
        buttonPanel.add(updateAppointmentButton);
        buttonPanel.add(cancelAppointmentButton);
        buttonPanel.add(searchButton);
        buttonPanel.add(clearFieldsButton);
        buttonPanel.add(loadAllButton);
        buttonPanel.add(exportCsvButton);

        mainPanel.add(formPanel, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(appointmentTable), BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void addEventListeners() {
        scheduleAppointmentButton.addActionListener(e -> scheduleAppointment());
        updateAppointmentButton.addActionListener(e -> updateAppointment());
        cancelAppointmentButton.addActionListener(e -> cancelAppointment());
        searchButton.addActionListener(e -> searchAppointments());
        clearFieldsButton.addActionListener(e -> clearFields());
        loadAllButton.addActionListener(e -> loadAppointments());
        exportCsvButton.addActionListener(e -> exportDataToCsv());

        appointmentTable.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting() && appointmentTable.getSelectedRow() != -1) {
                populateFieldsFromSelectedRow();
            }
        });
    }
    
    private SimpleDateFormat getDateTimeFormatter() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        sdf.setLenient(false); // Strict parsing
        return sdf;
    }

    private boolean validateInputs(boolean isUpdate) {
        // 1. Non-Empty Selections for Patient and Doctor
        if (patientComboBox.getSelectedItem() == null || !(patientComboBox.getSelectedItem() instanceof PatientWrapper)) {
            JOptionPane.showMessageDialog(this, "Please select a patient.", "Input Error", JOptionPane.WARNING_MESSAGE);
            patientComboBox.requestFocus();
            return false;
        }
        if (doctorComboBox.getSelectedItem() == null || !(doctorComboBox.getSelectedItem() instanceof DoctorWrapper)) {
            JOptionPane.showMessageDialog(this, "Please select a doctor.", "Input Error", JOptionPane.WARNING_MESSAGE);
            doctorComboBox.requestFocus();
            return false;
        }

        // 2. Date Format/Validity (YYYY-MM-DD HH:MM, not in the past for new appointments)
        String dateStr = appointmentDateField.getText().trim();
        if (dateStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Appointment date is required.", "Input Error", JOptionPane.WARNING_MESSAGE);
            appointmentDateField.requestFocus();
            return false;
        }
        try {
            Date appointmentDate = getDateTimeFormatter().parse(dateStr);
            if (!isUpdate && appointmentDate.before(new Date())) { // For new appointments, date cannot be in the past
                JOptionPane.showMessageDialog(this, "Appointment date cannot be in the past.", "Input Error", JOptionPane.WARNING_MESSAGE);
                appointmentDateField.requestFocus();
                return false;
            }
        } catch (ParseException pe) {
            JOptionPane.showMessageDialog(this, "Invalid date format for Appointment Date. Please use YYYY-MM-DD HH:MM.", "Input Error", JOptionPane.WARNING_MESSAGE);
            appointmentDateField.requestFocus();
            return false;
        }

        // 3. Status Selection (usually a default is selected, but good to have)
        if (statusComboBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select a status for the appointment.", "Input Error", JOptionPane.WARNING_MESSAGE);
            statusComboBox.requestFocus();
            return false;
        }
        
        // 4. Numeric ID for update
        if (isUpdate) {
            if (appointmentIdField.getText().trim().isEmpty()) {
                 JOptionPane.showMessageDialog(this, "Appointment ID is required for update. Please select an appointment.", "Input Error", JOptionPane.WARNING_MESSAGE);
                 return false;
            }
            try {
                Long.parseLong(appointmentIdField.getText().trim());
            } catch (NumberFormatException e) {
                 JOptionPane.showMessageDialog(this, "Appointment ID must be a valid number for update.", "Input Error", JOptionPane.WARNING_MESSAGE);
                 return false;
            }
        }
        return true; // All validations passed
    }


    private void populateFieldsFromSelectedRow() {
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow >= 0) {
            appointmentIdField.setText(tableModel.getValueAt(selectedRow, 0) != null ? tableModel.getValueAt(selectedRow, 0).toString() : "");
            
            Long patientId = (Long) tableModel.getValueAt(selectedRow, 6); 
            for (int i = 0; i < patientComboBox.getItemCount(); i++) {
                if (patientComboBox.getItemAt(i) != null && patientComboBox.getItemAt(i).getId().equals(patientId)) {
                    patientComboBox.setSelectedIndex(i);
                    break;
                }
            }

            Long doctorId = (Long) tableModel.getValueAt(selectedRow, 7); 
            for (int i = 0; i < doctorComboBox.getItemCount(); i++) {
                if (doctorComboBox.getItemAt(i) != null && doctorComboBox.getItemAt(i).getId().equals(doctorId)) {
                    doctorComboBox.setSelectedIndex(i);
                    break;
                }
            }
            
            appointmentDateField.setText(tableModel.getValueAt(selectedRow, 3) != null ? tableModel.getValueAt(selectedRow, 3).toString() : "");
            statusComboBox.setSelectedItem(tableModel.getValueAt(selectedRow, 4) != null ? tableModel.getValueAt(selectedRow, 4).toString() : "Scheduled");
            reasonArea.setText(tableModel.getValueAt(selectedRow, 5) != null ? tableModel.getValueAt(selectedRow, 5).toString() : "");
        }
    }

    private void loadPatientsForCombobox() {
        if (patientService == null) return;
        try {
            List<Patient> patients = patientService.getAllPatients(sessionToken);
            patientComboBox.removeAllItems();
            if (patients != null) {
                for (Patient patient : patients) {
                    patientComboBox.addItem(new PatientWrapper(patient));
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading patients for dropdown: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadDoctorsForCombobox() {
        if (doctorService == null) return;
        try {
            List<Doctor> doctors = doctorService.getAllDoctors(sessionToken);
            doctorComboBox.removeAllItems();
            if (doctors != null) {
                for (Doctor doctor : doctors) {
                    doctorComboBox.addItem(new DoctorWrapper(doctor));
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading doctors for dropdown: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadAppointments() {
        if (appointmentService == null) {
            JOptionPane.showMessageDialog(this, "Appointment service not available.", "Error", JOptionPane.ERROR_MESSAGE);
            addDummyDataToTable();
            return;
        }
        try {
            List<Appointment> appointments = appointmentService.getAllAppointments(sessionToken);
            tableModel.setRowCount(0);
            if (appointments != null) {
                for (Appointment appt : appointments) {
                    Vector<Object> row = new Vector<>();
                    row.add(appt.getAppointmentId());
                    row.add(appt.getPatient() != null ? appt.getPatient().getFirstName() + " " + appt.getPatient().getLastName() : "N/A");
                    row.add(appt.getDoctor() != null ? appt.getDoctor().getFirstName() + " " + appt.getDoctor().getLastName() : "N/A");
                    row.add(appt.getAppointmentDate() != null ? getDateTimeFormatter().format(appt.getAppointmentDate()) : "");
                    row.add(appt.getStatus());
                    row.add(appt.getReason());
                    row.add(appt.getPatient() != null ? appt.getPatient().getPatientId() : null); 
                    row.add(appt.getDoctor() != null ? appt.getDoctor().getDoctorId() : null);   
                    tableModel.addRow(row);
                }
            } else {
                 JOptionPane.showMessageDialog(this, "No appointments found or error loading appointments.", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading appointments: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            addDummyDataToTable();
        }
    }
    
    private void addDummyDataToTable() {
        tableModel.setRowCount(0);
        tableModel.addRow(new Object[]{1L, "John D (ID:1)", "Dr. Alice W (ID:1)", "2024-01-15 10:00", "Completed", "Regular Checkup", 1L, 1L});
        tableModel.addRow(new Object[]{2L, "Jane S (ID:2)", "Dr. Bob B (ID:2)", "2024-01-16 14:30", "Scheduled", "Flu Symptoms", 2L, 2L});
    }


    private void scheduleAppointment() {
        if (!validateInputs(false)) { // Pass false for 'isUpdate'
            return;
        }
        try {
            if (appointmentService == null) {
                JOptionPane.showMessageDialog(this, "Appointment service not available.", "Service Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            PatientWrapper selectedPatientWrapper = (PatientWrapper) patientComboBox.getSelectedItem();
            DoctorWrapper selectedDoctorWrapper = (DoctorWrapper) doctorComboBox.getSelectedItem();

            Appointment appointment = new Appointment();
            Patient patient = new Patient(); 
            patient.setPatientId(selectedPatientWrapper.getId());
            appointment.setPatient(patient);

            Doctor doctor = new Doctor();
            doctor.setDoctorId(selectedDoctorWrapper.getId());
            appointment.setDoctor(doctor);
            
            appointment.setAppointmentDate(getDateTimeFormatter().parse(appointmentDateField.getText().trim())); // Validated
            appointment.setStatus((String) statusComboBox.getSelectedItem());
            appointment.setReason(reasonArea.getText().trim());

            String response = appointmentService.scheduleAppointment(appointment, sessionToken);
            JOptionPane.showMessageDialog(this, response, "Schedule Appointment", JOptionPane.INFORMATION_MESSAGE);
            if (response.toLowerCase().contains("success")) {
                loadAppointments();
                clearFields();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error scheduling appointment: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateAppointment() {
        if (!validateInputs(true)) { // Pass true for 'isUpdate'
            return;
        }
        try {
            if (appointmentService == null) {
                JOptionPane.showMessageDialog(this, "Appointment service not available.", "Service Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            PatientWrapper selectedPatientWrapper = (PatientWrapper) patientComboBox.getSelectedItem();
            DoctorWrapper selectedDoctorWrapper = (DoctorWrapper) doctorComboBox.getSelectedItem();

            Appointment appointment = new Appointment();
            appointment.setAppointmentId(Long.parseLong(appointmentIdField.getText().trim())); // Validated
            
            Patient patient = new Patient(); 
            patient.setPatientId(selectedPatientWrapper.getId());
            appointment.setPatient(patient);

            Doctor doctor = new Doctor();
            doctor.setDoctorId(selectedDoctorWrapper.getId());
            appointment.setDoctor(doctor);
            
            appointment.setAppointmentDate(getDateTimeFormatter().parse(appointmentDateField.getText().trim())); // Validated
            appointment.setStatus((String) statusComboBox.getSelectedItem());
            appointment.setReason(reasonArea.getText().trim());

            String response = appointmentService.updateAppointment(appointment, sessionToken);
            JOptionPane.showMessageDialog(this, response, "Update Appointment", JOptionPane.INFORMATION_MESSAGE);
            if (response.toLowerCase().contains("success")) {
                loadAppointments();
                clearFields();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error updating appointment: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelAppointment() {
        try {
            if (appointmentService == null) {
                JOptionPane.showMessageDialog(this, "Appointment service not available.", "Service Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (appointmentIdField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select an appointment to cancel.", "Cancellation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Long appointmentId = Long.parseLong(appointmentIdField.getText().trim());
            int confirmation = JOptionPane.showConfirmDialog(this, "Are you sure you want to cancel appointment ID: " + appointmentId + "?", "Confirm Cancellation", JOptionPane.YES_NO_OPTION);
            if (confirmation == JOptionPane.YES_OPTION) {
                String response = appointmentService.cancelAppointment(appointmentId, sessionToken);
                JOptionPane.showMessageDialog(this, response, "Cancel Appointment", JOptionPane.INFORMATION_MESSAGE);
                if (response.toLowerCase().contains("success")) {
                    loadAppointments();
                    clearFields();
                }
            }
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Invalid Appointment ID format.", "Input Error", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error cancelling appointment: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchAppointments() {
        String patientIdStr = JOptionPane.showInputDialog(this, "Enter Patient ID to search appointments:");
        if (patientIdStr != null && !patientIdStr.trim().isEmpty()) {
            if (appointmentService == null) {
                JOptionPane.showMessageDialog(this, "Appointment service not available.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                Long patientId = Long.parseLong(patientIdStr.trim());
                List<Appointment> appointments = appointmentService.getAppointmentsForPatient(patientId, sessionToken);
                tableModel.setRowCount(0);
                if (appointments != null && !appointments.isEmpty()) {
                    for (Appointment appt : appointments) {
                        Vector<Object> row = new Vector<>();
                        row.add(appt.getAppointmentId());
                        row.add(appt.getPatient() != null ? appt.getPatient().getFirstName() + " " + appt.getPatient().getLastName() : "N/A");
                        row.add(appt.getDoctor() != null ? appt.getDoctor().getFirstName() + " " + appt.getDoctor().getLastName() : "N/A");
                        row.add(appt.getAppointmentDate() != null ? getDateTimeFormatter().format(appt.getAppointmentDate()) : "");
                        row.add(appt.getStatus());
                        row.add(appt.getReason());
                        row.add(appt.getPatient() != null ? appt.getPatient().getPatientId() : null);
                        row.add(appt.getDoctor() != null ? appt.getDoctor().getDoctorId() : null);
                        tableModel.addRow(row);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "No appointments found for Patient ID: " + patientId, "Search Result", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "Invalid Patient ID format.", "Search Error", JOptionPane.WARNING_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error searching appointments: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (patientIdStr != null) {
             JOptionPane.showMessageDialog(this, "Patient ID cannot be empty for search.", "Search Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void clearFields() {
        appointmentIdField.setText("");
        patientComboBox.setSelectedIndex(-1);
        doctorComboBox.setSelectedIndex(-1);
        appointmentDateField.setText("");
        statusComboBox.setSelectedIndex(0);
        reasonArea.setText("");
        appointmentTable.clearSelection();
    }
    
    private void exportDataToCsv() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Specify a file to save");
        fileChooser.setSelectedFile(new File("appointments_report.csv"));
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV Files", "csv"));

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getName().toLowerCase().endsWith(".csv")) {
                fileToSave = new File(fileToSave.getParentFile(), fileToSave.getName() + ".csv");
            }

            try (FileWriter fw = new FileWriter(fileToSave);
                 BufferedWriter bw = new BufferedWriter(fw)) {

                // Write headers (only visible columns)
                for (int i = 0; i < tableModel.getColumnCount() - 2; i++) { // Exclude last 2 hidden ID columns
                    bw.write("\"" + tableModel.getColumnName(i).replace("\"", "\"\"") + "\"" + (i == tableModel.getColumnCount() - 3 ? "" : ","));
                }
                bw.newLine();

                // Write data (only visible columns)
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    for (int j = 0; j < tableModel.getColumnCount() - 2; j++) { // Exclude last 2 hidden ID columns
                        Object value = tableModel.getValueAt(i, j);
                        String cellValue = (value == null) ? "" : value.toString();
                        cellValue = cellValue.replace("\"", "\"\"");
                        bw.write("\"" + cellValue + "\"" + (j == tableModel.getColumnCount() - 3 ? "" : ","));
                    }
                    bw.newLine();
                }
                JOptionPane.showMessageDialog(this, "Data exported successfully to " + fileToSave.getAbsolutePath(), "Export Successful", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error exporting data: " + ex.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static class PatientWrapper {
        private Patient patient;
        public PatientWrapper(Patient patient) { this.patient = patient; }
        public Long getId() { return patient.getPatientId(); }
        @Override public String toString() { return patient.getFirstName() + " " + patient.getLastName() + " (ID:" + patient.getPatientId() + ")"; }
    }

    private static class DoctorWrapper {
        private Doctor doctor;
        public DoctorWrapper(Doctor doctor) { this.doctor = doctor; }
        public Long getId() { return doctor.getDoctorId(); }
        @Override public String toString() { return doctor.getFirstName() + " " + doctor.getLastName() + " (ID:" + doctor.getDoctorId() + ")"; }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new AppointmentManagementForm("dummy-session-token-for-amf-test").setVisible(true);
        });
    }
}
