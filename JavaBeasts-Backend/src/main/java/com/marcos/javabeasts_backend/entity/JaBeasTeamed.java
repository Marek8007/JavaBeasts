package com.marcos.javabeasts_backend.entity;

import com.marcos.javabeasts_backend.entity.embeddables.JaBeaTeamedId;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name ="JaBeas_Teamed")
@Data
@NoArgsConstructor
public class JaBeasTeamed {

    @EmbeddedId
    private JaBeaTeamedId id;

    @ManyToOne
    @MapsId("teamId")
    @JoinColumn(name = "Team_Id")
    private Team team;

    @ManyToOne
    @JoinColumn(name = "JaBeas_Id")
    private JaBeas jaBeas;

    @ManyToOne
    @JoinColumn(name = "Move_1")
    private Move move1;

    @ManyToOne
    @JoinColumn(name = "Move_2")
    private Move move2;


    //Getter y setter de slot para tener más accesible desde fuera
    public Integer getSlot() {
        return id != null ? id.getSlot() : null;
    }

    public void setSlot(Integer slot) {
        if (id == null) {
            id = new JaBeaTeamedId();
        }
        id.setSlot(slot);
    }
}
