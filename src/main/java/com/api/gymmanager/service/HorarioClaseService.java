
package com.api.gymmanager.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.gymmanager.dto.HorarioClases.request.CreateHorarioClaseRequest;
import com.api.gymmanager.dto.HorarioClases.request.UpdateHorarioClaseRequest;
import com.api.gymmanager.dto.HorarioClases.response.HorarioClaseResponse;
import com.api.gymmanager.exception.BusinessRuleException;
import com.api.gymmanager.exception.ResourceAlreadyExistsException;
import com.api.gymmanager.exception.ResourceNotFoundException;
import com.api.gymmanager.model.Clase;
import com.api.gymmanager.model.HorarioClase;
import com.api.gymmanager.repository.ClaseRepository;
import com.api.gymmanager.repository.HorarioClaseRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HorarioClaseService {

    private final HorarioClaseRepository horarioClaseRepository;
    private final ClaseRepository claseRepository;

    @Transactional
    public HorarioClaseResponse createHorarioClase(
            CreateHorarioClaseRequest dto,
            boolean confirmarTraslape) {

        validarHorario(dto.horaInicio(), dto.horaFin());

        Clase clase = buscarClase(dto.claseId());

        boolean existeTraslape = horarioClaseRepository
                .existsByClase_ClaseIdAndDiasClaseAndHoraInicioLessThanAndHoraFinGreaterThan(
                        dto.claseId(),
                        dto.diasClase(),
                        dto.horaFin(),
                        dto.horaInicio());

        if (existeTraslape && !confirmarTraslape) {
            throw new ResourceAlreadyExistsException(
                    "Advertencia: el horario se traslapa con otro horario asignado a esta clase. ¿Desea continuar?");
        }

        HorarioClase horario = new HorarioClase();
        horario.setClase(clase);
        horario.setDiasClase(dto.diasClase());
        horario.setHoraInicio(dto.horaInicio());
        horario.setHoraFin(dto.horaFin());

        return mapToResponse(horarioClaseRepository.save(horario));
    }

    @Transactional(readOnly = true)
    public List<HorarioClaseResponse> getAllHorarios() {
        return horarioClaseRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public HorarioClaseResponse getHorarioById(Integer id) {
        return mapToResponse(buscarHorario(id));
    }

    @Transactional
    public HorarioClaseResponse updateHorarioClase(
            Integer id,
            UpdateHorarioClaseRequest dto) {

        HorarioClase horario = buscarHorario(id);

        validarHorario(dto.horaInicio(), dto.horaFin());

        Clase clase = buscarClase(dto.claseId());

        horario.setClase(clase);
        horario.setDiasClase(dto.diasClase());
        horario.setHoraInicio(dto.horaInicio());
        horario.setHoraFin(dto.horaFin());

        return mapToResponse(horarioClaseRepository.save(horario));
    }

    @Transactional
    public void deleteHorarioClase(Integer id) {
        horarioClaseRepository.delete(buscarHorario(id));
    }

    private HorarioClase buscarHorario(Integer id) {
        return horarioClaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Horario de clase " + id + " no encontrado"));
    }

    private Clase buscarClase(Integer claseId) {
        return claseRepository.findById(claseId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Clase " + claseId + " no encontrada"));
    }

    private void validarHorario(
            java.sql.Time horaInicio,
            java.sql.Time horaFin) {

        if (horaInicio == null || horaFin == null) {
            throw new BusinessRuleException(
                    "La hora de inicio y finalización son obligatorias");
        }

        if (!horaFin.after(horaInicio)) {
            throw new BusinessRuleException(
                    "La hora de finalización debe ser posterior a la hora de inicio");
        }
    }

    private HorarioClaseResponse mapToResponse(HorarioClase entity) {
        return new HorarioClaseResponse(
                entity.getHorarioId(),
                entity.getDiasClase(),
                entity.getClase().getClaseId(),
                entity.getHoraInicio(),
                entity.getHoraFin());
    }
}
