package dev.matichelo.service.user.entity;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Grade {
    private String id;
    private String userId;
    private String hotelId;
    private int grade;
    private String feedback;

    private Hotel hotel;

}
