package com.gymmanagement.dao;

import com.gymmanagement.exception.DatabaseException;
import com.gymmanagement.model.Attendance;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface AttendanceDAO {
    boolean markAttendance(Attendance attendance) throws DatabaseException;
    boolean isAttendanceMarked(int memberId, LocalDate date) throws DatabaseException;
    List<Attendance> getAttendanceByMemberId(int memberId) throws DatabaseException;
    List<Attendance> getAttendanceByDate(LocalDate date) throws DatabaseException;
    int getAttendanceCountForMember(int memberId) throws DatabaseException;
    Map<String, Integer> getMostActiveMembers(int limit) throws DatabaseException;
}
