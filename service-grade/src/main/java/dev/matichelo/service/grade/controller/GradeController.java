package dev.matichelo.service.grade.controller;

import dev.matichelo.service.grade.document.Grade;
import dev.matichelo.service.grade.service.GradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/grades")
@RequiredArgsConstructor
public class GradeController {

    private final GradeService gradeService;

    @PostMapping
    public ResponseEntity<Grade> saveGrade(@RequestBody Grade grade){
        Grade savedGrade = gradeService.saveGrade(grade);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedGrade);
    }

    @GetMapping
    public ResponseEntity<List<Grade>> getAllGrades(){
        return ResponseEntity.ok(gradeService.getAllGrades());
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<List<Grade>> getGradesByUserId(@PathVariable String id){
        return ResponseEntity.ok(gradeService.getGradesByUserId(id));
    }

    @GetMapping("/hotels/{id}")
    public ResponseEntity<List<Grade>> getGradesByHotelId(@PathVariable String id){
        return new ResponseEntity<>(gradeService.getGradesByHotelId(id), HttpStatus.OK);
    }


}
