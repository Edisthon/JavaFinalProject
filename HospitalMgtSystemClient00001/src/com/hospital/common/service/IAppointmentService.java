package com.hospital.common.service;

import com.hospital.server.model.Appointment;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IAppointmentService extends Remote {
    String scheduleAppointment(Appointment appointment, String sessionToken) throws RemoteException;
    Appointment findAppointmentById(Long appointmentId, String sessionToken) throws RemoteException;
    List<Appointment> getAllAppointments(String sessionToken) throws RemoteException;
    List<Appointment> getAppointmentsForPatient(Long patientId, String sessionToken) throws RemoteException;
    List<Appointment> getAppointmentsForDoctor(Long doctorId, String sessionToken) throws RemoteException;
    String updateAppointment(Appointment appointment, String sessionToken) throws RemoteException;
    String cancelAppointment(Long appointmentId, String sessionToken) throws RemoteException;
}
