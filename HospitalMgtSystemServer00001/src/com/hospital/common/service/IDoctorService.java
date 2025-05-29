package com.hospital.common.service;

import com.hospital.server.model.Doctor;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IDoctorService extends Remote {
    String addDoctor(Doctor doctor, String sessionToken) throws RemoteException;
    Doctor findDoctorById(Long doctorId, String sessionToken) throws RemoteException;
    List<Doctor> getAllDoctors(String sessionToken) throws RemoteException;
    String updateDoctor(Doctor doctor, String sessionToken) throws RemoteException;
    String deleteDoctor(Long doctorId, String sessionToken) throws RemoteException;
    List<Doctor> searchDoctors(String searchTerm, String sessionToken) throws RemoteException;
}
