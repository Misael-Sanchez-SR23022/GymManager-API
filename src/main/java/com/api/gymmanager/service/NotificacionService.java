package com.api.gymmanager.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.api.gymmanager.event.InasistenciaRegistradaEvent;
import com.api.gymmanager.notification.CanalNotificacion;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificacionService {

    private final CanalNotificacion canalNotificacion;

    // <<extend>> Notificar Inasistencia: se ejecuta solo si el registro se guardó
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void notificarInasistencia(InasistenciaRegistradaEvent evento) {
        // Flujo alterno: sin medio de contacto, la notificación se omite
        if (evento.correo() == null || evento.correo().isBlank()) {
            log.warn("Notificación omitida: el usuario {} no tiene medio de contacto",
                    evento.usuarioId());
            return;
        }

        String asunto = "Inasistencia registrada";
        String mensaje = "Hola " + evento.nombreMiembro() + ", se registró tu inasistencia a la clase '"
                + evento.nombreClase() + "' el " + evento.fechaAsistencia() + ".";

        // Un fallo al notificar nunca debe afectar el registro de asistencia
        try {
            canalNotificacion.enviar(evento.correo(), asunto, mensaje);
        } catch (Exception e) {
            log.error("No se pudo notificar la inasistencia al usuario {}: {}",
                    evento.usuarioId(), e.getMessage());
        }
    }
}