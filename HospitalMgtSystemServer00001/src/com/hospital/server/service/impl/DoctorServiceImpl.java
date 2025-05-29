package com.hospital.server.service.impl;

import com.hospital.common.service.IDoctorService;
import com.hospital.server.dao.DoctorDao;
import com.hospital.server.model.Doctor;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class DoctorServiceImpl extends UnicastRemoteObject implements IDoctorService {

    private final DoctorDao doctorDao;

    public DoctorServiceImpl() throws RemoteException {
        super();
        this.doctorDao = new DoctorDao();
    }

    @Override
    public String addDoctor(Doctor doctor, String sessionToken) throws RemoteException {
        // TODO: Add session validation
        if (!UserServiceImpl.validateSessionGlobally(sessionToken)) {
            return "Error: Invalid or expired session.";
        }
        Doctor savedDoctor = doctorDao.save(doctor);
        return savedDoctor != null ? "Doctor added successfully with ID: " + savedDoctor.getDoctorId() : "Error: Doctor addition failed.";
    }

    @Override
    public Doctor findDoctorById(Long doctorId, String sessionToken) throws RemoteException {
        // TODO: Add session validation
         if (!UserServiceImpl.validateSessionGlobally(sessionToken)) {
            throw new RemoteException("Error: Invalid or expired session.");
        }
        return doctorDao.findById(doctorId);
    }

    @Override
    public List<Doctor> getAllDoctors(String sessionToken) throws RemoteException {
        // TODO: Add session validation
        if (!UserServiceImpl.validateSessionGlobally(sessionToken)) {
            throw new RemoteException("Error: Invalid or expired session.");
        }
        return doctorDao.findAll();
    }

    @Override
    public String updateDoctor(Doctor doctor, String sessionToken) throws RemoteException {
        // TODO: Add session validation
        if (!UserServiceImpl.validateSessionGlobally(sessionToken)) {
            return "Error: Invalid or expired session.";
        }
        Doctor existingDoctor = doctorDao.findById(doctor.getDoctorId());
        if (existingDoctor == null) {
            return "Error: Doctor not found.";
        }
        try {
            doctorDao.update(doctor);
            return "Doctor updated successfully.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: Doctor update failed.";
        }
    }

    @Override
    public String deleteDoctor(Long doctorId, String sessionToken) throws RemoteException {
        // TODO: Add session validation
        if (!UserServiceImpl.validateSessionGlobally(sessionToken)) {
            return "Error: Invalid or expired session.";
        }
        Doctor doctor = doctorDao.findById(doctorId);
        if (doctor != null) {
            try {
                doctorDao.delete(doctor);
                return "Doctor deleted successfully.";
            } catch (Exception e) {
                e.printStackTrace();
                return "Error: Doctor deletion failed.";
            }
        }
        return "Error: Doctor not found.";
    }

    @Override
    public List<Doctor> searchDoctors(String searchTerm, String sessionToken) throws RemoteException {
        // TODO: Add session validation
        if (!UserServiceImpl.validateSessionGlobally(sessionToken)) {
            throw new RemoteException("Error: Invalid or expired session.");
        }
        return doctorDao.searchByNameOrSpecialization(searchTerm);
    }
}
