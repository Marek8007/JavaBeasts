package com.marcos.javabeasts_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "JaBeas_History")
@Data
@NoArgsConstructor
public class JaBeasHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "JaBeas_History_Id")
    private int jabeasHistoryId;

    @ManyToOne
    @JoinColumn(name = "Match_Id")
    private MatchHistory match;

    @ManyToOne
    @JoinColumn(name = "JaBeas_Id")
    private JaBeas jabeas;

    @ManyToOne
    @JoinColumn(name = "Owner_Id")
    private User owner;

    @ManyToOne
    @JoinColumn(name = "Move_1")
    private Move move1;

    @ManyToOne
    @JoinColumn(name = "Move_2")
    private Move move2;

    @Column(name = "Slot")
    private int slot;

}
