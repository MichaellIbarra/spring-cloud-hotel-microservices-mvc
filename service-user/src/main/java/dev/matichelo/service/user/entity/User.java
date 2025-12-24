package dev.matichelo.service.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
// Entity está mapeando la clase User a una tabla en la base de datos
@Entity
// Tabla especificando el nombre de la tabla en la base de datos
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "name", nullable = false, length = 20)
    private String name;
    @Column(name = "email", nullable = false, length = 50, unique = true)
    private String email;
    @Column(name = "info", length = 100)
    private String info;

    @Transient // Indica que este campo no se mapeará a una columna en la base de datos
    private List<Grade> grades = new ArrayList<>();

}
