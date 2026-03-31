package com.marcos.javabeasts_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Matches_History")
@Data
@NoArgsConstructor
public class Match_History {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Match_Id")
    private int matchId;

    @ManyToOne
    @JoinColumn(name = "Winner_Id")
    private User winner;

    @ManyToOne
    @JoinColumn(name = "Loser_Id")
    private User loser;

    @Column(name = "Turns")
    private int turns;
}
