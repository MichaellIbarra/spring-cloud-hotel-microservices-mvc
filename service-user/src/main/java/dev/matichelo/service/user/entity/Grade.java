package dev.matichelo.service.user.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Grade {
    private String id;
    private String userId;
    private String hotelId;
    private int grade;
    private String feedback;

    private Hotel hotel;

}
