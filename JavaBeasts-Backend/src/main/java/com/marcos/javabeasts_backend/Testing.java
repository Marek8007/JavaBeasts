package com.marcos.javabeasts_backend;

import com.marcos.javabeasts_backend.battle.Combat;
import com.marcos.javabeasts_backend.battle.services.ActionManagerService;
import com.marcos.javabeasts_backend.entity.JaBeas;
import com.marcos.javabeasts_backend.entity.JaBeasTeamed;
import com.marcos.javabeasts_backend.entity.Move;
import com.marcos.javabeasts_backend.entity.Type;
import com.marcos.javabeasts_backend.repositories.JaBeaRepository;
import com.marcos.javabeasts_backend.repositories.JaBeasTeamedRepository;
import com.marcos.javabeasts_backend.repositories.MoveRepository;
import com.marcos.javabeasts_backend.repositories.TypeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class Testing implements CommandLineRunner {

    private final MoveRepository moveRepo;
    private final TypeRepository typeRepo;
    private final JaBeaRepository jaBeaRepo;
    private final JaBeasTeamedRepository jaBeaTeamedRepo;
    private final ActionManagerService ams;

    public Testing(MoveRepository moveRepo, TypeRepository typeRepo, JaBeaRepository jaBeaRepo, JaBeasTeamedRepository jaBeaTeamedRepo, ActionManagerService ams) {
        this.moveRepo = moveRepo;
        this.typeRepo = typeRepo;
        this.jaBeaRepo = jaBeaRepo;
        this.jaBeaTeamedRepo = jaBeaTeamedRepo;
        this.ams = ams;
    }

    @Override
    public void run(String... args) {

        JaBeasTeamed jbt1 = jaBeaTeamedRepo.findByIdTeamIdAndIdSlot(1, 1);
        JaBeasTeamed jbt2 = jaBeaTeamedRepo.findByIdTeamIdAndIdSlot(2, 1);

        System.out.println(jbt1);
        System.out.println(jbt2);

        Combat combat = new Combat(jbt1, jbt2, ams);

        combat.play();
    }
}
