package cl.smapdev.curimapu.clases.bd;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class Migrations {
    public static final Migration MIGRATION_1_TO_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {

            database.execSQL("CREATE TABLE fotos_fichas ( " +
                    " id_fotos_fichas INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                    " nombre_foto_ficha TEXT , " +
                    " ruta_foto_ficha TEXT , " +
                    " id_ficha_fotos_local INTEGER NOT NULL DEFAULT 0, " +
                    " id_ficha_fotos_servidor INTEGER NOT NULL DEFAULT 0, " +
                    " fecha_hora_captura TEXT  DEFAULT '0000-00-00 00:00:00', " +
                    " id_usuario_captura INTEGER NOT NULL DEFAULT 0, " +
                    " id_dispo_captura INTEGER NOT NULL DEFAULT 0, " +
                    " estado_subida_foto INTEGER NOT NULL DEFAULT 0, " +
                    " encrypted_image TEXT " +
                    ");");

            database.execSQL("ALTER TABLE ficha ADD COLUMN id_ficha_local_ficha INTEGER DEFAULT 0 NOT NULL");
        }
    };

    public static final Migration MIGRATION_2_TO_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE fotos_fichas ADD COLUMN cabecera_subida INTEGER NOT NULL DEFAULT 0 ");
        }
    };


    public static final Migration MIGRATION_3_TO_4 = new Migration(3, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE agri_pred_temp ( " +
                    "id_agric INTEGER NOT NULL PRIMARY KEY ," +
                    "id_pred INTEGER NOT NULL DEFAULT 0, " +
                    "id_tempo INTEGER NOT NULL DEFAULT 0, " +
                    "norting TEXT  , " +
                    "easting TEXT " +
                    "); ");
//            database.execSQL("ALTER TABLE predio DROP id_agric, DROP id_tempo ");
        }
    };

    public static final Migration MIGRATION_4_TO_5 = new Migration(4, 5) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE ficha ADD COLUMN id_provincia_ficha TEXT ; ");
        }
    };

    public static final Migration MIGRATION_5_TO_6 = new Migration(5, 6) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE materiales ADD condition TEXT ; ");
            database.execSQL("ALTER TABLE materiales ADD certification TEXT ; ");

            database.execSQL("ALTER TABLE anexo_contrato ADD ready_batch TEXT ; ");
            database.execSQL("ALTER TABLE anexo_contrato ADD sectorial_office TEXT ; ");
            database.execSQL("ALTER TABLE anexo_contrato ADD field_sag TEXT ; ");
            database.execSQL("ALTER TABLE anexo_contrato ADD rch TEXT ; ");
            database.execSQL("ALTER TABLE anexo_contrato ADD sag_register_number TEXT ; ");
            database.execSQL("ALTER TABLE anexo_contrato ADD has_contrato TEXT ; ");
            database.execSQL("ALTER TABLE anexo_contrato ADD has_gps TEXT ; ");
            database.execSQL("ALTER TABLE anexo_contrato ADD orden_multiplicacion TEXT ; ");
            database.execSQL("ALTER TABLE anexo_contrato ADD sowing_date TEXT ; ");
            database.execSQL("ALTER TABLE anexo_contrato ADD lines_female TEXT ; ");
            database.execSQL("ALTER TABLE anexo_contrato ADD lines_male TEXT ; ");
            database.execSQL("ALTER TABLE anexo_contrato ADD sl_female TEXT ; ");
            database.execSQL("ALTER TABLE anexo_contrato ADD sl_real_sowing_female TEXT ; ");
            database.execSQL("ALTER TABLE anexo_contrato ADD sl_male TEXT ; ");
            database.execSQL("ALTER TABLE anexo_contrato ADD sl_real_sowing_male TEXT ; ");
            database.execSQL("ALTER TABLE anexo_contrato ADD sl_line_increase TEXT ; ");
            database.execSQL("ALTER TABLE anexo_contrato ADD sl_real_sowing_increase_line TEXT ; ");
            database.execSQL("ALTER TABLE anexo_contrato ADD results_raw_kgs TEXT ; ");
            database.execSQL("ALTER TABLE anexo_contrato ADD results_clean_kgs TEXT ; ");
            database.execSQL("ALTER TABLE anexo_contrato ADD yield_kg_ha TEXT ; ");


        }
    };

    public static final Migration MIGRATION_6_TO_7 = new Migration(6, 7) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE anexo_contrato ADD linea_incremento TEXT ; ");
        }
    };

    public static final Migration MIGRATION_7_TO_8 = new Migration(7, 8) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {

            database.execSQL("ALTER TABLE materiales ADD condition_mat TEXT ; ");

        }
    };

    public static final Migration MIGRATION_8_TO_9 = new Migration(8, 9) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {

            database.execSQL("ALTER TABLE crop_rotation ADD tipo_crop TEXT ; ");

        }
    };


    public static final Migration MIGRATION_9_TO_10 = new Migration(9, 10) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {

            database.execSQL("CREATE TABLE ficha_new ( " +
                    " id_ficha_new INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                    " id_ficha_local_ficha_new INTEGER NOT NULL DEFAULT 0, " +
                    " id_tempo_new TEXT , " +
                    " id_agric_new TEXT, " +
                    " oferta_de_negocio_new TEXT, " +
                    " id_region_new TEXT, " +
                    " id_comuna_new TEXT, " +
                    " id_provincia_new TEXT, " +
                    " localidad_new TEXT, " +
                    " ha_disponibles_new REAL NOT NULL  DEFAULT 0.0,  " +
                    " obs_new TEXT,  " +
                    " norting_new REAL NOT NULL  DEFAULT 0.0,  " +
                    " easting_new REAL NOT NULL DEFAULT 0.0,  " +
                    " id_est_fic_new INTEGER NOT NULL DEFAULT 0,  " +
                    " subida_new INTEGER NOT NULL DEFAULT 0,  " +
                    " id_pred_new INTEGER NOT NULL DEFAULT 0,  " +
                    " id_lote_new INTEGER NOT NULL DEFAULT 0 ,  " +
                    " coo_utm_ref_new TEXT ,  " +
                    " coo_utm_ampros_new TEXT ,  " +
                    " id_tipo_suelo_new TEXT ,  " +
                    " id_tipo_riego_new TEXT ,  " +
                    " experiencia_new TEXT ,  " +
                    " id_usuario_new INTEGER NOT NULL DEFAULT 0 ,  " +
                    " id_tipo_tenencia_maquinaria_new TEXT ,  " +
                    " id_tipo_tenencia_terreno_new TEXT ,  " +
                    " maleza_new TEXT ,  " +
                    " cabecera_new INTEGER NOT NULL DEFAULT 0 ,  " +
                    " predio_new TEXT ,  " +
                    " potrero_new TEXT ,  " +
                    " especie_new TEXT ,  " +
                    " estado_general_new TEXT ,  " +
                    " fecha_limite_siembra_new TEXT ,  " +
                    " observacion_negocio_new_new TEXT " +
                    ");");

        }
    };

    public static final Migration MIGRATION_10_TO_11 = new Migration(10, 11) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {

            database.execSQL("CREATE TABLE anexo_correo_fechas ( " +
                    " id_ac_cor_fech INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                    " id_ac_corr_fech INTEGER NOT NULL DEFAULT 0, " +
                    " id_fieldman INTEGER NOT NULL DEFAULT 0, " +
                    " inicio_despano TEXT , " +
                    " correo_inicio_despano INTEGER NOT NULL DEFAULT 0, " +
                    " cinco_porciento_floracion TEXT , " +
                    " correo_cinco_porciento_floracion INTEGER NOT NULL DEFAULT 0, " +
                    " inicio_corte_seda TEXT , " +
                    " correo_inicio_corte_seda INTEGER NOT NULL DEFAULT 0, " +
                    " inicio_cosecha TEXT , " +
                    " correo_inicio_cosecha INTEGER NOT NULL DEFAULT 0, " +
                    " termino_cosecha TEXT , " +
                    " correo_termino_cosecha INTEGER NOT NULL DEFAULT 0, " +
                    " termino_labores_post_cosechas TEXT , " +
                    " correo_termino_labores_post_cosechas INTEGER NOT NULL DEFAULT 0, " +
                    " detalle_labores TEXT , " +
                    " id_asistente INTEGER NOT NULL DEFAULT 0 );");

        }
    };


    public static final Migration MIGRATION_11_TO_12 = new Migration(11, 12) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {

            database.execSQL("ALTER TABLE temporada ADD especial_temporada INTEGER NOT NULL DEFAULT 0 ; ");

        }
    };


    public static final Migration MIGRATION_12_TO_13 = new Migration(12, 13) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {

            database.execSQL("ALTER TABLE visita ADD tomadas INTEGER NOT NULL DEFAULT 0 ; ");
            database.execSQL("ALTER TABLE detalle_visita_prop ADD tomada_detalle INTEGER NOT NULL DEFAULT 0 ; ");
            database.execSQL("ALTER TABLE fotos ADD tomada_foto INTEGER NOT NULL DEFAULT 0 ; ");

        }
    };


    public static final Migration MIGRATION_13_TO_14 = new Migration(13, 14) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE pro_cli_mat ADD COLUMN id_sub_propiedad_pcm INTEGER NOT NULL DEFAULT 0 ; ");
        }
    };

    public static
    final Migration MIGRATION_14_TO_15 = new Migration(14, 15) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL(" CREATE TABLE primera_prioridad (" +
                    "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT ," +
                    "id_ac INTEGER NOT NULL," +
                    "numAnexo TEXT," +
                    "nombreEspecie TEXT," +
                    "nombreAgricultor TEXT," +
                    "fechaUltimaVisita TEXT," +
                    "colorCrecimiento TEXT," +
                    "valorCrecimiento TEXT," +
                    "colorFitosanitario TEXT," +
                    "valorFitosanitario TEXT," +
                    "colorGeneral TEXT," +
                    "valorGeneral TEXT," +
                    "colorNdvi TEXT," +
                    "valorNdvi TEXT," +
                    "colorMi TEXT," +
                    "valorMi TEXT, " +
                    "colormaleza TEXT," +
                    "valormaleza TEXT," +
                    "colorCosecha TEXT," +
                    "valorCosecha TEXT " +
                    "); ");
        }
    };

    public static final Migration MIGRATION_15_TO_16 = new Migration(15, 16) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE primera_prioridad ADD COLUMN id_temporada INTEGER NOT NULL ;");
        }
    };

    public static final Migration MIGRATION_16_TO_17 = new Migration(16, 17) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL(" CREATE TABLE sitios_no_visitados (" +
                    "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT ," +
                    "id_ac INTEGER NOT NULL," +
                    "id_temporada INTEGER NOT NULL," +
                    "numAnexo TEXT," +
                    "nombreEspecie TEXT," +
                    "nombreAgricultor TEXT," +
                    "fechaUltimaVisita TEXT," +
                    "nombreUsuario TEXT," +
                    "nombreLote TEXT," +
                    "dias TEXT ); ");
        }
    };

    // TICKET 2491 - 2026-09-15: campos solo lectura, se ingresan y modifican desde el Libro de Campo (web)
    public static final Migration MIGRATION_17_TO_18 = new Migration(17, 18) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE anexo_correo_fechas ADD COLUMN fecha_floracion_hembra TEXT;");
            database.execSQL("ALTER TABLE anexo_correo_fechas ADD COLUMN correo_floracion_hembra INTEGER NOT NULL DEFAULT 0;");
            database.execSQL("ALTER TABLE anexo_correo_fechas ADD COLUMN fecha_incremento_linea TEXT;");
            database.execSQL("ALTER TABLE anexo_correo_fechas ADD COLUMN correo_incremento_linea INTEGER NOT NULL DEFAULT 0;");
        }
    };

    // TICKET 2494 - 2026-09-29: rediseno seccion Suelo y nuevo apartado Aislacion del checklist de siembra.
    // chequeo_aislacion y cultivo_anterior quedan como columnas huerfanas (SQLite no soporta DROP COLUMN aqui).
    public static final Migration MIGRATION_18_TO_19 = new Migration(18, 19) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE anexo_checklist_siembra ADD COLUMN cama_raices TEXT;");
            database.execSQL("ALTER TABLE anexo_checklist_siembra ADD COLUMN medicion_compactacion TEXT;");
            database.execSQL("ALTER TABLE anexo_checklist_siembra ADD COLUMN profundidad_cama_raices TEXT;");
            database.execSQL("ALTER TABLE anexo_checklist_siembra ADD COLUMN temperatura_suelo TEXT;");
            database.execSQL("ALTER TABLE anexo_checklist_siembra ADD COLUMN aislacion_norte TEXT;");
            database.execSQL("ALTER TABLE anexo_checklist_siembra ADD COLUMN aislacion_sur TEXT;");
            database.execSQL("ALTER TABLE anexo_checklist_siembra ADD COLUMN aislacion_este TEXT;");
            database.execSQL("ALTER TABLE anexo_checklist_siembra ADD COLUMN aislacion_oeste TEXT;");
        }
    };

    // TICKET 2494 - 2026-09-30: apartado Aislacion del Prospecto (tabla "ficha" = prospecto web).
    // Columnas reales en BD son aisla_norte/sur/este/oeste (sin "cion").
    public static final Migration MIGRATION_19_TO_20 = new Migration(19, 20) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE ficha ADD COLUMN aisla_norte TEXT;");
            database.execSQL("ALTER TABLE ficha ADD COLUMN aisla_sur TEXT;");
            database.execSQL("ALTER TABLE ficha ADD COLUMN aisla_este TEXT;");
            database.execSQL("ALTER TABLE ficha ADD COLUMN aisla_oeste TEXT;");
        }
    };

    // TICKET 2494 - 2026-09-30: campos nuevos de la seccion Cosechadora del checklist de cosecha
    // TICKET 2515 - 2026-10-05: se quita humedad_semilla (la humedad va en humedad_cosecha); la APK se reinstala, no se migra
    public static final Migration MIGRATION_20_TO_21 = new Migration(20, 21) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE anexo_checklist_cosecha ADD COLUMN concavo_utilizado TEXT;");
            database.execSQL("ALTER TABLE anexo_checklist_cosecha ADD COLUMN bushel_plus TEXT;");
            database.execSQL("ALTER TABLE anexo_checklist_cosecha ADD COLUMN fast_green TEXT;");
        }
    };

    // TICKET 2494 - 2026-09-30: eventos de siembra (H/M1/M2/M3), uno por fecha+tipo, ligados
    // al checklist de siembra via clave_unica_cl_siembra
    public static final Migration MIGRATION_21_TO_22 = new Migration(21, 22) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE anexo_checklist_siembra_evento ( " +
                    " id_evento INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                    " clave_unica_evento TEXT, " +
                    " clave_unica_cl_siembra TEXT, " +
                    " fecha_evento TEXT, " +
                    " tipo_evento TEXT, " +
                    " prestador_servicio TEXT, " +
                    " estado_discos TEXT, " +
                    " profundidad_siembra TEXT, " +
                    " distancia_fertilizante_semilla TEXT, " +
                    " tarros_semilla_pre_siembra TEXT, " +
                    " discos_sembradores_pre_siembra TEXT, " +
                    " estructura_maquinaria_pre_siembra TEXT, " +
                    " lugar_limpieza_pre_siembra TEXT, " +
                    " responsable_aseo_pre_siembra TEXT, " +
                    " rut_responsable_aseo_pre_siembra TEXT, " +
                    " responsable_revision_limpieza_pre_siembra TEXT, " +
                    " firma_responsable_aso_pre_siembra TEXT, " +
                    " stringed_responsable_aso_pre_siembra TEXT, " +
                    " firma_revision_limpieza_pre_siembra TEXT, " +
                    " stringed_revision_limpieza_pre_siembra TEXT, " +
                    " tarros_semilla_post_siembra TEXT, " +
                    " discos_sembradores_post_siembra TEXT, " +
                    " estructura_maquinaria_post_cosecha TEXT, " +
                    " lugar_limpieza_post_siembra TEXT, " +
                    " responsable_aseo_post_siembra TEXT, " +
                    " rut_responsable_aseo_post_siembra TEXT, " +
                    " encargado_revision_limpieza_post_siembra TEXT, " +
                    " firma_responsable_aseo_post_siembra TEXT, " +
                    " stringed_responsable_aseo_post_siembra TEXT, " +
                    " firma_revision_limpieza_post_siembra TEXT, " +
                    " stringed_revision_limpieza_post_siembra TEXT, " +
                    " desempeno_siembra TEXT, " +
                    " observacion_general TEXT, " +
                    " fecha_ingreso TEXT, " +
                    " hora_ingreso TEXT, " +
                    " nombre_supervisor_siembra TEXT, " +
                    " nombre_responsable_campo TEXT, " +
                    " nombre_operario_maquina TEXT, " +
                    " firma_responsable_campo TEXT, " +
                    " stringed_responsable_campo TEXT, " +
                    " firma_operario_maquina TEXT, " +
                    " stringed_operario_maquina TEXT, " +
                    " fecha_termino TEXT, " +
                    " hora_termino TEXT, " +
                    " nombre_supervisor_siembra_termino TEXT, " +
                    " nombre_responsable_campo_termino TEXT, " +
                    " nombre_operario_maquina_termino TEXT, " +
                    " firma_responsable_campo_termino TEXT, " +
                    " stringed_responsable_campo_termino TEXT, " +
                    " firma_operario_maquina_termino TEXT, " +
                    " stringed_operario_maquina_termino TEXT, " +
                    " estado_sincronizacion INTEGER NOT NULL DEFAULT 0 " +
                    ");");
        }
    };

    // TICKET 2494 - 2026-09-30: optimizacion de indices. Indices normales (no UNIQUE) en las
    // columnas que la descarga usa para buscar; no cambian datos ni consultas. Los nombres
    // deben ser exactamente los que genera Room (index_<tabla>_<columnas>) o la app se cae
    // al validar el esquema.
    public static final Migration MIGRATION_22_TO_23 = new Migration(22, 23) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_anexo_correo_fechas_id_ac_corr_fech` ON `anexo_correo_fechas` (`id_ac_corr_fech`)");
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_anexo_checklist_capacitacion_siembra_clave_unica` ON `anexo_checklist_capacitacion_siembra` (`clave_unica`)");
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_anexo_checklist_capacitacion_siembra_detalle_clave_unica_cl_cap_siembra_detalle` ON `anexo_checklist_capacitacion_siembra_detalle` (`clave_unica_cl_cap_siembra_detalle`)");
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_anexo_checklist_cosecha_clave_unica` ON `anexo_checklist_cosecha` (`clave_unica`)");
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_anexo_checklist_devolucion_semilla_clave_unica` ON `anexo_checklist_devolucion_semilla` (`clave_unica`)");
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_anexo_checklist_limpieza_camiones_clave_unica` ON `anexo_checklist_limpieza_camiones` (`clave_unica`)");
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_anexo_checklist_limpieza_camiones_detalle_clave_unica_cl_limpieza_camiones_detalle` ON `anexo_checklist_limpieza_camiones_detalle` (`clave_unica_cl_limpieza_camiones_detalle`)");
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_anexo_checklist_siembra_clave_unica` ON `anexo_checklist_siembra` (`clave_unica`)");
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_anexo_checklist_siembra_evento_clave_unica_evento` ON `anexo_checklist_siembra_evento` (`clave_unica_evento`)");
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_anexo_recomendaciones_clave_unica_recomendacion` ON `anexo_recomendaciones` (`clave_unica_recomendacion`)");
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_muestra_humedad_clave_unica_muestra` ON `muestra_humedad` (`clave_unica_muestra`)");
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_anexo_recomendaciones_id_ac` ON `anexo_recomendaciones` (`id_ac`)");
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_visita_clave_unica_visita` ON `visita` (`clave_unica_visita`)");
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_visita_id_anexo_visita` ON `visita` (`id_anexo_visita`)");
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_fotos_id_visita_foto` ON `fotos` (`id_visita_foto`)");
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_fotos_fichas_id_ficha_fotos_local` ON `fotos_fichas` (`id_ficha_fotos_local`)");
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_detalle_visita_prop_id_visita_detalle` ON `detalle_visita_prop` (`id_visita_detalle`)");
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_pro_cli_mat_id_materiales` ON `pro_cli_mat` (`id_materiales`)");
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_cli_pcm_id_prop_mat_cli` ON `cli_pcm` (`id_prop_mat_cli`)");
        }
    };

    // TICKET 2494 - 2026-10-01: seccion Siembra Anterior (especie/variedad/ogm/anexo_curimapu)
    // pasa de la cabecera a ser por evento, igual que Regulacion/Aseo/General/Ingreso/Salida
    public static final Migration MIGRATION_23_TO_24 = new Migration(23, 24) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE anexo_checklist_siembra_evento ADD COLUMN especie TEXT;");
            database.execSQL("ALTER TABLE anexo_checklist_siembra_evento ADD COLUMN variedad TEXT;");
            database.execSQL("ALTER TABLE anexo_checklist_siembra_evento ADD COLUMN ogm INTEGER NOT NULL DEFAULT 0;");
            database.execSQL("ALTER TABLE anexo_checklist_siembra_evento ADD COLUMN anexo_curimapu TEXT;");
        }
    };

    // TICKET 2494 - 2026-10-01: Mezcla se abre a 8 campos de fertilizacion (Carta A del correo del
    // ticket). mezcla queda en desuso (no se borra, se deja historico).
    public static final Migration MIGRATION_24_TO_25 = new Migration(24, 25) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE anexo_checklist_siembra ADD COLUMN cal_kg_ha REAL NOT NULL DEFAULT 0;");
            database.execSQL("ALTER TABLE anexo_checklist_siembra ADD COLUMN nitrogeno_pct REAL NOT NULL DEFAULT 0;");
            database.execSQL("ALTER TABLE anexo_checklist_siembra ADD COLUMN fosforo_pct REAL NOT NULL DEFAULT 0;");
            database.execSQL("ALTER TABLE anexo_checklist_siembra ADD COLUMN potasio_pct REAL NOT NULL DEFAULT 0;");
            database.execSQL("ALTER TABLE anexo_checklist_siembra ADD COLUMN magnesio_pct REAL NOT NULL DEFAULT 0;");
            database.execSQL("ALTER TABLE anexo_checklist_siembra ADD COLUMN azufre_pct REAL NOT NULL DEFAULT 0;");
            database.execSQL("ALTER TABLE anexo_checklist_siembra ADD COLUMN zinc_pct REAL NOT NULL DEFAULT 0;");
            database.execSQL("ALTER TABLE anexo_checklist_siembra ADD COLUMN boro_pct REAL NOT NULL DEFAULT 0;");
        }
    };

    // TICKET 2515 - 2026-10-02: fechas de siembra agregadas despues de Inicio Siembra (hembra) en anexo_correo_fechas
    // (termino hembra + inicio/termino macho 1, 2 y 3), cada una con su marca de correo.
    // Solo ADD COLUMN: fecha TEXT nullable y correo INTEGER NOT NULL DEFAULT 0 (igual que MIGRATION_17_TO_18).
    public static final Migration MIGRATION_25_TO_26 = new Migration(25, 26) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE anexo_correo_fechas ADD COLUMN termino_siembra_hembra TEXT;");
            database.execSQL("ALTER TABLE anexo_correo_fechas ADD COLUMN correo_termino_siembra_hembra INTEGER NOT NULL DEFAULT 0;");
            database.execSQL("ALTER TABLE anexo_correo_fechas ADD COLUMN inicio_siembra_macho1 TEXT;");
            database.execSQL("ALTER TABLE anexo_correo_fechas ADD COLUMN correo_inicio_siembra_macho1 INTEGER NOT NULL DEFAULT 0;");
            database.execSQL("ALTER TABLE anexo_correo_fechas ADD COLUMN termino_siembra_macho1 TEXT;");
            database.execSQL("ALTER TABLE anexo_correo_fechas ADD COLUMN correo_termino_siembra_macho1 INTEGER NOT NULL DEFAULT 0;");
            database.execSQL("ALTER TABLE anexo_correo_fechas ADD COLUMN inicio_siembra_macho2 TEXT;");
            database.execSQL("ALTER TABLE anexo_correo_fechas ADD COLUMN correo_inicio_siembra_macho2 INTEGER NOT NULL DEFAULT 0;");
            database.execSQL("ALTER TABLE anexo_correo_fechas ADD COLUMN termino_siembra_macho2 TEXT;");
            database.execSQL("ALTER TABLE anexo_correo_fechas ADD COLUMN correo_termino_siembra_macho2 INTEGER NOT NULL DEFAULT 0;");
            database.execSQL("ALTER TABLE anexo_correo_fechas ADD COLUMN inicio_siembra_macho3 TEXT;");
            database.execSQL("ALTER TABLE anexo_correo_fechas ADD COLUMN correo_inicio_siembra_macho3 INTEGER NOT NULL DEFAULT 0;");
            database.execSQL("ALTER TABLE anexo_correo_fechas ADD COLUMN termino_siembra_macho3 TEXT;");
            database.execSQL("ALTER TABLE anexo_correo_fechas ADD COLUMN correo_termino_siembra_macho3 INTEGER NOT NULL DEFAULT 0;");
        }
    };

    // TICKET 2515 - 2026-10-02: checklist de siembra es de H o M (tipo_siembra en cabecera) y el evento
    // pasa a definirse por prestador + maquina. En el evento se eliminan fecha_evento, tipo_evento,
    // profundidad_siembra y distancia_fertilizante_semilla (los 2 ultimos quedan en la cabecera) y se
    // agregan los 13 campos de Regulacion Sembradora. Como Room valida que no sobren columnas, la tabla
    // del evento se recrea y se copian los datos que siguen existiendo.
    public static final Migration MIGRATION_26_TO_27 = new Migration(26, 27) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE anexo_checklist_siembra ADD COLUMN tipo_siembra TEXT;");

            database.execSQL("CREATE TABLE anexo_checklist_siembra_evento_nuevo ( " +
                    " id_evento INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                    " clave_unica_evento TEXT, " +
                    " clave_unica_cl_siembra TEXT, " +
                    " especie TEXT, " +
                    " variedad TEXT, " +
                    " ogm INTEGER NOT NULL DEFAULT 0, " +
                    " anexo_curimapu TEXT, " +
                    " prestador_servicio TEXT, " +
                    " estado_discos TEXT, " +
                    " sembradora_marca TEXT, " +
                    " sembradora_modelo TEXT, " +
                    " trocha TEXT, " +
                    " tipo_sembradora TEXT, " +
                    " chequeo_selector TEXT, " +
                    " estado_maquina TEXT, " +
                    " desterronadores TEXT, " +
                    " presion_neumaticos TEXT, " +
                    " especie_lote_anterior TEXT, " +
                    " rueda_angosta TEXT, " +
                    " largo_guia TEXT, " +
                    " sistema_fertilizacion TEXT, " +
                    " cheque_caidas TEXT, " +
                    " tarros_semilla_pre_siembra TEXT, " +
                    " discos_sembradores_pre_siembra TEXT, " +
                    " estructura_maquinaria_pre_siembra TEXT, " +
                    " lugar_limpieza_pre_siembra TEXT, " +
                    " responsable_aseo_pre_siembra TEXT, " +
                    " rut_responsable_aseo_pre_siembra TEXT, " +
                    " responsable_revision_limpieza_pre_siembra TEXT, " +
                    " firma_responsable_aso_pre_siembra TEXT, " +
                    " stringed_responsable_aso_pre_siembra TEXT, " +
                    " firma_revision_limpieza_pre_siembra TEXT, " +
                    " stringed_revision_limpieza_pre_siembra TEXT, " +
                    " tarros_semilla_post_siembra TEXT, " +
                    " discos_sembradores_post_siembra TEXT, " +
                    " estructura_maquinaria_post_cosecha TEXT, " +
                    " lugar_limpieza_post_siembra TEXT, " +
                    " responsable_aseo_post_siembra TEXT, " +
                    " rut_responsable_aseo_post_siembra TEXT, " +
                    " encargado_revision_limpieza_post_siembra TEXT, " +
                    " firma_responsable_aseo_post_siembra TEXT, " +
                    " stringed_responsable_aseo_post_siembra TEXT, " +
                    " firma_revision_limpieza_post_siembra TEXT, " +
                    " stringed_revision_limpieza_post_siembra TEXT, " +
                    " desempeno_siembra TEXT, " +
                    " observacion_general TEXT, " +
                    " fecha_ingreso TEXT, " +
                    " hora_ingreso TEXT, " +
                    " nombre_supervisor_siembra TEXT, " +
                    " nombre_responsable_campo TEXT, " +
                    " nombre_operario_maquina TEXT, " +
                    " firma_responsable_campo TEXT, " +
                    " stringed_responsable_campo TEXT, " +
                    " firma_operario_maquina TEXT, " +
                    " stringed_operario_maquina TEXT, " +
                    " fecha_termino TEXT, " +
                    " hora_termino TEXT, " +
                    " nombre_supervisor_siembra_termino TEXT, " +
                    " nombre_responsable_campo_termino TEXT, " +
                    " nombre_operario_maquina_termino TEXT, " +
                    " firma_responsable_campo_termino TEXT, " +
                    " stringed_responsable_campo_termino TEXT, " +
                    " firma_operario_maquina_termino TEXT, " +
                    " stringed_operario_maquina_termino TEXT, " +
                    " estado_sincronizacion INTEGER NOT NULL DEFAULT 0 " +
                    ");");

            database.execSQL("INSERT INTO anexo_checklist_siembra_evento_nuevo ( " +
                    " id_evento, clave_unica_evento, clave_unica_cl_siembra, especie, variedad, ogm, anexo_curimapu, " +
                    " prestador_servicio, estado_discos, " +
                    " tarros_semilla_pre_siembra, discos_sembradores_pre_siembra, estructura_maquinaria_pre_siembra, " +
                    " lugar_limpieza_pre_siembra, responsable_aseo_pre_siembra, rut_responsable_aseo_pre_siembra, " +
                    " responsable_revision_limpieza_pre_siembra, firma_responsable_aso_pre_siembra, stringed_responsable_aso_pre_siembra, " +
                    " firma_revision_limpieza_pre_siembra, stringed_revision_limpieza_pre_siembra, " +
                    " tarros_semilla_post_siembra, discos_sembradores_post_siembra, estructura_maquinaria_post_cosecha, " +
                    " lugar_limpieza_post_siembra, responsable_aseo_post_siembra, rut_responsable_aseo_post_siembra, " +
                    " encargado_revision_limpieza_post_siembra, firma_responsable_aseo_post_siembra, stringed_responsable_aseo_post_siembra, " +
                    " firma_revision_limpieza_post_siembra, stringed_revision_limpieza_post_siembra, " +
                    " desempeno_siembra, observacion_general, fecha_ingreso, hora_ingreso, nombre_supervisor_siembra, " +
                    " nombre_responsable_campo, nombre_operario_maquina, firma_responsable_campo, stringed_responsable_campo, " +
                    " firma_operario_maquina, stringed_operario_maquina, fecha_termino, hora_termino, nombre_supervisor_siembra_termino, " +
                    " nombre_responsable_campo_termino, nombre_operario_maquina_termino, firma_responsable_campo_termino, " +
                    " stringed_responsable_campo_termino, firma_operario_maquina_termino, stringed_operario_maquina_termino, " +
                    " estado_sincronizacion ) " +
                    " SELECT " +
                    " id_evento, clave_unica_evento, clave_unica_cl_siembra, especie, variedad, ogm, anexo_curimapu, " +
                    " prestador_servicio, estado_discos, " +
                    " tarros_semilla_pre_siembra, discos_sembradores_pre_siembra, estructura_maquinaria_pre_siembra, " +
                    " lugar_limpieza_pre_siembra, responsable_aseo_pre_siembra, rut_responsable_aseo_pre_siembra, " +
                    " responsable_revision_limpieza_pre_siembra, firma_responsable_aso_pre_siembra, stringed_responsable_aso_pre_siembra, " +
                    " firma_revision_limpieza_pre_siembra, stringed_revision_limpieza_pre_siembra, " +
                    " tarros_semilla_post_siembra, discos_sembradores_post_siembra, estructura_maquinaria_post_cosecha, " +
                    " lugar_limpieza_post_siembra, responsable_aseo_post_siembra, rut_responsable_aseo_post_siembra, " +
                    " encargado_revision_limpieza_post_siembra, firma_responsable_aseo_post_siembra, stringed_responsable_aseo_post_siembra, " +
                    " firma_revision_limpieza_post_siembra, stringed_revision_limpieza_post_siembra, " +
                    " desempeno_siembra, observacion_general, fecha_ingreso, hora_ingreso, nombre_supervisor_siembra, " +
                    " nombre_responsable_campo, nombre_operario_maquina, firma_responsable_campo, stringed_responsable_campo, " +
                    " firma_operario_maquina, stringed_operario_maquina, fecha_termino, hora_termino, nombre_supervisor_siembra_termino, " +
                    " nombre_responsable_campo_termino, nombre_operario_maquina_termino, firma_responsable_campo_termino, " +
                    " stringed_responsable_campo_termino, firma_operario_maquina_termino, stringed_operario_maquina_termino, " +
                    " estado_sincronizacion " +
                    " FROM anexo_checklist_siembra_evento;");

            database.execSQL("DROP TABLE anexo_checklist_siembra_evento;");
            database.execSQL("ALTER TABLE anexo_checklist_siembra_evento_nuevo RENAME TO anexo_checklist_siembra_evento;");
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_anexo_checklist_siembra_evento_clave_unica_evento` ON `anexo_checklist_siembra_evento` (`clave_unica_evento`)");
        }
    };
}
