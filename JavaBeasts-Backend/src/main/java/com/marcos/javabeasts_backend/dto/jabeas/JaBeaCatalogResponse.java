package com.marcos.javabeasts_backend.dto.jabeas;

public record JaBeaCatalogResponse(
        Integer jaBeasId,
        String name,
        String description,
        Integer health,
        Integer damage,
        Integer defence,
        Integer speed,
        Integer typeId,
        String typeName,
        JaBeaMoveResponse uniqueMove
) {
}
