package com.api.gymmanager.dto.Inscripcion.request;

import jakarta.validation.constraints.NotNull;

public record CreateInscripcionRequest(
    @NotNull Integer claseId
) {}