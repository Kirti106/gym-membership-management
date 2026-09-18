USE gym_management;

INSERT INTO admins (username, password, full_name, email)
VALUES ('admin', 'admin123', 'System Admin', 'admin@gymmanagement.com')
ON DUPLICATE KEY UPDATE full_name = VALUES(full_name);

INSERT INTO membership_plans (plan_name, duration_months, price, description)
VALUES 
('Monthly Basic', 1, 49.99, 'Access to gym floor and basic cardio equipment'),
('Quarterly Standard', 3, 129.99, 'Access to gym floor, locker room, and 1 group class'),
('Bi-Annual Pro', 6, 239.99, 'Full gym access, group classes, and sauna'),
('Annual VIP', 12, 399.99, 'Unlimited access, personal trainer sessions, and free smoothie')
ON DUPLICATE KEY UPDATE price = VALUES(price);

INSERT INTO trainers (name, specialization, phone, email, is_available)
VALUES 
('John Cutter', 'Bodybuilding & Heavy Lifting', '9876543210', 'john.cutter@gym.com', 1),
('Sarah Jenkins', 'Cardio & HIIT Fitness', '9876543211', 'sarah.jenkins@gym.com', 1),
('Mike Ross', 'Yoga & Rehabilitation', '9876543212', 'mike.ross@gym.com', 1)
ON DUPLICATE KEY UPDATE name = VALUES(name);

INSERT INTO members (name, age, gender, phone, email, join_date, status)
VALUES 
('Alice Smith', 28, 'FEMALE', '9123456780', 'alice.smith@example.com', '2026-01-10', 'ACTIVE'),
('Bob Johnson', 35, 'MALE', '9123456781', 'bob.johnson@example.com', '2026-02-15', 'ACTIVE'),
('Charlie Brown', 22, 'MALE', '9123456782', 'charlie.brown@example.com', '2025-08-01', 'EXPIRED')
ON DUPLICATE KEY UPDATE name = VALUES(name);

INSERT INTO memberships (member_id, plan_id, start_date, end_date, status)
VALUES 
(1, 2, '2026-01-10', '2026-04-10', 'ACTIVE'),
(2, 4, '2026-02-15', '2027-02-15', 'ACTIVE'),
(3, 1, '2025-08-01', '2025-09-01', 'EXPIRED');

INSERT INTO payments (membership_id, member_id, amount, payment_date, payment_method, notes)
VALUES 
(1, 1, 129.99, '2026-01-10 10:30:00', 'CREDIT_CARD', 'Paid full amount'),
(2, 2, 399.99, '2026-02-15 14:15:00', 'UPI', 'Annual VIP payment'),
(3, 3, 49.99, '2025-08-01 09:00:00', 'CASH', 'Monthly Basic payment');

INSERT INTO attendance (member_id, attendance_date, check_in_time)
VALUES 
(1, CURRENT_DATE(), NOW()),
(2, CURRENT_DATE(), NOW());

INSERT INTO trainer_assignments (trainer_id, member_id, assigned_date, status)
VALUES 
(1, 1, '2026-01-12', 'ACTIVE');
