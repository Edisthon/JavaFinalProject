package com.hospital.server.service.impl;

import com.hospital.common.service.IPatientService;
import com.hospital.server.dao.PatientDao;
import com.hospital.server.model.Patient;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class PatientServiceImpl extends UnicastRemoteObject implements IPatientService {

    private final PatientDao patientDao;

    public PatientServiceImpl() throws RemoteException {
        super();
        this.patientDao = new PatientDao();
    }

    @Override
    public String registerPatient(Patient patient, String sessionToken) throws RemoteException {
        // TODO: Add session validation
        if (!UserServiceImpl.validateSessionGlobally(sessionToken)) {
            return "Error: Invalid or expired session.";
        }
        Patient savedPatient = patientDao.save(patient);
        return savedPatient != null ? "Patient registered successfully with ID: " + savedPatient.getPatientId() : "Error: Patient registration failed.";
    }

    @Override
    public Patient findPatientById(Long patientId, String sessionToken) throws RemoteException {
        // TODO: Add session validation
        if (!UserServiceImpl.validateSessionGlobally(sessionToken)) {
            throw new RemoteException("Error: Invalid or expired session.");
        }
        return patientDao.findById(patientId);
    }

    @Override
    public List<Patient> getAllPatients(String sessionToken) throws RemoteException {
        // TODO: Add session validation
        if (!UserServiceImpl.validateSessionGlobally(sessionToken)) {
            throw new RemoteException("Error: Invalid or expired session.");
        }
        return patientDao.findAll();
    }

    @Override
    public String updatePatient(Patient patient, String sessionToken) throws RemoteException {
        // TODO: Add session validation
        if (!UserServiceImpl.validateSessionGlobally(sessionToken)) {
            return "Error: Invalid or expired session.";
        }
        Patient existingPatient = patientDao.findById(patient.getPatientId());
        if (existingPatient == null) {
            return "Error: Patient not found.";
        }
        try {
            patientDao.update(patient);
            return "Patient updated successfully.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: Patient update failed.";
        }
    }

    @Override
    public String deletePatient(Long patientId, String sessionToken) throws RemoteException {
        // TODO: Add session validation
        if (!UserServiceImpl.validateSessionGlobally(sessionToken)) {
            return "Error: Invalid or expired session.";
        }
        Patient patient = patientDao.findById(patientId);
        if (patient != null) {
            try {
                patientDao.delete(patient);
                return "Patient deleted successfully.";
            } catch (Exception e) {
                e.printStackTrace();
                return "Error: Patient deletion failed.";
            }
        }
        return "Error: Patient not found.";
    }

    @Override
    public List<Patient> searchPatients(String searchTerm, String sessionToken) throws RemoteException {
        // TODO: Add session validation
        if (!UserServiceImpl.validateSessionGlobally(sessionToken)) {
            throw new RemoteException("Error: Invalid or expired session.");
        }
        return patientDao.searchByName(searchTerm);
    }
}
