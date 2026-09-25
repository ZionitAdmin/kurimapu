package cl.smapdev.curimapu.clases.relaciones;

import com.google.gson.annotations.SerializedName;

/* TICKET 2477 (extra) - 2026-09-25: mensaje OGM que llega en la descarga para mostrar en inicio */
public class MensajeInicio {

    @SerializedName("titulo")
    private String titulo;

    @SerializedName("texto")
    private String texto;

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }
}
