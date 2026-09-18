# DESIGN DOCUMENTATION & VIVA PREPARATION GUIDE

---

## 1. Functional Requirements

1. **FR-01: Admin Authentication**
   - The system must require administrators to log in using valid credentials before granting access to operational menus.

2. **FR-02: Member Lifecycle Management**
   - System must allow creating, retrieving, updating, and deactivating member records.

3. **FR-03: Plan Definition**
   - System must allow admins to create membership plans with specified duration in months and prices.

4. **FR-04: Automated Date Calculations**
   - Membership expiry date must automatically equal `start_date + duration_months`.

5. **FR-05: Duplicate Attendance Guard**
   - System must prevent duplicate attendance records for the same member ID on the same calendar date.

6. **FR-06: Payment Validation & Receipt**
   - Payments must be verified against negative/zero amounts. Formatted receipt containing receipt number and transaction details must be generated.

7. **FR-07: Business Reporting**
   - System must generate formatted tabular reports for revenue, active vs expired count, upcoming expiries (7 days), and trainer workload.

---

## 2. Non-Functional Requirements

1. **Performance**: All database queries must use indexed primary and foreign keys, completing execution in under 100 milliseconds for normal operations.
2. **Security**: Database passwords must never be hardcoded in source code; they are dynamically loaded from environment variables (`DB_PASSWORD`). SQL injections are mitigated via parameterized `PreparedStatement`.
3. **Reliability & Consistency**: Database updates involving multiple steps must maintain ACID compliance using JDBC transactions (`setAutoCommit(false)` and `commit()`).
4. **Maintainability**: Follow strict 3-tier layering (Presentation / Service / DAO) and Java naming conventions.
5. **Error Handling**: Input errors, SQL failures, and business constraint violations must be gracefully caught and displayed without crashing the JVM.
6. **Usability**: Clean menu navigation with ASCII formatted tables for intuitive console readability.

---

## 3. System Architecture & Component Workflow

```
+-------------------------------------------------------------+
|                     CONSOLE UI (Main.java)                  |
+-------------------------------------------------------------+
                              |
                              v
+-------------------------------------------------------------+
|                     SERVICE LAYER                           |
| (MemberService, MembershipService, PaymentService, etc.)   |
+-------------------------------------------------------------+
                              |
                              v
+-------------------------------------------------------------+
|                       DAO LAYER                             |
| (MemberDAO, MembershipDAO, PaymentDAO, AttendanceDAO, etc.)|
+-------------------------------------------------------------+
                              |
                              v
+-------------------------------------------------------------+
|                 JDBC CONTEXT (DBConnection)                 |
+-------------------------------------------------------------+
                              |
                              v
+-------------------------------------------------------------+
|                   MySQL DATABASE (`gym_management`)         |
+-------------------------------------------------------------+
```

---

## 4. Diagram Descriptions for viva / Documentation

### A. Use Case Diagram Description
- **Actor**: Gym Administrator / Front Desk Staff
- **Use Cases**:
  - `UC1`: Authenticate Admin Login
  - `UC2`: Manage Member Records (Add, Edit, View, Delete)
  - `UC3`: Manage Membership Plans
  - `UC4`: Enroll / Renew Member Subscription
  - `UC5`: Process Payment & Issue Receipt
  - `UC6`: Record Check-In Attendance
  - `UC7`: Manage Trainer Profiles & Assignments
  - `UC8`: Generate Financial & Operational Reports

### B. Class Diagram Description
- **Model Entities**: `Admin`, `Member`, `MembershipPlan`, `Membership`, `Payment`, `Attendance`, `Trainer`, `TrainerAssignment`.
- **Enums**: `Gender`, `MembershipStatus`, `PaymentMethod`.
- **DAO Interfaces & Impls**: `MemberDAO` (Interface) <-- `MemberDAOImpl` (Realization), etc.
- **Service Dependencies**: `MemberService` depends on `MemberDAO`, `MembershipService` depends on `MembershipDAO` & `MemberDAO`, etc.
- **Utility Helpers**: `DBConnection` (Singleton connection manager), `InputValidator` (Static regex & range validator), `ConsoleTableFormatter` (ASCII table builder).

### C. ER Diagram Description
- `members` (1) ------ (N) `memberships` (FK: `member_id`)
- `membership_plans` (1) ------ (N) `memberships` (FK: `plan_id`)
- `memberships` (1) ------ (N) `payments` (FK: `membership_id`)
- `members` (1) ------ (N) `payments` (FK: `member_id`)
- `members` (1) ------ (N) `attendance` (FK: `member_id`, UNIQUE Constraint: `member_id + attendance_date`)
- `trainers` (1) ------ (N) `trainer_assignments` (FK: `trainer_id`)
- `members` (1) ------ (N) `trainer_assignments` (FK: `member_id`)

---

## 5. Explanation of Core Java Concepts Used (Viva Preparation)

1. **Encapsulation**:
   - Model classes (`Member`, `Payment`, `Attendance`) declare fields as `private`. Access and mutations are tightly controlled via getters and setters.

2. **Abstraction**:
   - DAO Interfaces (`MemberDAO`, `MembershipDAO`, etc.) expose operational signatures without exposing JDBC SQL code. The implementation details are encapsulated inside `MemberDAOImpl`.

3. **Inheritance**:
   - Custom exception hierarchy: Base exception `GymManagementException` extends `java.lang.Exception`. Subclasses like `MemberNotFoundException` and `InvalidPaymentException` inherit from `GymManagementException`.

4. **Polymorphism**:
   - Method overloading in services/validators (e.g. `validatePositiveInt` with different parameter types). Dynamic method dispatch through DAO interfaces (`MemberDAO dao = new MemberDAOImpl()`).

5. **Static & Final**:
   - `final` variables used for immutability (regex patterns, date formatters, SQL queries). `static` methods in `DBConnection` and `InputValidator` avoid unnecessary object instantiation.

6. **Enums**:
   - Strongly-typed constants (`Gender`, `MembershipStatus`, `PaymentMethod`) eliminate invalid strings and magic values across the database and Java code.

7. **Try-With-Resources**:
   - Automatically closes `Connection`, `PreparedStatement`, and `ResultSet` objects, preventing connection leaks.
