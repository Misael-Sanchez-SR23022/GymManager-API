package com.api.gymmanager.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    private static final String NOMBRE_APLICACION = "GymManager";
    private static final String LOGO_URL = "https://res.cloudinary.com/kwxqqdwn/image/upload/v1790695945/logoGym.png";
    private static final DateTimeFormatter FORMATO_FECHA_HORA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${spring.mail.username:no-reply@gymmanager.local}")
    private String fromAddress;

    public EmailService(JavaMailSender mailSender, SpringTemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    @Async
    public void enviarBienvenida(String to, String nombreUsuario) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("nombreUsuario", nombreSeguro(nombreUsuario));
        variables.put("correo", valorSeguro(to));

        enviarCorreoHtml(to, "¡Bienvenido a GymManager!", "bienvenida", variables);
    }

    @Async
    public void enviarInicioSesion(String to, String nombreUsuario, LocalDateTime fechaHora) {
        LocalDateTime fecha = fechaHora != null ? fechaHora : LocalDateTime.now();

        Map<String, Object> variables = new HashMap<>();
        variables.put("nombreUsuario", nombreSeguro(nombreUsuario));
        variables.put("correo", valorSeguro(to));
        variables.put("fechaHora", fecha.format(FORMATO_FECHA_HORA));

        enviarCorreoHtml(to, "Nuevo inicio de sesión en GymManager", "inicio-sesion", variables);
    }

    @Async
    public void enviarInasistencia(String to, String nombreUsuario, String nombreClase, String fechaClase) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("nombreUsuario", nombreSeguro(nombreUsuario));
        variables.put("correo", valorSeguro(to));
        variables.put("nombreClase", valorSeguro(nombreClase));
        variables.put("fechaClase", valorSeguro(fechaClase));

        enviarCorreoHtml(to, "Aviso de inasistencia a clase - GymManager", "inasistencia", variables);
    }

    private void enviarCorreoHtml(String to, String subject, String templateName,
                                  Map<String, Object> variables) {
        if (to == null || to.isBlank()) {
            log.warn("No se envió correo '{}': destinatario vacío", subject);
            return;
        }

        try {
            Context context = new Context();
            context.setVariables(variables);

            context.setVariable("nombreAplicacion", NOMBRE_APLICACION);
            context.setVariable("anio", LocalDateTime.now().getYear());
            context.setVariable("logoUrl", LOGO_URL);

            context.setVariable("contenidoTemplate", templateName);
            String htmlContent = templateEngine.process("layout", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromAddress);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Correo '{}' enviado correctamente a {}", subject, to);
        } catch (MessagingException | MailException | org.thymeleaf.exceptions.TemplateEngineException ex) {
            log.error("Error enviando correo '{}' a {}: {}", subject, to, ex.getMessage(), ex);
        }
    }

    private String nombreSeguro(String nombre) {
        return (nombre == null || nombre.isBlank()) ? "Usuario" : nombre.trim();
    }

    private String valorSeguro(String valor) {
        return valor == null ? "No disponible" : valor;
    }
}
