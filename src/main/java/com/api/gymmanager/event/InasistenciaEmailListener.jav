package com.api.gymmanager.event;

import com.api.gymmanager.service.EmailService;

import java.time.format.DateTimeFormatter;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class InasistenciaEmailListener {

    private final EmailService emailService;

    public InasistenciaEmailListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @EventListener
    public void onInasistenciaRegistrada(InasistenciaRegistradaEvent event) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String fechaFormateada = event.fechaAsistencia().format(formatter);
        emailService.enviarInasistencia(
                event.correoUsuario(),
                event.nombreUsuario(),
                event.nombreClase(),
                fechaFormateada);
    }
}