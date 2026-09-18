package com.gymmanagement.model;

import java.time.LocalDate;

public class Member {
    private int memberId;
    private String name;
    private int age;
    private Gender gender;
    private String phone;
    private String email;
    private LocalDate joinDate;
    private MembershipStatus status;

    public Member() {}

    public Member(int memberId, String name, int age, Gender gender, String phone, String email, LocalDate joinDate, MembershipStatus status) {
        this.memberId = memberId;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.phone = phone;
        this.email = email;
        this.joinDate = joinDate;
        this.status = status;
    }

    public Member(String name, int age, Gender gender, String phone, String email, LocalDate joinDate, MembershipStatus status) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.phone = phone;
        this.email = email;
        this.joinDate = joinDate;
        this.status = status;
    }

    public int getMemberId() { return memberId; }
    public void setMemberId(int memberId) { this.memberId = memberId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public Gender getGender() { return gender; }
    public void setGender(Gender gender) { this.gender = gender; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDate getJoinDate() { return joinDate; }
    public void setJoinDate(LocalDate joinDate) { this.joinDate = joinDate; }

    public MembershipStatus getStatus() { return status; }
    public void setStatus(MembershipStatus status) { this.status = status; }

    @Override
    public String toString() {
        return "Member{" +
                "id=" + memberId +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", gender=" + gender +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", joinDate=" + joinDate +
                ", status=" + status +
                '}';
    }
}
