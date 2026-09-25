package cl.smapdev.curimapu.clases.utilidades;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cl.smapdev.curimapu.clases.relaciones.GsonDescargas;

/*
 * TICKET 2477 (extra) - 2026-09-25
 * Guarda en un archivo JSON propio (almacenamiento interno, uno por usuario) lo que llega en la
 * descarga para los filtros OGM/PROPIOS de la lista de anexos y el mensaje de inicio.
 * NO usa la BD local (Room). Si el archivo no existe o no se puede leer, se devuelve null y las
 * pantallas funcionan como antes (sin filtrar y sin mensaje).
 */
public class DatosUsuarioJson {

    private static final String TAG = "DATOS_USUARIO_JSON";

    private static final Gson gson = new Gson();

    // cache en memoria del ultimo usuario leido, para no leer el archivo en cada pantalla
    private static volatile int usuarioCache = -1;
    private static volatile Datos datosCache = null;

    public static class Datos {
        @SerializedName("anexos_propios")
        private List<Integer> anexosPropios;

        @SerializedName("anexos_ogm")
        private List<Integer> anexosOgm;

        @SerializedName("mensaje_titulo")
        private String mensajeTitulo;

        @SerializedName("mensaje_texto")
        private String mensajeTexto;

        // sets de id_anexo_contrato (String, igual que en AnexoContrato) armados al leer
        private transient Set<String> setPropios;
        private transient Set<String> setOgm;

        private void armarSets() {
            setPropios = aSet(anexosPropios);
            setOgm = aSet(anexosOgm);
        }

        private static Set<String> aSet(List<Integer> ids) {
            Set<String> set = new HashSet<>();
            if (ids != null) {
                for (Integer id : ids) {
                    if (id != null) set.add(String.valueOf(id));
                }
            }
            return Collections.unmodifiableSet(set);
        }

        public boolean esPropio(String idAnexo) {
            return idAnexo != null && setPropios.contains(idAnexo);
        }

        public boolean esOgm(String idAnexo) {
            return idAnexo != null && setOgm.contains(idAnexo);
        }

        public String getMensajeTitulo() {
            return (mensajeTitulo != null) ? mensajeTitulo : "";
        }

        public String getMensajeTexto() {
            return (mensajeTexto != null) ? mensajeTexto : "";
        }
    }

    private static File archivo(Context context, int idUsuario) {
        return new File(context.getFilesDir(), "datos_usuario_" + idUsuario + ".json");
    }

    // se llama despues de guardar bien la descarga. Si la respuesta no trae los campos
    // (servidor sin el cambio) no se toca el archivo existente.
    public static synchronized void guardar(Context context, int idUsuario, GsonDescargas descarga) {
        if (context == null || descarga == null
                || descarga.getAnexos_propios() == null
                || descarga.getAnexos_ogm() == null
                || descarga.getMensaje_inicio() == null) {
            return;
        }

        Datos datos = new Datos();
        datos.anexosPropios = new ArrayList<>(descarga.getAnexos_propios());
        datos.anexosOgm = new ArrayList<>(descarga.getAnexos_ogm());
        datos.mensajeTitulo = descarga.getMensaje_inicio().getTitulo();
        datos.mensajeTexto = descarga.getMensaje_inicio().getTexto();

        File destino = archivo(context, idUsuario);
        File temporal = new File(destino.getAbsolutePath() + ".tmp");

        // se escribe a un temporal y luego se renombra, asi nunca queda un archivo a medias
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(temporal), StandardCharsets.UTF_8)) {
            gson.toJson(datos, writer);
        } catch (Exception e) {
            Log.e(TAG, "no se pudo escribir " + temporal.getName() + ": " + e.getMessage());
            return;
        }

        if (!temporal.renameTo(destino)) {
            Log.e(TAG, "no se pudo renombrar " + temporal.getName());
            return;
        }

        datos.armarSets();
        usuarioCache = idUsuario;
        datosCache = datos;
    }

    // devuelve null si el usuario todavia no tiene archivo o no se puede leer
    public static synchronized Datos obtener(Context context, int idUsuario) {
        if (context == null) return null;

        if (usuarioCache == idUsuario && datosCache != null) {
            return datosCache;
        }

        File origen = archivo(context, idUsuario);
        if (!origen.exists()) return null;

        try (Reader reader = new InputStreamReader(new FileInputStream(origen), StandardCharsets.UTF_8)) {
            Datos datos = gson.fromJson(reader, Datos.class);
            if (datos == null) return null;
            datos.armarSets();
            usuarioCache = idUsuario;
            datosCache = datos;
            return datos;
        } catch (Exception e) {
            Log.e(TAG, "no se pudo leer " + origen.getName() + ": " + e.getMessage());
            return null;
        }
    }
}
