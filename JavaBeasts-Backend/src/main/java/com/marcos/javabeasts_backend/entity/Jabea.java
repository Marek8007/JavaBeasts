package com.marcos.javabeasts_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "JaBeas")
@Data
@NoArgsConstructor
public class Jabea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "JaBeas_Id")
    private int jabeasId;

    @Column(name = "Name")
    private String name;

    @Column(name = "Description")
    private String description;

    @Column(name = "Health")
    private int health;

    @Column(name = "Damage")
    private int damage;

    @Column(name = "Defence")
    private int defence;

    @Column(name = "Speed")
    private int speed;

    @ManyToOne
    @JoinColumn(name = "Type")
    private Type type;
}
