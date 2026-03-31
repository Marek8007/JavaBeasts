package com.marcos.javabeasts_backend.entity;

import com.marcos.javabeasts_backend.entity.embeddables.JaBeaTeammedId;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name ="JaBeas_Teammed")
@Data
@NoArgsConstructor
public class JaBeasTeammed {

    @EmbeddedId
    private JaBeaTeammedId id;

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

    public Integer getSlot() {
        return id != null ? id.getSlot() : null;
    }

    public void setSlot(Integer slot) {
        if (id == null) {
            id = new JaBeaTeammedId();
        }
        id.setSlot(slot);
    }
}
