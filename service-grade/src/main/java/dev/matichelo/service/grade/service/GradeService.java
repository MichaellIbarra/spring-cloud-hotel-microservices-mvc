package dev.matichelo.service.grade.service;

import dev.matichelo.service.grade.document.Grade;

import java.util.List;

public interface GradeService {
    Grade saveGrade(Grade grade);
    List<Grade> getAllGrades();
    List<Grade> getGradesByUserId(String id);
    List<Grade> getGradesByHotelId(String id);

    Grade upateGrade(Grade grade);
    void deleteGrade(String id);

}
