package com.api.gymmanager.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;

import com.api.gymmanager.dto.ControlAsistencia.request.AsistenciaItemRequest;
import com.api.gymmanager.dto.ControlAsistencia.request.CreateControlAsistenciaRequest;
import com.api.gymmanager.dto.ControlAsistencia.response.ControlAsistenciaResponse;
import com.api.gymmanager.dto.ControlAsistencia.response.InscritoResponse;
import com.api.gymmanager.exception.BusinessRuleException;
import com.api.gymmanager.exception.ResourceAlreadyExistsException;
import com.api.gymmanager.exception.ResourceNotFoundException;
import com.api.gymmanager.model.Clase;
import com.api.gymmanager.model.EnumModel.AsistenciaEnum;
import com.api.gymmanager.model.EnumModel.EstadoEnum;
import com.api.gymmanager.model.Inscripcion;
import com.api.gymmanager.model.Usuario;
import com.api.gymmanager.repository.ClaseRepository;
import com.api.gymmanager.repository.ControlAsistenciaRepository;
import com.api.gymmanager.repository.InscripcionRepository;

import static org.mockito.ArgumentMatchers.any;

import org.mockito.ArgumentCaptor;
import com.api.gymmanager.event.InasistenciaRegistradaEvent;

import com.api.gymmanager.dto.ControlAsistencia.response.HistorialClaseResponse;
import com.api.gymmanager.model.ControlAsistencia;

@ExtendWith(MockitoExtension.class)
class ControlAsistenciaServiceTest {

    @Mock ControlAsistenciaRepository controlAsistenciaRepository;
    @Mock InscripcionRepository inscripcionRepository;
    @Mock ClaseRepository claseRepository;
    @Mock ApplicationEventPublisher eventPublisher;

    @InjectMocks ControlAsistenciaService controlAsistenciaService;

    private final LocalDate fecha = LocalDate.of(2026, 11, 14);
    private Clase clase;
    private Inscripcion inscripcion;
    private CreateControlAsistenciaRequest request;

    @BeforeEach
    void setUp() {
        Usuario entrenador = new Usuario();
        entrenador.setUsuarioId(3);

        Usuario miembro = new Usuario();
        miembro.setUsuarioId(7);
        miembro.setPrimerNombre("Ana");

        clase = new Clase();
        clase.setClaseId(1);
        clase.setNombreClase("Yoga");
        clase.setEntrenador(entrenador);

        inscripcion = new Inscripcion();
        inscripcion.setInscripcionId(10);
        inscripcion.setUsuario(miembro);
        inscripcion.setClase(clase);
        inscripcion.setEstado(EstadoEnum.Activa);

        request = new CreateControlAsistenciaRequest(1, fecha,
                List.of(new AsistenciaItemRequest(7, AsistenciaEnum.Asistio)));
    }

    @Test
    void registrarAsistencia_ok_guardaYDevuelveRegistros() {
        when(claseRepository.findById(1)).thenReturn(Optional.of(clase));
        when(inscripcionRepository.findByClaseClaseIdAndUsuarioUsuarioIdAndEstado(1, 7, EstadoEnum.Activa))
                .thenReturn(Optional.of(inscripcion));
        when(controlAsistenciaRepository.existsByInscripcionInscripcionIdAndFechaAsistencia(10, fecha))
                .thenReturn(false);
        when(controlAsistenciaRepository.saveAll(anyList())).thenAnswer(i -> i.getArgument(0));

        List<ControlAsistenciaResponse> r = controlAsistenciaService.registrarAsistencia(3, request);

        assertEquals(1, r.size());
        assertEquals(AsistenciaEnum.Asistio, r.get(0).asistencia());
        assertEquals(7, r.get(0).usuarioId());
    }

    @Test
    void registrarAsistencia_sinInscripcionActiva_lanzaBusinessRuleYNoGuarda() {
        when(claseRepository.findById(1)).thenReturn(Optional.of(clase));
        when(inscripcionRepository.findByClaseClaseIdAndUsuarioUsuarioIdAndEstado(1, 7, EstadoEnum.Activa))
                .thenReturn(Optional.empty());

        assertThrows(BusinessRuleException.class,
                () -> controlAsistenciaService.registrarAsistencia(3, request));
        verify(controlAsistenciaRepository, never()).saveAll(anyList());
    }

    @Test
    void registrarAsistencia_yaRegistradaEnEsaFecha_lanzaAlreadyExists() {
        when(claseRepository.findById(1)).thenReturn(Optional.of(clase));
        when(inscripcionRepository.findByClaseClaseIdAndUsuarioUsuarioIdAndEstado(1, 7, EstadoEnum.Activa))
                .thenReturn(Optional.of(inscripcion));
        when(controlAsistenciaRepository.existsByInscripcionInscripcionIdAndFechaAsistencia(10, fecha))
                .thenReturn(true);

        assertThrows(ResourceAlreadyExistsException.class,
                () -> controlAsistenciaService.registrarAsistencia(3, request));
        verify(controlAsistenciaRepository, never()).saveAll(anyList());
    }

    @Test
    void registrarAsistencia_deOtroEntrenador_lanzaAccessDenied() {
        when(claseRepository.findById(1)).thenReturn(Optional.of(clase));

        assertThrows(AccessDeniedException.class,
                () -> controlAsistenciaService.registrarAsistencia(99, request));
    }

    @Test
    void registrarAsistencia_claseInexistente_lanzaNotFound() {
        when(claseRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> controlAsistenciaService.registrarAsistencia(3, request));
    }

    @Test
    void listarInscritos_devuelveSoloLosActivos() {
        when(claseRepository.findById(1)).thenReturn(Optional.of(clase));
        when(inscripcionRepository.findByClaseClaseIdAndEstado(1, EstadoEnum.Activa))
                .thenReturn(List.of(inscripcion));

        List<InscritoResponse> r = controlAsistenciaService.listarInscritos(1, 3);

        assertEquals(1, r.size());
        assertEquals("Ana", r.get(0).nombreMiembro());
        assertEquals(10, r.get(0).inscripcionId());
    }
        @Test
    void registrarAsistencia_ausente_publicaEventoDeInasistencia() {
        CreateControlAsistenciaRequest ausente = new CreateControlAsistenciaRequest(1, fecha,
                List.of(new AsistenciaItemRequest(7, AsistenciaEnum.Falto)));

        when(claseRepository.findById(1)).thenReturn(Optional.of(clase));
        when(inscripcionRepository.findByClaseClaseIdAndUsuarioUsuarioIdAndEstado(1, 7, EstadoEnum.Activa))
                .thenReturn(Optional.of(inscripcion));
        when(controlAsistenciaRepository.existsByInscripcionInscripcionIdAndFechaAsistencia(10, fecha))
                .thenReturn(false);
        when(controlAsistenciaRepository.saveAll(anyList())).thenAnswer(i -> i.getArgument(0));

        controlAsistenciaService.registrarAsistencia(3, ausente);

        ArgumentCaptor<InasistenciaRegistradaEvent> captor =
                ArgumentCaptor.forClass(InasistenciaRegistradaEvent.class);
        verify(eventPublisher).publishEvent(captor.capture());
        assertEquals(7, captor.getValue().usuarioId());
        assertEquals("Yoga", captor.getValue().nombreClase());
    }

    @Test
    void registrarAsistencia_asistio_noPublicaEvento() {
        when(claseRepository.findById(1)).thenReturn(Optional.of(clase));
        when(inscripcionRepository.findByClaseClaseIdAndUsuarioUsuarioIdAndEstado(1, 7, EstadoEnum.Activa))
                .thenReturn(Optional.of(inscripcion));
        when(controlAsistenciaRepository.existsByInscripcionInscripcionIdAndFechaAsistencia(10, fecha))
                .thenReturn(false);
        when(controlAsistenciaRepository.saveAll(anyList())).thenAnswer(i -> i.getArgument(0));

        controlAsistenciaService.registrarAsistencia(3, request);

        verify(eventPublisher, never()).publishEvent(any(Object.class));
    }
        private ControlAsistencia registro(Integer id, Inscripcion insc, AsistenciaEnum a, LocalDate f) {
        ControlAsistencia r = new ControlAsistencia();
        r.setAsistenciaId(id);
        r.setInscripcion(insc);
        r.setAsistencia(a);
        r.setFechaAsistencia(f);
        return r;
    }

    @Test
    void consultarHistorial_agrupaPorClaseYCuentaTotales() {
        Clase clase2 = new Clase();
        clase2.setClaseId(2);
        clase2.setNombreClase("Spinning");

        Inscripcion inscripcion2 = new Inscripcion();
        inscripcion2.setInscripcionId(11);
        inscripcion2.setClase(clase2);

        when(controlAsistenciaRepository
                .findByInscripcionUsuarioUsuarioIdOrderByFechaAsistenciaDesc(7))
                .thenReturn(List.of(
                        registro(1, inscripcion, AsistenciaEnum.Asistio, LocalDate.of(2026, 11, 14)),
                        registro(2, inscripcion, AsistenciaEnum.Falto, LocalDate.of(2026, 11, 13)),
                        registro(3, inscripcion2, AsistenciaEnum.Asistio, LocalDate.of(2026, 11, 12))));

        List<HistorialClaseResponse> r = controlAsistenciaService.consultarHistorial(7);

        assertEquals(2, r.size());
        assertEquals(1, r.get(0).claseId());
        assertEquals(1, r.get(0).totalAsistio());
        assertEquals(1, r.get(0).totalFalto());
        assertEquals(2, r.get(0).registros().size());
        assertEquals(2, r.get(1).claseId());
    }

    @Test
    void consultarHistorial_sinRegistros_devuelveListaVacia() {
        when(controlAsistenciaRepository
                .findByInscripcionUsuarioUsuarioIdOrderByFechaAsistenciaDesc(7))
                .thenReturn(List.of());

        List<HistorialClaseResponse> r = controlAsistenciaService.consultarHistorial(7);

        assertEquals(0, r.size());
    }
}
