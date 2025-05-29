package com.hospital.server.core;

import com.hospital.common.service.*;
import com.hospital.server.service.impl.*;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerDriver {

    public static void main(String[] args) {
        try {
            // Optional: Set security manager if needed
            // if (System.getSecurityManager() == null) {
            //     System.setSecurityManager(new SecurityManager());
            // }

            // Start RMI registry on port 6000
            Registry registry = LocateRegistry.createRegistry(6000);
            System.out.println("RMI Registry started on port 6000.");

            // Instantiate services
            IUserService userService = new UserServiceImpl();
            IPatientService patientService = new PatientServiceImpl();
            IDoctorService doctorService = new DoctorServiceImpl();
            IAppointmentService appointmentService = new AppointmentServiceImpl();
            IMedicalRecordService medicalRecordService = new MedicalRecordServiceImpl();

            // Bind services to RMI registry
            // Using registry.rebind() is generally preferred if you already have a Registry object
            String userServiceName = "rmi://localhost:6000/UserService";
            registry.rebind("UserService", userService);
            // Naming.rebind(userServiceName, userService); // Alternative using Naming
            System.out.println("UserService bound successfully as " + userServiceName);

            String patientServiceName = "rmi://localhost:6000/PatientService";
            registry.rebind("PatientService", patientService);
            // Naming.rebind(patientServiceName, patientService);
            System.out.println("PatientService bound successfully as " + patientServiceName);

            String doctorServiceName = "rmi://localhost:6000/DoctorService";
            registry.rebind("DoctorService", doctorService);
            // Naming.rebind(doctorServiceName, doctorService);
            System.out.println("DoctorService bound successfully as " + doctorServiceName);

            String appointmentServiceName = "rmi://localhost:6000/AppointmentService";
            registry.rebind("AppointmentService", appointmentService);
            // Naming.rebind(appointmentServiceName, appointmentService);
            System.out.println("AppointmentService bound successfully as " + appointmentServiceName);

            String medicalRecordServiceName = "rmi://localhost:6000/MedicalRecordService";
            registry.rebind("MedicalRecordService", medicalRecordService);
            // Naming.rebind(medicalRecordServiceName, medicalRecordService);
            System.out.println("MedicalRecordService bound successfully as " + medicalRecordServiceName);

            System.out.println("Hospital Management Server is running on port 6000...");

        } catch (RemoteException e) {
            System.err.println("Server RemoteException: " + e.toString());
            e.printStackTrace();
        } catch (Exception e) { // Catching general Exception for other issues like MalformedURLException
            System.err.println("Server Exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
