package com.hospital.server.service.impl;

import com.hospital.common.service.IMedicalRecordService;
import com.hospital.server.dao.MedicalRecordDao;
import com.hospital.server.dao.PatientDao;
import com.hospital.server.dao.DoctorDao;
import com.hospital.server.dao.AppointmentDao;
import com.hospital.server.model.MedicalRecord;
import com.hospital.server.model.Patient;
import com.hospital.server.model.Doctor;
import com.hospital.server.model.Appointment;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Date;

public class MedicalRecordServiceImpl extends UnicastRemoteObject implements IMedicalRecordService {

    private final MedicalRecordDao medicalRecordDao;
    private final PatientDao patientDao;
    private final DoctorDao doctorDao;
    private final AppointmentDao appointmentDao;

    public MedicalRecordServiceImpl() throws RemoteException {
        super();
        this.medicalRecordDao = new MedicalRecordDao();
        this.patientDao = new PatientDao();
        this.doctorDao = new DoctorDao();
        this.appointmentDao = new AppointmentDao();
    }

    @Override
    public String createMedicalRecord(MedicalRecord record, String sessionToken) throws RemoteException {
        // TODO: Add session validation
        if (!UserServiceImpl.validateSessionGlobally(sessionToken)) {
            return "Error: Invalid or expired session.";
        }

        if (record.getPatient() == null || record.getPatient().getPatientId() == null) {
            return "Error: Patient must be specified for a medical record.";
        }
        Patient patient = patientDao.findById(record.getPatient().getPatientId());
        if (patient == null) {
            return "Error: Patient not found.";
        }
        record.setPatient(patient); // Ensure the record has the full patient object

        if (record.getDoctor() == null || record.getDoctor().getDoctorId() == null) {
            return "Error: Doctor must be specified for a medical record.";
        }
        Doctor doctor = doctorDao.findById(record.getDoctor().getDoctorId());
        if (doctor == null) {
            return "Error: Doctor not found.";
        }
        record.setDoctor(doctor); // Ensure the record has the full doctor object

        if (record.getAppointment() != null && record.getAppointment().getAppointmentId() != null) {
            Appointment appointment = appointmentDao.findById(record.getAppointment().getAppointmentId());
            if (appointment == null) {
                return "Error: Associated appointment not found.";
            }
            record.setAppointment(appointment); // Ensure the record has the full appointment object
        } else {
            record.setAppointment(null); // Explicitly set to null if not provided or ID is null
        }
        
        if (record.getRecordDate() == null) {
            record.setRecordDate(new Date()); // Set current date if not provided
        }

        MedicalRecord savedRecord = medicalRecordDao.save(record);
        return savedRecord != null ? "Medical record created successfully with ID: " + savedRecord.getRecordId() : "Error: Medical record creation failed.";
    }

    @Override
    public MedicalRecord findMedicalRecordById(Long recordId, String sessionToken) throws RemoteException {
        // TODO: Add session validation
        if (!UserServiceImpl.validateSessionGlobally(sessionToken)) {
            throw new RemoteException("Error: Invalid or expired session.");
        }
        return medicalRecordDao.findById(recordId);
    }

    @Override
    public List<MedicalRecord> getMedicalRecordsForPatient(Long patientId, String sessionToken) throws RemoteException {
        // TODO: Add session validation
        if (!UserServiceImpl.validateSessionGlobally(sessionToken)) {
            throw new RemoteException("Error: Invalid or expired session.");
        }
        Patient patient = patientDao.findById(patientId);
        if (patient == null) {
            throw new RemoteException("Error: Patient not found.");
        }
        return medicalRecordDao.findByPatientId(patientId);
    }

    @Override
    public String updateMedicalRecord(MedicalRecord record, String sessionToken) throws RemoteException {
        // TODO: Add session validation
        if (!UserServiceImpl.validateSessionGlobally(sessionToken)) {
            return "Error: Invalid or expired session.";
        }
        MedicalRecord existingRecord = medicalRecordDao.findById(record.getRecordId());
        if (existingRecord == null) {
            return "Error: Medical record not found.";
        }

        // Update fields if they are provided in the 'record' object
        if (record.getDiagnosis() != null) {
            existingRecord.setDiagnosis(record.getDiagnosis());
        }
        if (record.getTreatment() != null) {
            existingRecord.setTreatment(record.getTreatment());
        }
        if (record.getNotes() != null) {
            existingRecord.setNotes(record.getNotes());
        }
        if (record.getRecordDate() != null) {
            existingRecord.setRecordDate(record.getRecordDate());
        }

        // Potentially update patient, doctor, appointment if allowed and valid IDs are provided
        if (record.getPatient() != null && record.getPatient().getPatientId() != null) {
            Patient patient = patientDao.findById(record.getPatient().getPatientId());
            if (patient == null) return "Error: Patient for update not found.";
            existingRecord.setPatient(patient);
        }
        if (record.getDoctor() != null && record.getDoctor().getDoctorId() != null) {
            Doctor doctor = doctorDao.findById(record.getDoctor().getDoctorId());
            if (doctor == null) return "Error: Doctor for update not found.";
            existingRecord.setDoctor(doctor);
        }
        if (record.getAppointment() != null && record.getAppointment().getAppointmentId() != null) {
            Appointment appointment = appointmentDao.findById(record.getAppointment().getAppointmentId());
            if (appointment == null) return "Error: Appointment for update not found.";
            existingRecord.setAppointment(appointment);
        } else if (record.getAppointment() == null) { 
            // If explicitly set to null in the input, allow clearing it
            existingRecord.setAppointment(null);
        }


        try {
            medicalRecordDao.update(existingRecord);
            return "Medical record updated successfully.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: Medical record update failed.";
        }
    }
}
