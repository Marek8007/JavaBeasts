package com.marcos.javabeasts_backend.battle;

import com.marcos.javabeasts_backend.entity.JaBeasTeamed;
import lombok.Data;

@Data
public class JaBeaCombat {
    private JaBeasTeamed jabea;
    private String statusEffects;
    private Integer healthRemaining;


}
