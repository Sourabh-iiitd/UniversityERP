DROP DATABASE IF EXISTS authDB;

CREATE DATABASE IF NOT EXISTS authDB;
       USE authDB;
CREATE TABLE IF NOT EXISTS users (
                                     userID INT AUTO_INCREMENT PRIMARY KEY,
                                     userName varchar(100) UNIQUE NOT NULL,
    role ENUM("ADMIN", "INSTRUCTOR", "STUDENT") NOT NULL,
    passwdHash varchar(255) NOT NULL,
    status ENUM("ACTIVE", "INACTIVE") DEFAULT "INACTIVE",
    lastLogin TIMESTAMP NULL
    );