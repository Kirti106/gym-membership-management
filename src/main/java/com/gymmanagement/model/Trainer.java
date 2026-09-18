package com.gymmanagement.model;

public class Trainer {
    private int trainerId;
    private String name;
    private String specialization;
    private String phone;
    private String email;
    private boolean available;

    public Trainer() {}

    public Trainer(int trainerId, String name, String specialization, String phone, String email, boolean available) {
        this.trainerId = trainerId;
        this.name = name;
        this.specialization = specialization;
        this.phone = phone;
        this.email = email;
        this.available = available;
    }

    public Trainer(String name, String specialization, String phone, String email, boolean available) {
        this.name = name;
        this.specialization = specialization;
        this.phone = phone;
        this.email = email;
        this.available = available;
    }

    public int getTrainerId() { return trainerId; }
    public void setTrainerId(int trainerId) { this.trainerId = trainerId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    @Override
    public String toString() {
        return "Trainer{" +
                "trainerId=" + trainerId +
                ", name='" + name + '\'' +
                ", specialization='" + specialization + '\'' +
                ", phone='" + phone + '\'' +
                ", available=" + available +
                '}';
    }
}
