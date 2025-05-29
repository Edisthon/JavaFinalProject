CREATE DATABASE IF NOT EXISTS hospital_management_system_db;

USE hospital_management_system_db;

CREATE TABLE IF NOT EXISTS `User` (
    `user_id` INT AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(255) UNIQUE NOT NULL,
    `password_hash` VARCHAR(255) NOT NULL,
    `role` VARCHAR(50) NOT NULL,
    `email` VARCHAR(255) UNIQUE NOT NULL,
    `phone_number` VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS `Patient` (
    `patient_id` INT AUTO_INCREMENT PRIMARY KEY,
    `first_name` VARCHAR(100) NOT NULL,
    `last_name` VARCHAR(100) NOT NULL,
    `date_of_birth` DATE NOT NULL,
    `gender` VARCHAR(10),
    `contact_number` VARCHAR(20),
    `address` TEXT,
    `email` VARCHAR(255) UNIQUE
);

CREATE TABLE IF NOT EXISTS `Doctor` (
    `doctor_id` INT AUTO_INCREMENT PRIMARY KEY,
    `first_name` VARCHAR(100) NOT NULL,
    `last_name` VARCHAR(100) NOT NULL,
    `specialization` VARCHAR(100),
    `contact_number` VARCHAR(20),
    `email` VARCHAR(255) UNIQUE,
    `user_id` INT UNIQUE,
    FOREIGN KEY (`user_id`) REFERENCES `User`(`user_id`)
);

CREATE TABLE IF NOT EXISTS `Appointment` (
    `appointment_id` INT AUTO_INCREMENT PRIMARY KEY,
    `patient_id` INT NOT NULL,
    `doctor_id` INT NOT NULL,
    `appointment_date` DATETIME NOT NULL,
    `status` VARCHAR(50) NOT NULL,
    `reason` TEXT,
    FOREIGN KEY (`patient_id`) REFERENCES `Patient`(`patient_id`),
    FOREIGN KEY (`doctor_id`) REFERENCES `Doctor`(`doctor_id`)
);

CREATE TABLE IF NOT EXISTS `MedicalRecord` (
    `record_id` INT AUTO_INCREMENT PRIMARY KEY,
    `patient_id` INT NOT NULL,
    `doctor_id` INT NOT NULL,
    `appointment_id` INT,
    `record_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `diagnosis` TEXT NOT NULL,
    `treatment` TEXT,
    `notes` TEXT,
    FOREIGN KEY (`patient_id`) REFERENCES `Patient`(`patient_id`),
    FOREIGN KEY (`doctor_id`) REFERENCES `Doctor`(`doctor_id`),
    FOREIGN KEY (`appointment_id`) REFERENCES `Appointment`(`appointment_id`)
);
