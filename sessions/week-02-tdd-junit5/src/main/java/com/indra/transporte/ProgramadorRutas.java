package com.indra.transporte;

import java.util.ArrayList;
import java.util.List;

import com.indra.transporte.model.Bus;
import com.indra.transporte.model.Horario;
import com.indra.transporte.model.Tipo;

import lombok.Data;

@Data
public class ProgramadorRutas {

    List<Horario> horarios = new ArrayList<>();

    public void programar(Horario horario) {
        if (horario == null) {
            throw new IllegalArgumentException("El horario no puede ser nulo");
        }
        if (horario.getHoraSalida() == null || horario.getHoraLlegada() == null) {
            throw new IllegalArgumentException("La hora de salida y la hora de llegada son obligatorias");
        }
        // Se rechaza tambien el rango de duracion cero: un viaje no puede durar 0 minutos.
        if (!horario.getHoraLlegada().isAfter(horario.getHoraSalida())) {
            throw new IllegalArgumentException("La hora de llegada debe ser posterior a la hora de salida");
        }
        if (seSolapaConHorarioExistente(horario)) {
            throw new IllegalArgumentException("El bus ya tiene un horario que se solapa en ese rango");
        }
        horarios.add(horario);
    }

    private boolean seSolapaConHorarioExistente(Horario nuevo) {
        return horarios.stream()
                .filter(existente -> existente.getBus().equals(nuevo.getBus()))
                .anyMatch(existente -> existente.getHoraSalida().isBefore(nuevo.getHoraLlegada())
                        && nuevo.getHoraSalida().isBefore(existente.getHoraLlegada()));
    }

    public boolean debeValidarTipoRutasYBuses(Horario horario) {
        if (horario == null) {
            throw new IllegalArgumentException("El horario no puede ser nulo");
        }
        Tipo tipoBus = horario.getBus().getTipo();
        Tipo tipoRuta = horario.getRuta().getTipo();

        if (Tipo.ELECTRIC.equals(tipoBus) && !Tipo.ELECTRIC.equals(tipoRuta)) {
            throw new IllegalArgumentException("Los buses eléctricos solo pueden ir a rutas eléctricas");
        }
        return true;
    }

    public List<Horario> consultarHorariosPorTipoBus(Bus bus, String tipo) {
        if (bus == null || horarios.stream().noneMatch(h -> h.getBus().equals(bus))) {
            throw new IllegalArgumentException("Bus desconocido: no tiene horarios programados");
        }
        // Se valida el catalogo de tipos antes de filtrar, incluso si el resultado termina vacio.
        Tipo tipoValidado = Tipo.from(tipo);
        return horarios.stream()
                .filter(h -> h.getBus().equals(bus) && tipoValidado.equals(h.getBus().getTipo()))
                .toList();
    }

}
