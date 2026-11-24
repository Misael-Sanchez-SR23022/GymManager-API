package com.api.gymmanager.notification;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

// Canal simulado: registra la notificación en el log.
// Para correo real, se reemplaza por una implementación con JavaMailSender.
@Slf4j
@Component
public class LogCanalNotificacion implements CanalNotificacion {

    @Override
    public void enviar(String destino, String asunto, String mensaje) {
        log.info("[NOTIFICACION] Para: {} | Asunto: {} | Mensaje: {}", destino, asunto, mensaje);
    }
}