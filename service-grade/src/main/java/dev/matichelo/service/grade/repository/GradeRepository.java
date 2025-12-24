package dev.matichelo.service.grade.repository;

import dev.matichelo.service.grade.document.Grade;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface GradeRepository extends MongoRepository<Grade, String> {
    List<Grade> findByUserId (String id);
    List<Grade> findByHotelId (String id);
}
