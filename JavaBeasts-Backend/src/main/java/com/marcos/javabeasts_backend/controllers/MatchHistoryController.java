package com.marcos.javabeasts_backend.controllers;

import com.marcos.javabeasts_backend.dto.history.MatchHistoryResponse;
import com.marcos.javabeasts_backend.services.history.MatchHistoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/matches")
public class MatchHistoryController {

    private final MatchHistoryService matchHistoryService;

    public MatchHistoryController(MatchHistoryService matchHistoryService) {
        this.matchHistoryService = matchHistoryService;
    }

    @GetMapping("/user/{userId}")
    public List<MatchHistoryResponse> getHistoryByUser(@PathVariable Integer userId) {
        return matchHistoryService.getHistoryByUser(userId);
    }
}
