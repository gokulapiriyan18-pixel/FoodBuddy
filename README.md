# FoodBuddy 🍱

**A Modern Java Swing & MySQL Food Donation Management Desktop Application**

FoodBuddy is an end-to-end food surplus management system designed to connect food donors with community recipients, shelters, and organizations. Featuring a modern, high-contrast dark dashboard and transactional workflow controls, FoodBuddy streamlines the donation lifecycle from registration to fulfillment while preventing claimed food from appearing as available.

---

## 🌟 Key Features

### 1. Modern Dark-Themed GUI (Java Swing)
- **Interactive Dashboard**: Real-time statistical metrics derived dynamically from MySQL (Total Donors, Total Donations, Available Food, Registered Recipients, Pending Requests, Approved Requests) and recent activity feeds.
- **Donation Management**: Searchable, filterable table showing food items, quantities, pickup locations, donor details, and live status badges (`AVAILABLE` vs `CLAIMED`). Includes dialogs for adding, updating, and safely deleting donations.
- **Donor Directory**: Manage individual and organizational donors, view phone contacts, track total contributions, and search by name or contact number.
- **Recipient Directory**: Register community recipients and distribution centers with linked request metrics.
- **Food Request Workflow**: Complete request tracking with approval and rejection actions. An **ACID transactional update** ensures that approving a request atomically updates the request to `APPROVED` and marks the donation as `CLAIMED`.
- **About Page**: Architectural overview, technology stack credits, and system specifications.

### 2. Dual Compatibility: GUI & Console
- **Swing Desktop GUI**: Default interface launched when running the application.
- **Terminal CLI Mode**: Fully retained interactive console application accessible via the `--cli` flag.

---

## 🛠️ Technologies Used

- **Java SE 21**: Object-Oriented Programming (OOP), encapsulation, and strong typing.
- **Java Swing & AWT**: Responsive layouts (`BorderLayout`, `CardLayout`, `GridBagLayout`, `BoxLayout`), custom painted cards, badges, and smooth dark theme styling.
- **MySQL 8.0**: Relational database storage with foreign-key constraints.
- **JDBC (Java Database Connectivity)**: Secure, parameterized queries with `PreparedStatement` and row-locking transactions (`SELECT ... FOR UPDATE`).
- **MySQL Connector/J**: High-performance database driver.

---

## 📁 Project Structure

```text
FoodBuddy/
├── .gitignore
├── db.properties                         # Local MySQL credentials (git-ignored)
├── prompt.txt                            # Requirement specifications
├── README.md                             # Documentation
│
├── database/
│   └── foodbuddy.sql                     # Database schema definition
│
├── lib/
│   └── mysql-connector-j-26.7.0.jar      # MySQL JDBC Driver
│
├── bin/                                  # Compiled .class bytecode
│
└── src/
    └── com/
        └── foodbuddy/
            ├── Main.java                 # Main entrypoint (GUI by default / CLI with --cli)
            ├── DBConnection.java         # JDBC Connection provider
            │
            ├── Donor.java                # Donor model
            ├── FoodDonation.java         # Donation model
            ├── Recipient.java            # Recipient model
            ├── FoodRequest.java          # Food request model
            │
            ├── DonorDAO.java             # Donor Data Access Object
            ├── DonationDAO.java          # Donation Data Access Object
            ├── RecipientDAO.java         # Recipient Data Access Object
            ├── FoodRequestDAO.java       # Food Request Data Access Object (Transactional)
            ├── DashboardDAO.java         # Real-time statistics & activity feeds
            │
            └── ui/
                ├── FoodBuddyApp.java     # Main JFrame desktop application
                ├── UITheme.java          # Dark theme colors, fonts, badges & styled components
                ├── SidebarPanel.java     # Collapsible navigation sidebar
                ├── HeaderPanel.java      # Dynamic breadcrumb title & live clock header
                ├── DashboardPanel.java   # Stat cards & recent activity dashboard
                ├── DonationsPanel.java   # Food donation management panel & modal dialogs
                ├── DonorsPanel.java      # Donor directory & registration panel
                ├── RecipientsPanel.java  # Recipient directory panel
                ├── FoodRequestsPanel.java# Request management & transactional approval
                └── AboutPanel.java       # System specifications & credits
```

---

## 🚀 Setup & Execution Instructions

### Prerequisites
1. **Java JDK 17+ or 21+** installed (`java -version`, `javac -version`).
2. **MySQL Server 8.0+** running locally.
3. **MySQL Connector/J JAR** in the `lib/` directory.

### 1. Database Setup
Execute the SQL script in MySQL Workbench or the MySQL command line:
```sql
SOURCE database/foodbuddy.sql;
```

### 2. Configure Database Credentials
Create or update `db.properties` in the project root:
```properties
db.url=jdbc:mysql://localhost:3306/foodbuddy
db.user=root
db.password=YOUR_MYSQL_PASSWORD
```
*(Note: `db.properties` is protected by `.gitignore` so your personal credentials are never exposed.)*

### 3. Compile the Complete Project
From the project root directory, run:

**On Windows (PowerShell / CMD):**
```powershell
javac -cp "lib/*" -d bin src\com\foodbuddy\*.java src\com\foodbuddy\ui\*.java
```

**On Linux / macOS:**
```bash
javac -cp "lib/*" -d bin src/com/foodbuddy/*.java src/com/foodbuddy/ui/*.java
```

### 4. Run FoodBuddy

#### A. Launch the Modern Java Swing Desktop Application (Default):
**On Windows:**
```powershell
java -cp "bin;lib/*" com.foodbuddy.Main
```
*Alternatively, launch `FoodBuddyApp` directly:*
```powershell
java -cp "bin;lib/*" com.foodbuddy.ui.FoodBuddyApp
```

**On Linux / macOS:**
```bash
java -cp "bin:lib/*" com.foodbuddy.Main
```

#### B. Launch the Interactive Terminal CLI:
If you wish to use the original terminal interface:
**On Windows:**
```powershell
java -cp "bin;lib/*" com.foodbuddy.Main --cli
```

**On Linux / macOS:**
```bash
java -cp "bin:lib/*" com.foodbuddy.Main --cli
```

---

## 🗄️ Database Tables & Relationships

| Table | Description | Relationships |
|---|---|---|
| `donors` | Stores donor names and phone numbers | Referenced by `donations.donor_id` |
| `donations` | Stores food item name, quantity, pickup location, donor ID, and status (`AVAILABLE` / `CLAIMED`) | Foreign Key to `donors(donor_id)` |
| `recipients` | Stores community recipient names and phone numbers | Referenced by `food_requests.recipient_id` |
| `food_requests` | Tracks food distribution requests (`PENDING`, `APPROVED`, `REJECTED`) and timestamps | Foreign Keys to `recipients(recipient_id)` and `donations(donation_id)` |

---

## 💡 Business Logic & Transactional Safety

- **Request Creation**: Only donations marked as `AVAILABLE` and with no active pending requests can be requested.
- **Approval Workflow**: When an admin approves a request, a database transaction locks both rows (`SELECT ... FOR UPDATE`), transitions the request status to `APPROVED`, and changes the food donation status to `CLAIMED`. If any step fails, the entire transaction is rolled back.
- **Rejection Workflow**: Rejecting a request marks the request as `REJECTED` while leaving the food donation `AVAILABLE` for other recipients.
- **Data Integrity**: Foreign key constraints prevent deleting donors or recipients that have active linked records.
