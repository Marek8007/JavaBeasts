package com.marcos.javabeasts_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "JaBeas_History")
@Data
@NoArgsConstructor
public class JaBeas_History {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "JaBeas_History_Id")
    private int jabeasHistoryId;

    @ManyToOne
    @JoinColumn(name = "Match_Id")
    private Match_History match;

    @ManyToOne
    @JoinColumn(name = "JaBeas_Id")
    private Jabea jabeas;

    @ManyToOne
    @JoinColumn(name = "Owner_Id")
    private User owner;


    private int move1;
    private int move2;
    private int slot;

}
