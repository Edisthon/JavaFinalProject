package com.hospital.common.service;

import com.hospital.server.model.User;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IUserService extends Remote {
    String login(String username, String password) throws RemoteException;
    String requestOtp(String usernameOrEmail) throws RemoteException;
    boolean verifyOtp(String usernameOrEmail, String otp) throws RemoteException;
    void logout(String sessionToken) throws RemoteException;
    User getUserByUsername(String username, String sessionToken) throws RemoteException;
}
