package com.marcos.javabeasts_backend.controllers;

import com.marcos.javabeasts_backend.dto.jabeas.JaBeaCatalogResponse;
import com.marcos.javabeasts_backend.services.JaBeaCatalogService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class JaBeaController {

    private final JaBeaCatalogService jaBeaCatalogService;

    public JaBeaController(JaBeaCatalogService jaBeaCatalogService) {
        this.jaBeaCatalogService = jaBeaCatalogService;
    }

    @GetMapping("/jabeas")
    public List<JaBeaCatalogResponse> getJaBeasCatalog() {
        return jaBeaCatalogService.getJaBeasCatalog();
    }
}
