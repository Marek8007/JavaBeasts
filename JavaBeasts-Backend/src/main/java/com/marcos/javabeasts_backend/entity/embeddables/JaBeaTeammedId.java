package com.marcos.javabeasts_backend.entity.embeddables;

import com.marcos.javabeasts_backend.entity.Team;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
public class JaBeaTeammedId implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "Team_Id")
    private Integer teamId;

    @Column(name = "Slot")
    private Integer slot;


}