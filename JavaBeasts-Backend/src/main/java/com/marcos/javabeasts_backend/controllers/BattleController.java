package com.marcos.javabeasts_backend.controllers;

import com.marcos.javabeasts_backend.dto.battle.BattleActionRequest;
import com.marcos.javabeasts_backend.dto.battle.BattleActionSubmissionResponse;
import com.marcos.javabeasts_backend.dto.battle.BattleSnapshotResponse;
import com.marcos.javabeasts_backend.services.battle.BattleSessionService;
import com.marcos.javabeasts_backend.services.battle.BattleSetupService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/battle")
public class BattleController {

    private final BattleSetupService battleSetupService;
    private final BattleSessionService battleSessionService;

    public BattleController(BattleSetupService battleSetupService, BattleSessionService battleSessionService) {
        this.battleSetupService = battleSetupService;
        this.battleSessionService = battleSessionService;
    }

    @GetMapping("/snapshot")
    public BattleSnapshotResponse getInitialSnapshot(@RequestParam String roomCode) {
        return battleSetupService.buildInitialSnapshot(roomCode);
    }

    @PostMapping("/action")
    public BattleActionSubmissionResponse submitAction(@RequestBody BattleActionRequest request) {
        return battleSessionService.submitAction(request);
    }
}
