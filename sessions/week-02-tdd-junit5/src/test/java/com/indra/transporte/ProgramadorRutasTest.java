package com.indra.transporte;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.indra.transporte.exception.UnsupportedTypeException;
import com.indra.transporte.model.Bus;
import com.indra.transporte.model.Horario;
import com.indra.transporte.model.Ruta;
import com.indra.transporte.model.Tipo;

public class ProgramadorRutasTest {
    private final ProgramadorRutas programador = new ProgramadorRutas();

    @Test
    @DisplayName("Debe registrar un horario")
    void debeRegistrarUnHorario() {
        Bus bus = new Bus("ABC123", Tipo.DIESEL);
        Ruta ruta = new Ruta(Tipo.ELECTRIC, "R001", "Ciudad A", "Ciudad B");
        Horario horario = new Horario(bus, ruta, LocalTime.of(8, 0), LocalTime.of(10, 0));

        programador.programar(horario);

        assertEquals(1, programador.getHorarios().size());
    }

    @Nested
    @DisplayName("Cuando el bus es eléctrico")
    class CuandoBusEsElectrico {

        @Test
        @DisplayName("Debe rechazar rutas no eléctricas")
        void debeRechazarRutasNoElectricas() {
            Bus bus = new Bus("ABC123", Tipo.ELECTRIC);
            Ruta ruta = new Ruta(Tipo.GENERAL, "R001", "Ciudad A", "Ciudad B");
            Horario horario = new Horario(bus, ruta, LocalTime.of(8, 0), LocalTime.of(10, 0));

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> programador.debeValidarTipoRutasYBuses(horario));

            assertEquals("Los buses eléctricos solo pueden ir a rutas eléctricas", exception.getMessage());
        }

        @Test
        @DisplayName("Debe permitir rutas eléctricas")
        void debePermitirRutasElectricas() {
            Bus bus = new Bus("ABC123", Tipo.ELECTRIC);
            Ruta ruta = new Ruta(Tipo.ELECTRIC, "R001", "Ciudad A", "Ciudad B");
            Horario horario = new Horario(bus, ruta, LocalTime.of(8, 0), LocalTime.of(10, 0));

            assertDoesNotThrow(() -> programador.debeValidarTipoRutasYBuses(horario));
        }
    }

    @Nested
    @DisplayName("Cuando el bus no es eléctrico")
    class CuandoBusNoEsElectrico {

        @Test
        @DisplayName("Debe permitir cualquier tipo de ruta")
        void debePermitirCualquierTipoDeRuta() {
            Bus bus = new Bus("ABC123", Tipo.DIESEL);
            Ruta ruta = new Ruta(Tipo.GENERAL, "R001", "Ciudad A", "Ciudad B");
            Horario horario = new Horario(bus, ruta, LocalTime.of(8, 0), LocalTime.of(10, 0));

            assertDoesNotThrow(() -> programador.debeValidarTipoRutasYBuses(horario));
        }
    }

    @Nested
    @DisplayName("Al consultar horarios por tipo de bus")
    class CuandoSeConsultaPorTipoDeBus {

        private final Bus busElectrico = new Bus("ELE-001", Tipo.ELECTRIC);
        private final Bus busDiesel = new Bus("DSL-002", Tipo.DIESEL);
        private final Ruta ruta = new Ruta(Tipo.GENERAL, "R001", "Ciudad A", "Ciudad B");

        @Test
        @DisplayName("Debe devolver solo los horarios del bus y tipo solicitado")
        void debeDevolverLosHorariosDelTipoSolicitado() {
            Horario horarioElectrico = new Horario(busElectrico, ruta, LocalTime.of(6, 0), LocalTime.of(7, 0));
            Horario horarioDiesel = new Horario(busDiesel, ruta, LocalTime.of(6, 0), LocalTime.of(7, 0));
            programador.programar(horarioElectrico);
            programador.programar(horarioDiesel);

            List<Horario> resultado = programador.consultarHorariosPorTipoBus(busElectrico, "Electric");

            assertEquals(List.of(horarioElectrico), resultado);
        }

        @ParameterizedTest
        @NullSource
        @DisplayName("Debe lanzar IllegalArgumentException cuando el bus es nulo")
        void debeLanzarIllegalArgumentExceptionCuandoBusEsNulo(Bus busNulo) {
            assertThrows(IllegalArgumentException.class,
                    () -> programador.consultarHorariosPorTipoBus(busNulo, "Electric"));
        }

        @Test
        @DisplayName("Debe lanzar IllegalArgumentException cuando el bus nunca fue programado")
        void debeLanzarIllegalArgumentExceptionCuandoBusNoTieneHorarios() {
            Bus busNoRegistrado = new Bus("ZZZ-999", Tipo.DIESEL);

            assertThrows(IllegalArgumentException.class,
                    () -> programador.consultarHorariosPorTipoBus(busNoRegistrado, "Diesel"));
        }

        @ParameterizedTest
        @ValueSource(strings = { "Hidrogeno", "electrico", "" })
        @DisplayName("Debe lanzar UnsupportedTypeException cuando el tipo no está en el catálogo soportado")
        void debeLanzarUnsupportedTypeExceptionCuandoTipoEsDesconocido(String tipoDesconocido) {
            programador.programar(new Horario(busElectrico, ruta, LocalTime.of(6, 0), LocalTime.of(7, 0)));

            assertThrows(UnsupportedTypeException.class,
                    () -> programador.consultarHorariosPorTipoBus(busElectrico, tipoDesconocido));
        }
    }

    @Nested
    @DisplayName("Al programar un horario con rango de horas inválido")
    class CuandoElRangoDeHorasEsInvalido {

        private final Bus bus = new Bus("ABC123", Tipo.DIESEL);
        private final Ruta ruta = new Ruta(Tipo.GENERAL, "R001", "Ciudad A", "Ciudad B");

        @ParameterizedTest
        @CsvSource({
                "10:00, 08:00", // llegada antes que salida
                "10:00, 10:00"  // duración cero: no es un viaje real
        })
        @DisplayName("Debe rechazar horarios donde la llegada no es posterior a la salida")
        void debeRechazarHorarioRangoInvalido(LocalTime salida, LocalTime llegada) {
            Horario horarioInvalido = new Horario(bus, ruta, salida, llegada);

            assertThrows(IllegalArgumentException.class, () -> programador.programar(horarioInvalido));
        }

        @Test
        @DisplayName("Debe aceptar un rango válido donde la llegada es posterior a la salida")
        void debeAceptarRangoValido() {
            Horario horarioValido = new Horario(bus, ruta, LocalTime.of(8, 0), LocalTime.of(10, 0));

            assertDoesNotThrow(() -> programador.programar(horarioValido));
        }
    }

    @Nested
    @DisplayName("Al programar un horario que se solapa con uno existente del mismo bus")
    class CuandoElHorarioSeSolapaConOtro {

        private final Bus bus = new Bus("ABC123", Tipo.DIESEL);
        private final Ruta ruta = new Ruta(Tipo.GENERAL, "R001", "Ciudad A", "Ciudad B");

        @ParameterizedTest
        @CsvSource({
                "08:30, 10:30", // se solapa por la mitad
                "07:00, 09:00", // se solapa al inicio
                "09:00, 11:00", // se solapa al final
                "08:00, 10:00", // exactamente igual
                "08:30, 09:30"  // contenido dentro del existente
        })
        @DisplayName("Debe rechazar cualquier horario que se solape con uno ya programado para el mismo bus")
        void debeRechazarHorarioSolapado(LocalTime salida, LocalTime llegada) {
            programador.programar(new Horario(bus, ruta, LocalTime.of(8, 0), LocalTime.of(10, 0)));
            Horario horarioSolapado = new Horario(bus, ruta, salida, llegada);

            assertThrows(IllegalArgumentException.class, () -> programador.programar(horarioSolapado));
        }

        @Test
        @DisplayName("Debe permitir un horario contiguo (sin solape) para el mismo bus")
        void debePermitirHorarioContiguo() {
            programador.programar(new Horario(bus, ruta, LocalTime.of(8, 0), LocalTime.of(10, 0)));
            Horario horarioContiguo = new Horario(bus, ruta, LocalTime.of(10, 0), LocalTime.of(12, 0));

            assertDoesNotThrow(() -> programador.programar(horarioContiguo));
            assertEquals(2, programador.getHorarios().size());
        }

        @Test
        @DisplayName("Debe permitir el mismo rango de horas para buses distintos")
        void debePermitirMismoRangoParaBusesDistintos() {
            Bus otroBus = new Bus("XYZ789", Tipo.DIESEL);
            programador.programar(new Horario(bus, ruta, LocalTime.of(8, 0), LocalTime.of(10, 0)));
            Horario horarioOtroBus = new Horario(otroBus, ruta, LocalTime.of(8, 0), LocalTime.of(10, 0));

            assertDoesNotThrow(() -> programador.programar(horarioOtroBus));
        }
    }

    @Nested
    @DisplayName("Al programar un horario con parámetros nulos")
    class CuandoElHorarioTieneParametrosNulos {

        private final Bus bus = new Bus("ABC123", Tipo.DIESEL);
        private final Ruta ruta = new Ruta(Tipo.GENERAL, "R001", "Ciudad A", "Ciudad B");

        @Test
        @DisplayName("Debe rechazar un horario nulo")
        void debeRechazarHorarioNulo() {
            assertThrows(IllegalArgumentException.class, () -> programador.programar(null));
        }

        @Test
        @DisplayName("Debe rechazar un horario sin hora de salida")
        void debeRechazarHorarioSinHoraSalida() {
            Horario horarioSinSalida = new Horario(bus, ruta, null, LocalTime.of(10, 0));

            assertThrows(IllegalArgumentException.class, () -> programador.programar(horarioSinSalida));
        }

        @Test
        @DisplayName("Debe rechazar un horario sin hora de llegada")
        void debeRechazarHorarioSinHoraLlegada() {
            Horario horarioSinLlegada = new Horario(bus, ruta, LocalTime.of(8, 0), null);

            assertThrows(IllegalArgumentException.class, () -> programador.programar(horarioSinLlegada));
        }
    }

    @Nested
    @DisplayName("Cuando un bus ya tiene horarios programados (integración)")
    class CuandoUnBusYaTieneHorariosProgramados {

        private final Bus bus = new Bus("ABC123", Tipo.ELECTRIC);
        private final Ruta rutaElectrica = new Ruta(Tipo.ELECTRIC, "R001", "Ciudad A", "Ciudad B");

        @Test
        @DisplayName("Debe acumular horarios válidos, exponerlos por consulta y bloquear el que se solapa")
        void debeGestionarElCicloCompletoDeProgramacionYConsulta() {
            Horario primerViaje = new Horario(bus, rutaElectrica, LocalTime.of(6, 0), LocalTime.of(8, 0));
            Horario segundoViaje = new Horario(bus, rutaElectrica, LocalTime.of(8, 0), LocalTime.of(10, 0));
            Horario viajeSolapado = new Horario(bus, rutaElectrica, LocalTime.of(9, 0), LocalTime.of(11, 0));

            programador.programar(primerViaje);
            programador.programar(segundoViaje);

            List<Horario> horariosDelBus = programador.consultarHorariosPorTipoBus(bus, "Electric");
            assertEquals(List.of(primerViaje, segundoViaje), horariosDelBus);

            assertThrows(IllegalArgumentException.class, () -> programador.programar(viajeSolapado));
            assertEquals(2, programador.getHorarios().size());
        }
    }
}
