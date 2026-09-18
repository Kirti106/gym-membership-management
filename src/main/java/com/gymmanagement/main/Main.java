package com.gymmanagement.main;

import com.gymmanagement.exception.GymManagementException;
import com.gymmanagement.model.*;
import com.gymmanagement.service.*;
import com.gymmanagement.util.ConsoleTableFormatter;
import com.gymmanagement.util.DBConnection;
import com.gymmanagement.util.InputValidator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);

    private static final AdminService adminService = new AdminService();
    private static final MemberService memberService = new MemberService();
    private static final MembershipPlanService planService = new MembershipPlanService();
    private static final MembershipService membershipService = new MembershipService();
    private static final PaymentService paymentService = new PaymentService();
    private static final AttendanceService attendanceService = new AttendanceService();
    private static final TrainerService trainerService = new TrainerService();
    private static final ReportService reportService = new ReportService();

    private static String loggedInUser = null;

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("      WELCOME TO GYM MANAGEMENT SYSTEM           ");
        System.out.println("==================================================");

        checkDatabaseConnection();

        while (true) {
            if (loggedInUser == null) {
                boolean success = handleLogin();
                if (!success) {
                    System.out.println("Exiting application. Goodbye!");
                    break;
                }
            }

            // Sync expired memberships automatically on menu refresh
            try {
                membershipService.runAutoExpirySync();
            } catch (Exception ignored) {}

            displayMainMenu();
            int choice = readInt("Enter your choice (1-8): ");

            switch (choice) {
                case 1 -> handleMemberManagement();
                case 2 -> handleMembershipPlanManagement();
                case 3 -> handleMembershipEnrollment();
                case 4 -> handlePaymentManagement();
                case 5 -> handleAttendanceManagement();
                case 6 -> handleTrainerManagement();
                case 7 -> handleReports();
                case 8 -> {
                    System.out.println("\nSuccessfully logged out admin: " + loggedInUser);
                    loggedInUser = null;
                }
                default -> System.out.println("\n[ERROR] Invalid option! Please select between 1 and 8.");
            }
        }
    }

    private static void checkDatabaseConnection() {
        System.out.print("[SYSTEM] Checking MySQL Database connection... ");
        if (DBConnection.testConnection()) {
            System.out.println("CONNECTED SUCCESSFULLY!");
        } else {
            System.out.println("FAILED!");
            System.out.println("[WARNING] DB Connection could not be established using current environment settings.");
            System.out.println("[INFO] Set DB_URL, DB_USER, and DB_PASSWORD environment variables if needed.");
        }
    }

    private static boolean handleLogin() {
        System.out.println("\n=== ADMIN LOGIN ===");
        int attempts = 0;
        while (attempts < 3) {
            System.out.print("Username (or 'exit' to quit): ");
            String username = scanner.nextLine().trim();
            if ("exit".equalsIgnoreCase(username)) return false;

            System.out.print("Password: ");
            String password = scanner.nextLine().trim();

            try {
                if (adminService.login(username, password)) {
                    loggedInUser = username;
                    System.out.println("\n[SUCCESS] Welcome, " + username + "! Login successful.");
                    return true;
                } else {
                    attempts++;
                    System.out.println("[ERROR] Invalid credentials! Attempts left: " + (3 - attempts));
                }
            } catch (Exception e) {
                System.out.println("[ERROR] Login failed: " + e.getMessage());
                attempts++;
            }
        }
        System.out.println("\nToo many failed login attempts.");
        return false;
    }

    private static void displayMainMenu() {
        System.out.println("\n========================================");
        System.out.println("       GYM MEMBERSHIP MANAGEMENT        ");
        System.out.println("========================================");
        System.out.println("1. Member Management");
        System.out.println("2. Membership Plans");
        System.out.println("3. Membership Enrollment");
        System.out.println("4. Payment Management");
        System.out.println("5. Attendance Management");
        System.out.println("6. Trainer Management");
        System.out.println("7. Reports");
        System.out.println("8. Logout");
        System.out.println("========================================");
    }

    private static void handleMemberManagement() {
        while (true) {
            System.out.println("\n--- MEMBER MANAGEMENT ---");
            System.out.println("1. Add New Member");
            System.out.println("2. View All Members");
            System.out.println("3. Search Member by ID or Name");
            System.out.println("4. Update Member Details");
            System.out.println("5. Delete / Deactivate Member");
            System.out.println("6. Back to Main Menu");
            int choice = readInt("Select option (1-6): ");

            switch (choice) {
                case 1 -> addMember();
                case 2 -> viewAllMembers();
                case 3 -> searchMember();
                case 4 -> updateMember();
                case 5 -> deleteOrDeactivateMember();
                case 6 -> { return; }
                default -> System.out.println("[ERROR] Invalid choice!");
            }
        }
    }

    private static void addMember() {
        System.out.println("\n--- ADD NEW MEMBER ---");
        try {
            System.out.print("Full Name: ");
            String name = scanner.nextLine().trim();

            int age = readInt("Age: ");
            System.out.print("Gender (MALE / FEMALE / OTHER): ");
            String gender = scanner.nextLine().trim();

            System.out.print("Phone Number (10 digits): ");
            String phone = scanner.nextLine().trim();

            System.out.print("Email Address: ");
            String email = scanner.nextLine().trim();

            Member m = memberService.registerMember(name, age, gender, phone, email);
            System.out.println("\n[SUCCESS] Member registered successfully with ID: " + m.getMemberId());
        } catch (GymManagementException e) {
            System.out.println("[ERROR] Registration Failed: " + e.getMessage());
        }
    }

    private static void viewAllMembers() {
        System.out.println("\n--- ALL REGISTERED MEMBERS ---");
        try {
            List<Member> members = memberService.getAllMembers();
            ConsoleTableFormatter table = new ConsoleTableFormatter("ID", "Name", "Age", "Gender", "Phone", "Email", "Join Date", "Status");
            for (Member m : members) {
                table.addRow(
                        String.valueOf(m.getMemberId()),
                        m.getName(),
                        String.valueOf(m.getAge()),
                        m.getGender().name(),
                        m.getPhone(),
                        m.getEmail(),
                        m.getJoinDate().toString(),
                        m.getStatus().name()
                );
            }
            table.print();
        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    private static void searchMember() {
        System.out.println("\n--- SEARCH MEMBER ---");
        System.out.println("1. By Member ID");
        System.out.println("2. By Member Name");
        int opt = readInt("Choice (1-2): ");

        try {
            if (opt == 1) {
                int id = readInt("Enter Member ID: ");
                Member m = memberService.getMemberById(id);
                ConsoleTableFormatter table = new ConsoleTableFormatter("ID", "Name", "Age", "Gender", "Phone", "Email", "Join Date", "Status");
                table.addRow(String.valueOf(m.getMemberId()), m.getName(), String.valueOf(m.getAge()), m.getGender().name(), m.getPhone(), m.getEmail(), m.getJoinDate().toString(), m.getStatus().name());
                table.print();
            } else if (opt == 2) {
                System.out.print("Enter Member Name to search: ");
                String name = scanner.nextLine().trim();
                List<Member> members = memberService.searchMembersByName(name);
                ConsoleTableFormatter table = new ConsoleTableFormatter("ID", "Name", "Age", "Gender", "Phone", "Email", "Join Date", "Status");
                for (Member m : members) {
                    table.addRow(String.valueOf(m.getMemberId()), m.getName(), String.valueOf(m.getAge()), m.getGender().name(), m.getPhone(), m.getEmail(), m.getJoinDate().toString(), m.getStatus().name());
                }
                table.print();
            } else {
                System.out.println("[ERROR] Invalid option.");
            }
        } catch (GymManagementException e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    private static void updateMember() {
        System.out.println("\n--- UPDATE MEMBER DETAILS ---");
        int id = readInt("Enter Member ID to update: ");

        try {
            Member existing = memberService.getMemberById(id);
            System.out.println("Updating member: " + existing.getName() + " (Press Enter to keep current value)");

            System.out.print("Name [" + existing.getName() + "]: ");
            String name = scanner.nextLine().trim();

            System.out.print("Age [" + existing.getAge() + "]: ");
            String ageStr = scanner.nextLine().trim();
            int age = ageStr.isEmpty() ? existing.getAge() : Integer.parseInt(ageStr);

            System.out.print("Gender [" + existing.getGender() + "]: ");
            String gender = scanner.nextLine().trim();

            System.out.print("Phone [" + existing.getPhone() + "]: ");
            String phone = scanner.nextLine().trim();

            System.out.print("Email [" + existing.getEmail() + "]: ");
            String email = scanner.nextLine().trim();

            System.out.print("Status (ACTIVE/INACTIVE/EXPIRED) [" + existing.getStatus() + "]: ");
            String status = scanner.nextLine().trim();

            boolean updated = memberService.updateMemberDetails(id, name, age, gender, phone, email, status);
            if (updated) {
                System.out.println("\n[SUCCESS] Member details updated!");
            } else {
                System.out.println("[ERROR] Failed to update member.");
            }
        } catch (Exception e) {
            System.out.println("[ERROR] Update failed: " + e.getMessage());
        }
    }

    private static void deleteOrDeactivateMember() {
        System.out.println("\n--- DELETE / DEACTIVATE MEMBER ---");
        int id = readInt("Enter Member ID: ");
        System.out.println("1. Deactivate Member (Set Status = INACTIVE)");
        System.out.println("2. Permanently Delete Member Record");
        int opt = readInt("Choice (1-2): ");

        try {
            if (opt == 1) {
                boolean success = memberService.deactivateMember(id);
                if (success) System.out.println("\n[SUCCESS] Member deactivated successfully.");
            } else if (opt == 2) {
                boolean success = memberService.deleteMember(id);
                if (success) System.out.println("\n[SUCCESS] Member record deleted permanently.");
            }
        } catch (Exception e) {
            System.out.println("[ERROR] Operation failed: " + e.getMessage());
        }
    }

    private static void handleMembershipPlanManagement() {
        while (true) {
            System.out.println("\n--- MEMBERSHIP PLAN MANAGEMENT ---");
            System.out.println("1. Add New Membership Plan");
            System.out.println("2. View All Plans");
            System.out.println("3. Update Membership Plan");
            System.out.println("4. Delete Membership Plan");
            System.out.println("5. Back to Main Menu");
            int choice = readInt("Select option (1-5): ");

            switch (choice) {
                case 1 -> addPlan();
                case 2 -> viewAllPlans();
                case 3 -> updatePlan();
                case 4 -> deletePlan();
                case 5 -> { return; }
                default -> System.out.println("[ERROR] Invalid choice!");
            }
        }
    }

    private static void addPlan() {
        System.out.println("\n--- ADD MEMBERSHIP PLAN ---");
        try {
            System.out.print("Plan Name (e.g. Annual VIP): ");
            String name = scanner.nextLine().trim();

            int months = readInt("Duration in Months: ");
            double price = readDouble("Price ($): ");

            System.out.print("Description: ");
            String desc = scanner.nextLine().trim();

            MembershipPlan plan = planService.createPlan(name, months, price, desc);
            System.out.println("\n[SUCCESS] Membership plan created with Plan ID: " + plan.getPlanId());
        } catch (Exception e) {
            System.out.println("[ERROR] Failed to add plan: " + e.getMessage());
        }
    }

    private static void viewAllPlans() {
        System.out.println("\n--- MEMBERSHIP PLANS ---");
        try {
            List<MembershipPlan> plans = planService.getAllPlans();
            ConsoleTableFormatter table = new ConsoleTableFormatter("Plan ID", "Plan Name", "Duration (Months)", "Price ($)", "Description");
            for (MembershipPlan p : plans) {
                table.addRow(
                        String.valueOf(p.getPlanId()),
                        p.getPlanName(),
                        String.valueOf(p.getDurationMonths()),
                        String.format("%.2f", p.getPrice()),
                        p.getDescription()
                );
            }
            table.print();
        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    private static void updatePlan() {
        System.out.println("\n--- UPDATE MEMBERSHIP PLAN ---");
        int planId = readInt("Enter Plan ID to update: ");
        try {
            MembershipPlan existing = planService.getPlanById(planId);
            System.out.println("Updating plan: " + existing.getPlanName() + " (Press Enter to keep current value)");

            System.out.print("Plan Name [" + existing.getPlanName() + "]: ");
            String name = scanner.nextLine().trim();

            System.out.print("Duration Months [" + existing.getDurationMonths() + "]: ");
            String monthsStr = scanner.nextLine().trim();
            int months = monthsStr.isEmpty() ? existing.getDurationMonths() : Integer.parseInt(monthsStr);

            System.out.print("Price [" + existing.getPrice() + "]: ");
            String priceStr = scanner.nextLine().trim();
            double price = priceStr.isEmpty() ? existing.getPrice() : Double.parseDouble(priceStr);

            System.out.print("Description [" + existing.getDescription() + "]: ");
            String desc = scanner.nextLine().trim();

            boolean ok = planService.updatePlan(planId, name, months, price, desc);
            if (ok) System.out.println("\n[SUCCESS] Plan updated successfully.");
        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    private static void deletePlan() {
        System.out.println("\n--- DELETE MEMBERSHIP PLAN ---");
        int planId = readInt("Enter Plan ID to delete: ");
        try {
            boolean ok = planService.deletePlan(planId);
            if (ok) System.out.println("\n[SUCCESS] Plan deleted successfully.");
        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    private static void handleMembershipEnrollment() {
        while (true) {
            System.out.println("\n--- MEMBERSHIP ENROLLMENT ---");
            System.out.println("1. Enroll Member in a Plan");
            System.out.println("2. Renew Member Membership");
            System.out.println("3. View Active & Expired Memberships");
            System.out.println("4. Back to Main Menu");
            int choice = readInt("Select option (1-4): ");

            switch (choice) {
                case 1 -> enrollMember();
                case 2 -> renewMembership();
                case 3 -> viewMemberships();
                case 4 -> { return; }
                default -> System.out.println("[ERROR] Invalid choice!");
            }
        }
    }

    private static void enrollMember() {
        System.out.println("\n--- ENROLL MEMBER IN PLAN ---");
        int memberId = readInt("Enter Member ID: ");
        viewAllPlans();
        int planId = readInt("Enter Plan ID to assign: ");

        try {
            Membership ms = membershipService.enrollMember(memberId, planId, LocalDate.now());
            System.out.println("\n[SUCCESS] Member enrolled successfully!");
            System.out.println("Membership ID : " + ms.getMembershipId());
            System.out.println("Start Date    : " + ms.getStartDate());
            System.out.println("Expiry Date   : " + ms.getEndDate());
            System.out.println("Status        : " + ms.getStatus());
        } catch (Exception e) {
            System.out.println("[ERROR] Enrollment failed: " + e.getMessage());
        }
    }

    private static void renewMembership() {
        System.out.println("\n--- RENEW MEMBERSHIP ---");
        int memberId = readInt("Enter Member ID: ");
        viewAllPlans();
        int planId = readInt("Select Plan ID for Renewal: ");

        try {
            Membership ms = membershipService.renewMembership(memberId, planId);
            System.out.println("\n[SUCCESS] Membership renewed successfully!");
            System.out.println("Membership ID : " + ms.getMembershipId());
            System.out.println("Start Date    : " + ms.getStartDate());
            System.out.println("New Expiry    : " + ms.getEndDate());
        } catch (Exception e) {
            System.out.println("[ERROR] Renewal failed: " + e.getMessage());
        }
    }

    private static void viewMemberships() {
        System.out.println("\n--- MEMBERSHIP LIST ---");
        try {
            List<Membership> list = membershipService.getAllMemberships();
            ConsoleTableFormatter table = new ConsoleTableFormatter("ID", "Member Name", "Plan Name", "Start Date", "End Date", "Status");
            for (Membership ms : list) {
                table.addRow(
                        String.valueOf(ms.getMembershipId()),
                        ms.getMemberName() != null ? ms.getMemberName() : "Member #" + ms.getMemberId(),
                        ms.getPlanName() != null ? ms.getPlanName() : "Plan #" + ms.getPlanId(),
                        ms.getStartDate().toString(),
                        ms.getEndDate().toString(),
                        ms.getStatus().name()
                );
            }
            table.print();
        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    private static void handlePaymentManagement() {
        while (true) {
            System.out.println("\n--- PAYMENT MANAGEMENT ---");
            System.out.println("1. Record Membership Payment");
            System.out.println("2. Display Payment History by Member");
            System.out.println("3. Display All Payments");
            System.out.println("4. Generate Payment Receipt");
            System.out.println("5. Back to Main Menu");
            int choice = readInt("Select option (1-5): ");

            switch (choice) {
                case 1 -> recordPayment();
                case 2 -> viewMemberPaymentHistory();
                case 3 -> viewAllPayments();
                case 4 -> generateReceipt();
                case 5 -> { return; }
                default -> System.out.println("[ERROR] Invalid choice!");
            }
        }
    }

    private static void recordPayment() {
        System.out.println("\n--- RECORD PAYMENT ---");
        int memberId = readInt("Enter Member ID: ");
        int membershipId = readInt("Enter Membership ID: ");
        double amount = readDouble("Payment Amount ($): ");

        System.out.print("Payment Method (CASH / CREDIT_CARD / DEBIT_CARD / UPI / NET_BANKING): ");
        String method = scanner.nextLine().trim();

        System.out.print("Notes / Reference: ");
        String notes = scanner.nextLine().trim();

        try {
            Payment p = paymentService.processPayment(membershipId, memberId, amount, method, notes);
            System.out.println("\n[SUCCESS] Payment recorded successfully!");
            System.out.println("Payment ID   : " + p.getPaymentId());
            System.out.println("Amount Paid  : $" + String.format("%.2f", p.getAmount()));
            System.out.println("Method       : " + p.getPaymentMethod());
            System.out.println("Timestamp    : " + p.getPaymentDate());
        } catch (Exception e) {
            System.out.println("[ERROR] Payment Failed: " + e.getMessage());
        }
    }

    private static void viewMemberPaymentHistory() {
        System.out.println("\n--- MEMBER PAYMENT HISTORY ---");
        int memberId = readInt("Enter Member ID: ");
        try {
            List<Payment> list = paymentService.getPaymentsByMember(memberId);
            ConsoleTableFormatter table = new ConsoleTableFormatter("Pay ID", "Member", "Amount ($)", "Method", "Date & Time", "Notes");
            for (Payment p : list) {
                table.addRow(
                        String.valueOf(p.getPaymentId()),
                        p.getMemberName() != null ? p.getMemberName() : "ID #" + memberId,
                        String.format("%.2f", p.getAmount()),
                        p.getPaymentMethod().name(),
                        p.getPaymentDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                        p.getNotes()
                );
            }
            table.print();
        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    private static void viewAllPayments() {
        System.out.println("\n--- ALL PAYMENTS LOG ---");
        try {
            List<Payment> list = paymentService.getAllPayments();
            ConsoleTableFormatter table = new ConsoleTableFormatter("Pay ID", "Member", "Amount ($)", "Method", "Date & Time", "Notes");
            for (Payment p : list) {
                table.addRow(
                        String.valueOf(p.getPaymentId()),
                        p.getMemberName() != null ? p.getMemberName() : "ID #" + p.getMemberId(),
                        String.format("%.2f", p.getAmount()),
                        p.getPaymentMethod().name(),
                        p.getPaymentDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                        p.getNotes()
                );
            }
            table.print();
        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    private static void generateReceipt() {
        System.out.println("\n--- GENERATE PAYMENT RECEIPT ---");
        int paymentId = readInt("Enter Payment ID: ");
        try {
            Payment p = paymentService.getPaymentReceipt(paymentId);
            System.out.println("========================================");
            System.out.println("           PAYMENT RECEIPT              ");
            System.out.println("========================================");
            System.out.println(" Receipt No   : REC-" + p.getPaymentId());
            System.out.println(" Member Name  : " + p.getMemberName());
            System.out.println(" Member ID    : " + p.getMemberId());
            System.out.println(" Membership ID: " + p.getMembershipId());
            System.out.println(" Amount Paid  : $" + String.format("%.2f", p.getAmount()));
            System.out.println(" Mode         : " + p.getPaymentMethod());
            System.out.println(" Date & Time  : " + p.getPaymentDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            System.out.println(" Notes        : " + p.getNotes());
            System.out.println(" Status       : PAID / VERIFIED");
            System.out.println("========================================");
        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    private static void handleAttendanceManagement() {
        while (true) {
            System.out.println("\n--- ATTENDANCE MANAGEMENT ---");
            System.out.println("1. Mark Member Attendance (Check-In)");
            System.out.println("2. View Member Attendance History");
            System.out.println("3. View Daily Attendance Log");
            System.out.println("4. Check Member Attendance Count");
            System.out.println("5. Back to Main Menu");
            int choice = readInt("Select option (1-5): ");

            switch (choice) {
                case 1 -> markAttendance();
                case 2 -> viewMemberAttendance();
                case 3 -> viewDailyAttendance();
                case 4 -> checkAttendanceCount();
                case 5 -> { return; }
                default -> System.out.println("[ERROR] Invalid choice!");
            }
        }
    }

    private static void markAttendance() {
        System.out.println("\n--- MARK ATTENDANCE ---");
        int memberId = readInt("Enter Member ID for check-in: ");
        try {
            Attendance a = attendanceService.checkInMember(memberId);
            System.out.println("\n[SUCCESS] Attendance marked successfully!");
            System.out.println("Member Name : " + a.getMemberName());
            System.out.println("Date        : " + a.getAttendanceDate());
            System.out.println("Check-in    : " + a.getCheckInTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        } catch (GymManagementException e) {
            System.out.println("[ERROR] Check-in failed: " + e.getMessage());
        }
    }

    private static void viewMemberAttendance() {
        System.out.println("\n--- MEMBER ATTENDANCE LOG ---");
        int memberId = readInt("Enter Member ID: ");
        try {
            List<Attendance> list = attendanceService.getMemberAttendanceHistory(memberId);
            ConsoleTableFormatter table = new ConsoleTableFormatter("Attendance ID", "Member", "Date", "Check-in Time");
            for (Attendance a : list) {
                table.addRow(
                        String.valueOf(a.getAttendanceId()),
                        a.getMemberName() != null ? a.getMemberName() : "ID #" + memberId,
                        a.getAttendanceDate().toString(),
                        a.getCheckInTime().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
                );
            }
            table.print();
        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    private static void viewDailyAttendance() {
        System.out.println("\n--- DAILY ATTENDANCE LOG ---");
        System.out.print("Enter Date (YYYY-MM-DD) or press Enter for Today: ");
        String dateStr = scanner.nextLine().trim();

        LocalDate date = LocalDate.now();
        if (!dateStr.isEmpty()) {
            try {
                date = InputValidator.validateDate(dateStr, "Attendance Date");
            } catch (Exception e) {
                System.out.println("[ERROR] " + e.getMessage());
                return;
            }
        }

        try {
            List<Attendance> list = attendanceService.getDailyAttendanceLog(date);
            ConsoleTableFormatter table = new ConsoleTableFormatter("Attendance ID", "Member Name", "Date", "Check-In Time");
            for (Attendance a : list) {
                table.addRow(
                        String.valueOf(a.getAttendanceId()),
                        a.getMemberName(),
                        a.getAttendanceDate().toString(),
                        a.getCheckInTime().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
                );
            }
            table.print();
        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    private static void checkAttendanceCount() {
        System.out.println("\n--- ATTENDANCE COUNT ---");
        int memberId = readInt("Enter Member ID: ");
        try {
            int count = attendanceService.getAttendanceCountForMember(memberId);
            Member m = memberService.getMemberById(memberId);
            System.out.println("Member Name : " + m.getName());
            System.out.println("Total Visits: " + count + " check-ins recorded.");
        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    private static void handleTrainerManagement() {
        while (true) {
            System.out.println("\n--- TRAINER MANAGEMENT ---");
            System.out.println("1. Add New Trainer");
            System.out.println("2. View All Trainers");
            System.out.println("3. Update Trainer Information");
            System.out.println("4. Assign Trainer to Member");
            System.out.println("5. Remove Trainer Assignment");
            System.out.println("6. View Active Trainer Assignments");
            System.out.println("7. Back to Main Menu");
            int choice = readInt("Select option (1-7): ");

            switch (choice) {
                case 1 -> addTrainer();
                case 2 -> viewTrainers();
                case 3 -> updateTrainer();
                case 4 -> assignTrainer();
                case 5 -> removeTrainerAssignment();
                case 6 -> viewTrainerAssignments();
                case 7 -> { return; }
                default -> System.out.println("[ERROR] Invalid choice!");
            }
        }
    }

    private static void addTrainer() {
        System.out.println("\n--- ADD TRAINER ---");
        try {
            System.out.print("Trainer Name: ");
            String name = scanner.nextLine().trim();

            System.out.print("Specialization: ");
            String spec = scanner.nextLine().trim();

            System.out.print("Phone: ");
            String phone = scanner.nextLine().trim();

            System.out.print("Email: ");
            String email = scanner.nextLine().trim();

            Trainer t = trainerService.addTrainer(name, spec, phone, email, true);
            System.out.println("\n[SUCCESS] Trainer added successfully with ID: " + t.getTrainerId());
        } catch (Exception e) {
            System.out.println("[ERROR] Failed to add trainer: " + e.getMessage());
        }
    }

    private static void viewTrainers() {
        System.out.println("\n--- TRAINERS LIST ---");
        try {
            List<Trainer> trainers = trainerService.getAllTrainers();
            ConsoleTableFormatter table = new ConsoleTableFormatter("ID", "Name", "Specialization", "Phone", "Email", "Available?");
            for (Trainer t : trainers) {
                table.addRow(
                        String.valueOf(t.getTrainerId()),
                        t.getName(),
                        t.getSpecialization(),
                        t.getPhone(),
                        t.getEmail(),
                        t.isAvailable() ? "YES" : "NO"
                );
            }
            table.print();
        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    private static void updateTrainer() {
        System.out.println("\n--- UPDATE TRAINER ---");
        int id = readInt("Enter Trainer ID to update: ");
        try {
            Trainer existing = trainerService.getTrainerById(id);
            System.out.println("Updating Trainer: " + existing.getName() + " (Press Enter to keep current value)");

            System.out.print("Name [" + existing.getName() + "]: ");
            String name = scanner.nextLine().trim();

            System.out.print("Specialization [" + existing.getSpecialization() + "]: ");
            String spec = scanner.nextLine().trim();

            System.out.print("Phone [" + existing.getPhone() + "]: ");
            String phone = scanner.nextLine().trim();

            System.out.print("Email [" + existing.getEmail() + "]: ");
            String email = scanner.nextLine().trim();

            System.out.print("Is Available? (true/false) [" + existing.isAvailable() + "]: ");
            String availStr = scanner.nextLine().trim();
            Boolean avail = availStr.isEmpty() ? null : Boolean.parseBoolean(availStr);

            boolean ok = trainerService.updateTrainerInfo(id, name, spec, phone, email, avail);
            if (ok) System.out.println("\n[SUCCESS] Trainer information updated!");
        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    private static void assignTrainer() {
        System.out.println("\n--- ASSIGN TRAINER TO MEMBER ---");
        viewTrainers();
        int trainerId = readInt("Select Trainer ID: ");
        int memberId = readInt("Select Member ID: ");

        try {
            TrainerAssignment ta = trainerService.assignTrainer(trainerId, memberId);
            System.out.println("\n[SUCCESS] Assigned Trainer " + ta.getTrainerName() + " to Member " + ta.getMemberName());
        } catch (Exception e) {
            System.out.println("[ERROR] Assignment failed: " + e.getMessage());
        }
    }

    private static void removeTrainerAssignment() {
        System.out.println("\n--- REMOVE TRAINER ASSIGNMENT ---");
        int memberId = readInt("Enter Member ID to unassign trainer: ");
        try {
            boolean ok = trainerService.removeAssignment(memberId);
            if (ok) System.out.println("\n[SUCCESS] Trainer assignment removed.");
        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    private static void viewTrainerAssignments() {
        System.out.println("\n--- ACTIVE TRAINER ASSIGNMENTS ---");
        try {
            List<TrainerAssignment> list = trainerService.getActiveAssignments();
            ConsoleTableFormatter table = new ConsoleTableFormatter("Assignment ID", "Trainer Name", "Member Name", "Assigned Date", "Status");
            for (TrainerAssignment ta : list) {
                table.addRow(
                        String.valueOf(ta.getAssignmentId()),
                        ta.getTrainerName(),
                        ta.getMemberName(),
                        ta.getAssignedDate().toString(),
                        ta.getStatus()
                );
            }
            table.print();
        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    private static void handleReports() {
        while (true) {
            System.out.println("\n--- SYSTEM REPORTS & ANALYTICS ---");
            System.out.println("1. Executive Summary Overview");
            System.out.println("2. Expired Memberships Report");
            System.out.println("3. Upcoming Expiry Warning (Next 7 Days)");
            System.out.println("4. Monthly Revenue Analytics");
            System.out.println("5. Most Active Members (Top 5)");
            System.out.println("6. Trainer Workload & Member Counts");
            System.out.println("7. Back to Main Menu");
            int choice = readInt("Select report option (1-7): ");

            switch (choice) {
                case 1 -> displayExecutiveSummary();
                case 2 -> displayExpiredMemberships();
                case 3 -> displayUpcomingExpiries();
                case 4 -> displayMonthlyRevenue();
                case 5 -> displayMostActiveMembers();
                case 6 -> displayTrainerWorkload();
                case 7 -> { return; }
                default -> System.out.println("[ERROR] Invalid choice!");
            }
        }
    }

    private static void displayExecutiveSummary() {
        System.out.println("\n========================================");
        System.out.println("      EXECUTIVE SYSTEM SUMMARY          ");
        System.out.println("========================================");
        try {
            int totalMembers = reportService.getTotalMembersCount();
            int activeMembers = reportService.getActiveMembersCount();
            double totalRevenue = reportService.getTotalRevenue();

            System.out.println(" Total Registered Members : " + totalMembers);
            System.out.println(" Active Members           : " + activeMembers);
            System.out.println(" Inactive / Expired       : " + (totalMembers - activeMembers));
            System.out.println(" Total Lifetime Revenue   : $" + String.format("%.2f", totalRevenue));
            System.out.println("========================================");
        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    private static void displayExpiredMemberships() {
        System.out.println("\n--- EXPIRED MEMBERSHIPS REPORT ---");
        try {
            List<Membership> list = reportService.getExpiredMemberships();
            ConsoleTableFormatter table = new ConsoleTableFormatter("Membership ID", "Member Name", "Plan Name", "End Date", "Status");
            for (Membership m : list) {
                table.addRow(
                        String.valueOf(m.getMembershipId()),
                        m.getMemberName(),
                        m.getPlanName(),
                        m.getEndDate().toString(),
                        m.getStatus().name()
                );
            }
            table.print();
        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    private static void displayUpcomingExpiries() {
        System.out.println("\n--- UPCOMING EXPIRIES (NEXT 7 DAYS) ---");
        try {
            List<Membership> list = reportService.getUpcomingExpiries(7);
            ConsoleTableFormatter table = new ConsoleTableFormatter("Membership ID", "Member Name", "Plan Name", "Expiry Date", "Status");
            for (Membership m : list) {
                table.addRow(
                        String.valueOf(m.getMembershipId()),
                        m.getMemberName(),
                        m.getPlanName(),
                        m.getEndDate().toString(),
                        m.getStatus().name()
                );
            }
            table.print();
        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    private static void displayMonthlyRevenue() {
        System.out.println("\n--- MONTHLY REVENUE REPORT ---");
        try {
            Map<String, Double> revenue = reportService.getMonthlyRevenue();
            ConsoleTableFormatter table = new ConsoleTableFormatter("Year-Month", "Total Revenue ($)");
            for (Map.Entry<String, Double> entry : revenue.entrySet()) {
                table.addRow(entry.getKey(), String.format("%.2f", entry.getValue()));
            }
            table.print();
        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    private static void displayMostActiveMembers() {
        System.out.println("\n--- TOP MOST ACTIVE MEMBERS ---");
        try {
            Map<String, Integer> active = reportService.getMostActiveMembers(5);
            ConsoleTableFormatter table = new ConsoleTableFormatter("Rank", "Member Name", "Total Gym Check-Ins");
            int rank = 1;
            for (Map.Entry<String, Integer> entry : active.entrySet()) {
                table.addRow(String.valueOf(rank++), entry.getKey(), String.valueOf(entry.getValue()));
            }
            table.print();
        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    private static void displayTrainerWorkload() {
        System.out.println("\n--- TRAINER WORKLOAD & MEMBER COUNTS ---");
        try {
            Map<String, Integer> map = reportService.getTrainerMemberCounts();
            ConsoleTableFormatter table = new ConsoleTableFormatter("Trainer Name", "Assigned Members Count");
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                table.addRow(entry.getKey(), String.valueOf(entry.getValue()));
            }
            table.print();
        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("[ERROR] Invalid number format. Please enter an integer.");
            }
        }
    }

    private static double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println("[ERROR] Invalid input. Please enter a valid decimal number.");
            }
        }
    }
}
