DROP DATABASE IF EXISTS erpDB;

CREATE DATABASE IF NOT EXISTS erpDB;

       USE erpDB;
           CREATE TABLE IF NOT EXISTS settings(
               settingKey varchar(50) PRIMARY KEY,
               settingValue varchar(250)
           );
            INSERT IGNORE INTO settings VALUES ("maintenanceOn", "false");



           CREATE TABLE IF NOT EXISTS students(
               userID INT PRIMARY KEY,
               roll VARCHAR(20) UNIQUE NOT NULL,
               firstName varchar(50) NOT NULL,
               lastName varchar(50) NOT NULL,
               program VARCHAR(50),
               cgpa DECIMAL(4,2) DEFAULT 0.0,
               passOutYear INT
           );

            CREATE TABLE IF NOT EXISTS instructors(
                userID INT PRIMARY KEY,
                fullName varchar(100) NOT NULL,
                department varchar(50)
            );

            CREATE TABLE IF NOT EXISTS courses(
                courseID INT AUTO_INCREMENT PRIMARY KEY,
                courseCode varchar(10) UNIQUE NOT NULL,
                courseName varchar(50) NOT NULL,
                credits INT NOT NULL
            );

            CREATE TABLE IF NOT EXISTS sections(
                sectionID INT AUTO_INCREMENT PRIMARY KEY,
                courseID INT NOT NULL,
                instructorID INT NOT NULL,
                term varchar(20),
                room varchar(20),
                schedule varchar(50),
                capacity INT NOT NULL,
                dropDeadline DATE,
                FOREIGN KEY (courseID) REFERENCES courses(courseID),
                FOREIGN KEY (instructorID) REFERENCES instructors(userID)
            );

            CREATE TABLE IF NOT EXISTS enrollments(
                enrollmentID INT AUTO_INCREMENT PRIMARY KEY,
                studentID INT NOT NULL,
                sectionID INT NOT NULL,
                status ENUM("ENROLLED", "DROPPED", "COMPLETED") DEFAULT "ENROLLED",
                FOREIGN KEY (studentID) REFERENCES students(userID),
                FOREIGN KEY (sectionID) REFERENCES sections(sectionID),
                UNIQUE(studentID, sectionID)


            );

            CREATE TABLE IF NOT EXISTS grades (
                gradeID INT AUTO_INCREMENT PRIMARY KEY,
                enrollmentID INT NOT NULL,
                componentName varchar(50),
                score DECIMAL(5,2),
                maxScore DECIMAL(5,2),
                FOREIGN KEY (enrollmentID) REFERENCES enrollments(enrollmentID)
            );

