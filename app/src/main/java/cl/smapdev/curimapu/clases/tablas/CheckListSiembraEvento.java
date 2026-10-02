package cl.smapdev.curimapu.clases.tablas;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

// TICKET 2494 - 2026-09-30: evento de siembra (H/M1/M2/M3), uno por fecha+tipo, asociado a un
// CheckListSiembra via clave_unica_cl_siembra (mismo patron que ChecklistLimpiezaCamionesDetalle)
// TICKET 2494 - 2026-09-30: optimizacion de indices (busquedas de la descarga), ver Migrations.MIGRATION_22_TO_23
@Entity(tableName = "anexo_checklist_siembra_evento", indices = {@Index("clave_unica_evento")})
public class CheckListSiembraEvento {

    @SerializedName("id_evento")
    @PrimaryKey(autoGenerate = true)
    @Expose
    private int id_evento;

    @SerializedName("clave_unica_evento")
    @Expose
    private String clave_unica_evento;

    @SerializedName("clave_unica_cl_siembra")
    @Expose
    private String clave_unica_cl_siembra;

    // TICKET 2515 - 2026-10-02: fecha_evento/tipo_evento (H/M1/M2/M3) eliminados. Ahora el evento se
    // define por prestador_servicio + sembradora_marca + sembradora_modelo, y el tipo (H/M) vive en
    // la cabecera (CheckListSiembra.tipo_siembra). Ver Migrations.MIGRATION_26_TO_27.
    // TICKET 2494 - 2026-10-01: seccion Siembra Anterior, tambien por evento (cada H/M1/M2/M3
    // puede venir de un cultivo anterior distinto)
    @SerializedName("especie")
    @Expose
    private String especie;
    @SerializedName("variedad")
    @Expose
    private String variedad;
    @SerializedName("ogm")
    @Expose
    private int ogm;
    @SerializedName("anexo_curimapu")
    @Expose
    private String anexo_curimapu;

    @SerializedName("prestador_servicio")
    @Expose
    private String prestador_servicio;
    @SerializedName("estado_discos")
    @Expose
    private String estado_discos;

    // TICKET 2515 - 2026-10-02: Regulacion Sembradora, todo por evento (los 5 campos de
    // Regulacion de Siembra - prof. fertilizante, dist. fert-semilla, dist. hileras, N semillas/mt,
    // prof. siembra - pasaron a la cabecera)
    @SerializedName("sembradora_marca")
    @Expose
    private String sembradora_marca;
    @SerializedName("sembradora_modelo")
    @Expose
    private String sembradora_modelo;
    @SerializedName("trocha")
    @Expose
    private String trocha;
    @SerializedName("tipo_sembradora")
    @Expose
    private String tipo_sembradora;
    @SerializedName("chequeo_selector")
    @Expose
    private String chequeo_selector;
    @SerializedName("estado_maquina")
    @Expose
    private String estado_maquina;
    @SerializedName("desterronadores")
    @Expose
    private String desterronadores;
    @SerializedName("presion_neumaticos")
    @Expose
    private String presion_neumaticos;
    @SerializedName("especie_lote_anterior")
    @Expose
    private String especie_lote_anterior;
    @SerializedName("rueda_angosta")
    @Expose
    private String rueda_angosta;
    @SerializedName("largo_guia")
    @Expose
    private String largo_guia;
    @SerializedName("sistema_fertilizacion")
    @Expose
    private String sistema_fertilizacion;
    @SerializedName("cheque_caidas")
    @Expose
    private String cheque_caidas;

    @SerializedName("tarros_semilla_pre_siembra")
    @Expose
    private String tarros_semilla_pre_siembra;
    @SerializedName("discos_sembradores_pre_siembra")
    @Expose
    private String discos_sembradores_pre_siembra;
    @SerializedName("estructura_maquinaria_pre_siembra")
    @Expose
    private String estructura_maquinaria_pre_siembra;
    @SerializedName("lugar_limpieza_pre_siembra")
    @Expose
    private String lugar_limpieza_pre_siembra;
    @SerializedName("responsable_aseo_pre_siembra")
    @Expose
    private String responsable_aseo_pre_siembra;
    @SerializedName("rut_responsable_aseo_pre_siembra")
    @Expose
    private String rut_responsable_aseo_pre_siembra;
    @SerializedName("responsable_revision_limpieza_pre_siembra")
    @Expose
    private String responsable_revision_limpieza_pre_siembra;
    @SerializedName("firma_responsable_aso_pre_siembra")
    @Expose
    private String firma_responsable_aso_pre_siembra;
    @SerializedName("stringed_responsable_aso_pre_siembra")
    @Expose
    private String stringed_responsable_aso_pre_siembra;
    @SerializedName("firma_revision_limpieza_pre_siembra")
    @Expose
    private String firma_revision_limpieza_pre_siembra;
    @SerializedName("stringed_revision_limpieza_pre_siembra")
    @Expose
    private String stringed_revision_limpieza_pre_siembra;

    @SerializedName("tarros_semilla_post_siembra")
    @Expose
    private String tarros_semilla_post_siembra;
    @SerializedName("discos_sembradores_post_siembra")
    @Expose
    private String discos_sembradores_post_siembra;
    @SerializedName("estructura_maquinaria_post_cosecha")
    @Expose
    private String estructura_maquinaria_post_cosecha;
    @SerializedName("lugar_limpieza_post_siembra")
    @Expose
    private String lugar_limpieza_post_siembra;
    @SerializedName("responsable_aseo_post_siembra")
    @Expose
    private String responsable_aseo_post_siembra;
    @SerializedName("rut_responsable_aseo_post_siembra")
    @Expose
    private String rut_responsable_aseo_post_siembra;
    @SerializedName("encargado_revision_limpieza_post_siembra")
    @Expose
    private String encargado_revision_limpieza_post_siembra;
    @SerializedName("firma_responsable_aseo_post_siembra")
    @Expose
    private String firma_responsable_aseo_post_siembra;
    @SerializedName("stringed_responsable_aseo_post_siembra")
    @Expose
    private String stringed_responsable_aseo_post_siembra;
    @SerializedName("firma_revision_limpieza_post_siembra")
    @Expose
    private String firma_revision_limpieza_post_siembra;
    @SerializedName("stringed_revision_limpieza_post_siembra")
    @Expose
    private String stringed_revision_limpieza_post_siembra;

    @SerializedName("desempeno_siembra")
    @Expose
    private String desempeno_siembra;
    @SerializedName("observacion_general")
    @Expose
    private String observacion_general;

    @SerializedName("fecha_ingreso")
    @Expose
    private String fecha_ingreso;
    @SerializedName("hora_ingreso")
    @Expose
    private String hora_ingreso;
    @SerializedName("nombre_supervisor_siembra")
    @Expose
    private String nombre_supervisor_siembra;
    @SerializedName("nombre_responsable_campo")
    @Expose
    private String nombre_responsable_campo;
    @SerializedName("nombre_operario_maquina")
    @Expose
    private String nombre_operario_maquina;
    @SerializedName("firma_responsable_campo")
    @Expose
    private String firma_responsable_campo;
    @SerializedName("stringed_responsable_campo")
    @Expose
    private String stringed_responsable_campo;
    @SerializedName("firma_operario_maquina")
    @Expose
    private String firma_operario_maquina;
    @SerializedName("stringed_operario_maquina")
    @Expose
    private String stringed_operario_maquina;

    @SerializedName("fecha_termino")
    @Expose
    private String fecha_termino;
    @SerializedName("hora_termino")
    @Expose
    private String hora_termino;
    @SerializedName("nombre_supervisor_siembra_termino")
    @Expose
    private String nombre_supervisor_siembra_termino;
    @SerializedName("nombre_responsable_campo_termino")
    @Expose
    private String nombre_responsable_campo_termino;
    @SerializedName("nombre_operario_maquina_termino")
    @Expose
    private String nombre_operario_maquina_termino;
    @SerializedName("firma_responsable_campo_termino")
    @Expose
    private String firma_responsable_campo_termino;
    @SerializedName("stringed_responsable_campo_termino")
    @Expose
    private String stringed_responsable_campo_termino;
    @SerializedName("firma_operario_maquina_termino")
    @Expose
    private String firma_operario_maquina_termino;
    @SerializedName("stringed_operario_maquina_termino")
    @Expose
    private String stringed_operario_maquina_termino;

    @SerializedName("estado_sincronizacion")
    @Expose
    private int estado_sincronizacion;

    public int getId_evento() {
        return id_evento;
    }

    public void setId_evento(int id_evento) {
        this.id_evento = id_evento;
    }

    public String getClave_unica_evento() {
        return clave_unica_evento;
    }

    public void setClave_unica_evento(String clave_unica_evento) {
        this.clave_unica_evento = clave_unica_evento;
    }

    public String getClave_unica_cl_siembra() {
        return clave_unica_cl_siembra;
    }

    public void setClave_unica_cl_siembra(String clave_unica_cl_siembra) {
        this.clave_unica_cl_siembra = clave_unica_cl_siembra;
    }

    public String getEspecie() {
        return especie;
    }

    public void setEspecie(String especie) {
        this.especie = especie;
    }

    public String getVariedad() {
        return variedad;
    }

    public void setVariedad(String variedad) {
        this.variedad = variedad;
    }

    public int getOgm() {
        return ogm;
    }

    public void setOgm(int ogm) {
        this.ogm = ogm;
    }

    public String getAnexo_curimapu() {
        return anexo_curimapu;
    }

    public void setAnexo_curimapu(String anexo_curimapu) {
        this.anexo_curimapu = anexo_curimapu;
    }

    public String getPrestador_servicio() {
        return prestador_servicio;
    }

    public void setPrestador_servicio(String prestador_servicio) {
        this.prestador_servicio = prestador_servicio;
    }

    public String getEstado_discos() {
        return estado_discos;
    }

    public void setEstado_discos(String estado_discos) {
        this.estado_discos = estado_discos;
    }

    public String getSembradora_marca() {
        return sembradora_marca;
    }

    public void setSembradora_marca(String sembradora_marca) {
        this.sembradora_marca = sembradora_marca;
    }

    public String getSembradora_modelo() {
        return sembradora_modelo;
    }

    public void setSembradora_modelo(String sembradora_modelo) {
        this.sembradora_modelo = sembradora_modelo;
    }

    public String getTrocha() {
        return trocha;
    }

    public void setTrocha(String trocha) {
        this.trocha = trocha;
    }

    public String getTipo_sembradora() {
        return tipo_sembradora;
    }

    public void setTipo_sembradora(String tipo_sembradora) {
        this.tipo_sembradora = tipo_sembradora;
    }

    public String getChequeo_selector() {
        return chequeo_selector;
    }

    public void setChequeo_selector(String chequeo_selector) {
        this.chequeo_selector = chequeo_selector;
    }

    public String getEstado_maquina() {
        return estado_maquina;
    }

    public void setEstado_maquina(String estado_maquina) {
        this.estado_maquina = estado_maquina;
    }

    public String getDesterronadores() {
        return desterronadores;
    }

    public void setDesterronadores(String desterronadores) {
        this.desterronadores = desterronadores;
    }

    public String getPresion_neumaticos() {
        return presion_neumaticos;
    }

    public void setPresion_neumaticos(String presion_neumaticos) {
        this.presion_neumaticos = presion_neumaticos;
    }

    public String getEspecie_lote_anterior() {
        return especie_lote_anterior;
    }

    public void setEspecie_lote_anterior(String especie_lote_anterior) {
        this.especie_lote_anterior = especie_lote_anterior;
    }

    public String getRueda_angosta() {
        return rueda_angosta;
    }

    public void setRueda_angosta(String rueda_angosta) {
        this.rueda_angosta = rueda_angosta;
    }

    public String getLargo_guia() {
        return largo_guia;
    }

    public void setLargo_guia(String largo_guia) {
        this.largo_guia = largo_guia;
    }

    public String getSistema_fertilizacion() {
        return sistema_fertilizacion;
    }

    public void setSistema_fertilizacion(String sistema_fertilizacion) {
        this.sistema_fertilizacion = sistema_fertilizacion;
    }

    public String getCheque_caidas() {
        return cheque_caidas;
    }

    public void setCheque_caidas(String cheque_caidas) {
        this.cheque_caidas = cheque_caidas;
    }

    public String getTarros_semilla_pre_siembra() {
        return tarros_semilla_pre_siembra;
    }

    public void setTarros_semilla_pre_siembra(String tarros_semilla_pre_siembra) {
        this.tarros_semilla_pre_siembra = tarros_semilla_pre_siembra;
    }

    public String getDiscos_sembradores_pre_siembra() {
        return discos_sembradores_pre_siembra;
    }

    public void setDiscos_sembradores_pre_siembra(String discos_sembradores_pre_siembra) {
        this.discos_sembradores_pre_siembra = discos_sembradores_pre_siembra;
    }

    public String getEstructura_maquinaria_pre_siembra() {
        return estructura_maquinaria_pre_siembra;
    }

    public void setEstructura_maquinaria_pre_siembra(String estructura_maquinaria_pre_siembra) {
        this.estructura_maquinaria_pre_siembra = estructura_maquinaria_pre_siembra;
    }

    public String getLugar_limpieza_pre_siembra() {
        return lugar_limpieza_pre_siembra;
    }

    public void setLugar_limpieza_pre_siembra(String lugar_limpieza_pre_siembra) {
        this.lugar_limpieza_pre_siembra = lugar_limpieza_pre_siembra;
    }

    public String getResponsable_aseo_pre_siembra() {
        return responsable_aseo_pre_siembra;
    }

    public void setResponsable_aseo_pre_siembra(String responsable_aseo_pre_siembra) {
        this.responsable_aseo_pre_siembra = responsable_aseo_pre_siembra;
    }

    public String getRut_responsable_aseo_pre_siembra() {
        return rut_responsable_aseo_pre_siembra;
    }

    public void setRut_responsable_aseo_pre_siembra(String rut_responsable_aseo_pre_siembra) {
        this.rut_responsable_aseo_pre_siembra = rut_responsable_aseo_pre_siembra;
    }

    public String getResponsable_revision_limpieza_pre_siembra() {
        return responsable_revision_limpieza_pre_siembra;
    }

    public void setResponsable_revision_limpieza_pre_siembra(String responsable_revision_limpieza_pre_siembra) {
        this.responsable_revision_limpieza_pre_siembra = responsable_revision_limpieza_pre_siembra;
    }

    public String getFirma_responsable_aso_pre_siembra() {
        return firma_responsable_aso_pre_siembra;
    }

    public void setFirma_responsable_aso_pre_siembra(String firma_responsable_aso_pre_siembra) {
        this.firma_responsable_aso_pre_siembra = firma_responsable_aso_pre_siembra;
    }

    public String getStringed_responsable_aso_pre_siembra() {
        return stringed_responsable_aso_pre_siembra;
    }

    public void setStringed_responsable_aso_pre_siembra(String stringed_responsable_aso_pre_siembra) {
        this.stringed_responsable_aso_pre_siembra = stringed_responsable_aso_pre_siembra;
    }

    public String getFirma_revision_limpieza_pre_siembra() {
        return firma_revision_limpieza_pre_siembra;
    }

    public void setFirma_revision_limpieza_pre_siembra(String firma_revision_limpieza_pre_siembra) {
        this.firma_revision_limpieza_pre_siembra = firma_revision_limpieza_pre_siembra;
    }

    public String getStringed_revision_limpieza_pre_siembra() {
        return stringed_revision_limpieza_pre_siembra;
    }

    public void setStringed_revision_limpieza_pre_siembra(String stringed_revision_limpieza_pre_siembra) {
        this.stringed_revision_limpieza_pre_siembra = stringed_revision_limpieza_pre_siembra;
    }

    public String getTarros_semilla_post_siembra() {
        return tarros_semilla_post_siembra;
    }

    public void setTarros_semilla_post_siembra(String tarros_semilla_post_siembra) {
        this.tarros_semilla_post_siembra = tarros_semilla_post_siembra;
    }

    public String getDiscos_sembradores_post_siembra() {
        return discos_sembradores_post_siembra;
    }

    public void setDiscos_sembradores_post_siembra(String discos_sembradores_post_siembra) {
        this.discos_sembradores_post_siembra = discos_sembradores_post_siembra;
    }

    public String getEstructura_maquinaria_post_cosecha() {
        return estructura_maquinaria_post_cosecha;
    }

    public void setEstructura_maquinaria_post_cosecha(String estructura_maquinaria_post_cosecha) {
        this.estructura_maquinaria_post_cosecha = estructura_maquinaria_post_cosecha;
    }

    public String getLugar_limpieza_post_siembra() {
        return lugar_limpieza_post_siembra;
    }

    public void setLugar_limpieza_post_siembra(String lugar_limpieza_post_siembra) {
        this.lugar_limpieza_post_siembra = lugar_limpieza_post_siembra;
    }

    public String getResponsable_aseo_post_siembra() {
        return responsable_aseo_post_siembra;
    }

    public void setResponsable_aseo_post_siembra(String responsable_aseo_post_siembra) {
        this.responsable_aseo_post_siembra = responsable_aseo_post_siembra;
    }

    public String getRut_responsable_aseo_post_siembra() {
        return rut_responsable_aseo_post_siembra;
    }

    public void setRut_responsable_aseo_post_siembra(String rut_responsable_aseo_post_siembra) {
        this.rut_responsable_aseo_post_siembra = rut_responsable_aseo_post_siembra;
    }

    public String getEncargado_revision_limpieza_post_siembra() {
        return encargado_revision_limpieza_post_siembra;
    }

    public void setEncargado_revision_limpieza_post_siembra(String encargado_revision_limpieza_post_siembra) {
        this.encargado_revision_limpieza_post_siembra = encargado_revision_limpieza_post_siembra;
    }

    public String getFirma_responsable_aseo_post_siembra() {
        return firma_responsable_aseo_post_siembra;
    }

    public void setFirma_responsable_aseo_post_siembra(String firma_responsable_aseo_post_siembra) {
        this.firma_responsable_aseo_post_siembra = firma_responsable_aseo_post_siembra;
    }

    public String getStringed_responsable_aseo_post_siembra() {
        return stringed_responsable_aseo_post_siembra;
    }

    public void setStringed_responsable_aseo_post_siembra(String stringed_responsable_aseo_post_siembra) {
        this.stringed_responsable_aseo_post_siembra = stringed_responsable_aseo_post_siembra;
    }

    public String getFirma_revision_limpieza_post_siembra() {
        return firma_revision_limpieza_post_siembra;
    }

    public void setFirma_revision_limpieza_post_siembra(String firma_revision_limpieza_post_siembra) {
        this.firma_revision_limpieza_post_siembra = firma_revision_limpieza_post_siembra;
    }

    public String getStringed_revision_limpieza_post_siembra() {
        return stringed_revision_limpieza_post_siembra;
    }

    public void setStringed_revision_limpieza_post_siembra(String stringed_revision_limpieza_post_siembra) {
        this.stringed_revision_limpieza_post_siembra = stringed_revision_limpieza_post_siembra;
    }

    public String getDesempeno_siembra() {
        return desempeno_siembra;
    }

    public void setDesempeno_siembra(String desempeno_siembra) {
        this.desempeno_siembra = desempeno_siembra;
    }

    public String getObservacion_general() {
        return observacion_general;
    }

    public void setObservacion_general(String observacion_general) {
        this.observacion_general = observacion_general;
    }

    public String getFecha_ingreso() {
        return fecha_ingreso;
    }

    public void setFecha_ingreso(String fecha_ingreso) {
        this.fecha_ingreso = fecha_ingreso;
    }

    public String getHora_ingreso() {
        return hora_ingreso;
    }

    public void setHora_ingreso(String hora_ingreso) {
        this.hora_ingreso = hora_ingreso;
    }

    public String getNombre_supervisor_siembra() {
        return nombre_supervisor_siembra;
    }

    public void setNombre_supervisor_siembra(String nombre_supervisor_siembra) {
        this.nombre_supervisor_siembra = nombre_supervisor_siembra;
    }

    public String getNombre_responsable_campo() {
        return nombre_responsable_campo;
    }

    public void setNombre_responsable_campo(String nombre_responsable_campo) {
        this.nombre_responsable_campo = nombre_responsable_campo;
    }

    public String getNombre_operario_maquina() {
        return nombre_operario_maquina;
    }

    public void setNombre_operario_maquina(String nombre_operario_maquina) {
        this.nombre_operario_maquina = nombre_operario_maquina;
    }

    public String getFirma_responsable_campo() {
        return firma_responsable_campo;
    }

    public void setFirma_responsable_campo(String firma_responsable_campo) {
        this.firma_responsable_campo = firma_responsable_campo;
    }

    public String getStringed_responsable_campo() {
        return stringed_responsable_campo;
    }

    public void setStringed_responsable_campo(String stringed_responsable_campo) {
        this.stringed_responsable_campo = stringed_responsable_campo;
    }

    public String getFirma_operario_maquina() {
        return firma_operario_maquina;
    }

    public void setFirma_operario_maquina(String firma_operario_maquina) {
        this.firma_operario_maquina = firma_operario_maquina;
    }

    public String getStringed_operario_maquina() {
        return stringed_operario_maquina;
    }

    public void setStringed_operario_maquina(String stringed_operario_maquina) {
        this.stringed_operario_maquina = stringed_operario_maquina;
    }

    public String getFecha_termino() {
        return fecha_termino;
    }

    public void setFecha_termino(String fecha_termino) {
        this.fecha_termino = fecha_termino;
    }

    public String getHora_termino() {
        return hora_termino;
    }

    public void setHora_termino(String hora_termino) {
        this.hora_termino = hora_termino;
    }

    public String getNombre_supervisor_siembra_termino() {
        return nombre_supervisor_siembra_termino;
    }

    public void setNombre_supervisor_siembra_termino(String nombre_supervisor_siembra_termino) {
        this.nombre_supervisor_siembra_termino = nombre_supervisor_siembra_termino;
    }

    public String getNombre_responsable_campo_termino() {
        return nombre_responsable_campo_termino;
    }

    public void setNombre_responsable_campo_termino(String nombre_responsable_campo_termino) {
        this.nombre_responsable_campo_termino = nombre_responsable_campo_termino;
    }

    public String getNombre_operario_maquina_termino() {
        return nombre_operario_maquina_termino;
    }

    public void setNombre_operario_maquina_termino(String nombre_operario_maquina_termino) {
        this.nombre_operario_maquina_termino = nombre_operario_maquina_termino;
    }

    public String getFirma_responsable_campo_termino() {
        return firma_responsable_campo_termino;
    }

    public void setFirma_responsable_campo_termino(String firma_responsable_campo_termino) {
        this.firma_responsable_campo_termino = firma_responsable_campo_termino;
    }

    public String getStringed_responsable_campo_termino() {
        return stringed_responsable_campo_termino;
    }

    public void setStringed_responsable_campo_termino(String stringed_responsable_campo_termino) {
        this.stringed_responsable_campo_termino = stringed_responsable_campo_termino;
    }

    public String getFirma_operario_maquina_termino() {
        return firma_operario_maquina_termino;
    }

    public void setFirma_operario_maquina_termino(String firma_operario_maquina_termino) {
        this.firma_operario_maquina_termino = firma_operario_maquina_termino;
    }

    public String getStringed_operario_maquina_termino() {
        return stringed_operario_maquina_termino;
    }

    public void setStringed_operario_maquina_termino(String stringed_operario_maquina_termino) {
        this.stringed_operario_maquina_termino = stringed_operario_maquina_termino;
    }

    public int getEstado_sincronizacion() {
        return estado_sincronizacion;
    }

    public void setEstado_sincronizacion(int estado_sincronizacion) {
        this.estado_sincronizacion = estado_sincronizacion;
    }
}
