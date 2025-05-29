package com.hospital.client.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainDashboardForm extends JFrame {

    private JMenuBar menuBar;
    private JToolBar toolBar;
    private JPanel mainPanel; // Central panel for content
    private JLabel statusLabel;
    private String sessionToken; // Store session token

    public MainDashboardForm(String sessionToken) {
        this.sessionToken = sessionToken; // Store the session token

        setTitle("Hospital Management System - Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null); // Center the form

        initComponents();
        layoutComponents();
        addEventListeners();

        // For testing, show the session token in status bar
        statusLabel.setText("Logged in. Session: " + this.sessionToken);
    }

    private void initComponents() {
        // Menu Bar
        menuBar = new JMenuBar();

        // File Menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem logoutMenuItem = new JMenuItem("Logout");
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        fileMenu.add(logoutMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(exitMenuItem);
        menuBar.add(fileMenu);

        // Manage Menu
        JMenu manageMenu = new JMenu("Manage");
        JMenuItem managePatientsMenuItem = new JMenuItem("Patients");
        JMenuItem manageDoctorsMenuItem = new JMenuItem("Doctors");
        JMenuItem manageAppointmentsMenuItem = new JMenuItem("Appointments");
        // TODO: Add more items as needed (e.g., Medical Records)
        manageMenu.add(managePatientsMenuItem);
        manageMenu.add(manageDoctorsMenuItem);
        manageMenu.add(manageAppointmentsMenuItem);
        menuBar.add(manageMenu);

        // Help Menu (Example)
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutMenuItem = new JMenuItem("About");
        helpMenu.add(aboutMenuItem);
        menuBar.add(helpMenu);
        
        setJMenuBar(menuBar);

        // ToolBar (Optional)
        toolBar = new JToolBar("Quick Actions");
        JButton patientButton = new JButton("Patients");
        JButton doctorButton = new JButton("Doctors");
        JButton appointmentButton = new JButton("Appointments");
        toolBar.add(patientButton);
        toolBar.add(doctorButton);
        toolBar.add(appointmentButton);
        toolBar.setFloatable(false); // Optional: Prevent toolbar from being dragged

        // Main Panel
        mainPanel = new JPanel(new BorderLayout()); // Using BorderLayout for now
        mainPanel.add(new JLabel("Welcome to the Hospital Management System!", SwingConstants.CENTER), BorderLayout.CENTER);

        // Status Label
        statusLabel = new JLabel("Status: Ready");
        statusLabel.setBorder(BorderFactory.createEtchedBorder());
    }

    private void layoutComponents() {
        setLayout(new BorderLayout());

        // Add toolbar to the top (North)
        add(toolBar, BorderLayout.NORTH);

        // Add main panel to the center
        add(mainPanel, BorderLayout.CENTER);

        // Add status label to the bottom (South)
        add(statusLabel, BorderLayout.SOUTH);
    }

    private void addEventListeners() {
        // File -> Logout
        ((JMenuItem)menuBar.getMenu(0).getItem(0)).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Perform logout actions: Invalidate session, show login form
                // For now, just a message and open login form
                JOptionPane.showMessageDialog(MainDashboardForm.this, "Logged out successfully.", "Logout", JOptionPane.INFORMATION_MESSAGE);
                MainDashboardForm.this.dispose(); // Close dashboard
                // TODO: Call userService.logout(sessionToken)
                SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
            }
        });

        // File -> Exit
        ((JMenuItem)menuBar.getMenu(0).getItem(2)).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int confirmed = JOptionPane.showConfirmDialog(MainDashboardForm.this,
                        "Are you sure you want to exit?", "Exit Confirmation",
                        JOptionPane.YES_NO_OPTION);
                if (confirmed == JOptionPane.YES_OPTION) {
                    // Optional: Call logout before exiting if session is active
                    System.exit(0);
                }
            }
        });

        // Manage -> Patients
        ((JMenuItem)menuBar.getMenu(1).getItem(0)).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PatientManagementForm patientForm = new PatientManagementForm(MainDashboardForm.this.sessionToken);
                patientForm.setVisible(true);
            }
        });
        // Manage -> Doctors
        ((JMenuItem)menuBar.getMenu(1).getItem(1)).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DoctorManagementForm doctorForm = new DoctorManagementForm(MainDashboardForm.this.sessionToken);
                doctorForm.setVisible(true);
            }
        });
        // Manage -> Appointments
        ((JMenuItem)menuBar.getMenu(1).getItem(2)).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AppointmentManagementForm appointmentForm = new AppointmentManagementForm(MainDashboardForm.this.sessionToken);
                appointmentForm.setVisible(true);
            }
        });

        // Help -> About
        ((JMenuItem)menuBar.getMenu(2).getItem(0)).addActionListener(e -> 
            JOptionPane.showMessageDialog(MainDashboardForm.this, 
                "Hospital Management System\nVersion 0.1.0\nDeveloped by AI", 
                "About", 
                JOptionPane.INFORMATION_MESSAGE)
        );

        // Toolbar button placeholders
        ((JButton)toolBar.getComponent(0)).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PatientManagementForm patientForm = new PatientManagementForm(MainDashboardForm.this.sessionToken);
                patientForm.setVisible(true);
            }
        });
        // Toolbar -> Doctors
        ((JButton)toolBar.getComponent(1)).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DoctorManagementForm doctorForm = new DoctorManagementForm(MainDashboardForm.this.sessionToken);
                doctorForm.setVisible(true);
            }
        });
        // Toolbar -> Appointments
        ((JButton)toolBar.getComponent(2)).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AppointmentManagementForm appointmentForm = new AppointmentManagementForm(MainDashboardForm.this.sessionToken);
                appointmentForm.setVisible(true);
            }
        });

    }

    private void openManagementModule(String moduleName) {
        // This method can be kept for modules not yet having a dedicated form
        // or for a different display style (e.g., internal frames).
        mainPanel.removeAll(); 
        mainPanel.add(new JLabel(moduleName + " management module placeholder.", SwingConstants.CENTER), BorderLayout.CENTER);
        statusLabel.setText(moduleName + " module selected.");
        mainPanel.revalidate();
        mainPanel.repaint();
        JOptionPane.showMessageDialog(this, moduleName + " module is under construction or uses a different view.", "Module Info", JOptionPane.INFORMATION_MESSAGE);
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // For direct testing of dashboard (e.g., during development)
            // In a real scenario, this is launched after login.
            new MainDashboardForm("test-session-token-for-dev").setVisible(true);
        });
    }
}
