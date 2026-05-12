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
    @Column(name = "Team_Id", nullable = false)
    private int teamId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "User_Id", nullable = false)
    private User user;

    @Column(name = "Name", nullable = false, length = 50)
    private String name;

    @Column(name = "Icon_Name", nullable = false, length = 60)
    private String iconName;

    @Column(name = "Is_Active", nullable = false)
    private boolean active;
}
