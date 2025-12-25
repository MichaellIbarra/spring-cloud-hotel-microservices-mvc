package dev.matichelo.service.user.client;

import dev.matichelo.service.user.entity.Grade;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "${service.grade.name}")
public interface GradeClient {

    @GetMapping("/api/v1/grades")
    List<Grade> getAllGrades();

    @GetMapping("/api/v1/grades/users/{id}")
    List<Grade> getGradesByUserId(@PathVariable String id);
}
