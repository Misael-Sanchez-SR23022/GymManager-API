package com.api.gymmanager.notification;

public interface CanalNotificacion {
    void enviar(String destino, String asunto, String mensaje);
}