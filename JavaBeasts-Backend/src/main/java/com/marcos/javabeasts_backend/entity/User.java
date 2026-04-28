package com.marcos.javabeasts_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Users")
@Data
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "User_Id", nullable = false)
    private int userId;

    @Column(name = "Username", nullable = false, unique = true, length = 25)
    private String username;

    @Column(name = "Password", nullable = false, length = 255)
    private String password;

    @Column(name = "Is_Logged", nullable = false)
    private boolean logged;

    @Column(name = "Matches_Won", nullable = false)
    private int matchesWon;

    @Column(name = "Matches_Lost", nullable = false)
    private int matchesLost;
}
