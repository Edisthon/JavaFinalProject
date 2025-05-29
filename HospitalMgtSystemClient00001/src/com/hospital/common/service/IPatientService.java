package com.hospital.common.service;

import com.hospital.server.model.Patient;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IPatientService extends Remote {
    String registerPatient(Patient patient, String sessionToken) throws RemoteException;
    Patient findPatientById(Long patientId, String sessionToken) throws RemoteException;
    List<Patient> getAllPatients(String sessionToken) throws RemoteException;
    String updatePatient(Patient patient, String sessionToken) throws RemoteException;
    String deletePatient(Long patientId, String sessionToken) throws RemoteException;
    List<Patient> searchPatients(String searchTerm, String sessionToken) throws RemoteException;
}
