# FoodBuddy 🍱

**A Java and MySQL-based food donation management system.**

FoodBuddy is a console-based application that connects food donors with recipients. It helps manage food donations, track requests, and prevent claimed food from appearing as available.

## Features

- Register and view donors
- Add, view, search, update, and delete food donations
- Register and view recipients
- View available food donations
- Submit food requests
- Approve or reject food requests
- Automatically mark approved donations as claimed
- Store and manage data using MySQL

## Technologies Used

- Java
- MySQL
- JDBC (Java Database Connectivity)
- Object-Oriented Programming (OOP)

## Prerequisites

Install the following before running FoodBuddy:

- Java JDK
- MySQL Server
- MySQL Connector/J

## Project Setup

### 1. Clone the repository

```bash
git clone YOUR_GITHUB_REPOSITORY_URL
cd FoodBuddy
```

Replace `YOUR_GITHUB_REPOSITORY_URL` with the URL of your GitHub repository.

### 2. Set up the database

Open MySQL Workbench or your MySQL command-line client and execute the SQL script in:

`database/foodbuddy.sql`

This creates the `foodbuddy` database and the required tables.

### 3. Configure database credentials

Create a `db.properties` file in the project root:

```properties
db.url=jdbc:mysql://localhost:3306/foodbuddy
db.user=root
db.password=YOUR_MYSQL_PASSWORD
```

Replace the username and password with your local MySQL credentials.

**Do not commit `db.properties` to GitHub.**

### 4. Add MySQL Connector/J

Place the MySQL Connector/J JAR inside the `lib` folder.

### 5. Compile the project

Run this command from the project root:

```powershell
javac -cp "lib/*" -d bin src\com\foodbuddy\*.java
```

### 6. Run FoodBuddy

On Windows, run:

```powershell
java -cp "bin;lib/*" com.foodbuddy.Main
```

## Database Tables

| Table | Purpose |
|---|---|
| `donors` | Stores donor information |
| `donations` | Stores food donation details and availability |
| `recipients` | Stores recipient information |
| `food_requests` | Tracks requests and their statuses |

## Learning Objectives

This project demonstrates:

- Classes and objects
- Encapsulation
- Constructors and getters/setters
- Database design and relationships
- JDBC connectivity
- CRUD operations
- SQL queries and joins
- Transaction handling for request approval

## Project Status

Developed as a college project to practise Java OOP concepts and DBMS integration.
