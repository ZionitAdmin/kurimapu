package cl.smapdev.curimapu.fragments.dialogos;

import android.content.Context;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cl.smapdev.curimapu.MainActivity;
import cl.smapdev.curimapu.R;
import cl.smapdev.curimapu.clases.adapters.OgmPropiosAdapter;
import cl.smapdev.curimapu.clases.relaciones.AnexoOgmPropio;
import cl.smapdev.curimapu.clases.tablas.Config;
import cl.smapdev.curimapu.clases.tablas.Usuario;
import cl.smapdev.curimapu.clases.utilidades.CuatrimestresOgm;
import cl.smapdev.curimapu.clases.utilidades.DatosUsuarioJson;

/*
 * TICKET 2512 - 2026-09-29
 * Pantalla "Ver OGM propios" (se abre desde la lista de anexos, FragmentVisitas). SOLO LECTURA.
 *
 * Muestra los anexos OGM propios del usuario y su estado de visita por cuatrimestre:
 *  - OGM    = anexo_contrato.condicion = 'GMO' (nombre de condicion id 2; "GMO FREE" NO es OGM)
 *  - propio = esta en la lista "anexos_propios" del JSON local (DatosUsuarioJson, TICKET 2477)
 *  - sin destruidos, igual que el push OGM
 *  - lee lo que esta en este telefono (no llama al servidor). La descarga trae siempre los OGM
 *    propios de las ultimas 4 temporadas con sus visitas MONITOREO, sin importar el filtro de
 *    temporada/especie (core/models/android/utils_descarga/descarga_ogm_propios_extra.php)
 *  - visitas: tipo MONITOREO de la tabla local de visitas, tanto las hechas en el telefono
 *    (aun no subidas) como las que llegaron en la descarga; vale la ultima de cada cuatrimestre
 *
 * Esta aislada del resto: lee la BD local con SQL de solo lectura (no agrega DAOs ni toca
 * entidades/tablas de Room) y cualquier error se muestra dentro de la misma pantalla.
 */
public class DialogOgmPropios extends DialogFragment {

    public static final String TAG_DIALOGO = "DIALOGO_OGM_PROPIOS";
    private static final String TAG = "OGM_PROPIOS";

    // filtros de cuatrimestre: 0, 1, 2 = C1, C2, C3
    private static final int FILTRO_TODOS = -1;
    private static final int FILTRO_PENDIENTES = -2;

    private TextView tvIniciales, tvNombre, tvSubtitulo, tvCuatrimestre;
    private TextView tvTotal, tvVisitados, tvPendientes;
    private TextView tvTituloLista, tvCantidad, tvMensaje;
    private TextView[] tabs;
    private ChipGroup chipsEspecies;
    private ProgressBar progreso;
    private final OgmPropiosAdapter adapter = new OgmPropiosAdapter();

    private List<CuatrimestresOgm.Cuatrimestre> ciclo;
    private int indiceActual;
    private List<AnexoOgmPropio> anexos = Collections.emptyList();
    private int filtro = FILTRO_PENDIENTES;
    private String especieSeleccionada = null; // null = todas
    private final Map<Integer, String> especiePorChip = new HashMap<>();

    // resultado de la carga en segundo plano
    private static class Carga {
        String error;
        String nombre = "";
        String subtitulo = "";
        List<AnexoOgmPropio> anexos = new ArrayList<>();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.OgmDialogPantallaCompleta);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            Window window = getDialog().getWindow();
            if (window != null) {
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_ogm_propios, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvIniciales = view.findViewById(R.id.ogm_tv_iniciales);
        tvNombre = view.findViewById(R.id.ogm_tv_nombre);
        tvSubtitulo = view.findViewById(R.id.ogm_tv_subtitulo);
        tvCuatrimestre = view.findViewById(R.id.ogm_tv_cuatrimestre);
        tvTotal = view.findViewById(R.id.ogm_tv_total);
        tvVisitados = view.findViewById(R.id.ogm_tv_visitados);
        tvPendientes = view.findViewById(R.id.ogm_tv_pendientes);
        tvTituloLista = view.findViewById(R.id.ogm_tv_titulo_lista);
        tvCantidad = view.findViewById(R.id.ogm_tv_cantidad);
        tvMensaje = view.findViewById(R.id.ogm_tv_mensaje);
        chipsEspecies = view.findViewById(R.id.ogm_chips_especies);
        progreso = view.findViewById(R.id.ogm_progreso);

        RecyclerView lista = view.findViewById(R.id.ogm_lista);
        lista.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        lista.setAdapter(adapter);

        view.findViewById(R.id.ogm_btn_cerrar).setOnClickListener(v -> dismiss());

        tabs = new TextView[]{
                view.findViewById(R.id.ogm_tab_todos),
                view.findViewById(R.id.ogm_tab_pendientes),
                view.findViewById(R.id.ogm_tab_c1),
                view.findViewById(R.id.ogm_tab_c2),
                view.findViewById(R.id.ogm_tab_c3),
        };
        final int[] filtroDeTab = {FILTRO_TODOS, FILTRO_PENDIENTES, 0, 1, 2};
        for (int i = 0; i < tabs.length; i++) {
            final int f = filtroDeTab[i];
            tabs[i].setOnClickListener(v -> {
                filtro = f;
                pintarTabs();
                mostrarLista();
            });
        }

        // cuatrimestre vigente segun la fecha del telefono
        LocalDate hoy = LocalDate.now();
        ciclo = CuatrimestresOgm.delCiclo(hoy);
        indiceActual = CuatrimestresOgm.indiceDe(ciclo, hoy);
        CuatrimestresOgm.Cuatrimestre actual = ciclo.get(indiceActual);
        tvCuatrimestre.setText("Cuatrimestre actual: " + actual.nombre + " (" + actual.getEtiqueta() + ")"
                + "  ·  día " + actual.diasTranscurridos(hoy)
                + "  ·  termina el " + actual.getUltimoDiaVista());
        pintarTabs();

        cargarDatos(hoy);
    }

    private void cargarDatos(LocalDate hoy) {
        final Context appContext = requireContext().getApplicationContext();
        final List<CuatrimestresOgm.Cuatrimestre> cicloCarga = ciclo;
        final Handler handler = new Handler(Looper.getMainLooper());
        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            Carga carga;
            try {
                carga = cargar(appContext, cicloCarga, hoy);
            } catch (Exception e) {
                Log.e(TAG, "error cargando pantalla OGM propios", e);
                carga = new Carga();
                carga.error = "No se pudo cargar la información: " + e.getMessage();
            }

            final Carga resultado = carga;
            handler.post(() -> {
                if (!isAdded() || getView() == null) return;
                mostrarCarga(resultado);
            });
        });
        executor.shutdown();
    }

    // corre en segundo plano: solo lee la BD local y el JSON del usuario
    private static Carga cargar(Context context, List<CuatrimestresOgm.Cuatrimestre> ciclo, LocalDate hoy) {
        Carga carga = new Carga();

        Config cnf = MainActivity.myAppDB.myDao().getConfig();
        if (cnf == null) {
            carga.error = "No hay un usuario configurado en este teléfono.";
            return carga;
        }
        int idUsuario = cnf.getId_usuario_suplandato();

        SupportSQLiteDatabase db = MainActivity.myAppDB.getOpenHelper().getReadableDatabase();

        // encabezado: nombre, tipo y region / comuna del usuario
        Usuario usuario = MainActivity.myAppDB.myDao().getUsuarioById(idUsuario);
        if (usuario != null) {
            carga.nombre = (texto(usuario.getNombre()) + " " + texto(usuario.getApellido_p())).trim();
            String zona = "";
            try (Cursor c = db.query("SELECT R.desc_region, C.desc_comuna FROM usuarios U "
                    + "LEFT JOIN region R ON (R.id_region = U.id_region) "
                    + "LEFT JOIN comuna C ON (C.id_comuna = U.id_comuna) "
                    + "WHERE U.id_usuario = ?", new Object[]{idUsuario})) {
                if (c.moveToFirst()) {
                    String region = texto(c, 0);
                    String comuna = texto(c, 1);
                    zona = region + ((!region.isEmpty() && !comuna.isEmpty()) ? " / " : "") + comuna;
                }
            }
            String tipo = (usuario.getTipo_usuario() == 5) ? "Administrador" : "Fieldman";
            carga.subtitulo = tipo + (zona.isEmpty() ? "" : "  ·  " + zona);
        }
        if (carga.nombre.isEmpty()) carga.nombre = "Usuario " + idUsuario;

        // anexos propios: lista del JSON que llega en la descarga (TICKET 2477)
        DatosUsuarioJson.Datos datos = DatosUsuarioJson.obtener(context, idUsuario);
        if (datos == null || !datos.tieneListas()) {
            carga.error = "Aún no hay lista de anexos propios en este teléfono. Descargue datos para ver esta pantalla.";
            return carga;
        }

        // ventana de 4 temporadas, igual que el push OGM (core/models/firebase/ogm_consulta_pendientes.php):
        // año de INICIO de temporada.nombre ("2022-2023" -> 2022) entre (año actual - 4) y (año actual - 1).
        // Decision TICKET 2512: se aplica para que la pantalla cuente los mismos campos que el push.
        // Para mostrar todos los OGM propios del telefono sin importar la temporada, dejar
        // aplicarVentanaTemporadas en false.
        final boolean aplicarVentanaTemporadas = true;
        final int anioMinimo = hoy.getYear() - 4;
        final int anioMaximo = hoy.getYear() - 1;

        Map<String, AnexoOgmPropio> porId = new LinkedHashMap<>();
        try (Cursor c = db.query("SELECT AC.id_anexo_contrato, AC.num_anexo, AC.destruido, "
                + "A.razon_social, C.desc_comuna, E.desc_especie, T.nombre_tempo "
                + "FROM anexo_contrato AC "
                + "LEFT JOIN agricultor A ON (A.id_agricultor = AC.id_agricultor_anexo) "
                + "LEFT JOIN especie E ON (E.id_especie = AC.id_especie_anexo) "
                + "LEFT JOIN ficha_new F ON (F.id_ficha_new = AC.id_ficha_contrato) "
                + "LEFT JOIN comuna C ON (C.id_comuna = F.id_comuna_new) "
                + "LEFT JOIN temporada T ON (T.id_tempo_tempo = AC.temporada_anexo) "
                + "WHERE AC.condicion = 'GMO' "
                + "ORDER BY E.desc_especie ASC, AC.num_anexo ASC")) {
            while (c.moveToNext()) {
                String idAnexo = texto(c, 0);
                if (porId.containsKey(idAnexo)) continue;
                if (!datos.esPropio(idAnexo)) continue;
                if ("1".equals(texto(c, 2))) continue; // destruido

                String temporada = texto(c, 6);
                if (aplicarVentanaTemporadas) {
                    int anioInicio = anioInicioTemporada(temporada);
                    if (anioInicio < anioMinimo || anioInicio > anioMaximo) continue;
                }

                porId.put(idAnexo, new AnexoOgmPropio(idAnexo, texto(c, 1), texto(c, 3), texto(c, 4),
                        texto(c, 5), temporada, ciclo.size()));
            }
        }

        // visitas MONITOREO del telefono: las hechas aqui (aun sin subir) y las descargadas del
        // servidor. Por cada anexo y cuatrimestre queda la mas reciente (fecha + hora).
        if (!porId.isEmpty()) {
            try (Cursor c = db.query("SELECT id_anexo_visita, fecha_visita, hora_visita, planta_voluntaria "
                    + "FROM visita WHERE tipo_visita = 'MONITOREO'")) {
                while (c.moveToNext()) {
                    AnexoOgmPropio anexo = porId.get(texto(c, 0));
                    if (anexo == null) continue;
                    String fecha = texto(c, 1);
                    int indice = CuatrimestresOgm.indiceDeFechaBD(ciclo, fecha);
                    if (indice < 0) continue; // visita de un ciclo anterior
                    anexo.registrarVisita(indice, new AnexoOgmPropio.UltimaVisita(
                            fecha.substring(0, 10), texto(c, 2), texto(c, 3)));
                }
            }
        }

        carga.anexos = new ArrayList<>(porId.values());
        return carga;
    }

    private void mostrarCarga(Carga carga) {
        progreso.setVisibility(View.GONE);

        tvNombre.setText(carga.nombre);
        tvSubtitulo.setText(carga.subtitulo);
        tvIniciales.setText(iniciales(carga.nombre));

        if (carga.error != null) {
            tvMensaje.setText(carga.error);
            tvMensaje.setVisibility(View.VISIBLE);
            return;
        }

        anexos = carga.anexos;

        // indicadores: siempre sobre TODOS los OGM propios en el cuatrimestre actual
        // (no cambian con los filtros de especie ni de cuatrimestre)
        int visitados = 0;
        for (AnexoOgmPropio a : anexos) {
            if (a.visitaEn(indiceActual) != null) visitados++;
        }
        tvTotal.setText(String.valueOf(anexos.size()));
        tvVisitados.setText(String.valueOf(visitados));
        tvPendientes.setText(String.valueOf(anexos.size() - visitados));

        armarChipsEspecies();
        mostrarLista();
    }

    // "Todas" + solo las especies que aparecen en los anexos OGM propios
    private void armarChipsEspecies() {
        chipsEspecies.setOnCheckedStateChangeListener(null);
        chipsEspecies.removeAllViews();
        especiePorChip.clear();

        TreeSet<String> especies = new TreeSet<>();
        for (AnexoOgmPropio a : anexos) {
            if (!a.especie.isEmpty()) especies.add(a.especie);
        }

        Chip todas = crearChip("Todas");
        chipsEspecies.addView(todas);
        especiePorChip.put(todas.getId(), null);
        for (String especie : especies) {
            Chip chip = crearChip(especie);
            chipsEspecies.addView(chip);
            especiePorChip.put(chip.getId(), especie);
        }
        todas.setChecked(true);
        especieSeleccionada = null;

        chipsEspecies.setOnCheckedStateChangeListener((group, ids) -> {
            especieSeleccionada = ids.isEmpty() ? null : especiePorChip.get(ids.get(0));
            mostrarLista();
        });
    }

    private Chip crearChip(String texto) {
        Context ctx = requireContext();
        Chip chip = new Chip(ctx);
        chip.setId(View.generateViewId());
        chip.setText(texto);
        chip.setCheckable(true);
        chip.setCheckedIconVisible(false);

        int[][] estados = new int[][]{{android.R.attr.state_checked}, {}};
        chip.setChipBackgroundColor(new ColorStateList(estados, new int[]{
                ContextCompat.getColor(ctx, R.color.ogm_verde_oscuro),
                ContextCompat.getColor(ctx, R.color.ogm_blanco)}));
        chip.setTextColor(new ColorStateList(estados, new int[]{
                ContextCompat.getColor(ctx, R.color.ogm_blanco),
                ContextCompat.getColor(ctx, R.color.ogm_texto)}));
        chip.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(ctx, R.color.ogm_borde_card)));
        chip.setChipStrokeWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics()));
        return chip;
    }

    private void mostrarLista() {
        if (anexos == null) return;

        List<OgmPropiosAdapter.Fila> filas = new ArrayList<>();
        for (AnexoOgmPropio a : anexos) {
            if (especieSeleccionada != null && !especieSeleccionada.equals(a.especie)) continue;

            if (filtro == FILTRO_TODOS) {
                // estado del cuatrimestre actual
                filas.add(new OgmPropiosAdapter.Fila(a, a.visitaEn(indiceActual), ciclo.get(indiceActual).getEtiqueta()));
            } else if (filtro == FILTRO_PENDIENTES) {
                if (a.visitaEn(indiceActual) == null) {
                    filas.add(new OgmPropiosAdapter.Fila(a, null, null));
                }
            } else {
                AnexoOgmPropio.UltimaVisita visita = a.visitaEn(filtro);
                if (visita != null) {
                    filas.add(new OgmPropiosAdapter.Fila(a, visita, ciclo.get(filtro).getEtiqueta()));
                }
            }
        }
        adapter.setFilas(filas);

        String titulo;
        if (filtro == FILTRO_TODOS) {
            titulo = "MIS CAMPOS OGM";
        } else if (filtro == FILTRO_PENDIENTES) {
            titulo = "PENDIENTES " + ciclo.get(indiceActual).getEtiqueta();
        } else {
            titulo = "VISITADOS " + ciclo.get(filtro).getEtiqueta() + " (" + ciclo.get(filtro).nombre + ")";
        }
        tvTituloLista.setText(titulo);
        tvCantidad.setText(filas.size() + (filas.size() == 1 ? " campo" : " campos"));

        if (filas.isEmpty()) {
            if (filtro >= 0 && filtro > indiceActual) {
                tvMensaje.setText("Este cuatrimestre aún no comienza.");
            } else if (anexos.isEmpty()) {
                tvMensaje.setText("No hay anexos OGM propios en este teléfono.");
            } else {
                tvMensaje.setText("No hay campos para mostrar con este filtro.");
            }
            tvMensaje.setVisibility(View.VISIBLE);
        } else {
            tvMensaje.setVisibility(View.GONE);
        }
    }

    private void pintarTabs() {
        final int[] filtroDeTab = {FILTRO_TODOS, FILTRO_PENDIENTES, 0, 1, 2};
        for (int i = 0; i < tabs.length; i++) {
            boolean activo = filtroDeTab[i] == filtro;
            tabs[i].setBackgroundResource(activo ? R.drawable.ogm_tab_sel : R.drawable.ogm_tab);
            tabs[i].setTypeface(null, activo ? android.graphics.Typeface.BOLD : android.graphics.Typeface.NORMAL);
        }
    }

    private static int anioInicioTemporada(String nombreTemporada) {
        if (nombreTemporada == null || nombreTemporada.length() < 4) return -1;
        try {
            return Integer.parseInt(nombreTemporada.substring(0, 4));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static String iniciales(String nombre) {
        StringBuilder sb = new StringBuilder();
        for (String parte : nombre.trim().split("\\s+")) {
            if (!parte.isEmpty() && sb.length() < 2) sb.append(Character.toUpperCase(parte.charAt(0)));
        }
        return sb.toString();
    }

    private static String texto(String s) {
        return (s != null) ? s.trim() : "";
    }

    private static String texto(Cursor c, int columna) {
        return c.isNull(columna) ? "" : texto(c.getString(columna));
    }
}
