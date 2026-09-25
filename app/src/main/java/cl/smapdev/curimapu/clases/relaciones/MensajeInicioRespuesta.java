package cl.smapdev.curimapu.clases.relaciones;

import com.google.gson.annotations.SerializedName;

/* TICKET 2477 (extra) - 2026-09-25: respuesta de mensaje_inicio.php (se pide despues de subir una visita) */
public class MensajeInicioRespuesta {

    @SerializedName("codigo_respuesta")
    private int codigoRespuesta;

    @SerializedName("mensaje_inicio")
    private MensajeInicio mensajeInicio;

    public int getCodigoRespuesta() {
        return codigoRespuesta;
    }

    public MensajeInicio getMensajeInicio() {
        return mensajeInicio;
    }
}
