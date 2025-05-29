package com.hospital.server.service.impl;

import com.hospital.common.service.IUserService;
import com.hospital.server.dao.UserDao;
import com.hospital.server.model.User;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Random;

public class UserServiceImpl extends UnicastRemoteObject implements IUserService {

    private final UserDao userDao;
    private static final Map<String, String> activeSessions = new HashMap<>(); // Token -> Username
    private static final Map<String, String> userOtps = new HashMap<>(); // Username/Email -> OTP

    public UserServiceImpl() throws RemoteException {
        super();
        this.userDao = new UserDao();
    }

    @Override
    public String login(String username, String password) throws RemoteException {
        User user = userDao.findByUsername(username);
        if (user != null) {
            // TODO: Implement proper password hashing
            if (user.getPasswordHash().equals(password)) { // Placeholder: Direct comparison
                String sessionToken = UUID.randomUUID().toString();
                activeSessions.put(sessionToken, username);
                return sessionToken;
            }
        }
        return null; // Or "Error: Invalid credentials"
    }

    @Override
    public String requestOtp(String usernameOrEmail) throws RemoteException {
        User user = userDao.findByUsername(usernameOrEmail);
        if (user == null) {
            user = userDao.findByEmail(usernameOrEmail);
        }

        if (user != null) {
            String otp = String.format("%06d", new Random().nextInt(999999));
            userOtps.put(user.getUsername(), otp); // Store OTP against username for simplicity
            System.out.println("OTP for " + user.getUsername() + ": " + otp); // For testing
            return "OTP generated and sent (check server console).";
        }
        return "Error: User not found.";
    }

    @Override
    public boolean verifyOtp(String usernameOrEmail, String otp) throws RemoteException {
        User user = userDao.findByUsername(usernameOrEmail);
        if (user == null) {
            user = userDao.findByEmail(usernameOrEmail);
        }

        if (user != null) {
            String storedOtp = userOtps.get(user.getUsername());
            if (storedOtp != null && storedOtp.equals(otp)) {
                userOtps.remove(user.getUsername());
                return true;
            }
        }
        return false;
    }

    @Override
    public void logout(String sessionToken) throws RemoteException {
        if (sessionToken != null) {
            activeSessions.remove(sessionToken);
        }
    }

    @Override
    public User getUserByUsername(String username, String sessionToken) throws RemoteException {
        if (!isValidSession(sessionToken)) {
            throw new RemoteException("Error: Invalid or expired session.");
        }
        return userDao.findByUsername(username);
    }

    // Private helper method
    private boolean isValidSession(String sessionToken) {
        return sessionToken != null && activeSessions.containsKey(sessionToken);
    }

    // Static method for other services to validate session if needed,
    // though ideally a shared session management component would be better.
    public static boolean validateSessionGlobally(String sessionToken) {
         return sessionToken != null && activeSessions.containsKey(sessionToken);
    }
}
