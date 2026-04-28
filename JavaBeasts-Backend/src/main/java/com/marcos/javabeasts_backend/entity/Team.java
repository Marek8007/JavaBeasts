package com.marcos.javabeasts_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Teams")
@Data
@NoArgsConstructor
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Team_Id")
    private int teamId;

    @ManyToOne
    @JoinColumn(name = "User_Id")
    private User user;

    @Column(name = "Name")
    private String name;

    @Column(name = "Is_Active")
    private boolean active;
}
