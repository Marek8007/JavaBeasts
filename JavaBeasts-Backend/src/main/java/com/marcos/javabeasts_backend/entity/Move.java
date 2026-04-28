package com.marcos.javabeasts_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Moves")
@Data
@NoArgsConstructor
public class Move {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Move_Id")
    private int moveId;

    @ManyToOne
    @JoinColumn(name = "Type_Id")
    private Type type;

    @Column(name = "Name")
    private String name;

    @Column(name = "Description")
    private String description;

    @Column(name = "Damage")
    private int damage;

    @Column(name = "Special_Effect")
    private String specialEffect;

    @Column(name = "Accuracy")
    private int accuracy;

    @ManyToOne
    @JoinColumn(name = "Unique_JaBeas_Id")
    private JaBeas uniqueJabea;

}
