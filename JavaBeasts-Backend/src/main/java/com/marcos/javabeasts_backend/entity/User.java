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
    @Column(name = "User_Id")
    private int userId;

    @Column(name = "Username")
    private String username;

    @Column(name = "Username")
    private String password;

    @Column(name = "Matches_Won")
    private int matchesWon;

    @Column(name = "Matches_Won")
    private int matchesLost;
}
