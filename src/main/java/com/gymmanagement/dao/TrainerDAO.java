package com.gymmanagement.dao;

import com.gymmanagement.exception.DatabaseException;
import com.gymmanagement.model.Trainer;
import com.gymmanagement.model.TrainerAssignment;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface TrainerDAO {
    boolean addTrainer(Trainer trainer) throws DatabaseException;
    List<Trainer> getAllTrainers() throws DatabaseException;
    Optional<Trainer> getTrainerById(int trainerId) throws DatabaseException;
    boolean updateTrainer(Trainer trainer) throws DatabaseException;
    boolean updateAvailability(int trainerId, boolean available) throws DatabaseException;
    
    // Assignment methods
    boolean assignTrainerToMember(TrainerAssignment assignment) throws DatabaseException;
    boolean removeTrainerAssignment(int assignmentId) throws DatabaseException;
    List<TrainerAssignment> getActiveAssignments() throws DatabaseException;
    Optional<TrainerAssignment> getAssignmentForMember(int memberId) throws DatabaseException;
    Map<String, Integer> getTrainerMemberCounts() throws DatabaseException;
}
