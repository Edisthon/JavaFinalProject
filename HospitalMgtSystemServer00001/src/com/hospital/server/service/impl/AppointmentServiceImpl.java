package com.hospital.server.service.impl;

import com.hospital.common.service.IAppointmentService;
import com.hospital.server.dao.AppointmentDao;
import com.hospital.server.dao.PatientDao;
import com.hospital.server.dao.DoctorDao;
import com.hospital.server.model.Appointment;
import com.hospital.server.model.Patient;
import com.hospital.server.model.Doctor;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class AppointmentServiceImpl extends UnicastRemoteObject implements IAppointmentService {

    private final AppointmentDao appointmentDao;
    private final PatientDao patientDao;
    private final DoctorDao doctorDao;

    public AppointmentServiceImpl() throws RemoteException {
        super();
        this.appointmentDao = new AppointmentDao();
        this.patientDao = new PatientDao();
        this.doctorDao = new DoctorDao();
    }

    @Override
    public String scheduleAppointment(Appointment appointment, String sessionToken) throws RemoteException {
        // TODO: Add session validation
        if (!UserServiceImpl.validateSessionGlobally(sessionToken)) {
            return "Error: Invalid or expired session.";
        }
        if (appointment.getPatient() == null || appointment.getDoctor() == null) {
            return "Error: Patient and Doctor must be specified for an appointment.";
        }
        Patient patient = patientDao.findById(appointment.getPatient().getPatientId());
        if (patient == null) {
            return "Error: Patient not found.";
        }
        Doctor doctor = doctorDao.findById(appointment.getDoctor().getDoctorId());
        if (doctor == null) {
            return "Error: Doctor not found.";
        }
        // Ensure the appointment object has the fetched entities if they were passed by ID
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);

        Appointment scheduledAppointment = appointmentDao.save(appointment);
        return scheduledAppointment != null ? "Appointment scheduled successfully with ID: " + scheduledAppointment.getAppointmentId() : "Error: Appointment scheduling failed.";
    }

    @Override
    public Appointment findAppointmentById(Long appointmentId, String sessionToken) throws RemoteException {
        // TODO: Add session validation
        if (!UserServiceImpl.validateSessionGlobally(sessionToken)) {
            throw new RemoteException("Error: Invalid or expired session.");
        }
        return appointmentDao.findById(appointmentId);
    }

    @Override
    public List<Appointment> getAllAppointments(String sessionToken) throws RemoteException {
        // TODO: Add session validation
        if (!UserServiceImpl.validateSessionGlobally(sessionToken)) {
            throw new RemoteException("Error: Invalid or expired session.");
        }
        return appointmentDao.findAll();
    }

    @Override
    public List<Appointment> getAppointmentsForPatient(Long patientId, String sessionToken) throws RemoteException {
        // TODO: Add session validation
        if (!UserServiceImpl.validateSessionGlobally(sessionToken)) {
            throw new RemoteException("Error: Invalid or expired session.");
        }
        return appointmentDao.findByPatientId(patientId);
    }

    @Override
    public List<Appointment> getAppointmentsForDoctor(Long doctorId, String sessionToken) throws RemoteException {
        // TODO: Add session validation
        if (!UserServiceImpl.validateSessionGlobally(sessionToken)) {
            throw new RemoteException("Error: Invalid or expired session.");
        }
        return appointmentDao.findByDoctorId(doctorId);
    }

    @Override
    public String updateAppointment(Appointment appointment, String sessionToken) throws RemoteException {
        // TODO: Add session validation
        if (!UserServiceImpl.validateSessionGlobally(sessionToken)) {
            return "Error: Invalid or expired session.";
        }
        Appointment existingAppointment = appointmentDao.findById(appointment.getAppointmentId());
        if (existingAppointment == null) {
            return "Error: Appointment not found.";
        }
        // Potentially re-validate patient and doctor if they can be changed
        if (appointment.getPatient() != null && appointment.getPatient().getPatientId() != null) {
             Patient patient = patientDao.findById(appointment.getPatient().getPatientId());
            if (patient == null) return "Error: Patient for update not found.";
            existingAppointment.setPatient(patient);
        }
         if (appointment.getDoctor() != null && appointment.getDoctor().getDoctorId() != null) {
            Doctor doctor = doctorDao.findById(appointment.getDoctor().getDoctorId());
            if (doctor == null) return "Error: Doctor for update not found.";
            existingAppointment.setDoctor(doctor);
        }
        existingAppointment.setAppointmentDate(appointment.getAppointmentDate());
        existingAppointment.setStatus(appointment.getStatus());
        existingAppointment.setReason(appointment.getReason());

        try {
            appointmentDao.update(existingAppointment);
            return "Appointment updated successfully.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: Appointment update failed.";
        }
    }

    @Override
    public String cancelAppointment(Long appointmentId, String sessionToken) throws RemoteException {
        // TODO: Add session validation
        if (!UserServiceImpl.validateSessionGlobally(sessionToken)) {
            return "Error: Invalid or expired session.";
        }
        Appointment appointment = appointmentDao.findById(appointmentId);
        if (appointment != null) {
            try {
                // Instead of deleting, we might want to set status to "Cancelled"
                appointment.setStatus("Cancelled");
                appointmentDao.update(appointment);
                // or appointmentDao.delete(appointment); if physical deletion is preferred
                return "Appointment cancelled successfully.";
            } catch (Exception e) {
                e.printStackTrace();
                return "Error: Appointment cancellation failed.";
            }
        }
        return "Error: Appointment not found.";
    }
}
