package com.api.gymmanager.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.api.gymmanager.dto.Clase.response.ClaseResponse;
import com.api.gymmanager.exception.BusinessRuleException;
import com.api.gymmanager.exception.GlobalExceptionHandler;
import com.api.gymmanager.exception.ResourceNotFoundException;
import com.api.gymmanager.service.ClaseService;

@ExtendWith(MockitoExtension.class)
class ClaseControllerTest {

    @Mock
    ClaseService claseService;

    @InjectMocks
    ClaseController claseController;

    MockMvc mockMvc;

    private static final String JSON_VALIDO =
            "{\"nombreClase\":\"Yoga\",\"capacidadClase\":20,\"entrenadorId\":5}";

    private final ClaseResponse respuesta =
            new ClaseResponse(1, "Yoga", 20, 5, "Juan");

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(claseController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void post_conDatosValidos_devuelve201() throws Exception {
        when(claseService.createClase(any())).thenReturn(respuesta);

        mockMvc.perform(post("/api/clases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_VALIDO))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombreClase").value("Yoga"))
                .andExpect(jsonPath("$.entrenadorId").value(5));
    }

    @Test
    void post_conDatosInvalidos_devuelve400() throws Exception {
        String invalido = "{\"nombreClase\":\"\",\"capacidadClase\":0}";

        mockMvc.perform(post("/api/clases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalido))
                .andExpect(status().isBadRequest());
    }

    @Test
    void post_usuarioNoEsEntrenador_devuelve400() throws Exception {
        when(claseService.createClase(any()))
                .thenThrow(new BusinessRuleException("El usuario 7 no tiene el rol de entrenador"));

        mockMvc.perform(post("/api/clases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_VALIDO))
                .andExpect(status().isBadRequest());
    }

    @Test
    void get_todas_devuelve200() throws Exception {
        when(claseService.getAllClases()).thenReturn(List.of(respuesta));

        mockMvc.perform(get("/api/clases"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombreClase").value("Yoga"));
    }

    @Test
    void get_porId_existente_devuelve200() throws Exception {
        when(claseService.getClaseById(1)).thenReturn(respuesta);

        mockMvc.perform(get("/api/clases/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.claseId").value(1));
    }

    @Test
    void get_porId_inexistente_devuelve404() throws Exception {
        when(claseService.getClaseById(99))
                .thenThrow(new ResourceNotFoundException("Clase 99 no encontrada"));

        mockMvc.perform(get("/api/clases/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void get_entrenadores_sinEntrenadores_devuelve400() throws Exception {
        when(claseService.getEntrenadores())
                .thenThrow(new BusinessRuleException("No hay entrenadores registrados."));

        mockMvc.perform(get("/api/clases/entrenadores"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void put_conDatosValidos_devuelve200() throws Exception {
        when(claseService.updateClase(eq(1), any())).thenReturn(respuesta);

        mockMvc.perform(put("/api/clases/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_VALIDO))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreClase").value("Yoga"));
    }

    @Test
    void delete_existente_devuelve204() throws Exception {
        mockMvc.perform(delete("/api/clases/1"))
                .andExpect(status().isNoContent());

        verify(claseService).deleteClase(1);
    }
}
