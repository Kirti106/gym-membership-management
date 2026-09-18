# PROJECT STATEMENT: GYM MEMBERSHIP MANAGEMENT SYSTEM

## 1. Problem Statement
Fitness centers and gym facilities often struggle with managing member subscriptions, tracking attendance, scheduling personal trainers, and processing payments manually. Paper-based registers or fragmented spreadsheets lead to revenue leaks, un-tracked expired memberships, duplicate attendance records, and inadequate business insights. 

The **Gym Membership Management System** solves these challenges by providing a centralized, secure, console-based solution that automates member registration, plan assignments, payment receipts, daily check-ins, trainer allocations, and executive financial reports.

---

## 2. Scope of the System
The system is built as a standalone console application utilizing Core Java and MySQL. It covers:
- Complete administrative authentication and privilege control.
- Lifecycle management of gym members (registration, updates, deactivation).
- Management of membership plans (pricing, duration, packages).
- Dynamic calculations of subscription start and end dates with automatic expiry synchronization.
- Monetary transaction tracking, receipt generation, and payment validation.
- Attendance check-ins with automated duplicate prevention for the same date.
- Trainer profiles and member-trainer assignment tracking.
- Real-time business reporting for revenue, active members, expiries, and member activity.

---

## 3. Target Users
- **Gym Administrators / Front Desk Staff**: Manage day-to-day operations, register members, check in visitors, record payments, and assign trainers.
- **Gym Managers / Owners**: Access executive reports, monitor monthly revenue streams, review member renewal trends, and evaluate trainer workloads.

---

## 4. High-Level Features Summary

| Module | Core Functionalities |
| :--- | :--- |
| **Admin Module** | Login authentication, session management, administrative logout. |
| **Member Management** | Register, view list, search by ID/name, update contact info, deactivate/delete. |
| **Plan Management** | Define plans, set prices and duration (months), update plan details. |
| **Enrollment & Expiry** | Assign plans to members, compute validity dates, extend on renewal, auto-expire. |
| **Payment Management** | Record payments, validate amounts, track payment methods, generate ASCII receipts. |
| **Attendance Tracking** | Record daily check-ins, prevent same-day duplicates, track total check-in counts. |
| **Trainer Management** | Register trainers, track availability, assign trainers to members, track workloads. |
| **Reports & Analytics** | Total revenue, monthly earnings, active/expired counts, top visitors, upcoming expiries. |
