package com.hospital.client.util;

import com.hospital.common.service.*;
import java.rmi.Naming;

public class RmiClientUtil {

    private static final String SERVER_URL = "rmi://localhost:6000/";

    public static IUserService getUserService() throws Exception {
        return (IUserService) Naming.lookup(SERVER_URL + "UserService");
    }

    public static IPatientService getPatientService() throws Exception {
        return (IPatientService) Naming.lookup(SERVER_URL + "PatientService");
    }

    public static IDoctorService getDoctorService() throws Exception {
        return (IDoctorService) Naming.lookup(SERVER_URL + "DoctorService");
    }

    public static IAppointmentService getAppointmentService() throws Exception {
        return (IAppointmentService) Naming.lookup(SERVER_URL + "AppointmentService");
    }

    public static IMedicalRecordService getMedicalRecordService() throws Exception {
        return (IMedicalRecordService) Naming.lookup(SERVER_URL + "MedicalRecordService");
    }
}
