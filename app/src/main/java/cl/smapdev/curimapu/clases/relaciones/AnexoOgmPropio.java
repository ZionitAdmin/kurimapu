package cl.smapdev.curimapu.clases.relaciones;

/*
 * TICKET 2512 - 2026-09-29
 * Un anexo OGM propio para la pantalla "Ver OGM propios" (DialogOgmPropios). Se arma en memoria
 * leyendo la BD local; no es una tabla de Room.
 */
public class AnexoOgmPropio {

    public static class UltimaVisita {
        public final String fecha;             // yyyy-MM-dd
        public final String hora;
        public final String plantaVoluntaria;  // SI / NO

        public UltimaVisita(String fecha, String hora, String plantaVoluntaria) {
            this.fecha = (fecha != null) ? fecha : "";
            this.hora = (hora != null) ? hora : "";
            this.plantaVoluntaria = plantaVoluntaria;
        }

        // para comparar cual visita es mas reciente
        public String clave() {
            return fecha + " " + hora;
        }

        // misma regla de la web (funBuscaVistaMonitoreo en monitoreo_ogm.php): SI = CPV, otro = SPV
        public boolean esCpv() {
            return "SI".equalsIgnoreCase(plantaVoluntaria);
        }
    }

    public final String idAnexo;
    public final String numAnexo;
    public final String agricultor;
    public final String comuna;
    public final String especie;
    public final String temporada;

    // ultima visita MONITOREO de cada cuatrimestre del ciclo vigente (posicion 0 = C1 ...)
    public final UltimaVisita[] ultimaPorCuatrimestre;

    public AnexoOgmPropio(String idAnexo, String numAnexo, String agricultor, String comuna,
                          String especie, String temporada, int cantidadCuatrimestres) {
        this.idAnexo = idAnexo;
        this.numAnexo = numAnexo;
        this.agricultor = agricultor;
        this.comuna = comuna;
        this.especie = especie;
        this.temporada = temporada;
        this.ultimaPorCuatrimestre = new UltimaVisita[cantidadCuatrimestres];
    }

    public void registrarVisita(int indiceCuatrimestre, UltimaVisita visita) {
        if (indiceCuatrimestre < 0 || indiceCuatrimestre >= ultimaPorCuatrimestre.length) return;
        UltimaVisita actual = ultimaPorCuatrimestre[indiceCuatrimestre];
        if (actual == null || visita.clave().compareTo(actual.clave()) > 0) {
            ultimaPorCuatrimestre[indiceCuatrimestre] = visita;
        }
    }

    public UltimaVisita visitaEn(int indiceCuatrimestre) {
        if (indiceCuatrimestre < 0 || indiceCuatrimestre >= ultimaPorCuatrimestre.length) return null;
        return ultimaPorCuatrimestre[indiceCuatrimestre];
    }
}
