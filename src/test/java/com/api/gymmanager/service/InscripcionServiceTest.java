package com.api.gymmanager.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.api.gymmanager.dto.Inscripcion.request.CreateInscripcionRequest;
import com.api.gymmanager.dto.Inscripcion.response.CreateInscripcionResponse;
import com.api.gymmanager.exception.BusinessRuleException;
import com.api.gymmanager.exception.ResourceAlreadyExistsException;
import com.api.gymmanager.exception.ResourceNotFoundException;
import com.api.gymmanager.model.Clase;
import com.api.gymmanager.model.EnumModel.EstadoEnum;
import com.api.gymmanager.model.Inscripcion;
import com.api.gymmanager.model.Usuario;
import com.api.gymmanager.repository.ClaseRepository;
import com.api.gymmanager.repository.InscripcionRepository;
import com.api.gymmanager.repository.UsuarioRepository;

import java.time.LocalDateTime;

import org.springframework.security.access.AccessDeniedException;

@ExtendWith(MockitoExtension.class)
class InscripcionServiceTest {

    @Mock InscripcionRepository inscripcionRepository;
    @Mock ClaseRepository claseRepository;
    @Mock UsuarioRepository usuarioRepository;

    @InjectMocks InscripcionService inscripcionService;

    private Usuario usuario;
    private Clase clase;
    private final CreateInscripcionRequest request = new CreateInscripcionRequest(1);

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setUsuarioId(7);

        clase = new Clase();
        clase.setClaseId(1);
        clase.setNombreClase("Yoga");
        clase.setCapacidadClase(2);
    }

    @Test
    void inscribirse_conCupo_creaInscripcionActiva() {
        when(usuarioRepository.findById(7)).thenReturn(Optional.of(usuario));
        when(claseRepository.findById(1)).thenReturn(Optional.of(clase));
        when(inscripcionRepository.existsByUsuarioUsuarioIdAndClaseClaseIdAndEstado(7, 1, EstadoEnum.Activa))
                .thenReturn(false);
        when(inscripcionRepository.countByClaseClaseIdAndEstado(1, EstadoEnum.Activa)).thenReturn(1L);
        when(inscripcionRepository.save(any(Inscripcion.class))).thenAnswer(i -> i.getArgument(0));

        CreateInscripcionResponse r = inscripcionService.inscribirse(7, request);

        assertEquals(EstadoEnum.Activa, r.estado());
        assertEquals(1, r.claseId());
        assertEquals(7, r.usuarioId());
    }

    @Test
    void inscribirse_claseLlena_lanzaBusinessRuleYNoGuarda() {
        when(usuarioRepository.findById(7)).thenReturn(Optional.of(usuario));
        when(claseRepository.findById(1)).thenReturn(Optional.of(clase));
        when(inscripcionRepository.existsByUsuarioUsuarioIdAndClaseClaseIdAndEstado(7, 1, EstadoEnum.Activa))
                .thenReturn(false);
        when(inscripcionRepository.countByClaseClaseIdAndEstado(1, EstadoEnum.Activa)).thenReturn(2L);

        assertThrows(BusinessRuleException.class, () -> inscripcionService.inscribirse(7, request));
        verify(inscripcionRepository, never()).save(any());
    }

    @Test
    void inscribirse_yaInscrito_lanzaAlreadyExistsYNoGuarda() {
        when(usuarioRepository.findById(7)).thenReturn(Optional.of(usuario));
        when(claseRepository.findById(1)).thenReturn(Optional.of(clase));
        when(inscripcionRepository.existsByUsuarioUsuarioIdAndClaseClaseIdAndEstado(7, 1, EstadoEnum.Activa))
                .thenReturn(true);

        assertThrows(ResourceAlreadyExistsException.class, () -> inscripcionService.inscribirse(7, request));
        verify(inscripcionRepository, never()).save(any());
    }

    @Test
    void inscribirse_claseInexistente_lanzaNotFound() {
        when(usuarioRepository.findById(7)).thenReturn(Optional.of(usuario));
        when(claseRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> inscripcionService.inscribirse(7, request));
    }
        private Inscripcion inscripcionActiva() {
        Inscripcion i = new Inscripcion();
        i.setInscripcionId(10);
        i.setUsuario(usuario);
        i.setClase(clase);
        i.setFechaRegistro(LocalDateTime.now());
        i.setEstado(EstadoEnum.Activa);
        return i;
    }

    @Test
    void cancelar_inscripcionActiva_cambiaEstadoYNoElimina() {
        when(inscripcionRepository.findById(10)).thenReturn(Optional.of(inscripcionActiva()));
        when(inscripcionRepository.save(any(Inscripcion.class))).thenAnswer(i -> i.getArgument(0));

        CreateInscripcionResponse r = inscripcionService.cancelarInscripcion(10, 7);

        assertEquals(EstadoEnum.Cancelada, r.estado());
        verify(inscripcionRepository, never()).delete(any());
        verify(inscripcionRepository, never()).deleteById(any());
    }

    @Test
    void cancelar_yaCancelada_lanzaBusinessRuleYNoGuarda() {
        Inscripcion i = inscripcionActiva();
        i.setEstado(EstadoEnum.Cancelada);
        when(inscripcionRepository.findById(10)).thenReturn(Optional.of(i));

        assertThrows(BusinessRuleException.class, () -> inscripcionService.cancelarInscripcion(10, 7));
        verify(inscripcionRepository, never()).save(any());
    }

    @Test
    void cancelar_deOtroMiembro_lanzaAccessDenied() {
        when(inscripcionRepository.findById(10)).thenReturn(Optional.of(inscripcionActiva()));

        assertThrows(AccessDeniedException.class, () -> inscripcionService.cancelarInscripcion(10, 99));
        verify(inscripcionRepository, never()).save(any());
    }

    @Test
    void cancelar_inexistente_lanzaNotFound() {
        when(inscripcionRepository.findById(10)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> inscripcionService.cancelarInscripcion(10, 7));
    }
}