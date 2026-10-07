package cl.smapdev.curimapu.fragments.checklist;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import cl.smapdev.curimapu.MainActivity;
import cl.smapdev.curimapu.R;
import cl.smapdev.curimapu.clases.relaciones.AnexoCompleto;
import cl.smapdev.curimapu.clases.tablas.CheckListSiembra;
import cl.smapdev.curimapu.clases.tablas.CheckListSiembraEvento;
import cl.smapdev.curimapu.clases.tablas.Config;
import cl.smapdev.curimapu.clases.tablas.Usuario;
import cl.smapdev.curimapu.clases.temporales.TempFirmas;
import cl.smapdev.curimapu.clases.utilidades.Utilidades;
import cl.smapdev.curimapu.fragments.dialogos.DialogFirma;
import es.dmoral.toasty.Toasty;

public class FragmentCheckListSiembra extends Fragment {


    private MainActivity activity;
    private SharedPreferences prefs;


    // botones que ocultan contenedores
    private ImageView btn_oculta_cabecera;
    private ImageView btn_oculta_suelo;
    private ImageView btn_oculta_siembra;
    private ImageView btn_oculta_chequeo_envases;
    private ImageView btn_oculta_siembra_anterior;
    private ImageView btn_oculta_regulacion_siembra;
    private ImageView btn_oculta_aseo_maquinaria_pre_siembra;
    private ImageView btn_oculta_aseo_maquinaria_post_siembra;
    private ImageView btn_oculta_general;
    private ImageView btn_oculta_ingreso;
    private ImageView btn_oculta_salida;

    //cabecera
    private TextView tv_numero_anexo;
    private TextView tv_variedad;
    private TextView tv_agricultor;
    private TextView tv_potrero;
    private TextView tv_rch;
    private TextView tv_sag_ogm;
    private TextView tv_sag_idase;
    private TextView tv_condicion_semilla;
    private TextView tv_supervisor_curimapu;

    //suelo - TICKET 2494 - 2026-09-29: rediseno, se saca chequeo_aislacion y cultivo_anterior
    private Spinner sp_cama_raices;
    private Spinner sp_medicion_compactacion;
    private EditText et_profundidad_cama_raices;
    private Spinner sp_cama_semilla;
    private Spinner sp_estado_humedad;
    private EditText et_temperatura_suelo;
    //aislacion - TICKET 2494 - 2026-09-29: nuevo apartado, para todas las especies
    private EditText et_aislacion_norte;
    private EditText et_aislacion_sur;
    private EditText et_aislacion_este;
    private EditText et_aislacion_oeste;

    //siembra
    private EditText et_protocolo_siembra;
    private RadioGroup grupo_fotografia_cartel;
    private RadioButton btn_fotografia_si;
    private RadioButton btn_fotografia_no;
    private RadioGroup grupo_indica_fecha_siembra;
    private RadioButton btn_indica_fecha_siembra_si;
    private RadioButton btn_indica_fecha_siembra_no;
    private EditText et_relacion_m;
    private EditText et_relacion_h;

    //chequeo de envases
    private RadioGroup grupo_foto_envase;
    private RadioButton btn_foto_envase_si;
    private RadioButton btn_foto_envase_no;
    private RadioGroup grupo_foto_semilla;
    private RadioButton btn_foto_semilla_si;
    private RadioButton btn_foto_semilla_no;
    // TICKET 2494 - 2026-10-01: Mezcla abierta a 8 campos de fertilizacion (et_mezcla ya no existe)
    private EditText et_cal_kg_ha;
    private EditText et_nitrogeno_pct;
    private EditText et_fosforo_pct;
    private EditText et_potasio_pct;
    private EditText et_magnesio_pct;
    private EditText et_azufre_pct;
    private EditText et_zinc_pct;
    private EditText et_boro_pct;
    private EditText et_cantidad_fertilizante;
    private EditText et_cantidad_envases_h;
    private EditText et_lote_hembra;
    private EditText et_cantidad_envases_m;
    private EditText et_lote_macho;

    //siembra anterior
    private EditText et_especie;
    private EditText et_variedad;
    private RadioGroup grupo_ogm;
    private RadioButton btn_ogm_si;
    private RadioButton btn_ogm_no;
    private EditText et_anexo_curimapu;

    //regulacion de siembra
    private EditText et_prestador_servicio;
    private Spinner sp_estado_discos;
    private EditText et_sembradora_marca;
    private EditText et_sembradora_modelo;
    private EditText et_trocha;
    private Spinner sp_tipo_sembradora;
    private Spinner sp_chequeo_selector;
    private Spinner sp_estado_maquina;
    private RadioGroup grupo_desterronadores;
    private RadioButton btn_desterronadores_si;
    private RadioButton btn_desterronadores_no;
    private Spinner sp_presion_neumaticos;
    private EditText et_especie_lote;
    private RadioGroup grupo_rueda_angosta;
    private RadioButton btn_rueda_angosta_si;
    private RadioButton btn_rueda_angosta_no;
    private EditText et_largo_guia;
    private EditText et_sistema_fertilizacion;
    private EditText et_distancia_hileras;
    private Spinner sp_cheque_caidas;
    private RadioGroup grupo_cheque_caidas;
    private RadioButton btn_cheque_caidas_si;
    private RadioButton btn_cheque_caidas_no;
    private EditText et_numero_semillas_mt;
    private EditText et_profundidad_fertilizante;
    private EditText et_profundidad_siembra;
    private EditText et_dist_entre_fert_semilla;

    //aseo maquinaria pre siembra
    private RadioGroup grupo_tarros_semilla;
    private RadioButton btn_tarros_semilla_si;
    private RadioButton btn_tarros_semilla_no;
    private RadioGroup grupo_discos_sembradores;
    private RadioButton btn_discos_sembradores_si;
    private RadioButton btn_discos_sembradores_no;
    private RadioGroup grupo_estructura_maquinaria;
    private RadioButton btn_estructura_maquinaria_si;
    private RadioButton btn_estructura_maquinaria_no;
    private EditText et_lugar_limpieza;
    private EditText et_responsable_aseo;
    private EditText et_rut_responsable_aseo;
    private Button btn_firma_responsable_aseo_ingreso;
    private ImageView check_firma_responsable_aseo_ingreso;
    private EditText et_responsable_revision_limpieza_ingreso;
    private Button btn_firma_responsable_revision_limpieza_ingreso;
    private ImageView check_firma_responsable_revision_limpieza_ingreso;

    //aseo maquinaria post siembra
    private RadioGroup grupo_tarros_semilla_post_siembra;
    private RadioButton btn_tarros_semilla_post_siembra_si;
    private RadioButton btn_tarros_semilla_post_siembra_no;
    private RadioGroup grupo_discos_sembradores_post_siembra;
    private RadioButton btn_discos_sembradores_post_siembra_si;
    private RadioButton btn_discos_sembradores_post_siembra_no;
    private RadioGroup grupo_estructura_maquinaria_post_siembra;
    private RadioButton btn_estructura_maquinaria_post_siembra_si;
    private RadioButton btn_estructura_maquinaria_post_siembra_no;
    private EditText et_lugar_limpieza_post_siembra;
    private EditText et_responsable_aseo_post_siembra;
    private EditText et_rut_responsable_aseo_post_siembra;
    private Button btn_firma_responsable_aseo_ingreso_post_siembra;
    private ImageView check_firma_responsable_aseo_ingreso_post_siembra;
    private EditText et_responsable_revision_limpieza_ingreso_post_siembra;
    private Button btn_firma_responsable_revision_limpieza_ingreso_post_siembra;
    private ImageView check_firma_responsable_revision_limpieza_ingreso_post_siembra;

    //general
    private Spinner sp_desempeno_siembra;
    private EditText et_observaciones_general;

    //ingreso
    private EditText et_fecha_ingreso;
    private EditText et_hora_ingreso;
    private EditText et_nombre_supervisor_ingreso_siembra;
    private EditText et_nombre_responsable_campo_ingreso;
    private Button btn_firma_responsable_campo_ingreso;
    private ImageView check_firma_responsable_campo_ingreso;

    private EditText et_operador_maquina_ingreso;
    private Button btn_firma_operario_maquina_ingreso;
    private ImageView check_firma_operario_maquina_ingreso;


    //salida
    private EditText et_fecha_termino;
    private EditText et_hora_termino;
    private EditText et_nombre_supervisor_termino_siembra;
    private EditText et_nombre_responsable_campo_termino;
    private Button btn_firma_responsable_campo_termino;
    private ImageView check_firma_responsable_campo_termino;
    private EditText et_operador_maquina_termino;
    private Button btn_firma_operario_maquina_termino;
    private ImageView check_firma_operario_maquina_termino;


    //botonera
    private Button btn_guardar_cl_siembra;
    private Button btn_cancelar_cl_siembra;


    private ConstraintLayout contenedor_vista;
    private ConstraintLayout cont_suelo;
    private ConstraintLayout cont_siembra;
    private ConstraintLayout cont_chequeo_envases;
    private ConstraintLayout cont_siembra_anterior;
    private ConstraintLayout cont_regulacion_siembra;
    private ConstraintLayout cont_aseo_maquinaria_pre_siembra;
    private ConstraintLayout cont_aseo_maquinaria_post_siembra;
    private ConstraintLayout cont_general;
    private ConstraintLayout cont_ingreso;
    private ConstraintLayout cont_termino;


    private AnexoCompleto anexoCompleto;
    private Usuario usuario;
    private Config config;

    private CheckListSiembra checkListSiembra;

    // TICKET 2494 - 2026-09-30: eventos de siembra (H/M1/M2/M3). Cada evento carga sus datos en
    // los mismos campos de Regulacion/Aseo/General/Ingreso/Salida (ver seleccionarEventoSiembra).
    // TICKET 2515 - 2026-10-02: el evento ya no tiene tipo ni fecha, se define por prestador + sembradora
    // (marca y modelo). El tipo H/M es del checklist completo y se elige al crearlo.
    private String tipoSiembra = null;
    private int posicionTipoAceptada = 0;
    private boolean revirtiendoTipoSiembra = false;
    private Spinner sp_tipo_siembra;
    private ImageView btn_oculta_regulacion_de_siembra;
    private ConstraintLayout cont_regulacion_de_siembra;
    private LinearLayout cont_tabs_eventos_siembra;
    private Button btn_agregar_evento_siembra;
    private androidx.constraintlayout.widget.Group group_campos_evento_siembra;
    private final List<CheckListSiembraEvento> eventosSiembra = new ArrayList<>();
    private CheckListSiembraEvento eventoActualSiembra = null;
    private String snapshotEventoActualCargado = "";

    private final ArrayList<String> chk_1 = new ArrayList<>();
    private final ArrayList<String> chk_2 = new ArrayList<>();
    private final ArrayList<String> chk_3 = new ArrayList<>();
    // TICKET 2494 - 2026-09-29: opciones nuevas de Cama de raices y Cama de semillas
    private final ArrayList<String> chkCamaRaices = new ArrayList<>();
    private final ArrayList<String> chkCamaSemilla = new ArrayList<>();


    public void setCheckListSiembra(CheckListSiembra checkListSiembra) {
        this.checkListSiembra = checkListSiembra;
    }

    public static FragmentCheckListSiembra newInstance(CheckListSiembra checkListSiembra) {

        FragmentCheckListSiembra fs = new FragmentCheckListSiembra();

        fs.setCheckListSiembra(checkListSiembra);

        return fs;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            activity = (MainActivity) context;
            prefs = activity.getSharedPreferences(Utilidades.SHARED_NAME, Context.MODE_PRIVATE);
        } else {
            throw new RuntimeException(context.toString() + " must be MainActivity");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_checklist_siembra, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bind(view);


        chk_1.addAll(Arrays.asList(getResources().getStringArray(R.array.desplegable_checklist_1)));
        chk_2.addAll(Arrays.asList(getResources().getStringArray(R.array.desplegable_checklist_2)));
        chk_3.addAll(Arrays.asList(getResources().getStringArray(R.array.desplegable_checklist_3)));
        // TICKET 2494 - 2026-09-29: rediseno seccion Suelo
        chkCamaRaices.addAll(Arrays.asList(getResources().getStringArray(R.array.desplegable_cama_raices)));
        chkCamaSemilla.addAll(Arrays.asList(getResources().getStringArray(R.array.desplegable_cama_semilla)));

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<AnexoCompleto> futureVisitas = executor.submit(() ->
                MainActivity
                        .myAppDB
                        .myDao()
                        .getAnexoCompletoById(
                                prefs.getString(Utilidades.SHARED_VISIT_ANEXO_ID, "")
                        )
        );

        Future<Config> futureConfig = executor.submit(() ->
                MainActivity.myAppDB.myDao().getConfig());


        try {
            anexoCompleto = futureVisitas.get();
            config = futureConfig.get();

            Future<Usuario> usuarioFuture = executor.submit(() ->
                    MainActivity.myAppDB.myDao().getUsuarioById(config.getId_usuario()));

            usuario = usuarioFuture.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        executor.shutdown();


        if (checkListSiembra != null) {

            levantarDatos();

        }

        // TICKET 2515 - 2026-10-02: el tipo (Hembra / Macho) se elige en un combobox, siempre editable
        configurarSpinnerTipoSiembra();

        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menu.clear();
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.CREATED);

        // la toolbar se arma una sola vez; despues solo cambia el subtitulo (ver actualizarToolbarTipoSiembra)
        Utilidades.setToolbar(activity, view, getResources().getString(R.string.app_name), "CHECKLIST SIEMBRA");
        actualizarToolbarTipoSiembra();
    }

    // TICKET 2515 - 2026-10-02: el checklist de siembra es de Hembra (H) o Macho 1/2/3 (M1, M2, M3).
    // Solo cambia el subtitulo: antes rearmaba toda la toolbar (Utilidades.setToolbar) en cada cambio del
    // combobox, acumulando listeners del drawer. Todo protegido: nunca debe tumbar la pantalla.
    private void actualizarToolbarTipoSiembra() {
        try {
            if (activity == null) return;
            androidx.appcompat.app.ActionBar actionBar = activity.getSupportActionBar();
            if (actionBar == null) return;
            String sufijo = CheckListSiembra.textoTipo(tipoSiembra).isEmpty() ? "" : " - " + CheckListSiembra.textoTipo(tipoSiembra);
            actionBar.setSubtitle("CHECKLIST SIEMBRA" + sufijo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // TICKET 2515 - 2026-10-02: conversiones tolerantes (coma decimal, texto raro, notacion cientifica de datos
    // antiguos): un valor mal escrito o heredado nunca debe tumbar el guardado, queda en 0.
    private double parseDoubleSeguro(String texto) {
        try {
            return Double.parseDouble(texto.trim().replace(',', '.'));
        } catch (Exception e) {
            return 0;
        }
    }

    private int parseIntSeguro(String texto) {
        try {
            return Integer.parseInt(texto.trim());
        } catch (Exception e) {
            return (int) parseDoubleSeguro(texto);
        }
    }

    // TICKET 2515 - 2026-10-02: true si OTRO checklist de siembra de este mismo anexo ya es de ese tipo
    // (un tipo H / M1 / M2 / M3 por anexo). El checklist que se esta editando no cuenta contra si mismo.
    // Cualquier error al consultar deja pasar (el servidor sigue siendo el que guarda); nunca tumba la pantalla.
    private boolean tipoSiembraYaUsadoEnAnexo(String tipo) {
        if (tipo == null || anexoCompleto == null) return false;
        try {
            final int idAnexo = Integer.parseInt(anexoCompleto.getAnexoContrato().getId_anexo_contrato());
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<List<CheckListSiembra>> future = executor.submit(() ->
                    MainActivity.myAppDB.DaoClSiembra().getAllClSiembraByAc(idAnexo));
            List<CheckListSiembra> existentes = future.get();
            executor.shutdown();
            if (existentes == null) return false;

            String tipoNormalizado = "M".equals(tipo) ? "M1" : tipo;
            for (CheckListSiembra otro : existentes) {
                if (checkListSiembra != null && otro.getId_cl_siembra() == checkListSiembra.getId_cl_siembra()) {
                    continue;
                }
                String tipoOtro = "M".equals(otro.getTipo_siembra()) ? "M1" : otro.getTipo_siembra();
                if (tipoNormalizado.equals(tipoOtro)) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Combobox Hembra / Macho. La seleccion inicial (checklist existente) se pone ANTES de enganchar el
    // listener para que la carga no cuente como un cambio del usuario.
    private void configurarSpinnerTipoSiembra() {
        ArrayAdapter<String> adapterTipo = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                Arrays.asList("--Seleccione--", "HEMBRA", "MACHO 1", "MACHO 2", "MACHO 3"));
        sp_tipo_siembra.setAdapter(adapterTipo);
        int posicionInicial = 0;
        for (int i = 0; i < CheckListSiembra.TIPOS_SIEMBRA.length; i++) {
            // un "M" suelto (pruebas anteriores) se muestra como MACHO 1
            if (CheckListSiembra.TIPOS_SIEMBRA[i].equals(tipoSiembra) || ("M".equals(tipoSiembra) && i == 1)) {
                posicionInicial = i + 1;
            }
        }
        sp_tipo_siembra.setSelection(posicionInicial);
        posicionTipoAceptada = posicionInicial;
        sp_tipo_siembra.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (revirtiendoTipoSiembra) {
                    revirtiendoTipoSiembra = false;
                    return;
                }
                String nuevoTipo = (position >= 1 && position <= CheckListSiembra.TIPOS_SIEMBRA.length) ? CheckListSiembra.TIPOS_SIEMBRA[position - 1] : null;

                // TICKET 2515 - 2026-10-02: un solo checklist por tipo en cada anexo
                // (solo cuando el usuario cambia el valor: la disparada inicial al abrir la pantalla no se valida,
                // asi un checklist antiguo con tipo repetido se puede abrir y editar sin bloqueos)
                if (position != posicionTipoAceptada && nuevoTipo != null && tipoSiembraYaUsadoEnAnexo(nuevoTipo)) {
                    Toasty.error(requireActivity(), "Este anexo ya tiene un checklist de tipo " + CheckListSiembra.textoTipo(nuevoTipo)
                            + ". Solo se permite uno por tipo.", Toast.LENGTH_LONG, true).show();
                    revirtiendoTipoSiembra = true;
                    sp_tipo_siembra.setSelection(posicionTipoAceptada);
                    return;
                }

                tipoSiembra = nuevoTipo;
                posicionTipoAceptada = position;
                actualizarToolbarTipoSiembra();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    // TICKET 2494 - 2026-09-29: colores pedidos en la reunion para Medicion de compactacion.
    // Reutiliza colores ya existentes en colors.xml (colorGreenLight, colorGold, colorRedLight)
    // en vez de crear colores nuevos.
    private void aplicarColorMedicionCompactacion(View itemView, String valor) {
        if (!(itemView instanceof TextView) || valor == null) {
            return;
        }
        TextView textView = (TextView) itemView;
        if (valor.equalsIgnoreCase("BUENO")) {
            textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorGreenLight));
        } else if (valor.equalsIgnoreCase("REGULAR")) {
            textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorGold));
        } else if (valor.equalsIgnoreCase("MALO")) {
            textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorRedLight));
        } else {
            textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorOnBackground));
        }
    }

    private void levantarDatos() {


        // TICKET 2494 - 2026-09-29: rediseno seccion Suelo y nuevo apartado Aislacion
        if (checkListSiembra.getCama_raices() != null && !checkListSiembra.getCama_raices().isEmpty()) {
            seleccionarConservandoValor(sp_cama_raices, checkListSiembra.getCama_raices());
        }

        if (checkListSiembra.getMedicion_compactacion() != null && !checkListSiembra.getMedicion_compactacion().isEmpty()) {
            seleccionarConservandoValor(sp_medicion_compactacion, checkListSiembra.getMedicion_compactacion());
            // TICKET 2494 - 2026-09-29: no depender solo del listener (setSelection en la carga
            // inicial no siempre lo dispara, y getSelectedView() puede ser null hasta que termine
            // el layout) - se aplica el color en un post() para asegurar que la vista ya exista.
            final String medicionCompactacionCargada = checkListSiembra.getMedicion_compactacion();
            sp_medicion_compactacion.post(new Runnable() {
                @Override
                public void run() {
                    aplicarColorMedicionCompactacion(sp_medicion_compactacion.getSelectedView(), medicionCompactacionCargada);
                }
            });
        }

        if (checkListSiembra.getProfundidad_cama_raices() != null && !checkListSiembra.getProfundidad_cama_raices().isEmpty()) {
            et_profundidad_cama_raices.setText(checkListSiembra.getProfundidad_cama_raices());
        }

        if (checkListSiembra.getCama_semilla() != null && !checkListSiembra.getCama_semilla().isEmpty()) {
            seleccionarConservandoValor(sp_cama_semilla, checkListSiembra.getCama_semilla());
        }

        if (checkListSiembra.getEstado_humedad() != null && !checkListSiembra.getEstado_humedad().isEmpty()) {
            seleccionarConservandoValor(sp_estado_humedad, checkListSiembra.getEstado_humedad());
        }

        if (checkListSiembra.getTemperatura_suelo() != null && !checkListSiembra.getTemperatura_suelo().isEmpty()) {
            et_temperatura_suelo.setText(checkListSiembra.getTemperatura_suelo());
        }

        if (checkListSiembra.getAislacion_norte() != null && !checkListSiembra.getAislacion_norte().isEmpty()) {
            et_aislacion_norte.setText(checkListSiembra.getAislacion_norte());
        }

        if (checkListSiembra.getAislacion_sur() != null && !checkListSiembra.getAislacion_sur().isEmpty()) {
            et_aislacion_sur.setText(checkListSiembra.getAislacion_sur());
        }

        if (checkListSiembra.getAislacion_este() != null && !checkListSiembra.getAislacion_este().isEmpty()) {
            et_aislacion_este.setText(checkListSiembra.getAislacion_este());
        }

        if (checkListSiembra.getAislacion_oeste() != null && !checkListSiembra.getAislacion_oeste().isEmpty()) {
            et_aislacion_oeste.setText(checkListSiembra.getAislacion_oeste());
        }

        if (checkListSiembra.getProtocolo_siembra() > 0) {
            et_protocolo_siembra.setText(String.valueOf(checkListSiembra.getProtocolo_siembra()));
        }

        if (checkListSiembra.getFotografia_cartel_identificacion() > 0) {
            btn_fotografia_si.setChecked((checkListSiembra.getFotografia_cartel_identificacion() == 1));
            btn_fotografia_no.setChecked((checkListSiembra.getFotografia_cartel_identificacion() == 2));
        }

        if (checkListSiembra.getSe_indica_fecha_siembra_lc() > 0) {
            btn_indica_fecha_siembra_si.setChecked((checkListSiembra.getSe_indica_fecha_siembra_lc() == 1));
            btn_indica_fecha_siembra_no.setChecked((checkListSiembra.getSe_indica_fecha_siembra_lc() == 2));
        }

        if (checkListSiembra.getRelacion_m() > 0) {
            et_relacion_m.setText(String.valueOf(checkListSiembra.getRelacion_m()));
        }

        if (checkListSiembra.getRelacion_h() > 0) {
            et_relacion_h.setText(String.valueOf(checkListSiembra.getRelacion_h()));
        }

        if (checkListSiembra.getFoto_envase() > 0) {
            btn_foto_envase_si.setChecked((checkListSiembra.getFoto_envase() == 1));
            btn_foto_envase_no.setChecked((checkListSiembra.getFoto_envase() == 2));
        }

        if (checkListSiembra.getFoto_semilla() > 0) {
            btn_foto_semilla_si.setChecked((checkListSiembra.getFoto_semilla() == 1));
            btn_foto_semilla_no.setChecked((checkListSiembra.getFoto_semilla() == 2));
        }

        // TICKET 2494 - 2026-10-01: Mezcla abierta a 8 campos de fertilizacion
        if (checkListSiembra.getCal_kg_ha() > 0) {
            et_cal_kg_ha.setText(String.valueOf(checkListSiembra.getCal_kg_ha()));
        }
        if (checkListSiembra.getNitrogeno_pct() > 0) {
            et_nitrogeno_pct.setText(String.valueOf(checkListSiembra.getNitrogeno_pct()));
        }
        if (checkListSiembra.getFosforo_pct() > 0) {
            et_fosforo_pct.setText(String.valueOf(checkListSiembra.getFosforo_pct()));
        }
        if (checkListSiembra.getPotasio_pct() > 0) {
            et_potasio_pct.setText(String.valueOf(checkListSiembra.getPotasio_pct()));
        }
        if (checkListSiembra.getMagnesio_pct() > 0) {
            et_magnesio_pct.setText(String.valueOf(checkListSiembra.getMagnesio_pct()));
        }
        if (checkListSiembra.getAzufre_pct() > 0) {
            et_azufre_pct.setText(String.valueOf(checkListSiembra.getAzufre_pct()));
        }
        if (checkListSiembra.getZinc_pct() > 0) {
            et_zinc_pct.setText(String.valueOf(checkListSiembra.getZinc_pct()));
        }
        if (checkListSiembra.getBoro_pct() > 0) {
            et_boro_pct.setText(String.valueOf(checkListSiembra.getBoro_pct()));
        }

        if (checkListSiembra.getCantidad_aplicada() > 0) {
            et_cantidad_fertilizante.setText(String.valueOf(checkListSiembra.getCantidad_aplicada()));
        }

        if (checkListSiembra.getCantidad_envase_h() > 0) {
            et_cantidad_envases_h.setText(String.valueOf(checkListSiembra.getCantidad_envase_h()));
        }

        if (checkListSiembra.getLote_hembra() != null && !checkListSiembra.getLote_hembra().isEmpty()) {
            et_lote_hembra.setText(String.valueOf(checkListSiembra.getLote_hembra()));
        }

        if (checkListSiembra.getCantidad_envase_m() > 0) {
            et_cantidad_envases_m.setText(String.valueOf(checkListSiembra.getCantidad_envase_m()));
        }

        if (checkListSiembra.getLote_macho() != null && !checkListSiembra.getLote_macho().isEmpty()) {
            et_lote_macho.setText(checkListSiembra.getLote_macho());
        }

        // TICKET 2494 - 2026-10-01: especie/variedad/ogm/anexo_curimapu (Siembra Anterior) ya no
        // se cargan desde la cabecera - ahora son por evento, ver cargarCamposDesdeEventoSiembra().

        // TICKET 2515 - 2026-10-02: tipo del checklist (H/M) y Regulacion Sembradora (prestador, estado de
        // discos, marca, modelo, trocha, etc.) ya no se cargan desde la cabecera: son por evento, ver
        // cargarCamposDesdeEventoSiembra(). En la cabecera quedan solo los 5 campos de Regulacion de Siembra.
        tipoSiembra = checkListSiembra.getTipo_siembra();

        if (checkListSiembra.getDistancia_hileras() > 0) {
            et_distancia_hileras.setText(String.valueOf(checkListSiembra.getDistancia_hileras()));
        }

        if (checkListSiembra.getNumero_semillas() > 0) {
            et_numero_semillas_mt.setText(String.valueOf(checkListSiembra.getNumero_semillas()));
        }

        if (checkListSiembra.getProfundidad_fertilizante() > 0) {
            et_profundidad_fertilizante.setText(String.valueOf(checkListSiembra.getProfundidad_fertilizante()));
        }

        if (checkListSiembra.getProfundidad_siembra() > 0) {
            et_profundidad_siembra.setText(String.valueOf(checkListSiembra.getProfundidad_siembra()));
        }

        if (checkListSiembra.getDistancia_fertilizante_semilla() > 0) {
            et_dist_entre_fert_semilla.setText(String.valueOf(checkListSiembra.getDistancia_fertilizante_semilla()));
        }

        // TICKET 2494 y 2515 - 2026-10-06: desde aca hasta el bloque de Salida (firma_operario_maquina_termino)
        // se carga desde la CABECERA lo de aseo pre/post, general, ingreso y salida (y sus firmas). Es un resto
        // del diseno anterior: esos datos ahora son por evento. No genera errores porque al final de este metodo
        // cargarEventosSiembraDesdeBD() -> cargarCamposDesdeEventoSiembra() sobrescribe todos estos campos (y el
        // estado de los botones de firma) con los del evento; sin eventos estos apartados quedan ocultos.
        if (checkListSiembra.getTarros_semilla_pre_siembra() > 0) {
            btn_tarros_semilla_si.setChecked((checkListSiembra.getTarros_semilla_pre_siembra() == 1));
            btn_tarros_semilla_no.setChecked((checkListSiembra.getTarros_semilla_pre_siembra() == 2));
        }

        if (checkListSiembra.getDiscos_sembradores_pre_siembra() > 0) {
            btn_discos_sembradores_si.setChecked((checkListSiembra.getDiscos_sembradores_pre_siembra() == 1));
            btn_discos_sembradores_no.setChecked((checkListSiembra.getDiscos_sembradores_pre_siembra() == 2));
        }

        if (checkListSiembra.getEstructura_maquinaria_pre_siembra() > 0) {
            btn_estructura_maquinaria_si.setChecked((checkListSiembra.getEstructura_maquinaria_pre_siembra() == 1));
            btn_estructura_maquinaria_no.setChecked((checkListSiembra.getEstructura_maquinaria_pre_siembra() == 2));
        }

        if (checkListSiembra.getLugar_limpieza_pre_siembra() != null && !checkListSiembra.getLugar_limpieza_pre_siembra().isEmpty()) {
            et_lugar_limpieza.setText(checkListSiembra.getLugar_limpieza_pre_siembra());
        }

        if (checkListSiembra.getResponsable_aseo_pre_siembra() != null && !checkListSiembra.getResponsable_aseo_pre_siembra().isEmpty()) {
            et_responsable_aseo.setText(checkListSiembra.getResponsable_aseo_pre_siembra());
        }

        if (checkListSiembra.getRut_responsable_aseo_pre_siembra() != null && !checkListSiembra.getRut_responsable_aseo_pre_siembra().isEmpty()) {
            et_rut_responsable_aseo.setText(checkListSiembra.getRut_responsable_aseo_pre_siembra());
        }

        if (checkListSiembra.getFirma_responsable_aso_pre_siembra() != null && !checkListSiembra.getFirma_responsable_aso_pre_siembra().isEmpty()) {
            btn_firma_responsable_aseo_ingreso.setEnabled(false);
            check_firma_responsable_aseo_ingreso.setVisibility(View.VISIBLE);
        }

        if (checkListSiembra.getResponsable_revision_limpieza_pre_siembra() != null && !checkListSiembra.getResponsable_revision_limpieza_pre_siembra().isEmpty()) {
            et_responsable_revision_limpieza_ingreso.setText(checkListSiembra.getResponsable_revision_limpieza_pre_siembra());
        }

        if (checkListSiembra.getFirma_revision_limpieza_pre_siembra() != null && !checkListSiembra.getFirma_revision_limpieza_pre_siembra().isEmpty()) {
            btn_firma_responsable_revision_limpieza_ingreso.setEnabled(false);
            check_firma_responsable_revision_limpieza_ingreso.setVisibility(View.VISIBLE);
        }

        if (checkListSiembra.getTarros_semilla_pre_siembra() > 0) {
            btn_tarros_semilla_post_siembra_si.setChecked((checkListSiembra.getTarros_semilla_post_siembra() == 1));
            btn_tarros_semilla_post_siembra_no.setChecked((checkListSiembra.getTarros_semilla_post_siembra() == 2));
        }

        if (checkListSiembra.getDiscos_sembradores_pre_siembra() > 0) {
            btn_discos_sembradores_post_siembra_si.setChecked((checkListSiembra.getDiscos_sembradores_post_siembra() == 1));
            btn_discos_sembradores_post_siembra_no.setChecked((checkListSiembra.getDiscos_sembradores_post_siembra() == 2));
        }

        if (checkListSiembra.getEstructura_maquinaria_pre_siembra() > 0) {
            btn_estructura_maquinaria_post_siembra_si.setChecked((checkListSiembra.getEstructura_maquinaria_post_cosecha() == 1));
            btn_estructura_maquinaria_post_siembra_no.setChecked((checkListSiembra.getEstructura_maquinaria_post_cosecha() == 2));
        }

        if (checkListSiembra.getLugar_limpieza_post_siembra() != null && !checkListSiembra.getLugar_limpieza_post_siembra().isEmpty()) {
            et_lugar_limpieza_post_siembra.setText(checkListSiembra.getLugar_limpieza_post_siembra());
        }

        if (checkListSiembra.getResponsable_aseo_post_siembra() != null && !checkListSiembra.getResponsable_aseo_post_siembra().isEmpty()) {
            et_responsable_aseo_post_siembra.setText(checkListSiembra.getResponsable_aseo_post_siembra());
        }

        if (checkListSiembra.getRut_responsable_aseo_post_siembra() != null && !checkListSiembra.getRut_responsable_aseo_post_siembra().isEmpty()) {
            et_rut_responsable_aseo_post_siembra.setText(checkListSiembra.getRut_responsable_aseo_post_siembra());
        }

        if (checkListSiembra.getFirma_responsable_aseo_post_siembra() != null && !checkListSiembra.getFirma_responsable_aseo_post_siembra().isEmpty()) {
            btn_firma_responsable_aseo_ingreso_post_siembra.setEnabled(false);
            check_firma_responsable_aseo_ingreso_post_siembra.setVisibility(View.VISIBLE);
        }

        if (checkListSiembra.getEncargado_revision_limpieza_post_siembra() != null && !checkListSiembra.getEncargado_revision_limpieza_post_siembra().isEmpty()) {
            et_responsable_revision_limpieza_ingreso_post_siembra.setText(checkListSiembra.getEncargado_revision_limpieza_post_siembra());
        }

        if (checkListSiembra.getFirma_revision_limpieza_post_siembra() != null && !checkListSiembra.getFirma_revision_limpieza_post_siembra().isEmpty()) {
            btn_firma_responsable_revision_limpieza_ingreso_post_siembra.setEnabled(false);
            check_firma_responsable_revision_limpieza_ingreso_post_siembra.setVisibility(View.VISIBLE);
        }


        if (checkListSiembra.getDesempeno_siembra() != null && !checkListSiembra.getDesempeno_siembra().isEmpty()) {
            seleccionarConservandoValor(sp_desempeno_siembra, checkListSiembra.getDesempeno_siembra());
        }

        if (checkListSiembra.getObservacion_general() != null && !checkListSiembra.getObservacion_general().isEmpty()) {
            et_observaciones_general.setText(checkListSiembra.getObservacion_general());
        }

        if (checkListSiembra.getFecha_ingreso() != null && !checkListSiembra.getFecha_ingreso().isEmpty()) {
            et_fecha_ingreso.setText(checkListSiembra.getFecha_ingreso());
        }

        if (checkListSiembra.getHora_ingreso() != null && !checkListSiembra.getHora_ingreso().isEmpty()) {
            et_hora_ingreso.setText(checkListSiembra.getHora_ingreso());
        }

        if (checkListSiembra.getNombre_supervisor_siembra() != null && !checkListSiembra.getNombre_supervisor_siembra().isEmpty()) {
            et_nombre_supervisor_ingreso_siembra.setText(checkListSiembra.getNombre_supervisor_siembra());
        }

        if (checkListSiembra.getNombre_responsable_campo() != null && !checkListSiembra.getNombre_responsable_campo().isEmpty()) {
            et_nombre_responsable_campo_ingreso.setText(checkListSiembra.getNombre_responsable_campo());
        }

        // TICKET 2494 - 2026-09-30: bug preexistente corregido, comparaba firma_responsable_campo_termino
        // en vez de firma_responsable_campo para habilitar/deshabilitar el boton de INGRESO
        if (checkListSiembra.getFirma_responsable_campo() != null && !checkListSiembra.getFirma_responsable_campo().isEmpty()) {
            btn_firma_responsable_campo_ingreso.setEnabled(false);
            check_firma_responsable_campo_ingreso.setVisibility(View.VISIBLE);
        }

        if (checkListSiembra.getNombre_operario_maquina() != null && !checkListSiembra.getNombre_operario_maquina().isEmpty()) {
            et_operador_maquina_ingreso.setText(checkListSiembra.getNombre_operario_maquina());
        }

        if (checkListSiembra.getFirma_operario_maquina() != null && !checkListSiembra.getFirma_operario_maquina().isEmpty()) {
            btn_firma_operario_maquina_ingreso.setEnabled(false);
            check_firma_operario_maquina_ingreso.setVisibility(View.VISIBLE);
        }

        if (checkListSiembra.getFecha_termino() != null && !checkListSiembra.getFecha_termino().isEmpty()) {
            et_fecha_termino.setText(checkListSiembra.getFecha_termino());
        }

        if (checkListSiembra.getHora_termino() != null && !checkListSiembra.getHora_termino().isEmpty()) {
            et_hora_termino.setText(checkListSiembra.getHora_termino());
        }

        if (checkListSiembra.getNombre_supervisor_siembra_termino() != null && !checkListSiembra.getNombre_supervisor_siembra_termino().isEmpty()) {
            et_nombre_supervisor_termino_siembra.setText(checkListSiembra.getNombre_supervisor_siembra_termino());
        }

        if (checkListSiembra.getNombre_responsable_campo_termino() != null && !checkListSiembra.getNombre_responsable_campo_termino().isEmpty()) {
            et_nombre_responsable_campo_termino.setText(checkListSiembra.getNombre_responsable_campo_termino());
        }

        if (checkListSiembra.getFirma_responsable_campo_termino() != null && !checkListSiembra.getFirma_responsable_campo_termino().isEmpty()) {
            btn_firma_responsable_campo_termino.setEnabled(false);
            check_firma_responsable_campo_termino.setVisibility(View.VISIBLE);
        }

        if (checkListSiembra.getNombre_operario_maquina_termino() != null && !checkListSiembra.getNombre_operario_maquina_termino().isEmpty()) {
            et_operador_maquina_termino.setText(checkListSiembra.getNombre_operario_maquina_termino());
        }

        if (checkListSiembra.getFirma_operario_maquina_termino() != null && !checkListSiembra.getFirma_operario_maquina_termino().isEmpty()) {
            btn_firma_operario_maquina_termino.setEnabled(false);
            check_firma_operario_maquina_termino.setVisibility(View.VISIBLE);
        }

        btn_guardar_cl_siembra.setText("EDITAR");

        // TICKET 2494 - 2026-09-30: eventos de siembra (H/M1/M2/M3) del checklist
        cargarEventosSiembraDesdeBD();
    }


    @Override
    public void onStart() {
        super.onStart();

        cargarDatosPrevios();
    }

    // TICKET 2494 - 2026-10-01: red de seguridad - Android llama onPause() cuando la pantalla deja
    // de estar en primer plano (boton atras, se bloquea el telefono, cambia de app, etc). Si hay un
    // evento activo con cambios que no pasaron por un cambio de tab ni por GUARDAR, se guardan aca.
    // OJO: onPause() debe ser rapido y no bloqueante (Android lo espera para completar la
    // transicion), por eso aca NO se usa guardarEventoSiembraActual() -que hace una consulta
    // bloqueante (.get()) para recoger firmas pendientes-, solo se guardan campos de texto/combos
    // en segundo plano sin esperar respuesta. Las firmas no se pierden: ya quedaron en TempFirmas
    // desde que se dibujaron, y se recogen en el proximo cambio de tab o GUARDAR.
    @Override
    public void onPause() {
        super.onPause();
        if (eventoActualSiembra != null && checkListSiembra != null) {
            leerCamposHaciaEventoSiembra(eventoActualSiembra);
            eventoActualSiembra.setClave_unica_cl_siembra(checkListSiembra.getClave_unica());
            eventoActualSiembra.setEstado_sincronizacion(0);
            final CheckListSiembraEvento eventoAGuardar = eventoActualSiembra;
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.submit(() -> MainActivity.myAppDB.DaoClSiembra().updateEvento(eventoAGuardar));
            executor.shutdown();
        }
    }

    private void cargarDatosPrevios() {
        if (anexoCompleto == null) {
            Toasty.error(requireActivity(), "No se pudo obtener informacion del anexo", Toast.LENGTH_LONG, true).show();
        }

        tv_numero_anexo.setText(anexoCompleto.getAnexoContrato().getAnexo_contrato());
        tv_variedad.setText(anexoCompleto.getVariedad().getDesc_variedad());
        tv_agricultor.setText(anexoCompleto.getAgricultor().getNombre_agricultor());

        tv_potrero.setText(anexoCompleto.getLotes().getNombre_lote());
        tv_rch.setText(anexoCompleto.getAnexoContrato().getRch());

        tv_sag_ogm.setText(anexoCompleto.getAnexoContrato().getSag_register_number());
        tv_sag_idase.setText(anexoCompleto.getAnexoContrato().getSag_register_idase());
        tv_condicion_semilla.setText(anexoCompleto.getAnexoContrato().getCondicion());
        tv_supervisor_curimapu.setText(usuario.getNombre() + " " + usuario.getApellido_p());


    }

    private void bind(View view) {
        //imageviews
        btn_oculta_cabecera = view.findViewById(R.id.btn_oculta_cabecera);
        btn_oculta_suelo = view.findViewById(R.id.btn_oculta_suelo);
        btn_oculta_siembra = view.findViewById(R.id.btn_oculta_siembra);
        btn_oculta_chequeo_envases = view.findViewById(R.id.btn_oculta_chequeo_envases);
        btn_oculta_siembra_anterior = view.findViewById(R.id.btn_oculta_siembra_anterior);
        btn_oculta_regulacion_siembra = view.findViewById(R.id.btn_oculta_regulacion_siembra);
        btn_oculta_regulacion_de_siembra = view.findViewById(R.id.btn_oculta_regulacion_de_siembra);
        sp_tipo_siembra = view.findViewById(R.id.sp_tipo_siembra);
        btn_oculta_aseo_maquinaria_pre_siembra = view.findViewById(R.id.btn_oculta_aseo_maquinaria_pre_siembra);
        btn_oculta_aseo_maquinaria_post_siembra = view.findViewById(R.id.btn_oculta_aseo_maquinaria_post_siembra);
        btn_oculta_general = view.findViewById(R.id.btn_oculta_general);
        btn_oculta_ingreso = view.findViewById(R.id.btn_oculta_ingreso);
        btn_oculta_salida = view.findViewById(R.id.btn_oculta_salida);

        //cabecera
        tv_numero_anexo = view.findViewById(R.id.tv_numero_anexo);
        tv_variedad = view.findViewById(R.id.tv_variedad);
        tv_agricultor = view.findViewById(R.id.tv_agricultor);
        tv_potrero = view.findViewById(R.id.tv_potrero);
        tv_rch = view.findViewById(R.id.tv_rch);
        tv_sag_ogm = view.findViewById(R.id.tv_sag_ogm);
        tv_sag_idase = view.findViewById(R.id.tv_sag_idase);
        tv_condicion_semilla = view.findViewById(R.id.tv_condicion_semilla);
        tv_supervisor_curimapu = view.findViewById(R.id.tv_supervisor_curimapu);


        //suelo - TICKET 2494 - 2026-09-29: rediseno seccion Suelo y nuevo apartado Aislacion
        sp_cama_raices = view.findViewById(R.id.sp_cama_raices);
        sp_medicion_compactacion = view.findViewById(R.id.sp_medicion_compactacion);
        // TICKET 2494 - 2026-09-29: pedido explicito de la reunion ("en la aplicacion debera
        // mostrar esos colores") - Bueno=verde, Regular=amarillo, Malo=rojo. Se aplica tanto al
        // seleccionar manualmente como al cargar un valor ya guardado (levantarDatos hace
        // setSelection, que dispara este mismo listener).
        sp_medicion_compactacion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View itemView, int position, long id) {
                aplicarColorMedicionCompactacion(itemView, (String) parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        et_profundidad_cama_raices = view.findViewById(R.id.et_profundidad_cama_raices);
        sp_cama_semilla = view.findViewById(R.id.sp_cama_semilla);
        sp_estado_humedad = view.findViewById(R.id.sp_estado_humedad);
        et_temperatura_suelo = view.findViewById(R.id.et_temperatura_suelo);
        et_aislacion_norte = view.findViewById(R.id.et_aislacion_norte);
        et_aislacion_sur = view.findViewById(R.id.et_aislacion_sur);
        et_aislacion_este = view.findViewById(R.id.et_aislacion_este);
        et_aislacion_oeste = view.findViewById(R.id.et_aislacion_oeste);

        //siembra
        et_protocolo_siembra = view.findViewById(R.id.et_protocolo_siembra);
        grupo_fotografia_cartel = view.findViewById(R.id.grupo_fotografia_cartel);
        btn_fotografia_si = view.findViewById(R.id.btn_fotografia_si);
        btn_fotografia_no = view.findViewById(R.id.btn_fotografia_no);
        grupo_indica_fecha_siembra = view.findViewById(R.id.grupo_indica_fecha_siembra);
        btn_indica_fecha_siembra_si = view.findViewById(R.id.btn_indica_fecha_siembra_si);
        btn_indica_fecha_siembra_no = view.findViewById(R.id.btn_indica_fecha_siembra_no);
        et_relacion_m = view.findViewById(R.id.et_relacion_m);
        et_relacion_h = view.findViewById(R.id.et_relacion_h);

        //chequeo de envases
        grupo_foto_envase = view.findViewById(R.id.grupo_foto_envase);
        btn_foto_envase_si = view.findViewById(R.id.btn_foto_envase_si);
        btn_foto_envase_no = view.findViewById(R.id.btn_foto_envase_no);
        grupo_foto_semilla = view.findViewById(R.id.grupo_foto_semilla);
        btn_foto_semilla_si = view.findViewById(R.id.btn_foto_semilla_si);
        btn_foto_semilla_no = view.findViewById(R.id.btn_foto_semilla_no);
        et_cal_kg_ha = view.findViewById(R.id.et_cal_kg_ha);
        et_nitrogeno_pct = view.findViewById(R.id.et_nitrogeno_pct);
        et_fosforo_pct = view.findViewById(R.id.et_fosforo_pct);
        et_potasio_pct = view.findViewById(R.id.et_potasio_pct);
        et_magnesio_pct = view.findViewById(R.id.et_magnesio_pct);
        et_azufre_pct = view.findViewById(R.id.et_azufre_pct);
        et_zinc_pct = view.findViewById(R.id.et_zinc_pct);
        et_boro_pct = view.findViewById(R.id.et_boro_pct);
        et_cantidad_fertilizante = view.findViewById(R.id.et_cantidad_fertilizante);
        et_cantidad_envases_h = view.findViewById(R.id.et_cantidad_envases_h);
        et_lote_hembra = view.findViewById(R.id.et_lote_hembra);
        et_cantidad_envases_m = view.findViewById(R.id.et_cantidad_envases_m);
        et_lote_macho = view.findViewById(R.id.et_lote_macho);

        //siembra anterior
        et_especie = view.findViewById(R.id.et_especie);
        et_variedad = view.findViewById(R.id.et_variedad);
        grupo_ogm = view.findViewById(R.id.grupo_ogm);
        btn_ogm_si = view.findViewById(R.id.btn_ogm_si);
        btn_ogm_no = view.findViewById(R.id.btn_ogm_no);
        et_anexo_curimapu = view.findViewById(R.id.et_anexo_curimapu);

        // TICKET 2494 - 2026-09-30: tira de eventos de siembra
        cont_tabs_eventos_siembra = view.findViewById(R.id.cont_tabs_eventos_siembra);
        btn_agregar_evento_siembra = view.findViewById(R.id.btn_agregar_evento_siembra);
        btn_agregar_evento_siembra.setOnClickListener(view1 -> mostrarDialogoNuevoEventoSiembra());
        group_campos_evento_siembra = view.findViewById(R.id.group_campos_evento_siembra);

        //regulacion de siembra
        et_prestador_servicio = view.findViewById(R.id.et_prestador_servicio);
        sp_estado_discos = view.findViewById(R.id.sp_estado_discos);
        et_sembradora_marca = view.findViewById(R.id.et_sembradora_marca);
        et_sembradora_modelo = view.findViewById(R.id.et_sembradora_modelo);
        et_trocha = view.findViewById(R.id.et_trocha);
        sp_tipo_sembradora = view.findViewById(R.id.sp_tipo_sembradora);
        sp_chequeo_selector = view.findViewById(R.id.sp_chequeo_selector);
        sp_estado_maquina = view.findViewById(R.id.sp_estado_maquina);
        grupo_desterronadores = view.findViewById(R.id.grupo_desterronadores);
        btn_desterronadores_si = view.findViewById(R.id.btn_desterronadores_si);
        btn_desterronadores_no = view.findViewById(R.id.btn_desterronadores_no);
        sp_presion_neumaticos = view.findViewById(R.id.sp_presion_neumaticos);
        et_especie_lote = view.findViewById(R.id.et_especie_lote);
        grupo_rueda_angosta = view.findViewById(R.id.grupo_rueda_angosta);
        btn_rueda_angosta_si = view.findViewById(R.id.btn_rueda_angosta_si);
        btn_rueda_angosta_no = view.findViewById(R.id.btn_rueda_angosta_no);
        et_largo_guia = view.findViewById(R.id.et_largo_guia);
        et_sistema_fertilizacion = view.findViewById(R.id.et_sistema_fertilizacion);
        et_distancia_hileras = view.findViewById(R.id.et_distancia_hileras);
        grupo_cheque_caidas = view.findViewById(R.id.grupo_cheque_caidas);
        btn_cheque_caidas_si = view.findViewById(R.id.btn_cheque_caidas_si);
        btn_cheque_caidas_no = view.findViewById(R.id.btn_cheque_caidas_no);
        et_numero_semillas_mt = view.findViewById(R.id.et_numero_semillas_mt);
        et_profundidad_fertilizante = view.findViewById(R.id.et_profundidad_fertilizante);
        et_profundidad_siembra = view.findViewById(R.id.et_profundidad_siembra);
        et_dist_entre_fert_semilla = view.findViewById(R.id.et_dist_entre_fert_semilla);

        //aseo maquinaria pre siembra
        grupo_tarros_semilla = view.findViewById(R.id.grupo_tarros_semilla);
        btn_tarros_semilla_si = view.findViewById(R.id.btn_tarros_semilla_si);
        btn_tarros_semilla_no = view.findViewById(R.id.btn_tarros_semilla_no);
        grupo_discos_sembradores = view.findViewById(R.id.grupo_discos_sembradores);
        btn_discos_sembradores_si = view.findViewById(R.id.btn_discos_sembradores_si);
        btn_discos_sembradores_no = view.findViewById(R.id.btn_discos_sembradores_no);
        grupo_estructura_maquinaria = view.findViewById(R.id.grupo_estructura_maquinaria);
        btn_estructura_maquinaria_si = view.findViewById(R.id.btn_estructura_maquinaria_si);
        btn_estructura_maquinaria_no = view.findViewById(R.id.btn_estructura_maquinaria_no);
        et_lugar_limpieza = view.findViewById(R.id.et_lugar_limpieza);
        et_responsable_aseo = view.findViewById(R.id.et_responsable_aseo);
        et_rut_responsable_aseo = view.findViewById(R.id.et_rut_responsable_aseo);
        btn_firma_responsable_aseo_ingreso = view.findViewById(R.id.btn_firma_responsable_aseo_ingreso);
        check_firma_responsable_aseo_ingreso = view.findViewById(R.id.check_firma_responsable_aseo_ingreso);
        et_responsable_revision_limpieza_ingreso = view.findViewById(R.id.et_responsable_revision_limpieza_ingreso);
        btn_firma_responsable_revision_limpieza_ingreso = view.findViewById(R.id.btn_firma_responsable_revision_limpieza_ingreso);
        check_firma_responsable_revision_limpieza_ingreso = view.findViewById(R.id.check_firma_responsable_revision_limpieza_ingreso);

        //aseo maquinaria post siembra
        grupo_tarros_semilla_post_siembra = view.findViewById(R.id.grupo_tarros_semilla_post_siembra);
        btn_tarros_semilla_post_siembra_si = view.findViewById(R.id.btn_tarros_semilla_post_siembra_si);
        btn_tarros_semilla_post_siembra_no = view.findViewById(R.id.btn_tarros_semilla_post_siembra_no);
        grupo_discos_sembradores_post_siembra = view.findViewById(R.id.grupo_discos_sembradores_post_siembra);
        btn_discos_sembradores_post_siembra_si = view.findViewById(R.id.btn_discos_sembradores_post_siembra_si);
        btn_discos_sembradores_post_siembra_no = view.findViewById(R.id.btn_discos_sembradores_post_siembra_no);
        grupo_estructura_maquinaria_post_siembra = view.findViewById(R.id.grupo_estructura_maquinaria_post_siembra);
        btn_estructura_maquinaria_post_siembra_si = view.findViewById(R.id.btn_estructura_maquinaria_post_siembra_si);
        btn_estructura_maquinaria_post_siembra_no = view.findViewById(R.id.btn_estructura_maquinaria_post_siembra_no);
        et_lugar_limpieza_post_siembra = view.findViewById(R.id.et_lugar_limpieza_post_siembra);
        et_responsable_aseo_post_siembra = view.findViewById(R.id.et_responsable_aseo_post_siembra);
        et_rut_responsable_aseo_post_siembra = view.findViewById(R.id.et_rut_responsable_aseo_post_siembra);
        btn_firma_responsable_aseo_ingreso_post_siembra = view.findViewById(R.id.btn_firma_responsable_aseo_ingreso_post_siembra);
        check_firma_responsable_aseo_ingreso_post_siembra = view.findViewById(R.id.check_firma_responsable_aseo_ingreso_post_siembra);
        et_responsable_revision_limpieza_ingreso_post_siembra = view.findViewById(R.id.et_responsable_revision_limpieza_ingreso_post_siembra);
        btn_firma_responsable_revision_limpieza_ingreso_post_siembra = view.findViewById(R.id.btn_firma_responsable_revision_limpieza_ingreso_post_siembra);
        check_firma_responsable_revision_limpieza_ingreso_post_siembra = view.findViewById(R.id.check_firma_responsable_revision_limpieza_ingreso_post_siembra);


        //general
        sp_desempeno_siembra = view.findViewById(R.id.sp_desempeno_siembra);
        et_observaciones_general = view.findViewById(R.id.et_observaciones_general);

        //ingreso
        et_fecha_ingreso = view.findViewById(R.id.et_fecha_ingreso);
        et_hora_ingreso = view.findViewById(R.id.et_hora_ingreso);
        et_nombre_supervisor_ingreso_siembra = view.findViewById(R.id.et_nombre_supervisor_ingreso_siembra);
        et_nombre_responsable_campo_ingreso = view.findViewById(R.id.et_nombre_responsable_campo_ingreso);
        btn_firma_responsable_campo_ingreso = view.findViewById(R.id.btn_firma_responsable_campo_ingreso);
        check_firma_responsable_campo_ingreso = view.findViewById(R.id.check_firma_responsable_campo_ingreso);
        et_operador_maquina_ingreso = view.findViewById(R.id.et_operador_maquina_ingreso);
        btn_firma_operario_maquina_ingreso = view.findViewById(R.id.btn_firma_operario_maquina_ingreso);
        check_firma_operario_maquina_ingreso = view.findViewById(R.id.check_firma_operario_maquina_ingreso);

        //salida
        et_fecha_termino = view.findViewById(R.id.et_fecha_termino);
        et_hora_termino = view.findViewById(R.id.et_hora_termino);
        et_nombre_supervisor_termino_siembra = view.findViewById(R.id.et_nombre_supervisor_termino_siembra);
        et_nombre_responsable_campo_termino = view.findViewById(R.id.et_nombre_responsable_campo_termino);
        btn_firma_responsable_campo_termino = view.findViewById(R.id.btn_firma_responsable_campo_termino);
        check_firma_responsable_campo_termino = view.findViewById(R.id.check_firma_responsable_campo_termino);
        et_operador_maquina_termino = view.findViewById(R.id.et_operador_maquina_termino);
        btn_firma_operario_maquina_termino = view.findViewById(R.id.btn_firma_operario_maquina_termino);
        check_firma_operario_maquina_termino = view.findViewById(R.id.check_firma_operario_maquina_termino);

        //constraint layout
        contenedor_vista = view.findViewById(R.id.contenedor_vista);
        cont_suelo = view.findViewById(R.id.cont_suelo);
        cont_siembra = view.findViewById(R.id.cont_siembra);
        cont_chequeo_envases = view.findViewById(R.id.cont_chequeo_envases);
        cont_siembra_anterior = view.findViewById(R.id.cont_siembra_anterior);
        cont_regulacion_siembra = view.findViewById(R.id.cont_regulacion_siembra);
        cont_regulacion_de_siembra = view.findViewById(R.id.cont_regulacion_de_siembra);
        cont_aseo_maquinaria_pre_siembra = view.findViewById(R.id.cont_aseo_maquinaria_pre_siembra);
        cont_aseo_maquinaria_post_siembra = view.findViewById(R.id.cont_aseo_maquinaria_post_siembra);
        cont_general = view.findViewById(R.id.cont_general);
        cont_ingreso = view.findViewById(R.id.cont_ingreso);
        cont_termino = view.findViewById(R.id.cont_termino);

        //botonera
        btn_guardar_cl_siembra = view.findViewById(R.id.btn_guardar_cl_siembra);
        btn_cancelar_cl_siembra = view.findViewById(R.id.btn_cancelar_cl_siembra);


        //ocultadores
        btn_oculta_cabecera.setOnClickListener(view1 -> {
            contenedor_vista.setVisibility((contenedor_vista.getVisibility() == View.VISIBLE) ? View.GONE : View.VISIBLE);
            btn_oculta_cabecera.setImageDrawable((contenedor_vista.getVisibility() == View.VISIBLE) ? getResources().getDrawable(R.drawable.ic_expand_up) : getResources().getDrawable(R.drawable.ic_expand_down));
        });
        btn_oculta_suelo.setOnClickListener(view1 -> {
            cont_suelo.setVisibility((cont_suelo.getVisibility() == View.VISIBLE) ? View.GONE : View.VISIBLE);
            btn_oculta_suelo.setImageDrawable((cont_suelo.getVisibility() == View.VISIBLE) ? getResources().getDrawable(R.drawable.ic_expand_up) : getResources().getDrawable(R.drawable.ic_expand_down));
        });
        btn_oculta_siembra.setOnClickListener(view1 -> {
            cont_siembra.setVisibility((cont_siembra.getVisibility() == View.VISIBLE) ? View.GONE : View.VISIBLE);
            btn_oculta_siembra.setImageDrawable((cont_siembra.getVisibility() == View.VISIBLE) ? getResources().getDrawable(R.drawable.ic_expand_up) : getResources().getDrawable(R.drawable.ic_expand_down));
        });
        btn_oculta_chequeo_envases.setOnClickListener(view1 -> {
            cont_chequeo_envases.setVisibility((cont_chequeo_envases.getVisibility() == View.VISIBLE) ? View.GONE : View.VISIBLE);
            btn_oculta_chequeo_envases.setImageDrawable((cont_chequeo_envases.getVisibility() == View.VISIBLE) ? getResources().getDrawable(R.drawable.ic_expand_up) : getResources().getDrawable(R.drawable.ic_expand_down));
        });
        btn_oculta_siembra_anterior.setOnClickListener(view1 -> {
            cont_siembra_anterior.setVisibility((cont_siembra_anterior.getVisibility() == View.VISIBLE) ? View.GONE : View.VISIBLE);
            btn_oculta_siembra_anterior.setImageDrawable((cont_siembra_anterior.getVisibility() == View.VISIBLE) ? getResources().getDrawable(R.drawable.ic_expand_up) : getResources().getDrawable(R.drawable.ic_expand_down));
        });
        // TICKET 2515 - 2026-10-02: nueva seccion Regulacion de Siembra (cabecera, 5 campos)
        btn_oculta_regulacion_de_siembra.setOnClickListener(view1 -> {
            cont_regulacion_de_siembra.setVisibility((cont_regulacion_de_siembra.getVisibility() == View.VISIBLE) ? View.GONE : View.VISIBLE);
            btn_oculta_regulacion_de_siembra.setImageDrawable((cont_regulacion_de_siembra.getVisibility() == View.VISIBLE) ? getResources().getDrawable(R.drawable.ic_expand_up) : getResources().getDrawable(R.drawable.ic_expand_down));
        });
        btn_oculta_regulacion_siembra.setOnClickListener(view1 -> {
            cont_regulacion_siembra.setVisibility((cont_regulacion_siembra.getVisibility() == View.VISIBLE) ? View.GONE : View.VISIBLE);
            btn_oculta_regulacion_siembra.setImageDrawable((cont_regulacion_siembra.getVisibility() == View.VISIBLE) ? getResources().getDrawable(R.drawable.ic_expand_up) : getResources().getDrawable(R.drawable.ic_expand_down));
        });
        btn_oculta_aseo_maquinaria_pre_siembra.setOnClickListener(view1 -> {
            cont_aseo_maquinaria_pre_siembra.setVisibility((cont_aseo_maquinaria_pre_siembra.getVisibility() == View.VISIBLE) ? View.GONE : View.VISIBLE);
            btn_oculta_aseo_maquinaria_pre_siembra.setImageDrawable((cont_aseo_maquinaria_pre_siembra.getVisibility() == View.VISIBLE) ? getResources().getDrawable(R.drawable.ic_expand_up) : getResources().getDrawable(R.drawable.ic_expand_down));
        });
        btn_oculta_aseo_maquinaria_post_siembra.setOnClickListener(view1 -> {
            cont_aseo_maquinaria_post_siembra.setVisibility((cont_aseo_maquinaria_post_siembra.getVisibility() == View.VISIBLE) ? View.GONE : View.VISIBLE);
            btn_oculta_aseo_maquinaria_post_siembra.setImageDrawable((cont_aseo_maquinaria_post_siembra.getVisibility() == View.VISIBLE) ? getResources().getDrawable(R.drawable.ic_expand_up) : getResources().getDrawable(R.drawable.ic_expand_down));
        });
        btn_oculta_general.setOnClickListener(view1 -> {
            cont_general.setVisibility((cont_general.getVisibility() == View.VISIBLE) ? View.GONE : View.VISIBLE);
            btn_oculta_general.setImageDrawable((cont_general.getVisibility() == View.VISIBLE) ? getResources().getDrawable(R.drawable.ic_expand_up) : getResources().getDrawable(R.drawable.ic_expand_down));
        });
        btn_oculta_ingreso.setOnClickListener(view1 -> {
            cont_ingreso.setVisibility((cont_ingreso.getVisibility() == View.VISIBLE) ? View.GONE : View.VISIBLE);
            btn_oculta_ingreso.setImageDrawable((cont_ingreso.getVisibility() == View.VISIBLE) ? getResources().getDrawable(R.drawable.ic_expand_up) : getResources().getDrawable(R.drawable.ic_expand_down));
        });
        btn_oculta_salida.setOnClickListener(view1 -> {
            cont_termino.setVisibility((cont_termino.getVisibility() == View.VISIBLE) ? View.GONE : View.VISIBLE);
            btn_oculta_salida.setImageDrawable((cont_termino.getVisibility() == View.VISIBLE) ? getResources().getDrawable(R.drawable.ic_expand_up) : getResources().getDrawable(R.drawable.ic_expand_down));
        });


        //botonera
        btn_guardar_cl_siembra.setOnClickListener(view1 -> showAlertForConfirmarGuardar());
        btn_cancelar_cl_siembra.setOnClickListener(view1 -> cancelar());


        et_fecha_ingreso.setOnClickListener(view1 -> levantarFecha(et_fecha_ingreso));
        et_fecha_ingreso.setOnFocusChangeListener((view1, b) -> {
            if (b) levantarFecha(et_fecha_ingreso);
        });
        et_fecha_termino.setOnFocusChangeListener((view1, b) -> {
            if (b) levantarFecha(et_fecha_termino);
        });
        et_fecha_termino.setOnClickListener(view1 -> levantarFecha(et_fecha_termino));

        et_hora_ingreso.setOnFocusChangeListener((view1, b) -> {
            if (b) Utilidades.levantarHora(et_hora_ingreso, requireActivity());
        });
        et_hora_termino.setOnFocusChangeListener((view1, b) -> {
            if (b) Utilidades.levantarHora(et_hora_termino, requireActivity());
        });
        et_hora_ingreso.setOnClickListener(view1 -> Utilidades.levantarHora(et_hora_ingreso, requireActivity()));
        et_hora_termino.setOnClickListener(view1 -> Utilidades.levantarHora(et_hora_termino, requireActivity()));


//        check_firma_responsable_aseo_ingreso
//                check_firma_responsable_revision_limpieza_ingreso


        btn_firma_responsable_aseo_ingreso.setOnClickListener(view1 -> {
            if (eventoActualSiembra == null) {
                Toasty.warning(requireActivity(), "Selecciona o crea un evento de siembra antes de firmar", Toast.LENGTH_LONG, true).show();
                return;
            }
            if (et_responsable_aseo.getText().toString().isEmpty() ||
                    et_rut_responsable_aseo.getText().toString().isEmpty()) {
                Toasty.warning(
                        requireActivity(),
                        "Debe ingresar nombre y rut de responsable",
                        Toast.LENGTH_LONG, true).show();
                return;
            }

            String tagEvento = tagFirmaEvento(Utilidades.DIALOG_TAG_RESPONSABLE_ASEO_INGRESO);

            FragmentTransaction ft = requireActivity().getSupportFragmentManager().beginTransaction();
            Fragment prev = requireActivity()
                    .getSupportFragmentManager()
                    .findFragmentByTag(tagEvento);
            if (prev != null) {
                ft.remove(prev);
            }

            String etRA = et_responsable_aseo.getText().toString()
                    .trim()
                    .toLowerCase(Locale.ROOT)
                    .replaceAll(" ", "_")
                    .replaceAll("ñ", "n")
                    .replaceAll("á", "a")
                    .replaceAll("é", "e")
                    .replaceAll("í", "i")
                    .replaceAll("ó", "o")
                    .replaceAll("ú", "u")
                    + "_" +
                    Utilidades.fechaActualConHora()
                            .replaceAll(" ", "")
                            .replaceAll("-", "")
                            .replaceAll(":", "_") + ".png";

            DialogFirma dialogo = DialogFirma.newInstance(
                    Utilidades.TIPO_DOCUMENTO_CHECKLIST_SIEMBRA,
                    etRA,
                    tagEvento,
                    (isSaved, path) -> {
                        if (isSaved) {
                            check_firma_responsable_aseo_ingreso.setVisibility(View.VISIBLE);
                            btn_firma_responsable_aseo_ingreso.setEnabled(false);
                            if (eventoActualSiembra != null) eventoActualSiembra.setFirma_responsable_aso_pre_siembra(path);
                        }
                    }
            );

            dialogo.show(ft, tagEvento);
        });

        btn_firma_responsable_revision_limpieza_ingreso.setOnClickListener(view1 -> {

            if (eventoActualSiembra == null) {
                Toasty.warning(requireActivity(), "Selecciona o crea un evento de siembra antes de firmar", Toast.LENGTH_LONG, true).show();
                return;
            }
            if (et_responsable_revision_limpieza_ingreso.getText().toString().isEmpty()) {
                Toasty.warning(
                        requireActivity(),
                        "Debe ingresar nombre de responsable",
                        Toast.LENGTH_LONG, true).show();
                return;
            }

            String tagEvento = tagFirmaEvento(Utilidades.DIALOG_TAG_REVISOR_LIMPIEZA_INGRESO);

            FragmentTransaction ft = requireActivity().getSupportFragmentManager().beginTransaction();
            Fragment prev = requireActivity()
                    .getSupportFragmentManager()
                    .findFragmentByTag(tagEvento);
            if (prev != null) {
                ft.remove(prev);
            }

            String etRA = et_responsable_revision_limpieza_ingreso.getText().toString()
                    .trim()
                    .toLowerCase(Locale.ROOT)
                    .replaceAll(" ", "_")
                    .replaceAll("ñ", "n")
                    .replaceAll("á", "a")
                    .replaceAll("é", "e")
                    .replaceAll("í", "i")
                    .replaceAll("ó", "o")
                    .replaceAll("ú", "u")
                    + "_" +
                    Utilidades.fechaActualConHora()
                            .replaceAll(" ", "")
                            .replaceAll("-", "")
                            .replaceAll(":", "_") + ".png";

            DialogFirma dialogo = DialogFirma.newInstance(
                    Utilidades.TIPO_DOCUMENTO_CHECKLIST_SIEMBRA,
                    etRA,
                    tagEvento,
                    (isSaved, path) -> {
                        if (isSaved) {
                            check_firma_responsable_revision_limpieza_ingreso
                                    .setVisibility(View.VISIBLE);
                            btn_firma_responsable_revision_limpieza_ingreso.setEnabled(false);
                            if (eventoActualSiembra != null) eventoActualSiembra.setFirma_revision_limpieza_pre_siembra(path);
                        }
                    }
            );

            dialogo.show(ft, tagEvento);

        });


        btn_firma_responsable_aseo_ingreso_post_siembra.setOnClickListener(view1 -> {

            if (eventoActualSiembra == null) {
                Toasty.warning(requireActivity(), "Selecciona o crea un evento de siembra antes de firmar", Toast.LENGTH_LONG, true).show();
                return;
            }
            if (et_responsable_aseo_post_siembra.getText().toString().isEmpty() ||
                    et_rut_responsable_aseo_post_siembra.getText().toString().isEmpty()) {
                Toasty.warning(
                        requireActivity(),
                        "Debe ingresar nombre y rut de responsable",
                        Toast.LENGTH_LONG, true).show();
                return;
            }

            String tagEvento = tagFirmaEvento(Utilidades.DIALOG_TAG_RESPONSABLE_ASEO_SALIDA);

            FragmentTransaction ft = requireActivity().getSupportFragmentManager().beginTransaction();
            Fragment prev = requireActivity()
                    .getSupportFragmentManager()
                    .findFragmentByTag(tagEvento);
            if (prev != null) {
                ft.remove(prev);
            }

            String etRA = et_responsable_aseo_post_siembra.getText().toString()
                    .trim()
                    .toLowerCase(Locale.ROOT)
                    .replaceAll(" ", "_")
                    .replaceAll("ñ", "n")
                    .replaceAll("á", "a")
                    .replaceAll("é", "e")
                    .replaceAll("í", "i")
                    .replaceAll("ó", "o")
                    .replaceAll("ú", "u")
                    + "_" +
                    Utilidades.fechaActualConHora()
                            .replaceAll(" ", "")
                            .replaceAll("-", "")
                            .replaceAll(":", "_") + ".png";

            DialogFirma dialogo = DialogFirma.newInstance(
                    Utilidades.TIPO_DOCUMENTO_CHECKLIST_SIEMBRA,
                    etRA,
                    tagEvento,
                    (isSaved, path) -> {
                        if (isSaved) {
                            check_firma_responsable_aseo_ingreso_post_siembra
                                    .setVisibility(View.VISIBLE);
                            btn_firma_responsable_aseo_ingreso_post_siembra.setEnabled(false);
                            if (eventoActualSiembra != null) eventoActualSiembra.setFirma_responsable_aseo_post_siembra(path);
                        }
                    }
            );

            dialogo.show(ft, tagEvento);
        });

        btn_firma_responsable_revision_limpieza_ingreso_post_siembra.setOnClickListener(view1 -> {

            if (eventoActualSiembra == null) {
                Toasty.warning(requireActivity(), "Selecciona o crea un evento de siembra antes de firmar", Toast.LENGTH_LONG, true).show();
                return;
            }
            if (et_responsable_revision_limpieza_ingreso_post_siembra.getText().toString().isEmpty()) {
                Toasty.warning(
                        requireActivity(),
                        "Debe ingresar nombre de responsable",
                        Toast.LENGTH_LONG, true).show();
                return;
            }

            String tagEvento = tagFirmaEvento(Utilidades.DIALOG_TAG_REVISOR_LIMPIEZA_SALIDA);

            FragmentTransaction ft = requireActivity().getSupportFragmentManager().beginTransaction();
            Fragment prev = requireActivity()
                    .getSupportFragmentManager()
                    .findFragmentByTag(tagEvento);
            if (prev != null) {
                ft.remove(prev);
            }

            String etRA = et_responsable_revision_limpieza_ingreso_post_siembra.getText().toString()
                    .trim()
                    .toLowerCase(Locale.ROOT)
                    .replaceAll(" ", "_")
                    .replaceAll("ñ", "n")
                    .replaceAll("á", "a")
                    .replaceAll("é", "e")
                    .replaceAll("í", "i")
                    .replaceAll("ó", "o")
                    .replaceAll("ú", "u")
                    + "_" +
                    Utilidades.fechaActualConHora()
                            .replaceAll(" ", "")
                            .replaceAll("-", "")
                            .replaceAll(":", "_") + ".png";

            DialogFirma dialogo = DialogFirma.newInstance(
                    Utilidades.TIPO_DOCUMENTO_CHECKLIST_SIEMBRA,
                    etRA,
                    tagEvento,
                    (isSaved, path) -> {
                        if (isSaved) {
                            check_firma_responsable_revision_limpieza_ingreso_post_siembra
                                    .setVisibility(View.VISIBLE);
                            btn_firma_responsable_revision_limpieza_ingreso_post_siembra.setEnabled(false);
                            if (eventoActualSiembra != null) eventoActualSiembra.setFirma_revision_limpieza_post_siembra(path);
                        }
                    }
            );

            dialogo.show(ft, tagEvento);
        });


        btn_firma_responsable_campo_ingreso.setOnClickListener(view1 -> {
            if (eventoActualSiembra == null) {
                Toasty.warning(requireActivity(), "Selecciona o crea un evento de siembra antes de firmar", Toast.LENGTH_LONG, true).show();
                return;
            }
            if (et_nombre_responsable_campo_ingreso.getText().toString().isEmpty()) {
                Toasty.warning(
                        requireActivity(),
                        "Debe ingresar nombre de responsable",
                        Toast.LENGTH_LONG, true).show();
                return;
            }

            String tagEvento = tagFirmaEvento(Utilidades.DIALOG_TAG_RESPONSABLE_CAMPO_INGRESO);

            FragmentTransaction ft = requireActivity().getSupportFragmentManager().beginTransaction();
            Fragment prev = requireActivity()
                    .getSupportFragmentManager()
                    .findFragmentByTag(tagEvento);
            if (prev != null) {
                ft.remove(prev);
            }

            String etRA = et_nombre_responsable_campo_ingreso.getText().toString()
                    .trim()
                    .toLowerCase(Locale.ROOT)
                    .replaceAll(" ", "_")
                    .replaceAll("ñ", "n")
                    .replaceAll("á", "a")
                    .replaceAll("é", "e")
                    .replaceAll("í", "i")
                    .replaceAll("ó", "o")
                    .replaceAll("ú", "u")
                    + "_" +
                    Utilidades.fechaActualConHora()
                            .replaceAll(" ", "")
                            .replaceAll("-", "")
                            .replaceAll(":", "_") + ".png";

            DialogFirma dialogo = DialogFirma.newInstance(
                    Utilidades.TIPO_DOCUMENTO_CHECKLIST_SIEMBRA,
                    etRA,
                    tagEvento,
                    (isSaved, path) -> {
                        if (isSaved) {
                            check_firma_responsable_campo_ingreso
                                    .setVisibility(View.VISIBLE);
                            btn_firma_responsable_campo_ingreso.setEnabled(false);
                            if (eventoActualSiembra != null) eventoActualSiembra.setFirma_responsable_campo(path);
                        }
                    }
            );

            dialogo.show(ft, tagEvento);
        });

        btn_firma_operario_maquina_ingreso.setOnClickListener(view1 -> {
            if (eventoActualSiembra == null) {
                Toasty.warning(requireActivity(), "Selecciona o crea un evento de siembra antes de firmar", Toast.LENGTH_LONG, true).show();
                return;
            }
            if (et_operador_maquina_ingreso.getText().toString().isEmpty()) {
                Toasty.warning(
                        requireActivity(),
                        "Debe ingresar nombre de responsable",
                        Toast.LENGTH_LONG, true).show();
                return;
            }

            String tagEvento = tagFirmaEvento(Utilidades.DIALOG_TAG_RESPONSABLE_OPERARIO_INGRESO);

            FragmentTransaction ft = requireActivity().getSupportFragmentManager().beginTransaction();
            Fragment prev = requireActivity()
                    .getSupportFragmentManager()
                    .findFragmentByTag(tagEvento);
            if (prev != null) {
                ft.remove(prev);
            }

            String etRA = et_operador_maquina_ingreso.getText().toString()
                    .trim()
                    .toLowerCase(Locale.ROOT)
                    .replaceAll(" ", "_")
                    .replaceAll("ñ", "n")
                    .replaceAll("á", "a")
                    .replaceAll("é", "e")
                    .replaceAll("í", "i")
                    .replaceAll("ó", "o")
                    .replaceAll("ú", "u")
                    + "_" +
                    Utilidades.fechaActualConHora()
                            .replaceAll(" ", "")
                            .replaceAll("-", "")
                            .replaceAll(":", "_") + ".png";

            DialogFirma dialogo = DialogFirma.newInstance(
                    Utilidades.TIPO_DOCUMENTO_CHECKLIST_SIEMBRA,
                    etRA,
                    tagEvento,
                    (isSaved, path) -> {
                        if (isSaved) {
                            check_firma_operario_maquina_ingreso
                                    .setVisibility(View.VISIBLE);
                            btn_firma_operario_maquina_ingreso.setEnabled(false);
                            if (eventoActualSiembra != null) eventoActualSiembra.setFirma_operario_maquina(path);
                        }
                    }
            );

            dialogo.show(ft, tagEvento);
        });

        btn_firma_operario_maquina_termino.setOnClickListener(view1 -> {
            if (eventoActualSiembra == null) {
                Toasty.warning(requireActivity(), "Selecciona o crea un evento de siembra antes de firmar", Toast.LENGTH_LONG, true).show();
                return;
            }
            if (et_operador_maquina_termino.getText().toString().isEmpty()) {
                Toasty.warning(
                        requireActivity(),
                        "Debe ingresar nombre de responsable",
                        Toast.LENGTH_LONG, true).show();
                return;
            }

            String tagEvento = tagFirmaEvento(Utilidades.DIALOG_TAG_RESPONSABLE_OPERARIO_TERMINO);

            FragmentTransaction ft = requireActivity().getSupportFragmentManager().beginTransaction();
            Fragment prev = requireActivity()
                    .getSupportFragmentManager()
                    .findFragmentByTag(tagEvento);
            if (prev != null) {
                ft.remove(prev);
            }

            String etRA = et_operador_maquina_termino.getText().toString()
                    .trim()
                    .toLowerCase(Locale.ROOT)
                    .replaceAll(" ", "_")
                    .replaceAll("ñ", "n")
                    .replaceAll("á", "a")
                    .replaceAll("é", "e")
                    .replaceAll("í", "i")
                    .replaceAll("ó", "o")
                    .replaceAll("ú", "u")
                    + "_" +
                    Utilidades.fechaActualConHora()
                            .replaceAll(" ", "")
                            .replaceAll("-", "")
                            .replaceAll(":", "_") + ".png";

            DialogFirma dialogo = DialogFirma.newInstance(
                    Utilidades.TIPO_DOCUMENTO_CHECKLIST_SIEMBRA,
                    etRA,
                    tagEvento,
                    (isSaved, path) -> {
                        if (isSaved) {
                            check_firma_operario_maquina_termino
                                    .setVisibility(View.VISIBLE);
                            btn_firma_operario_maquina_termino.setEnabled(false);
                            if (eventoActualSiembra != null) eventoActualSiembra.setFirma_operario_maquina_termino(path);
                        }
                    }
            );

            dialogo.show(ft, tagEvento);
        });

        btn_firma_responsable_campo_termino.setOnClickListener(view1 -> {

            if (eventoActualSiembra == null) {
                Toasty.warning(requireActivity(), "Selecciona o crea un evento de siembra antes de firmar", Toast.LENGTH_LONG, true).show();
                return;
            }
            if (et_nombre_responsable_campo_termino.getText().toString().isEmpty()) {
                Toasty.warning(
                        requireActivity(),
                        "Debe ingresar nombre de responsable",
                        Toast.LENGTH_LONG, true).show();
                return;
            }

            String tagEvento = tagFirmaEvento(Utilidades.DIALOG_TAG_RESPONSABLE_CAMPO_TERMINO);

            FragmentTransaction ft = requireActivity().getSupportFragmentManager().beginTransaction();
            Fragment prev = requireActivity()
                    .getSupportFragmentManager()
                    .findFragmentByTag(tagEvento);
            if (prev != null) {
                ft.remove(prev);
            }

            String etRA = et_nombre_responsable_campo_termino.getText().toString()
                    .trim()
                    .toLowerCase(Locale.ROOT)
                    .replaceAll(" ", "_")
                    .replaceAll("ñ", "n")
                    .replaceAll("á", "a")
                    .replaceAll("é", "e")
                    .replaceAll("í", "i")
                    .replaceAll("ó", "o")
                    .replaceAll("ú", "u")
                    + "_" +
                    Utilidades.fechaActualConHora()
                            .replaceAll(" ", "")
                            .replaceAll("-", "")
                            .replaceAll(":", "_") + ".png";

            DialogFirma dialogo = DialogFirma.newInstance(
                    Utilidades.TIPO_DOCUMENTO_CHECKLIST_SIEMBRA,
                    etRA,
                    tagEvento,
                    (isSaved, path) -> {
                        if (isSaved) {
                            check_firma_responsable_campo_termino
                                    .setVisibility(View.VISIBLE);
                            btn_firma_responsable_campo_termino.setEnabled(false);
                            if (eventoActualSiembra != null) eventoActualSiembra.setFirma_responsable_campo_termino(path);
                        }
                    }
            );

            dialogo.show(ft, tagEvento);

        });

    }

    private boolean guardar(int state, String description) {

        String comparaSpinner = "--Seleccione--";

        // TICKET 2515 - 2026-10-02: el checklist es de Hembra o de Macho, no se puede guardar sin elegir
        if (tipoSiembra == null || tipoSiembra.isEmpty()) {
            Toasty.error(requireActivity(), "Debes seleccionar el tipo de checklist (Hembra, Macho 1, Macho 2 o Macho 3)", Toast.LENGTH_LONG, true).show();
            return false;
        }
        // un solo checklist por tipo en cada anexo (tambien se valida al elegirlo en el combobox)
        // (si el checklist ya existia con ese mismo tipo no se vuelve a validar, para no bloquear la edicion de
        // checklists antiguos que quedaron con tipo repetido)
        boolean tipoSinCambio = checkListSiembra != null && checkListSiembra.getTipo_siembra() != null
                && ("M".equals(checkListSiembra.getTipo_siembra()) ? "M1" : checkListSiembra.getTipo_siembra())
                .equals("M".equals(tipoSiembra) ? "M1" : tipoSiembra);
        if (!tipoSinCambio && tipoSiembraYaUsadoEnAnexo(tipoSiembra)) {
            Toasty.error(requireActivity(), "Este anexo ya tiene un checklist de tipo " + CheckListSiembra.textoTipo(tipoSiembra)
                    + ". Solo se permite uno por tipo.", Toast.LENGTH_LONG, true).show();
            return false;
        }
        //crear clase y guardar en bd

        //levantar modal para preguntar si quiere guardar y activar o solo guardar.
        CheckListSiembra siembra = new CheckListSiembra();

        siembra.setEstado_documento(state);
        siembra.setApellido_checklist(description);
        siembra.setId_ac_cl_siembra(Integer.parseInt(anexoCompleto.getAnexoContrato().getId_anexo_contrato()));


        if (checkListSiembra == null) {
            String claveUnica = config.getId()
                    + "" + config.getId_usuario()
                    + "" + Utilidades.fechaActualConHora()
                    .replaceAll(" ", "")
                    .replaceAll("-", "")
                    .replaceAll(":", "");

            siembra.setClave_unica(claveUnica);
        } else {
            siembra.setClave_unica(checkListSiembra.getClave_unica());
        }

        //suelo - TICKET 2494 - 2026-09-29: rediseno seccion Suelo y nuevo apartado Aislacion
        if (!textoSpinner(sp_cama_raices).equals(comparaSpinner)) {
            String camaRaices = textoSpinner(sp_cama_raices);
            siembra.setCama_raices(camaRaices);
        }

        if (!textoSpinner(sp_medicion_compactacion).equals(comparaSpinner)) {
            String medicionCompactacion = textoSpinner(sp_medicion_compactacion);
            siembra.setMedicion_compactacion(medicionCompactacion);
        }

        if (!et_profundidad_cama_raices.getText().toString().isEmpty()) {
            String profundidadCamaRaices = et_profundidad_cama_raices.getText().toString();
            siembra.setProfundidad_cama_raices(profundidadCamaRaices);
        }

        if (!textoSpinner(sp_cama_semilla).equals(comparaSpinner)) {
            String cama_semilla = textoSpinner(sp_cama_semilla);
            siembra.setCama_semilla(cama_semilla);
        }

        if (!textoSpinner(sp_estado_humedad).equals(comparaSpinner)) {
            String estadoHumedad = textoSpinner(sp_estado_humedad);
            siembra.setEstado_humedad(estadoHumedad);
        }

        if (!et_temperatura_suelo.getText().toString().isEmpty()) {
            String temperaturaSuelo = et_temperatura_suelo.getText().toString();
            siembra.setTemperatura_suelo(temperaturaSuelo);
        }

        if (!et_aislacion_norte.getText().toString().isEmpty()) {
            siembra.setAislacion_norte(et_aislacion_norte.getText().toString());
        }

        if (!et_aislacion_sur.getText().toString().isEmpty()) {
            siembra.setAislacion_sur(et_aislacion_sur.getText().toString());
        }

        if (!et_aislacion_este.getText().toString().isEmpty()) {
            siembra.setAislacion_este(et_aislacion_este.getText().toString());
        }

        if (!et_aislacion_oeste.getText().toString().isEmpty()) {
            siembra.setAislacion_oeste(et_aislacion_oeste.getText().toString());
        }

        //siembra
        if (!et_protocolo_siembra.getText().toString().isEmpty()) {
            String protocoloSiembra = et_protocolo_siembra.getText().toString();
            siembra.setProtocolo_siembra(parseIntSeguro(protocoloSiembra));
        }

        if (btn_fotografia_si.isChecked() || btn_fotografia_no.isChecked()) {
            int fotografiaCartel = (btn_fotografia_si.isChecked()) ? 1 : 2;
            siembra.setFotografia_cartel_identificacion(fotografiaCartel);
        }

        if (btn_indica_fecha_siembra_si.isChecked() || btn_indica_fecha_siembra_no.isChecked()) {
            int indicaFechaSiembra = (btn_indica_fecha_siembra_si.isChecked()) ? 1 : 2;
            siembra.setSe_indica_fecha_siembra_lc(indicaFechaSiembra);
        }

        if (!et_relacion_m.getText().toString().isEmpty()) {
            String relacionM = et_relacion_m.getText().toString();
            siembra.setRelacion_m(parseDoubleSeguro(relacionM));
        }

        if (!et_relacion_h.getText().toString().isEmpty()) {
            String relacionH = et_relacion_h.getText().toString();
            siembra.setRelacion_h(parseDoubleSeguro(relacionH));
        }

        //chequeo envases

        if (btn_foto_envase_si.isChecked() || btn_foto_envase_no.isChecked()) {
            int fotoEnvase = (btn_foto_envase_si.isChecked()) ? 1 : 2;
            siembra.setFoto_envase(fotoEnvase);
        }

        if (btn_foto_semilla_si.isChecked() || btn_foto_semilla_no.isChecked()) {
            int fotoSemilla = (btn_foto_semilla_si.isChecked()) ? 1 : 2;
            siembra.setFoto_semilla(fotoSemilla);
        }

        // TICKET 2494 - 2026-10-01: Mezcla abierta a 8 campos de fertilizacion
        if (!et_cal_kg_ha.getText().toString().isEmpty()) {
            siembra.setCal_kg_ha(parseDoubleSeguro(et_cal_kg_ha.getText().toString()));
        }
        if (!et_nitrogeno_pct.getText().toString().isEmpty()) {
            siembra.setNitrogeno_pct(parseDoubleSeguro(et_nitrogeno_pct.getText().toString()));
        }
        if (!et_fosforo_pct.getText().toString().isEmpty()) {
            siembra.setFosforo_pct(parseDoubleSeguro(et_fosforo_pct.getText().toString()));
        }
        if (!et_potasio_pct.getText().toString().isEmpty()) {
            siembra.setPotasio_pct(parseDoubleSeguro(et_potasio_pct.getText().toString()));
        }
        if (!et_magnesio_pct.getText().toString().isEmpty()) {
            siembra.setMagnesio_pct(parseDoubleSeguro(et_magnesio_pct.getText().toString()));
        }
        if (!et_azufre_pct.getText().toString().isEmpty()) {
            siembra.setAzufre_pct(parseDoubleSeguro(et_azufre_pct.getText().toString()));
        }
        if (!et_zinc_pct.getText().toString().isEmpty()) {
            siembra.setZinc_pct(parseDoubleSeguro(et_zinc_pct.getText().toString()));
        }
        if (!et_boro_pct.getText().toString().isEmpty()) {
            siembra.setBoro_pct(parseDoubleSeguro(et_boro_pct.getText().toString()));
        }

        if (!et_cantidad_fertilizante.getText().toString().isEmpty()) {
            String cantidadFertilizante = et_cantidad_fertilizante.getText().toString();
            siembra.setCantidad_aplicada(parseDoubleSeguro(cantidadFertilizante));
        }


        if (!et_cantidad_envases_h.getText().toString().isEmpty()) {
            String cantidadEnvasesH = et_cantidad_envases_h.getText().toString();
            siembra.setCantidad_envase_h(parseDoubleSeguro(cantidadEnvasesH));
        }

        if (!et_lote_hembra.getText().toString().isEmpty()) {
            String loteHembra = et_lote_hembra.getText().toString();
            siembra.setLote_hembra(loteHembra);
        }

        if (!et_cantidad_envases_m.getText().toString().isEmpty()) {
            String cantidadEnvasesM = et_cantidad_envases_m.getText().toString();
            siembra.setCantidad_envase_m(parseDoubleSeguro(cantidadEnvasesM));
        }

        if (!et_lote_macho.getText().toString().isEmpty()) {
            String loteMacho = et_lote_macho.getText().toString();
            siembra.setLote_macho(loteMacho);
        }


        //siembra anterior

        // TICKET 2494 - 2026-10-01: especie/variedad/ogm/anexo_curimapu (Siembra Anterior) ya no se
        // guardan en la cabecera - ahora son por evento, ver leerCamposHaciaEventoSiembra().

        // TICKET 2515 - 2026-10-02: regulacion de siembra (cabecera) = SOLO estos 5 campos. Los datos de
        // la sembradora (prestador, estado de discos, marca, modelo, trocha, tipo, selector, estado de la
        // maquina, desterronadores, presion, especie lote anterior, rueda angosta, largo guia, sistema de
        // fertilizacion y cheque de caidas) son por evento, ver leerCamposHaciaEventoSiembra().
        siembra.setTipo_siembra(tipoSiembra);

        if (!et_distancia_hileras.getText().toString().isEmpty()) {
            String distanciaHileras = et_distancia_hileras.getText().toString();
            siembra.setDistancia_hileras(parseDoubleSeguro(distanciaHileras));
        }

        if (!et_numero_semillas_mt.getText().toString().isEmpty()) {
            String numeroSemillas = et_numero_semillas_mt.getText().toString();
            siembra.setNumero_semillas(parseDoubleSeguro(numeroSemillas));
        }
        if (!et_profundidad_fertilizante.getText().toString().isEmpty()) {
            String profFertilizante = et_profundidad_fertilizante.getText().toString();
            siembra.setProfundidad_fertilizante(parseDoubleSeguro(profFertilizante));
        }
        if (!et_profundidad_siembra.getText().toString().isEmpty()) {
            String profSiembra = et_profundidad_siembra.getText().toString();
            siembra.setProfundidad_siembra(parseDoubleSeguro(profSiembra));
        }

        if (!et_dist_entre_fert_semilla.getText().toString().isEmpty()) {
            String distanciaFertSemilla = et_dist_entre_fert_semilla.getText().toString();
            siembra.setDistancia_fertilizante_semilla(parseDoubleSeguro(distanciaFertSemilla));
        }


        //aseo maquinaria pre siembra
        if (btn_tarros_semilla_si.isChecked() || btn_tarros_semilla_no.isChecked()) {
            int tarrosSemillas = (btn_tarros_semilla_si.isChecked()) ? 1 : 2;
            siembra.setTarros_semilla_pre_siembra(tarrosSemillas);
        }
        if (btn_discos_sembradores_si.isChecked() || btn_discos_sembradores_no.isChecked()) {
            int discosSembradores = (btn_discos_sembradores_si.isChecked()) ? 1 : 2;
            siembra.setDiscos_sembradores_pre_siembra(discosSembradores);
        }
        if (btn_estructura_maquinaria_si.isChecked() || btn_estructura_maquinaria_no.isChecked()) {
            int estructuraMaquinaria = (btn_estructura_maquinaria_si.isChecked()) ? 1 : 2;
            siembra.setEstructura_maquinaria_pre_siembra(estructuraMaquinaria);
        }

        if (!et_lugar_limpieza.getText().toString().isEmpty()) {
            String lugarLimpieza = et_lugar_limpieza.getText().toString();
            siembra.setLugar_limpieza_pre_siembra(lugarLimpieza);
        }

        if (!et_responsable_aseo.getText().toString().isEmpty()) {
            String responsableAseo = et_responsable_aseo.getText().toString();
            siembra.setResponsable_aseo_pre_siembra(responsableAseo);
        }
        if (!et_rut_responsable_aseo.getText().toString().isEmpty()) {
            String rutResponsableAseo = et_rut_responsable_aseo.getText().toString();
            siembra.setRut_responsable_aseo_pre_siembra(rutResponsableAseo);
        }
        //firma

        if (!et_responsable_revision_limpieza_ingreso.getText().toString().isEmpty()) {
            String responsableRevisionLimpieza = et_responsable_revision_limpieza_ingreso.getText().toString();
            siembra.setResponsable_revision_limpieza_pre_siembra(responsableRevisionLimpieza);
        }
        //firma


        //aseo maquinaria post siembra
        if (btn_tarros_semilla_post_siembra_si.isChecked() || btn_tarros_semilla_post_siembra_no.isChecked()) {
            int tarroSemillaPostSiembra = (btn_tarros_semilla_post_siembra_si.isChecked()) ? 1 : 2;
            siembra.setTarros_semilla_post_siembra(tarroSemillaPostSiembra);
        }
        if (btn_discos_sembradores_post_siembra_si.isChecked() || btn_discos_sembradores_post_siembra_no.isChecked()) {
            int discosSembradoresPostSiembra = (btn_discos_sembradores_post_siembra_si.isChecked()) ? 1 : 2;
            siembra.setDiscos_sembradores_post_siembra(discosSembradoresPostSiembra);
        }

        if (btn_estructura_maquinaria_post_siembra_si.isChecked() || btn_estructura_maquinaria_post_siembra_no.isChecked()) {
            int estrucutraMaquinariaPostSiembra = (btn_estructura_maquinaria_post_siembra_si.isChecked()) ? 1 : 2;
            siembra.setEstructura_maquinaria_post_cosecha(estrucutraMaquinariaPostSiembra);
        }

        if (!et_lugar_limpieza_post_siembra.getText().toString().isEmpty()) {
            String lugarLimpiezaPS = et_lugar_limpieza_post_siembra.getText().toString();
            siembra.setLugar_limpieza_post_siembra(lugarLimpiezaPS);
        }
        if (!et_responsable_aseo_post_siembra.getText().toString().isEmpty()) {
            String responsableAseoPS = et_responsable_aseo_post_siembra.getText().toString();
            siembra.setResponsable_aseo_post_siembra(responsableAseoPS);
        }

        if (!et_rut_responsable_aseo_post_siembra.getText().toString().isEmpty()) {
            String rutResponsableAseoPS = et_rut_responsable_aseo_post_siembra.getText().toString();
            siembra.setRut_responsable_aseo_post_siembra(rutResponsableAseoPS);
        }
        //firma


        if (!et_responsable_revision_limpieza_ingreso_post_siembra.getText().toString().isEmpty()) {
            String rutResponsableRevisionAseoPS = et_responsable_revision_limpieza_ingreso_post_siembra.getText().toString();
            siembra.setEncargado_revision_limpieza_post_siembra(rutResponsableRevisionAseoPS);
        }
        //firma


        //general
        if (!textoSpinner(sp_desempeno_siembra).equals(comparaSpinner)) {
            String desempenoSiembra = textoSpinner(sp_desempeno_siembra);
            siembra.setDesempeno_siembra(desempenoSiembra);
        }

        if (!et_observaciones_general.getText().toString().isEmpty()) {
            String observaciones = et_observaciones_general.getText().toString();
            siembra.setObservacion_general(observaciones);
        }


        //ingreso
        if (!et_fecha_ingreso.getText().toString().isEmpty()) {
            String fechaIngreso = et_fecha_ingreso.getText().toString();
            siembra.setFecha_ingreso(fechaIngreso);
        }

        if (!et_hora_ingreso.getText().toString().isEmpty()) {
            String horaIngreso = et_hora_ingreso.getText().toString();
            siembra.setHora_ingreso(horaIngreso);
        }

        if (!et_nombre_supervisor_ingreso_siembra.getText().toString().isEmpty()) {
            String nombreSupervisor = et_nombre_supervisor_ingreso_siembra.getText().toString();
            siembra.setNombre_supervisor_siembra(nombreSupervisor);
        }
        //firma

        if (!et_nombre_responsable_campo_ingreso.getText().toString().isEmpty()) {
            String resposableCampo = et_nombre_responsable_campo_ingreso.getText().toString();
            siembra.setNombre_responsable_campo(resposableCampo);
        }
        //firma

        if (!et_operador_maquina_ingreso.getText().toString().isEmpty()) {
            String operarioMaquina = et_operador_maquina_ingreso.getText().toString();
            siembra.setNombre_operario_maquina(operarioMaquina);
        }
        //firma


        //salida
        if (!et_fecha_termino.getText().toString().isEmpty()) {
            String fechaIngreso = et_fecha_termino.getText().toString();
            siembra.setFecha_termino(fechaIngreso);
        }

        if (!et_hora_termino.getText().toString().isEmpty()) {
            String horaIngreso = et_hora_termino.getText().toString();
            siembra.setHora_termino(horaIngreso);
        }

        if (!et_nombre_supervisor_termino_siembra.getText().toString().isEmpty()) {
            String nombreSupervisor = et_nombre_supervisor_termino_siembra.getText().toString();
            siembra.setNombre_supervisor_siembra_termino(nombreSupervisor);
        }
        //firma

        if (!et_nombre_responsable_campo_termino.getText().toString().isEmpty()) {
            String resposableCampo = et_nombre_responsable_campo_termino.getText().toString();
            siembra.setNombre_responsable_campo_termino(resposableCampo);
        }
        //firma

        if (!et_operador_maquina_termino.getText().toString().isEmpty()) {
            String operarioMaquina = et_operador_maquina_termino.getText().toString();
            siembra.setNombre_operario_maquina_termino(operarioMaquina);
        }
        //firma


        ExecutorService executor = Executors.newSingleThreadExecutor();

        Future<List<TempFirmas>> firmasF = executor.submit(()
                -> MainActivity.myAppDB.DaoFirmas()
                .getFirmasByDocum(Utilidades.TIPO_DOCUMENTO_CHECKLIST_SIEMBRA));

        Future<Config> configFuture = executor.submit(() -> MainActivity.myAppDB.myDao().getConfig());


        if (checkListSiembra != null) {
            siembra.setFirma_responsable_aso_pre_siembra(checkListSiembra.getFirma_responsable_aso_pre_siembra());
            siembra.setFirma_revision_limpieza_pre_siembra(checkListSiembra.getFirma_revision_limpieza_pre_siembra());
            siembra.setFirma_responsable_aseo_post_siembra(checkListSiembra.getFirma_responsable_aseo_post_siembra());
            siembra.setFirma_revision_limpieza_post_siembra(checkListSiembra.getFirma_revision_limpieza_post_siembra());
            siembra.setFirma_responsable_campo(checkListSiembra.getFirma_responsable_campo());
            siembra.setFirma_operario_maquina(checkListSiembra.getFirma_operario_maquina());
            siembra.setFirma_operario_maquina_termino(checkListSiembra.getFirma_operario_maquina_termino());
            siembra.setFirma_responsable_campo_termino(checkListSiembra.getFirma_responsable_campo_termino());
        }


        try {
            List<TempFirmas> firmas = firmasF.get();
            Config config = configFuture.get();
            siembra.setId_usuario(config.getId_usuario());

            for (TempFirmas ff : firmas) {

                switch (ff.getLugar_firma()) {
                    case Utilidades.DIALOG_TAG_RESPONSABLE_ASEO_INGRESO:
                        siembra.setFirma_responsable_aso_pre_siembra(ff.getPath());
                        break;
                    case Utilidades.DIALOG_TAG_REVISOR_LIMPIEZA_INGRESO:
                        siembra.setFirma_revision_limpieza_pre_siembra(ff.getPath());
                        break;
                    case Utilidades.DIALOG_TAG_RESPONSABLE_ASEO_SALIDA:
                        siembra.setFirma_responsable_aseo_post_siembra(ff.getPath());
                        break;
                    case Utilidades.DIALOG_TAG_REVISOR_LIMPIEZA_SALIDA:
                        siembra.setFirma_revision_limpieza_post_siembra(ff.getPath());
                        break;
                    case Utilidades.DIALOG_TAG_RESPONSABLE_CAMPO_INGRESO:
                        siembra.setFirma_responsable_campo(ff.getPath());
                        break;
                    case Utilidades.DIALOG_TAG_RESPONSABLE_OPERARIO_INGRESO:
                        siembra.setFirma_operario_maquina(ff.getPath());
                        break;
                    case Utilidades.DIALOG_TAG_RESPONSABLE_OPERARIO_TERMINO:
                        siembra.setFirma_operario_maquina_termino(ff.getPath());
                        break;
                    case Utilidades.DIALOG_TAG_RESPONSABLE_CAMPO_TERMINO:
                        siembra.setFirma_responsable_campo_termino(ff.getPath());
                        break;
                }
            }

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        Future<Long> newIdFuture = null;
        Future<Integer> UpdIdFuture = null;

        if (checkListSiembra == null) {
            newIdFuture = executor.submit(() ->
                    MainActivity.myAppDB.DaoClSiembra().insertClSiembra(siembra));
        } else {

            siembra.setId_cl_siembra(checkListSiembra.getId_cl_siembra());
            UpdIdFuture = executor.submit(() ->
                    MainActivity.myAppDB.DaoClSiembra().updateClSiembra(siembra));
        }


        try {

            if (checkListSiembra == null && newIdFuture != null) {
                long newId = newIdFuture.get();
                if (newId > 0) {
                    // TICKET 2494 - 2026-10-01: al crear el checklist por primera vez, quedarse en
                    // el mismo formulario (en vez de cancelar()/salir) para poder agregar eventos
                    // de siembra al tiro, sin tener que volver a entrar.
                    siembra.setId_cl_siembra((int) newId);
                    checkListSiembra = siembra;
                    btn_guardar_cl_siembra.setText("EDITAR");
                    cargarEventosSiembraDesdeBD();
                    Toasty.success(requireActivity(), "Guardado con exito", Toast.LENGTH_LONG, true).show();
                } else {
                    Toasty.error(requireActivity(), "No se pudo guardar con exito", Toast.LENGTH_LONG, true).show();
                }
            } else if (checkListSiembra != null && UpdIdFuture != null) {
                long newId = UpdIdFuture.get();
                if (newId > 0) {
                    Toasty.success(requireActivity(), "Editado con exito", Toast.LENGTH_LONG, true).show();
                } else {
                    Toasty.error(requireActivity(), "No se pudo editar con exito", Toast.LENGTH_LONG, true).show();
                }
            }

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            Toasty.warning(requireActivity(), "Error al guardar ->" + e.getMessage(), Toast.LENGTH_LONG, true).show();
        }

        // TICKET 2494 - 2026-09-30: si hay un evento de siembra activo, se guarda junto con la cabecera
        if (eventoActualSiembra != null) {
            guardarEventoSiembraActual(siembra.getClave_unica());
        }

        return true;
    }


    private void cancelar() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(()
                -> MainActivity.myAppDB.DaoFirmas()
                .deleteFirmasByDoc(Utilidades.TIPO_DOCUMENTO_CHECKLIST_SIEMBRA));
        executorService.shutdown();
        activity.onBackPressed();
    }

    // TICKET 2494 - 2026-09-30: eventos de siembra (H/M1/M2/M3). Cada evento reutiliza los mismos
    // campos de pantalla de Regulacion/Aseo Pre/Aseo Post/General/Ingreso/Salida; al cambiar de tab
    // se cargan/guardan esos campos contra el CheckListSiembraEvento seleccionado en vez de la cabecera.

    private String tagFirmaEvento(String tagBase) {
        return tagBase + "_EV_" + (eventoActualSiembra != null ? eventoActualSiembra.getClave_unica_evento() : "0");
    }

    private void cargarEventosSiembraDesdeBD() {
        if (checkListSiembra == null) return;

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<List<CheckListSiembraEvento>> future = executor.submit(() ->
                MainActivity.myAppDB.DaoClSiembra().getEventosByClaveUnicaClSiembra(checkListSiembra.getClave_unica()));

        try {
            List<CheckListSiembraEvento> eventos = future.get();
            eventosSiembra.clear();
            if (eventos != null) eventosSiembra.addAll(eventos);
            pintarTabsEventosSiembra();
            if (!eventosSiembra.isEmpty()) {
                seleccionarEventoSiembra(eventosSiembra.get(0));
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        executor.shutdown();
    }

    private void pintarTabsEventosSiembra() {
        cont_tabs_eventos_siembra.removeAllViews();

        for (CheckListSiembraEvento evento : eventosSiembra) {
            Button tab = new Button(requireContext());
            // TICKET 2515 - 2026-10-02: la pestana se identifica por prestador + sembradora (marca y modelo)
            tab.setText(textoOVacio(evento.getPrestador_servicio()) + "\n"
                    + (textoOVacio(evento.getSembradora_marca()) + " " + textoOVacio(evento.getSembradora_modelo())).trim());
            tab.setAllCaps(false);
            tab.setTextSize(12);

            boolean esActual = eventoActualSiembra != null
                    && evento.getClave_unica_evento() != null
                    && evento.getClave_unica_evento().equals(eventoActualSiembra.getClave_unica_evento());
            // TICKET 2494 - 2026-10-01: colorOnBackground es negro y estaba puesto como FONDO del
            // boton no seleccionado (por eso no se leia el texto). Fondo gris claro + texto oscuro
            // para el no seleccionado, morado + texto blanco para el seleccionado.
            tab.setBackgroundColor(ContextCompat.getColor(requireContext(),
                    esActual ? R.color.colorPrimary : R.color.colorGrey));
            tab.setTextColor(ContextCompat.getColor(requireContext(),
                    esActual ? R.color.colorOnPrimary : R.color.colorOnBackground));

            tab.setOnClickListener(v -> seleccionarEventoSiembra(evento));

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMarginEnd(16);
            tab.setLayoutParams(lp);

            cont_tabs_eventos_siembra.addView(tab);
        }
    }

    private void seleccionarEventoSiembra(CheckListSiembraEvento evento) {
        // TICKET 2494 - 2026-10-01: si tocas el mismo evento en el que ya estas (doble toque), no
        // se hace nada - recargar desde el objeto en memoria descartaria lo que recien tipeaste
        // sin haber cambiado de tab.
        boolean esElMismoEvento = eventoActualSiembra != null
                && evento.getClave_unica_evento() != null
                && evento.getClave_unica_evento().equals(eventoActualSiembra.getClave_unica_evento());
        if (esElMismoEvento) {
            return;
        }

        // Antes solo se avisaba "tienes cambios sin guardar" pero el cambio de tab igual
        // descartaba lo editado (nunca se escribia en el evento ni en Room). Ahora se guarda de
        // una vez el evento que se esta dejando (campos + firmas pendientes) antes de cargar el
        // siguiente, para que nunca se pierda ni se mezcle con el otro evento.
        if (eventoActualSiembra != null && checkListSiembra != null) {
            guardarEventoSiembraActual(checkListSiembra.getClave_unica());
        }
        eventoActualSiembra = evento;
        cargarCamposDesdeEventoSiembra(evento);
        pintarTabsEventosSiembra();
    }

    private void cargarCamposDesdeEventoSiembra(CheckListSiembraEvento evento) {

        // TICKET 2494 - 2026-10-01: los campos/secciones por evento solo se muestran cuando hay
        // un evento seleccionado - evita que se piense que hay que llenarlos sin haber creado uno.
        group_campos_evento_siembra.setVisibility(View.VISIBLE);

        // TICKET 2494 - 2026-10-01: Siembra Anterior (especie/variedad/ogm/anexo_curimapu) por evento
        et_especie.setText(evento.getEspecie() != null ? evento.getEspecie() : "");
        et_variedad.setText(evento.getVariedad() != null ? evento.getVariedad() : "");
        btn_ogm_si.setChecked(evento.getOgm() == 1);
        btn_ogm_no.setChecked(evento.getOgm() == 2);
        et_anexo_curimapu.setText(evento.getAnexo_curimapu() != null ? evento.getAnexo_curimapu() : "");

        et_prestador_servicio.setText(evento.getPrestador_servicio() != null ? evento.getPrestador_servicio() : "");
        if (evento.getEstado_discos() != null && !evento.getEstado_discos().isEmpty()) {
            int d = chk_1.indexOf(evento.getEstado_discos());
            sp_estado_discos.setSelection(Math.max(d, 0));
        } else {
            sp_estado_discos.setSelection(0);
        }

        // TICKET 2515 - 2026-10-02: Regulacion Sembradora completa por evento (los 5 campos de
        // Regulacion de Siembra - prof. fertilizante, dist. fert-semilla, dist. hileras, N semillas/mt y
        // prof. siembra - son de cabecera y NO se cargan aca)
        et_sembradora_marca.setText(evento.getSembradora_marca() != null ? evento.getSembradora_marca() : "");
        et_sembradora_modelo.setText(evento.getSembradora_modelo() != null ? evento.getSembradora_modelo() : "");
        et_trocha.setText(evento.getTrocha() != null ? evento.getTrocha() : "");
        sp_tipo_sembradora.setSelection(spinnerIndice(chk_3, evento.getTipo_sembradora()));
        sp_chequeo_selector.setSelection(spinnerIndice(chk_1, evento.getChequeo_selector()));
        sp_estado_maquina.setSelection(spinnerIndice(chk_1, evento.getEstado_maquina()));
        btn_desterronadores_si.setChecked("1".equals(evento.getDesterronadores()));
        btn_desterronadores_no.setChecked("2".equals(evento.getDesterronadores()));
        sp_presion_neumaticos.setSelection(spinnerIndice(chk_1, evento.getPresion_neumaticos()));
        et_especie_lote.setText(evento.getEspecie_lote_anterior() != null ? evento.getEspecie_lote_anterior() : "");
        btn_rueda_angosta_si.setChecked("1".equals(evento.getRueda_angosta()));
        btn_rueda_angosta_no.setChecked("2".equals(evento.getRueda_angosta()));
        et_largo_guia.setText(evento.getLargo_guia() != null ? evento.getLargo_guia() : "");
        et_sistema_fertilizacion.setText(evento.getSistema_fertilizacion() != null ? evento.getSistema_fertilizacion() : "");
        btn_cheque_caidas_si.setChecked("1".equals(evento.getCheque_caidas()));
        btn_cheque_caidas_no.setChecked("2".equals(evento.getCheque_caidas()));

        btn_tarros_semilla_si.setChecked("1".equals(evento.getTarros_semilla_pre_siembra()));
        btn_tarros_semilla_no.setChecked("2".equals(evento.getTarros_semilla_pre_siembra()));
        btn_discos_sembradores_si.setChecked("1".equals(evento.getDiscos_sembradores_pre_siembra()));
        btn_discos_sembradores_no.setChecked("2".equals(evento.getDiscos_sembradores_pre_siembra()));
        btn_estructura_maquinaria_si.setChecked("1".equals(evento.getEstructura_maquinaria_pre_siembra()));
        btn_estructura_maquinaria_no.setChecked("2".equals(evento.getEstructura_maquinaria_pre_siembra()));
        et_lugar_limpieza.setText(evento.getLugar_limpieza_pre_siembra() != null ? evento.getLugar_limpieza_pre_siembra() : "");
        et_responsable_aseo.setText(evento.getResponsable_aseo_pre_siembra() != null ? evento.getResponsable_aseo_pre_siembra() : "");
        et_rut_responsable_aseo.setText(evento.getRut_responsable_aseo_pre_siembra() != null ? evento.getRut_responsable_aseo_pre_siembra() : "");
        et_responsable_revision_limpieza_ingreso.setText(evento.getResponsable_revision_limpieza_pre_siembra() != null ? evento.getResponsable_revision_limpieza_pre_siembra() : "");

        btn_tarros_semilla_post_siembra_si.setChecked("1".equals(evento.getTarros_semilla_post_siembra()));
        btn_tarros_semilla_post_siembra_no.setChecked("2".equals(evento.getTarros_semilla_post_siembra()));
        btn_discos_sembradores_post_siembra_si.setChecked("1".equals(evento.getDiscos_sembradores_post_siembra()));
        btn_discos_sembradores_post_siembra_no.setChecked("2".equals(evento.getDiscos_sembradores_post_siembra()));
        btn_estructura_maquinaria_post_siembra_si.setChecked("1".equals(evento.getEstructura_maquinaria_post_cosecha()));
        btn_estructura_maquinaria_post_siembra_no.setChecked("2".equals(evento.getEstructura_maquinaria_post_cosecha()));
        et_lugar_limpieza_post_siembra.setText(evento.getLugar_limpieza_post_siembra() != null ? evento.getLugar_limpieza_post_siembra() : "");
        et_responsable_aseo_post_siembra.setText(evento.getResponsable_aseo_post_siembra() != null ? evento.getResponsable_aseo_post_siembra() : "");
        et_rut_responsable_aseo_post_siembra.setText(evento.getRut_responsable_aseo_post_siembra() != null ? evento.getRut_responsable_aseo_post_siembra() : "");
        et_responsable_revision_limpieza_ingreso_post_siembra.setText(evento.getEncargado_revision_limpieza_post_siembra() != null ? evento.getEncargado_revision_limpieza_post_siembra() : "");

        if (evento.getDesempeno_siembra() != null && !evento.getDesempeno_siembra().isEmpty()) {
            int d = chk_1.indexOf(evento.getDesempeno_siembra());
            sp_desempeno_siembra.setSelection(Math.max(d, 0));
        } else {
            sp_desempeno_siembra.setSelection(0);
        }
        et_observaciones_general.setText(evento.getObservacion_general() != null ? evento.getObservacion_general() : "");

        et_fecha_ingreso.setText(fechaParaMostrar(evento.getFecha_ingreso()));
        et_hora_ingreso.setText(evento.getHora_ingreso() != null ? evento.getHora_ingreso() : "");
        et_nombre_supervisor_ingreso_siembra.setText(evento.getNombre_supervisor_siembra() != null ? evento.getNombre_supervisor_siembra() : "");
        et_nombre_responsable_campo_ingreso.setText(evento.getNombre_responsable_campo() != null ? evento.getNombre_responsable_campo() : "");
        et_operador_maquina_ingreso.setText(evento.getNombre_operario_maquina() != null ? evento.getNombre_operario_maquina() : "");

        et_fecha_termino.setText(fechaParaMostrar(evento.getFecha_termino()));
        et_hora_termino.setText(evento.getHora_termino() != null ? evento.getHora_termino() : "");
        et_nombre_supervisor_termino_siembra.setText(evento.getNombre_supervisor_siembra_termino() != null ? evento.getNombre_supervisor_siembra_termino() : "");
        et_nombre_responsable_campo_termino.setText(evento.getNombre_responsable_campo_termino() != null ? evento.getNombre_responsable_campo_termino() : "");
        et_operador_maquina_termino.setText(evento.getNombre_operario_maquina_termino() != null ? evento.getNombre_operario_maquina_termino() : "");

        // TICKET 2494 - 2026-09-30: las 8 firmas ahora son por evento, no por cabecera. El
        // boton/check se habilita o deshabilita segun tenga o no firma el evento seleccionado.
        aplicarEstadoFirmaEvento(evento.getFirma_responsable_aso_pre_siembra(), btn_firma_responsable_aseo_ingreso, check_firma_responsable_aseo_ingreso);
        aplicarEstadoFirmaEvento(evento.getFirma_revision_limpieza_pre_siembra(), btn_firma_responsable_revision_limpieza_ingreso, check_firma_responsable_revision_limpieza_ingreso);
        aplicarEstadoFirmaEvento(evento.getFirma_responsable_aseo_post_siembra(), btn_firma_responsable_aseo_ingreso_post_siembra, check_firma_responsable_aseo_ingreso_post_siembra);
        aplicarEstadoFirmaEvento(evento.getFirma_revision_limpieza_post_siembra(), btn_firma_responsable_revision_limpieza_ingreso_post_siembra, check_firma_responsable_revision_limpieza_ingreso_post_siembra);
        aplicarEstadoFirmaEvento(evento.getFirma_responsable_campo(), btn_firma_responsable_campo_ingreso, check_firma_responsable_campo_ingreso);
        aplicarEstadoFirmaEvento(evento.getFirma_operario_maquina(), btn_firma_operario_maquina_ingreso, check_firma_operario_maquina_ingreso);
        aplicarEstadoFirmaEvento(evento.getFirma_operario_maquina_termino(), btn_firma_operario_maquina_termino, check_firma_operario_maquina_termino);
        aplicarEstadoFirmaEvento(evento.getFirma_responsable_campo_termino(), btn_firma_responsable_campo_termino, check_firma_responsable_campo_termino);

        snapshotEventoActualCargado = snapshotCamposEventoSiembra();
    }

    // TICKET 2515 - 2026-10-02: indice del valor guardado en el spinner (0 = "--Seleccione--" si no existe)
    private int spinnerIndice(List<String> opciones, String valor) {
        if (valor == null || valor.isEmpty()) return 0;
        return Math.max(opciones.indexOf(valor), 0);
    }

    // TICKET 2515 - 2026-10-02: selecciona en el combobox el valor guardado del checklist. Si ese valor ya no existe en
    // la lista de opciones (checklist antiguo), lo agrega al final como una opcion mas y lo deja seleccionado, asi no se
    // pierde al guardar (el usuario puede cambiarlo por una opcion nueva cuando quiera). Para valores que SI estan en la
    // lista el resultado es el mismo de antes. Protegido: un error aqui nunca debe tumbar la pantalla.
    private void seleccionarConservandoValor(Spinner spinner, String valor) {
        try {
            if (valor == null || valor.isEmpty() || spinner.getAdapter() == null) return;

            List<String> items = new ArrayList<>();
            for (int i = 0; i < spinner.getAdapter().getCount(); i++) {
                Object item = spinner.getAdapter().getItem(i);
                items.add(item != null ? item.toString() : "");
            }

            int posicion = items.indexOf(valor);
            if (posicion < 0) {
                items.add(valor);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, items);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
                posicion = items.size() - 1;
            }
            spinner.setSelection(posicion);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // TICKET 2515 - 2026-10-02: texto del combobox sin riesgo de null. Un checklist antiguo puede traer un valor que
    // ya no existe en la lista de opciones (ej: Cama de semillas cambio de opciones en el ticket 2494): indexOf daba
    // -1, el combobox quedaba sin seleccion y getSelectedItem() devolvia null (NullPointerException al guardar,
    // detectado en el registro de errores). Ahora un combobox sin seleccion cuenta como "--Seleccione--".
    private String textoSpinner(Spinner spinner) {
        Object item = spinner.getSelectedItem();
        return item != null ? item.toString() : "--Seleccione--";
    }

    private String textoOVacio(String valor) {
        return valor != null ? valor : "";
    }

    private void aplicarEstadoFirmaEvento(String firma, Button btnFirma, ImageView checkFirma) {
        boolean tieneFirma = firma != null && !firma.isEmpty();
        btnFirma.setEnabled(!tieneFirma);
        checkFirma.setVisibility(tieneFirma ? View.VISIBLE : View.GONE);
    }

    private void leerCamposHaciaEventoSiembra(CheckListSiembraEvento evento) {
        String comparaSpinner = "--Seleccione--";

        // TICKET 2494 - 2026-10-01: Siembra Anterior (especie/variedad/ogm/anexo_curimapu) por evento
        if (!et_especie.getText().toString().isEmpty()) {
            evento.setEspecie(et_especie.getText().toString());
        }
        if (!et_variedad.getText().toString().isEmpty()) {
            evento.setVariedad(et_variedad.getText().toString());
        }
        if (btn_ogm_si.isChecked() || btn_ogm_no.isChecked()) {
            evento.setOgm(btn_ogm_si.isChecked() ? 1 : 2);
        }
        if (!et_anexo_curimapu.getText().toString().isEmpty()) {
            evento.setAnexo_curimapu(et_anexo_curimapu.getText().toString());
        }

        if (!et_prestador_servicio.getText().toString().isEmpty()) {
            evento.setPrestador_servicio(et_prestador_servicio.getText().toString());
        }
        if (!textoSpinner(sp_estado_discos).equals(comparaSpinner)) {
            evento.setEstado_discos(textoSpinner(sp_estado_discos));
        }
        // TICKET 2515 - 2026-10-02: Regulacion Sembradora completa por evento
        if (!et_sembradora_marca.getText().toString().isEmpty()) {
            evento.setSembradora_marca(et_sembradora_marca.getText().toString());
        }
        if (!et_sembradora_modelo.getText().toString().isEmpty()) {
            evento.setSembradora_modelo(et_sembradora_modelo.getText().toString());
        }
        if (!et_trocha.getText().toString().isEmpty()) {
            evento.setTrocha(et_trocha.getText().toString());
        }
        if (!textoSpinner(sp_tipo_sembradora).equals(comparaSpinner)) {
            evento.setTipo_sembradora(textoSpinner(sp_tipo_sembradora));
        }
        if (!textoSpinner(sp_chequeo_selector).equals(comparaSpinner)) {
            evento.setChequeo_selector(textoSpinner(sp_chequeo_selector));
        }
        if (!textoSpinner(sp_estado_maquina).equals(comparaSpinner)) {
            evento.setEstado_maquina(textoSpinner(sp_estado_maquina));
        }
        if (btn_desterronadores_si.isChecked() || btn_desterronadores_no.isChecked()) {
            evento.setDesterronadores(btn_desterronadores_si.isChecked() ? "1" : "2");
        }
        if (!textoSpinner(sp_presion_neumaticos).equals(comparaSpinner)) {
            evento.setPresion_neumaticos(textoSpinner(sp_presion_neumaticos));
        }
        if (!et_especie_lote.getText().toString().isEmpty()) {
            evento.setEspecie_lote_anterior(et_especie_lote.getText().toString());
        }
        if (btn_rueda_angosta_si.isChecked() || btn_rueda_angosta_no.isChecked()) {
            evento.setRueda_angosta(btn_rueda_angosta_si.isChecked() ? "1" : "2");
        }
        if (!et_largo_guia.getText().toString().isEmpty()) {
            evento.setLargo_guia(et_largo_guia.getText().toString());
        }
        if (!et_sistema_fertilizacion.getText().toString().isEmpty()) {
            evento.setSistema_fertilizacion(et_sistema_fertilizacion.getText().toString());
        }
        if (btn_cheque_caidas_si.isChecked() || btn_cheque_caidas_no.isChecked()) {
            evento.setCheque_caidas(btn_cheque_caidas_si.isChecked() ? "1" : "2");
        }

        if (btn_tarros_semilla_si.isChecked() || btn_tarros_semilla_no.isChecked()) {
            evento.setTarros_semilla_pre_siembra(btn_tarros_semilla_si.isChecked() ? "1" : "2");
        }
        if (btn_discos_sembradores_si.isChecked() || btn_discos_sembradores_no.isChecked()) {
            evento.setDiscos_sembradores_pre_siembra(btn_discos_sembradores_si.isChecked() ? "1" : "2");
        }
        if (btn_estructura_maquinaria_si.isChecked() || btn_estructura_maquinaria_no.isChecked()) {
            evento.setEstructura_maquinaria_pre_siembra(btn_estructura_maquinaria_si.isChecked() ? "1" : "2");
        }
        if (!et_lugar_limpieza.getText().toString().isEmpty()) {
            evento.setLugar_limpieza_pre_siembra(et_lugar_limpieza.getText().toString());
        }
        if (!et_responsable_aseo.getText().toString().isEmpty()) {
            evento.setResponsable_aseo_pre_siembra(et_responsable_aseo.getText().toString());
        }
        if (!et_rut_responsable_aseo.getText().toString().isEmpty()) {
            evento.setRut_responsable_aseo_pre_siembra(et_rut_responsable_aseo.getText().toString());
        }
        if (!et_responsable_revision_limpieza_ingreso.getText().toString().isEmpty()) {
            evento.setResponsable_revision_limpieza_pre_siembra(et_responsable_revision_limpieza_ingreso.getText().toString());
        }

        if (btn_tarros_semilla_post_siembra_si.isChecked() || btn_tarros_semilla_post_siembra_no.isChecked()) {
            evento.setTarros_semilla_post_siembra(btn_tarros_semilla_post_siembra_si.isChecked() ? "1" : "2");
        }
        if (btn_discos_sembradores_post_siembra_si.isChecked() || btn_discos_sembradores_post_siembra_no.isChecked()) {
            evento.setDiscos_sembradores_post_siembra(btn_discos_sembradores_post_siembra_si.isChecked() ? "1" : "2");
        }
        if (btn_estructura_maquinaria_post_siembra_si.isChecked() || btn_estructura_maquinaria_post_siembra_no.isChecked()) {
            evento.setEstructura_maquinaria_post_cosecha(btn_estructura_maquinaria_post_siembra_si.isChecked() ? "1" : "2");
        }
        if (!et_lugar_limpieza_post_siembra.getText().toString().isEmpty()) {
            evento.setLugar_limpieza_post_siembra(et_lugar_limpieza_post_siembra.getText().toString());
        }
        if (!et_responsable_aseo_post_siembra.getText().toString().isEmpty()) {
            evento.setResponsable_aseo_post_siembra(et_responsable_aseo_post_siembra.getText().toString());
        }
        if (!et_rut_responsable_aseo_post_siembra.getText().toString().isEmpty()) {
            evento.setRut_responsable_aseo_post_siembra(et_rut_responsable_aseo_post_siembra.getText().toString());
        }
        if (!et_responsable_revision_limpieza_ingreso_post_siembra.getText().toString().isEmpty()) {
            evento.setEncargado_revision_limpieza_post_siembra(et_responsable_revision_limpieza_ingreso_post_siembra.getText().toString());
        }

        if (!textoSpinner(sp_desempeno_siembra).equals(comparaSpinner)) {
            evento.setDesempeno_siembra(textoSpinner(sp_desempeno_siembra));
        }
        if (!et_observaciones_general.getText().toString().isEmpty()) {
            evento.setObservacion_general(et_observaciones_general.getText().toString());
        }

        if (!et_fecha_ingreso.getText().toString().isEmpty()) {
            evento.setFecha_ingreso(et_fecha_ingreso.getText().toString());
        }
        if (!et_hora_ingreso.getText().toString().isEmpty()) {
            evento.setHora_ingreso(et_hora_ingreso.getText().toString());
        }
        if (!et_nombre_supervisor_ingreso_siembra.getText().toString().isEmpty()) {
            evento.setNombre_supervisor_siembra(et_nombre_supervisor_ingreso_siembra.getText().toString());
        }
        if (!et_nombre_responsable_campo_ingreso.getText().toString().isEmpty()) {
            evento.setNombre_responsable_campo(et_nombre_responsable_campo_ingreso.getText().toString());
        }
        if (!et_operador_maquina_ingreso.getText().toString().isEmpty()) {
            evento.setNombre_operario_maquina(et_operador_maquina_ingreso.getText().toString());
        }

        if (!et_fecha_termino.getText().toString().isEmpty()) {
            evento.setFecha_termino(et_fecha_termino.getText().toString());
        }
        if (!et_hora_termino.getText().toString().isEmpty()) {
            evento.setHora_termino(et_hora_termino.getText().toString());
        }
        if (!et_nombre_supervisor_termino_siembra.getText().toString().isEmpty()) {
            evento.setNombre_supervisor_siembra_termino(et_nombre_supervisor_termino_siembra.getText().toString());
        }
        if (!et_nombre_responsable_campo_termino.getText().toString().isEmpty()) {
            evento.setNombre_responsable_campo_termino(et_nombre_responsable_campo_termino.getText().toString());
        }
        if (!et_operador_maquina_termino.getText().toString().isEmpty()) {
            evento.setNombre_operario_maquina_termino(et_operador_maquina_termino.getText().toString());
        }
    }

    private String snapshotCamposEventoSiembra() {
        StringBuilder sb = new StringBuilder();
        sb.append(et_prestador_servicio.getText().toString());
        sb.append("|").append(sp_estado_discos.getSelectedItem() != null ? textoSpinner(sp_estado_discos) : "");
        sb.append("|").append(et_sembradora_marca.getText().toString());
        sb.append("|").append(et_sembradora_modelo.getText().toString());
        sb.append("|").append(et_trocha.getText().toString());
        sb.append("|").append(sp_tipo_sembradora.getSelectedItem() != null ? textoSpinner(sp_tipo_sembradora) : "");
        sb.append("|").append(sp_chequeo_selector.getSelectedItem() != null ? textoSpinner(sp_chequeo_selector) : "");
        sb.append("|").append(sp_estado_maquina.getSelectedItem() != null ? textoSpinner(sp_estado_maquina) : "");
        sb.append("|").append(btn_desterronadores_si.isChecked()).append(btn_desterronadores_no.isChecked());
        sb.append("|").append(sp_presion_neumaticos.getSelectedItem() != null ? textoSpinner(sp_presion_neumaticos) : "");
        sb.append("|").append(et_especie_lote.getText().toString());
        sb.append("|").append(btn_rueda_angosta_si.isChecked()).append(btn_rueda_angosta_no.isChecked());
        sb.append("|").append(et_largo_guia.getText().toString());
        sb.append("|").append(et_sistema_fertilizacion.getText().toString());
        sb.append("|").append(btn_cheque_caidas_si.isChecked()).append(btn_cheque_caidas_no.isChecked());
        sb.append("|").append(btn_tarros_semilla_si.isChecked()).append(btn_tarros_semilla_no.isChecked());
        sb.append("|").append(btn_discos_sembradores_si.isChecked()).append(btn_discos_sembradores_no.isChecked());
        sb.append("|").append(btn_estructura_maquinaria_si.isChecked()).append(btn_estructura_maquinaria_no.isChecked());
        sb.append("|").append(et_lugar_limpieza.getText().toString());
        sb.append("|").append(et_responsable_aseo.getText().toString());
        sb.append("|").append(et_rut_responsable_aseo.getText().toString());
        sb.append("|").append(et_responsable_revision_limpieza_ingreso.getText().toString());
        sb.append("|").append(btn_tarros_semilla_post_siembra_si.isChecked()).append(btn_tarros_semilla_post_siembra_no.isChecked());
        sb.append("|").append(btn_discos_sembradores_post_siembra_si.isChecked()).append(btn_discos_sembradores_post_siembra_no.isChecked());
        sb.append("|").append(btn_estructura_maquinaria_post_siembra_si.isChecked()).append(btn_estructura_maquinaria_post_siembra_no.isChecked());
        sb.append("|").append(et_lugar_limpieza_post_siembra.getText().toString());
        sb.append("|").append(et_responsable_aseo_post_siembra.getText().toString());
        sb.append("|").append(et_rut_responsable_aseo_post_siembra.getText().toString());
        sb.append("|").append(et_responsable_revision_limpieza_ingreso_post_siembra.getText().toString());
        sb.append("|").append(sp_desempeno_siembra.getSelectedItem() != null ? textoSpinner(sp_desempeno_siembra) : "");
        sb.append("|").append(et_observaciones_general.getText().toString());
        sb.append("|").append(et_fecha_ingreso.getText().toString());
        sb.append("|").append(et_hora_ingreso.getText().toString());
        sb.append("|").append(et_nombre_supervisor_ingreso_siembra.getText().toString());
        sb.append("|").append(et_nombre_responsable_campo_ingreso.getText().toString());
        sb.append("|").append(et_operador_maquina_ingreso.getText().toString());
        sb.append("|").append(et_fecha_termino.getText().toString());
        sb.append("|").append(et_hora_termino.getText().toString());
        sb.append("|").append(et_nombre_supervisor_termino_siembra.getText().toString());
        sb.append("|").append(et_nombre_responsable_campo_termino.getText().toString());
        sb.append("|").append(et_operador_maquina_termino.getText().toString());
        return sb.toString();
    }

    // TICKET 2515 - 2026-10-02: el evento ya no pide fecha ni tipo. Se define por prestador de servicio +
    // sembradora (marca y modelo); no se puede repetir la misma combinacion dentro del checklist.
    private void mostrarDialogoNuevoEventoSiembra() {
        if (checkListSiembra == null) {
            Toasty.warning(requireActivity(), "Guarda el checklist primero para poder agregar eventos de siembra", Toast.LENGTH_LONG, true).show();
            return;
        }

        LinearLayout cont = new LinearLayout(requireContext());
        cont.setOrientation(LinearLayout.VERTICAL);
        int padding = (int) (16 * getResources().getDisplayMetrics().density);
        cont.setPadding(padding, padding, padding, padding);

        TextView lblPrestador = new TextView(requireContext());
        lblPrestador.setText("Prestador de servicio");
        cont.addView(lblPrestador);

        final EditText etPrestador = new EditText(requireContext());
        etPrestador.setInputType(android.text.InputType.TYPE_CLASS_TEXT);
        etPrestador.setMaxLines(1);
        cont.addView(etPrestador);

        TextView lblMarca = new TextView(requireContext());
        lblMarca.setText("Sembradora marca");
        cont.addView(lblMarca);

        final EditText etMarca = new EditText(requireContext());
        etMarca.setInputType(android.text.InputType.TYPE_CLASS_TEXT);
        etMarca.setMaxLines(1);
        cont.addView(etMarca);

        TextView lblModelo = new TextView(requireContext());
        lblModelo.setText("Sembradora modelo");
        cont.addView(lblModelo);

        final EditText etModelo = new EditText(requireContext());
        etModelo.setInputType(android.text.InputType.TYPE_CLASS_TEXT);
        etModelo.setMaxLines(1);
        cont.addView(etModelo);

        new androidx.appcompat.app.AlertDialog.Builder(requireActivity())
                .setTitle("Nuevo evento de siembra")
                .setView(cont)
                .setPositiveButton("Crear", (dialog, which) -> {
                    String prestador = etPrestador.getText().toString().trim();
                    String marca = etMarca.getText().toString().trim();
                    String modelo = etModelo.getText().toString().trim();

                    if (prestador.isEmpty() || marca.isEmpty() || modelo.isEmpty()) {
                        Toasty.error(requireActivity(), "Debes ingresar prestador de servicio, marca y modelo de la sembradora", Toast.LENGTH_LONG, true).show();
                        return;
                    }

                    for (CheckListSiembraEvento existente : eventosSiembra) {
                        if (prestador.equalsIgnoreCase(textoOVacio(existente.getPrestador_servicio()).trim())
                                && marca.equalsIgnoreCase(textoOVacio(existente.getSembradora_marca()).trim())
                                && modelo.equalsIgnoreCase(textoOVacio(existente.getSembradora_modelo()).trim())) {
                            Toasty.error(requireActivity(), "Ya existe un evento con ese prestador y esa sembradora", Toast.LENGTH_LONG, true).show();
                            return;
                        }
                    }

                    // TICKET 2494 - 2026-10-01: el evento que se esta dejando se guarda dentro de
                    // seleccionarEventoSiembra() mas abajo, ya no hace falta avisar ni chequear aca.

                    String claveUnicaEvento = config.getId()
                            + "" + config.getId_usuario()
                            + "" + Utilidades.fechaActualConHora()
                            .replaceAll(" ", "")
                            .replaceAll("-", "")
                            .replaceAll(":", "")
                            + "EV";

                    CheckListSiembraEvento nuevoEvento = new CheckListSiembraEvento();
                    nuevoEvento.setClave_unica_evento(claveUnicaEvento);
                    nuevoEvento.setClave_unica_cl_siembra(checkListSiembra.getClave_unica());
                    nuevoEvento.setPrestador_servicio(prestador);
                    nuevoEvento.setSembradora_marca(marca);
                    nuevoEvento.setSembradora_modelo(modelo);
                    nuevoEvento.setEstado_sincronizacion(0);

                    ExecutorService executor = Executors.newSingleThreadExecutor();
                    Future<Long> idFuture = executor.submit(() -> MainActivity.myAppDB.DaoClSiembra().insertEvento(nuevoEvento));
                    try {
                        // TICKET 2494 - 2026-10-01: bug critico corregido - el id autogenerado por
                        // Room nunca se guardaba de vuelta en nuevoEvento (quedaba en 0). Como
                        // @Update de Room busca la fila por ese id, cualquier guardado posterior de
                        // este evento (cambio de tab, GUARDAR) fallaba en silencio (0 filas
                        // afectadas) porque no existe una fila con id_evento = 0, perdiendose todo
                        // lo que se editara despues de crear el evento.
                        long newIdEvento = idFuture.get();
                        nuevoEvento.setId_evento((int) newIdEvento);
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
                    executor.shutdown();

                    eventosSiembra.add(nuevoEvento);
                    seleccionarEventoSiembra(nuevoEvento);
                })
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void guardarEventoSiembraActual(String claveUnicaClSiembra) {
        if (eventoActualSiembra == null) return;

        leerCamposHaciaEventoSiembra(eventoActualSiembra);
        eventoActualSiembra.setClave_unica_cl_siembra(claveUnicaClSiembra);
        eventoActualSiembra.setEstado_sincronizacion(0);

        // TICKET 2494 - 2026-09-30: las firmas de este evento quedan en TempFirmas con un tag
        // dinamico (tagFirmaEvento). Se consumen aca, hacia el evento, nunca hacia la cabecera.
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<List<TempFirmas>> firmasF = executor.submit(() ->
                MainActivity.myAppDB.DaoFirmas().getFirmasByDocum(Utilidades.TIPO_DOCUMENTO_CHECKLIST_SIEMBRA));

        try {
            List<TempFirmas> firmas = firmasF.get();
            String sufijoEvento = "_EV_" + eventoActualSiembra.getClave_unica_evento();

            for (TempFirmas ff : firmas) {
                String lugar = ff.getLugar_firma();
                if (lugar == null || !lugar.endsWith(sufijoEvento)) continue;

                String tagBase = lugar.substring(0, lugar.length() - sufijoEvento.length());

                if (tagBase.equals(Utilidades.DIALOG_TAG_RESPONSABLE_ASEO_INGRESO)) {
                    eventoActualSiembra.setFirma_responsable_aso_pre_siembra(ff.getPath());
                } else if (tagBase.equals(Utilidades.DIALOG_TAG_REVISOR_LIMPIEZA_INGRESO)) {
                    eventoActualSiembra.setFirma_revision_limpieza_pre_siembra(ff.getPath());
                } else if (tagBase.equals(Utilidades.DIALOG_TAG_RESPONSABLE_ASEO_SALIDA)) {
                    eventoActualSiembra.setFirma_responsable_aseo_post_siembra(ff.getPath());
                } else if (tagBase.equals(Utilidades.DIALOG_TAG_REVISOR_LIMPIEZA_SALIDA)) {
                    eventoActualSiembra.setFirma_revision_limpieza_post_siembra(ff.getPath());
                } else if (tagBase.equals(Utilidades.DIALOG_TAG_RESPONSABLE_CAMPO_INGRESO)) {
                    eventoActualSiembra.setFirma_responsable_campo(ff.getPath());
                } else if (tagBase.equals(Utilidades.DIALOG_TAG_RESPONSABLE_OPERARIO_INGRESO)) {
                    eventoActualSiembra.setFirma_operario_maquina(ff.getPath());
                } else if (tagBase.equals(Utilidades.DIALOG_TAG_RESPONSABLE_OPERARIO_TERMINO)) {
                    eventoActualSiembra.setFirma_operario_maquina_termino(ff.getPath());
                } else if (tagBase.equals(Utilidades.DIALOG_TAG_RESPONSABLE_CAMPO_TERMINO)) {
                    eventoActualSiembra.setFirma_responsable_campo_termino(ff.getPath());
                }
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        final CheckListSiembraEvento eventoAGuardar = eventoActualSiembra;
        executor.submit(() -> MainActivity.myAppDB.DaoClSiembra().updateEvento(eventoAGuardar));
        executor.shutdown();

        snapshotEventoActualCargado = snapshotCamposEventoSiembra();
    }

    private void showAlertForConfirmarGuardar() {
        View viewInfalted = LayoutInflater.from(requireActivity()).inflate(R.layout.alert_guardar_checklist, null);

        RadioGroup grupo_radios_estado = viewInfalted.findViewById(R.id.grupo_radios_estado);
        RadioButton rbtn_activo = viewInfalted.findViewById(R.id.rbtn_activo);
        RadioButton rbtn_pendiente = viewInfalted.findViewById(R.id.rbtn_pendiente);
        EditText et_apellido = viewInfalted.findViewById(R.id.et_apellido);


        if (checkListSiembra != null) {

            et_apellido.setText(checkListSiembra.getApellido_checklist());

            if (checkListSiembra.getEstado_documento() > 0) {
                rbtn_activo.setChecked(checkListSiembra.getEstado_documento() == 1);
                rbtn_pendiente.setChecked(checkListSiembra.getEstado_documento() == 2);
            }

        }

        final androidx.appcompat.app.AlertDialog builder = new androidx.appcompat.app.AlertDialog.Builder(requireActivity())
                .setView(viewInfalted)
                .setPositiveButton(getResources().getString(R.string.guardar), (dialogInterface, i) -> {
                })
                .setNegativeButton(getResources().getString(R.string.nav_cancel), (dialogInterface, i) -> {
                })
                .create();


        builder.setOnShowListener(dialog -> {
            Button b = builder.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE);
            Button c = builder.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE);
            b.setOnClickListener(view -> {

                if ((!rbtn_activo.isChecked() && !rbtn_pendiente.isChecked()) || et_apellido.getText().toString().isEmpty()) {
                    Toasty.error(requireActivity(), "Debes seleccionar un estado e ingresar una descripcion", Toast.LENGTH_LONG, true).show();
                    return;
                }
                int state = (rbtn_activo.isChecked()) ? 1 : 2;
                boolean isSaved = guardar(state, et_apellido.getText().toString());
                if (isSaved) builder.dismiss();

            });
            c.setOnClickListener(view -> builder.dismiss());
        });
        builder.setCancelable(false);
        builder.show();
    }

    // TICKET 2515 - 2026-10-02: las fechas del evento pueden venir en varios formatos (el servidor las guarda como
    // AAAA-MM-DD; la APK las muestra como DD-MM-AAAA; datos antiguos traen DD-MM-AA o con basura, ej "-02-11-2024").
    // Antes el selector de fecha asumia DD-MM-AAAA y hacia parseInt sin proteccion: cualquier otro formato tumbaba la APK.
    // Devuelve {anio, mes, dia} o null si no se puede interpretar.
    private int[] partesFecha(String texto) {
        try {
            if (texto == null) return null;
            String limpio = texto.trim().replaceAll("^[^0-9]+|[^0-9]+$", "");
            String[] p = limpio.split("[-/]");
            if (p.length != 3) return null;
            int a = Integer.parseInt(p[0]);
            int b = Integer.parseInt(p[1]);
            int c = Integer.parseInt(p[2]);
            int anio, mes, dia;
            if (p[0].length() == 4) {
                anio = a;
                mes = b;
                dia = c;
            } else {
                dia = a;
                mes = b;
                anio = (p[2].length() == 2) ? 2000 + c : c;
            }
            if (anio < 1900 || anio > 2100 || mes < 1 || mes > 12 || dia < 1 || dia > 31) return null;
            return new int[]{anio, mes, dia};
        } catch (Exception e) {
            return null;
        }
    }

    // fecha como la muestra la APK (DD-MM-AAAA); si no se puede interpretar queda vacia
    private String fechaParaMostrar(String texto) {
        int[] f = partesFecha(texto);
        if (f == null) return "";
        return String.format(Locale.ROOT, "%02d-%02d-%04d", f[2], f[1], f[0]);
    }

    private void levantarFecha(final EditText edit) {

        int[] inicial = partesFecha(edit.getText() != null ? edit.getText().toString() : "");
        if (inicial == null) {
            inicial = partesFecha(Utilidades.fechaActualSinHora());
        }
        if (inicial == null) {
            java.util.Calendar hoy = java.util.Calendar.getInstance();
            inicial = new int[]{hoy.get(java.util.Calendar.YEAR), hoy.get(java.util.Calendar.MONTH) + 1, hoy.get(java.util.Calendar.DAY_OF_MONTH)};
        }

        try {
            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (datePicker, year, month, dayOfMonth) -> {

                month = month + 1;
                String mes = "", dia;

                if (month < 10) {
                    mes = "0" + month;
                } else {
                    mes = String.valueOf(month);
                }

                if (dayOfMonth < 10) dia = "0" + dayOfMonth;
                else dia = String.valueOf(dayOfMonth);

                String finalDate = dia + "-" + mes + "-" + year;
                edit.setText(finalDate);
            }, inicial[0], inicial[1] - 1, inicial[2]);
            datePickerDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
