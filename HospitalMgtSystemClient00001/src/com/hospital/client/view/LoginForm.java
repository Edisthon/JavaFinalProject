package com.hospital.client.view;

import com.hospital.client.util.RmiClientUtil;
import com.hospital.common.service.IUserService;
// import com.hospital.common.model.User; // Not directly used for now

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginForm extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField otpField;
    private JButton loginButton;
    private JButton requestOtpButton;
    private JLabel otpLabel;

    private IUserService userService;
    private static String sessionToken; // Static field to store session token

    public LoginForm() {
        setTitle("Login - Hospital Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 280); // Slightly increased height for better spacing
        setLocationRelativeTo(null); // Center the form

        // Initialize RMI User Service
        try {
            userService = RmiClientUtil.getUserService();
        } catch (Exception e) {
            // e.printStackTrace(); // Keep for debugging, but user gets a JOptionPane
            JOptionPane.showMessageDialog(this,
                    "Server communication error: " + e.getMessage(),
                    "Connection Error", JOptionPane.ERROR_MESSAGE);
            // Consider disabling form fields or exiting if service is crucial at startup
        }

        initComponents();
        layoutComponents();
        addEventListeners();
    }

    // Public static getter for sessionToken
    public static String getSessionToken() {
        return sessionToken;
    }

    private void initComponents() {
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        otpField = new JTextField(10);
        otpLabel = new JLabel("OTP:");
        loginButton = new JButton("Login");
        requestOtpButton = new JButton("Request OTP");

        // Initially OTP field is not visible/editable
        otpLabel.setVisible(false);
        otpField.setVisible(false);
        otpField.setEnabled(false); // Also set enabled to false
    }

    private void layoutComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Padding
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        add(usernameField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        add(passwordField, gbc);

        // OTP Label and Field (initially hidden)
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(otpLabel, gbc);
        gbc.gridx = 1;
        add(otpField, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(requestOtpButton);
        buttonPanel.add(loginButton);

        gbc.gridx = 0;
        gbc.gridy = 3; // Adjusted gridy for the button panel
        gbc.gridwidth = 2; // Span two columns
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        add(buttonPanel, gbc);
    }

    private void addEventListeners() {
        requestOtpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText().trim();
                if (username.isEmpty()) {
                    JOptionPane.showMessageDialog(LoginForm.this,
                            "Please enter your username to request OTP.",
                            "Username Required", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                try {
                    if (userService == null) {
                         JOptionPane.showMessageDialog(LoginForm.this,
                                "User service is not available. Cannot request OTP.",
                                "Service Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    String response = userService.requestOtp(username);
                    JOptionPane.showMessageDialog(LoginForm.this, response,
                            "OTP Request", JOptionPane.INFORMATION_MESSAGE);
                    
                    // Assuming server response "OTP generated..." indicates success
                    if (response != null && response.toLowerCase().startsWith("otp generated")) {
                        otpLabel.setVisible(true);
                        otpField.setVisible(true);
                        otpField.setEnabled(true); // Enable OTP field
                        // passwordField.setEnabled(false); // Optionally disable password
                        // requestOtpButton.setEnabled(false); // Optionally disable after one request
                        loginButton.setText("Login with OTP"); // Update login button text
                    }
                } catch (Exception ex) {
                    // ex.printStackTrace(); // Keep for debugging
                    JOptionPane.showMessageDialog(LoginForm.this,
                            "Server communication error during OTP request: " + ex.getMessage(),
                            "OTP Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword());
                String otp = otpField.getText().trim();

                try {
                    if (userService == null) {
                         JOptionPane.showMessageDialog(LoginForm.this,
                                "User service is not available. Cannot login.",
                                "Service Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    String newSessionToken; 

                    // Scenario 2: OTP field is populated and enabled
                    if (otpField.isEnabled() && !otp.isEmpty()) { 
                        if (username.isEmpty()) {
                           JOptionPane.showMessageDialog(LoginForm.this, "Username is required for OTP login.", "Login Error", JOptionPane.ERROR_MESSAGE);
                           return;
                        }
                        boolean otpVerified = userService.verifyOtp(username, otp);
                        if (otpVerified) {
                            // Assuming password is still required with OTP for this flow,
                            // or a dedicated OTP login method exists on the server.
                            // If your server supports passwordless OTP login, adjust this call.
                            if (password.isEmpty() && !loginButton.getText().contains("OTP")) { 
                                JOptionPane.showMessageDialog(LoginForm.this, "Password is required even with OTP for this login type.", "Login Error", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                            newSessionToken = userService.login(username, password); // Or a dedicated userService.loginWithOtp(username, otp);
                            if (newSessionToken != null && !newSessionToken.toLowerCase().startsWith("error:")) {
                                LoginForm.sessionToken = newSessionToken;
                                JOptionPane.showMessageDialog(LoginForm.this, "OTP Verified. Login Successful!", "Login Success", JOptionPane.INFORMATION_MESSAGE);
                                openMainDashboard();
                            } else {
                                JOptionPane.showMessageDialog(LoginForm.this, "Login failed after OTP verification: " + (newSessionToken != null ? newSessionToken : "Unknown error"), "Login Failed", JOptionPane.ERROR_MESSAGE);
                            }
                        } else {
                            JOptionPane.showMessageDialog(LoginForm.this, "Invalid OTP.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                        }
                    } else { // Scenario 1: OTP field is empty/disabled (Password-only login)
                        if (username.isEmpty() || password.isEmpty()) {
                            JOptionPane.showMessageDialog(LoginForm.this, "Username and Password are required for password-only login.", "Login Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        newSessionToken = userService.login(username, password);
                        if (newSessionToken != null && !newSessionToken.toLowerCase().startsWith("error:")) {
                            LoginForm.sessionToken = newSessionToken;
                            JOptionPane.showMessageDialog(LoginForm.this, "Login Successful!", "Login Success", JOptionPane.INFORMATION_MESSAGE);
                            openMainDashboard();
                        } else {
                            JOptionPane.showMessageDialog(LoginForm.this, "Login Failed: " + (newSessionToken != null ? newSessionToken : "Invalid credentials or server error"), "Login Failed", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } catch (Exception ex) {
                    // ex.printStackTrace(); // Keep for debugging
                    JOptionPane.showMessageDialog(LoginForm.this,
                            "Server communication error during login: " + ex.getMessage(),
                            "Login Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private void openMainDashboard() {
        // Close login form
        this.dispose();
        // Open main dashboard, passing the actual session token
        SwingUtilities.invokeLater(() -> {
            new MainDashboardForm(LoginForm.sessionToken).setVisible(true); 
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginForm().setVisible(true);
        });
    }
}
