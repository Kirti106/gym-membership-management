# GYM MEMBERSHIP MANAGEMENT SYSTEM

A complete, production-ready, console-based Java application built using **Core Java**, **MySQL**, **JDBC**, and **Maven**. Designed for gym administrators to manage members, membership plans, enrollments, payment processing, daily check-in attendance, trainer assignments, and comprehensive business analytics reports.

---

## 📌 Features Overview

1. **Admin Authentication System**
   - Secure login with admin credentials stored in the database.
   - Default seed account: Username `admin` / Password `admin123`.

2. **Member Management**
   - Register new members with name, age, gender, phone, email, join date, and status.
   - View all registered members in a formatted ASCII console table.
   - Search members dynamically by ID or Name.
   - Update member contact info and status.
   - Deactivate or permanently delete member accounts.

3. **Membership Plan Management**
   - Define custom plans (e.g., Monthly Basic, Quarterly Standard, Annual VIP).
   - Set duration in months, prices, and descriptions.
   - Update or remove existing plans.

4. **Membership Enrollment & Renewal**
   - Assign membership plans to members.
   - Automatic calculation of membership start date and expiry date (`start_date + duration_months`).
   - Seamless renewal extending validities or creating new active subscriptions.
   - Automated identification and syncing of expired memberships.

5. **Payment Processing & Receipts**
   - Record membership payments with payment methods (`CASH`, `CREDIT_CARD`, `DEBIT_CARD`, `UPI`, `NET_BANKING`).
   - Validate positive amounts and handle invalid input with custom exceptions.
   - View member-wise payment history or overall payment logs.
   - Generate formatted CLI payment receipts.

6. **Daily Attendance Tracking**
   - Check-in members with date and time.
   - Enforce duplicate check (prevents a member from checking in twice on the same day).
   - View attendance logs by member or date.
   - Display total attendance count.

7. **Trainer Management & Assignments**
   - Register trainers with specialization, contact details, and availability.
   - Assign trainers to active members.
   - Unassign trainers and track workload distribution.

8. **Executive Reports & Analytics**
   - Overall Gym Executive Summary (Total Members, Active Count, Lifetime Revenue).
   - Expired Memberships Report.
   - Upcoming Expiries Warning (Expiries in the next 7 days).
   - Monthly Revenue Analytics Breakdown.
   - Top Most Active Members Ranking.
   - Trainer-wise Member Allocation Summary.

---

## 🛠️ Technology Stack

- **Language**: Java 17 (Core Java)
- **Database**: MySQL 8.0+
- **Database Driver**: MySQL Connector/J JDBC Driver (`com.mysql:mysql-connector-j:8.2.0`)
- **Build System**: Apache Maven
- **Testing**: JUnit 5

---

## 📁 Project Folder Structure

```
gym_management/
├── pom.xml
├── sql/
│   ├── schema.sql
│   └── seed.sql
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/
│   │           └── gymmanagement/
│   │               ├── main/
│   │               │   └── Main.java
│   │               ├── model/
│   │               │   ├── Admin.java
│   │               │   ├── Member.java
│   │               │   ├── MembershipPlan.java
│   │               │   ├── Membership.java
│   │               │   ├── Payment.java
│   │               │   ├── Attendance.java
│   │               │   ├── Trainer.java
│   │               │   ├── TrainerAssignment.java
│   │               │   ├── Gender.java
│   │               │   ├── MembershipStatus.java
│   │               │   └── PaymentMethod.java
│   │               ├── dao/
│   │               │   ├── AdminDAO.java
│   │               │   ├── AdminDAOImpl.java
│   │               │   ├── MemberDAO.java
│   │               │   ├── MemberDAOImpl.java
│   │               │   ├── MembershipPlanDAO.java
│   │               │   ├── MembershipPlanDAOImpl.java
│   │               │   ├── MembershipDAO.java
│   │               │   ├── MembershipDAOImpl.java
│   │               │   ├── PaymentDAO.java
│   │               │   ├── PaymentDAOImpl.java
│   │               │   ├── AttendanceDAO.java
│   │               │   ├── AttendanceDAOImpl.java
│   │               │   ├── TrainerDAO.java
│   │               │   └── TrainerDAOImpl.java
│   │               ├── service/
│   │               │   ├── AdminService.java
│   │               │   ├── MemberService.java
│   │               │   ├── MembershipPlanService.java
│   │               │   ├── MembershipService.java
│   │               │   ├── PaymentService.java
│   │               │   ├── AttendanceService.java
│   │               │   ├── TrainerService.java
│   │               │   └── ReportService.java
│   │               ├── exception/
│   │               │   ├── GymManagementException.java
│   │               │   ├── MemberNotFoundException.java
│   │               │   ├── InvalidPaymentException.java
│   │               │   ├── MembershipExpiredException.java
│   │               │   ├── DuplicateAttendanceException.java
│   │               │   ├── EmptyFieldException.java
│   │               │   ├── DatabaseException.java
│   │               │   ├── TrainerNotFoundException.java
│   │               │   ├── PlanNotFoundException.java
│   │               │   └── ValidationException.java
│   │               └── util/
│   │                   ├── DBConnection.java
│   │                   ├── InputValidator.java
│   │                   └── ConsoleTableFormatter.java
│   └── test/
│       └── java/
│           └── com/
│               └── gymmanagement/
│                   ├── InputValidatorTest.java
│                   ├── MemberServiceTest.java
│                   ├── MembershipServiceTest.java
│                   └── PaymentServiceTest.java
├── README.md
├── statement.md
└── design_docs.md
```

---

## 🗄️ Database Setup Instructions

1. Open your MySQL Terminal or Workbench.
2. Execute the database schema script:
   ```sql
   SOURCE sql/schema.sql;
   ```
3. Populate initial seed data (default admin, plans, members, trainers):
   ```sql
   SOURCE sql/seed.sql;
   ```

---

## 🔑 Environment Variables Setup

Security Rule: The application does not hardcode passwords in Java source code. It dynamically reads connection details from environment variables.

Set the environment variables before running:

### Windows Command Prompt (cmd)
```cmd
set DB_URL=jdbc:mysql://localhost:3306/gym_management
set DB_USER=root
set DB_PASSWORD=your_mysql_password
```

### Windows PowerShell
```powershell
$env:DB_URL="jdbc:mysql://localhost:3306/gym_management"
$env:DB_USER="root"
$env:DB_PASSWORD="your_mysql_password"
```

### Linux / macOS Terminal
```bash
export DB_URL="jdbc:mysql://localhost:3306/gym_management"
export DB_USER="root"
export DB_PASSWORD="your_mysql_password"
```

---

## 🚀 How to Compile and Run in VS Code / CLI

### Prerequisites
- JDK 17 or higher (`java -version`)
- Apache Maven (`mvn -version`)
- MySQL Server running on localhost:3306

### 1. Run via Maven (Recommended)
Open Terminal in the project root folder (`gym_management`) and execute:
```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="com.gymmanagement.main.Main"
```

### 2. Run Automated Unit Tests
```bash
mvn test
```

---

## 💻 Sample Console Output

```text
==================================================
      WELCOME TO GYM MANAGEMENT SYSTEM           
==================================================
[SYSTEM] Checking MySQL Database connection... CONNECTED SUCCESSFULLY!

=== ADMIN LOGIN ===
Username (or 'exit' to quit): admin
Password: 

[SUCCESS] Welcome, admin! Login successful.

========================================
       GYM MEMBERSHIP MANAGEMENT        
========================================
1. Member Management
2. Membership Plans
3. Membership Enrollment
4. Payment Management
5. Attendance Management
6. Trainer Management
7. Reports
8. Logout
========================================
Enter your choice (1-8): 1

--- MEMBER MANAGEMENT ---
1. Add New Member
2. View All Members
3. Search Member by ID or Name
4. Update Member Details
5. Delete / Deactivate Member
6. Back to Main Menu
Select option (1-6): 2

--- ALL REGISTERED MEMBERS ---
+----+---------------+-----+--------+------------+-------------------------+------------+--------+
| ID | Name          | Age | Gender | Phone      | Email                   | Join Date  | Status |
+----+---------------+-----+--------+------------+-------------------------+------------+--------+
| 1  | Alice Smith   | 28  | FEMALE | 9123456780 | alice.smith@example.com | 2026-01-10 | ACTIVE |
| 2  | Bob Johnson   | 35  | MALE   | 9123456781 | bob.johnson@example.com | 2026-02-15 | ACTIVE |
+----+---------------+-----+--------+------------+-------------------------+------------+--------+
```

---

## 🔮 Future Enhancements
- Biometric & RFID scanner integration for automated turnstile attendance.
- Email/SMS notification gateway for automated expiry reminders.
- Graphical User Interface (Swing/JavaFX) or RESTful API web extension.
