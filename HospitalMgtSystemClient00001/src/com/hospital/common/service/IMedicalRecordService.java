package com.hospital.common.service;

import com.hospital.server.model.MedicalRecord;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IMedicalRecordService extends Remote {
    String createMedicalRecord(MedicalRecord record, String sessionToken) throws RemoteException;
    MedicalRecord findMedicalRecordById(Long recordId, String sessionToken) throws RemoteException;
    List<MedicalRecord> getMedicalRecordsForPatient(Long patientId, String sessionToken) throws RemoteException;
    String updateMedicalRecord(MedicalRecord record, String sessionToken) throws RemoteException;
}
