package com.api.gymmanager.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.api.gymmanager.event.InasistenciaRegistradaEvent;
import com.api.gymmanager.notification.CanalNotificacion;

@ExtendWith(MockitoExtension.class)
class NotificacionServiceTest {

    @Mock CanalNotificacion canalNotificacion;

    @InjectMocks NotificacionService notificacionService;

    private InasistenciaRegistradaEvent evento(String correo) {
        return new InasistenciaRegistradaEvent(7, "Ana", correo, "Yoga", LocalDate.of(2026, 11, 14));
    }

    @Test
    void ausenteConContacto_enviaLaNotificacion() {
        notificacionService.notificarInasistencia(evento("ana@correo.com"));

        verify(canalNotificacion).enviar(eq("ana@correo.com"), anyString(), anyString());
    }

    @Test
    void ausenteSinContacto_seOmiteLaNotificacion() {
        notificacionService.notificarInasistencia(evento("  "));

        verify(canalNotificacion, never()).enviar(any(), any(), any());
    }

    @Test
    void fallaDelCanal_noPropagaLaExcepcion() {
        doThrow(new RuntimeException("SMTP caído"))
                .when(canalNotificacion).enviar(any(), any(), any());

        assertDoesNotThrow(() -> notificacionService.notificarInasistencia(evento("ana@correo.com")));
    }
}