package dev.matichelo.service.grade.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "grades")
public class Grade {

    @Id
    private String id;
    private String userId;
    private String hotelId;
    private int grade;
    private String feedback;

}
