package cl.smapdev.curimapu.clases.utilidades;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
 * TICKET 2512 - 2026-09-29
 * Cuatrimestres fijos del monitoreo OGM, mismos del cron core/cron/envio_firebase.php:
 * Febrero-Mayo (C1), Junio-Septiembre (C2), Octubre-Enero (C3).
 * El ciclo va de Febrero a Enero y se toma el que contiene la fecha de hoy: en Enero el ciclo
 * vigente es el que empezo en Febrero del año anterior. Cada 1 de Febrero parte un ciclo nuevo.
 * Si a futuro cambian los cuatrimestres, se ajusta solo DEFINICION (igual que el array del cron).
 */
public final class CuatrimestresOgm {

    // {mes de inicio, cantidad de meses}. El primero define el mes en que parte el ciclo.
    private static final int[][] DEFINICION = {
            {2, 4},
            {6, 4},
            {10, 4},
    };
    private static final String[] NOMBRES = {"Febrero-Mayo", "Junio-Septiembre", "Octubre-Enero"};

    private static final DateTimeFormatter FORMATO_BD = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter FORMATO_VISTA = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public static class Cuatrimestre {
        public final int numero;          // 1, 2, 3 -> C1, C2, C3
        public final String nombre;
        public final LocalDate inicio;    // incluido
        public final LocalDate fin;       // excluido (primer dia del siguiente)

        Cuatrimestre(int numero, String nombre, LocalDate inicio, LocalDate fin) {
            this.numero = numero;
            this.nombre = nombre;
            this.inicio = inicio;
            this.fin = fin;
        }

        public String getEtiqueta() {
            return "C" + numero;
        }

        // fechas en formato de la BD (yyyy-MM-dd) para comparar como texto con fecha_visita
        public String getInicioBD() {
            return inicio.format(FORMATO_BD);
        }

        public String getFinBD() {
            return fin.format(FORMATO_BD);
        }

        public String getUltimoDiaVista() {
            return fin.minusDays(1).format(FORMATO_VISTA);
        }

        public boolean contiene(LocalDate fecha) {
            return !fecha.isBefore(inicio) && fecha.isBefore(fin);
        }

        // mismo calculo que el cron y la pantalla web "Recordatorios OGM Cuatrimestres":
        // dias corridos desde el inicio del cuatrimestre (el primer dia es el dia 0)
        public long diasTranscurridos(LocalDate hoy) {
            return ChronoUnit.DAYS.between(inicio, hoy);
        }
    }

    private CuatrimestresOgm() {
    }

    // los cuatrimestres del ciclo vigente a la fecha indicada, en orden C1, C2, C3
    public static List<Cuatrimestre> delCiclo(LocalDate hoy) {
        int mesInicioCiclo = DEFINICION[0][0];
        int anioCiclo = (hoy.getMonthValue() >= mesInicioCiclo) ? hoy.getYear() : hoy.getYear() - 1;

        List<Cuatrimestre> lista = new ArrayList<>();
        for (int i = 0; i < DEFINICION.length; i++) {
            // un cuatrimestre cuyo mes de inicio es menor al del ciclo ya cae en el año siguiente
            int anio = (DEFINICION[i][0] >= mesInicioCiclo) ? anioCiclo : anioCiclo + 1;
            LocalDate inicio = LocalDate.of(anio, DEFINICION[i][0], 1);
            lista.add(new Cuatrimestre(i + 1, NOMBRES[i], inicio, inicio.plusMonths(DEFINICION[i][1])));
        }
        return Collections.unmodifiableList(lista);
    }

    // posicion (0, 1, 2) del cuatrimestre que contiene la fecha, o -1 si no esta en el ciclo
    public static int indiceDe(List<Cuatrimestre> ciclo, LocalDate fecha) {
        for (int i = 0; i < ciclo.size(); i++) {
            if (ciclo.get(i).contiene(fecha)) return i;
        }
        return -1;
    }

    // posicion del cuatrimestre para una fecha de la BD ("yyyy-MM-dd" o "yyyy-MM-dd HH:mm:ss"),
    // comparando como texto. -1 si la fecha viene vacia/mal formada o esta fuera del ciclo.
    public static int indiceDeFechaBD(List<Cuatrimestre> ciclo, String fecha) {
        if (fecha == null || fecha.length() < 10) return -1;
        String dia = fecha.substring(0, 10);
        for (int i = 0; i < ciclo.size(); i++) {
            Cuatrimestre c = ciclo.get(i);
            if (dia.compareTo(c.getInicioBD()) >= 0 && dia.compareTo(c.getFinBD()) < 0) return i;
        }
        return -1;
    }
}
