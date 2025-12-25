package dev.matichelo.service.user.entity;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Hotel {
    private String id;
    private String name;
    private String description;
    private String address;
}
