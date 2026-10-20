
CREATE DATABASE IF NOT EXISTS foodbuddy;
USE foodbuddy;

CREATE TABLE IF NOT EXISTS donors (
    donor_id INT PRIMARY KEY AUTO_INCREMENT,
    donor_name VARCHAR(100) NOT NULL,
    phone VARCHAR(15) NOT NULL
);

CREATE TABLE IF NOT EXISTS donations (
    donation_id INT PRIMARY KEY AUTO_INCREMENT,
    food_name VARCHAR(100) NOT NULL,
    quantity INT NOT NULL,
    location VARCHAR(150) NOT NULL,
    donor_id INT NOT NULL,
    donation_status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
    FOREIGN KEY (donor_id) REFERENCES donors(donor_id)
);

CREATE TABLE IF NOT EXISTS recipients (
    recipient_id INT PRIMARY KEY AUTO_INCREMENT,
    recipient_name VARCHAR(100) NOT NULL,
    phone VARCHAR(15) NOT NULL
);

CREATE TABLE IF NOT EXISTS food_requests (
    request_id INT PRIMARY KEY AUTO_INCREMENT,
    recipient_id INT NOT NULL,
    donation_id INT NOT NULL,
    request_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    request_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (recipient_id)
        REFERENCES recipients(recipient_id),
    FOREIGN KEY (donation_id)
        REFERENCES donations(donation_id)
);
