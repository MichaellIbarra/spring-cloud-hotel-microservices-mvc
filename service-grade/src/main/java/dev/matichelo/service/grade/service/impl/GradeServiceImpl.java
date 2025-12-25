package dev.matichelo.service.grade.service.impl;

import dev.matichelo.service.grade.document.Grade;
import dev.matichelo.service.grade.repository.GradeRepository;
import dev.matichelo.service.grade.service.GradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GradeServiceImpl implements GradeService {

    private final GradeRepository gradeRepository;

    @Override
    public Grade saveGrade(Grade grade) {
        return gradeRepository.save(grade);
    }

    @Override
    public List<Grade> getAllGrades() {
        return gradeRepository.findAll();
    }

    @Override
    public List<Grade> getGradesByUserId(String id) {
        return gradeRepository.findByUserId(id);
    }

    @Override
    public List<Grade> getGradesByHotelId(String id) {
        return gradeRepository.findByHotelId(id);
    }

    @Override
    public Grade upateGrade(Grade grade) {
        return gradeRepository.findById(grade.getId())
                .map(existing ->{
                    existing.setUserId(grade.getUserId());
                    existing.setHotelId(grade.getHotelId());
                    existing.setGrade(grade.getGrade());
                    existing.setFeedback(grade.getFeedback());
                    return gradeRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Grade not found with id: " + grade.getId()));
    }

    @Override
    public void deleteGrade(String id) {
        gradeRepository.deleteById(id);
    }
}
