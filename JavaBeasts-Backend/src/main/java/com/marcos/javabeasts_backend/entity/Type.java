package com.marcos.javabeasts_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Types")
@Data
@NoArgsConstructor
public class Type {
    @Id
    @Column(name = "Type_Id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int typeId;

    @Column(name = "Type")
    private String type;

    @Column(name = "Description")
    private String description;
}
