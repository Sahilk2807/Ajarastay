-- Ajara Stay - MySQL schema
CREATE DATABASE IF NOT EXISTS ajara_stay_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE ajara_stay_db;

-- Users table
CREATE TABLE IF NOT EXISTS users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  prn VARCHAR(50) UNIQUE,
  email VARCHAR(120) UNIQUE NOT NULL,
  phone VARCHAR(20),
  role ENUM("student","admin") NOT NULL DEFAULT "student",
  password_hash VARCHAR(255) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Rooms table
CREATE TABLE IF NOT EXISTS rooms (
  room_id INT AUTO_INCREMENT PRIMARY KEY,
  room_no VARCHAR(20) UNIQUE NOT NULL,
  capacity INT NOT NULL,
  available_beds INT NOT NULL
);

-- Students table
CREATE TABLE IF NOT EXISTS students (
  student_id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,
  room_id INT NULL,
  fee_status ENUM("paid","pending") DEFAULT "pending",
  complaint_status ENUM("none","open","resolved") DEFAULT "none",
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  FOREIGN KEY (room_id) REFERENCES rooms(room_id) ON DELETE SET NULL
);

-- Fees table
CREATE TABLE IF NOT EXISTS fees (
  fee_id INT AUTO_INCREMENT PRIMARY KEY,
  student_id INT NOT NULL,
  amount DECIMAL(10,2) NOT NULL,
  status ENUM("paid","pending") DEFAULT "pending",
  due_date DATE,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE
);

-- Complaints table
CREATE TABLE IF NOT EXISTS complaints (
  complaint_id INT AUTO_INCREMENT PRIMARY KEY,
  student_id INT NOT NULL,
  complaint_text TEXT NOT NULL,
  status ENUM("open","resolved") DEFAULT "open",
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE
);

-- Notices table
CREATE TABLE IF NOT EXISTS notices (
  notice_id INT AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(200) NOT NULL,
  description TEXT NOT NULL,
  date_posted TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Seed an admin user placeholder (password to be set via app)
INSERT INTO users (name, prn, email, phone, role, password_hash)
VALUES ("Hostel Admin", NULL, "admin@ajarastay.local", "0000000000", "admin", "a0");
