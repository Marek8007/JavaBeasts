package com.marcos.javabeasts_backend.controllers;

import com.marcos.javabeasts_backend.dto.battle.BattleSnapshotResponse;
import com.marcos.javabeasts_backend.services.battle.BattleSetupService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/battle")
public class BattleController {

    private final BattleSetupService battleSetupService;

    public BattleController(BattleSetupService battleSetupService) {
        this.battleSetupService = battleSetupService;
    }

    @GetMapping("/snapshot")
    public BattleSnapshotResponse getInitialSnapshot(@RequestParam String roomCode) {
        return battleSetupService.buildInitialSnapshot(roomCode);
    }
}
