package com.gymmanagement.service;

import com.gymmanagement.dao.*;
import com.gymmanagement.exception.*;
import com.gymmanagement.model.Member;
import com.gymmanagement.model.Trainer;
import com.gymmanagement.model.TrainerAssignment;
import com.gymmanagement.util.InputValidator;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TrainerService {

    private final TrainerDAO trainerDAO;
    private final MemberDAO memberDAO;

    public TrainerService() {
        this.trainerDAO = new TrainerDAOImpl();
        this.memberDAO = new MemberDAOImpl();
    }

    public TrainerService(TrainerDAO trainerDAO, MemberDAO memberDAO) {
        this.trainerDAO = trainerDAO;
        this.memberDAO = memberDAO;
    }

    public Trainer addTrainer(String name, String specialization, String phone, String email, boolean isAvailable) throws GymManagementException {
        InputValidator.validateNonEmpty(name, "Trainer Name");
        InputValidator.validateNonEmpty(specialization, "Specialization");
        String validPhone = InputValidator.validatePhone(phone);
        String validEmail = InputValidator.validateEmail(email);

        Trainer trainer = new Trainer(name, specialization, validPhone, validEmail, isAvailable);
        boolean success = trainerDAO.addTrainer(trainer);
        if (!success) {
            throw new DatabaseException("Failed to add new trainer.");
        }
        return trainer;
    }

    public List<Trainer> getAllTrainers() throws DatabaseException {
        return trainerDAO.getAllTrainers();
    }

    public Trainer getTrainerById(int trainerId) throws TrainerNotFoundException, DatabaseException {
        return trainerDAO.getTrainerById(trainerId)
                .orElseThrow(() -> new TrainerNotFoundException("Trainer with ID " + trainerId + " not found."));
    }

    public boolean updateTrainerInfo(int trainerId, String name, String specialization, String phone, String email, Boolean isAvailable) throws GymManagementException {
        Trainer trainer = getTrainerById(trainerId);

        if (name != null && !name.trim().isEmpty()) trainer.setName(name.trim());
        if (specialization != null && !specialization.trim().isEmpty()) trainer.setSpecialization(specialization.trim());
        if (phone != null && !phone.trim().isEmpty()) trainer.setPhone(InputValidator.validatePhone(phone));
        if (email != null && !email.trim().isEmpty()) trainer.setEmail(InputValidator.validateEmail(email));
        if (isAvailable != null) trainer.setAvailable(isAvailable);

        return trainerDAO.updateTrainer(trainer);
    }

    public TrainerAssignment assignTrainer(int trainerId, int memberId) throws GymManagementException {
        Trainer trainer = getTrainerById(trainerId);
        if (!trainer.isAvailable()) {
            throw new ValidationException("Trainer " + trainer.getName() + " is currently unavailable.");
        }

        Member member = memberDAO.getMemberById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("Member with ID " + memberId + " not found."));

        Optional<TrainerAssignment> existing = trainerDAO.getAssignmentForMember(memberId);
        if (existing.isPresent()) {
            throw new ValidationException("Member " + member.getName() + " already has an assigned trainer: " + existing.get().getTrainerName());
        }

        TrainerAssignment assignment = new TrainerAssignment(trainerId, memberId, LocalDate.now(), "ACTIVE");
        assignment.setTrainerName(trainer.getName());
        assignment.setMemberName(member.getName());

        boolean success = trainerDAO.assignTrainerToMember(assignment);
        if (!success) {
            throw new DatabaseException("Failed to assign trainer to member.");
        }
        return assignment;
    }

    public boolean removeAssignment(int memberId) throws GymManagementException {
        TrainerAssignment assignment = trainerDAO.getAssignmentForMember(memberId)
                .orElseThrow(() -> new ValidationException("No active trainer assignment found for member ID: " + memberId));
        return trainerDAO.removeTrainerAssignment(assignment.getAssignmentId());
    }

    public List<TrainerAssignment> getActiveAssignments() throws DatabaseException {
        return trainerDAO.getActiveAssignments();
    }

    public Map<String, Integer> getTrainerMemberCounts() throws DatabaseException {
        return trainerDAO.getTrainerMemberCounts();
    }
}
