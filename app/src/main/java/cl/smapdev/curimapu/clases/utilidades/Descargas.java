package cl.smapdev.curimapu.clases.utilidades;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.List;

import cl.smapdev.curimapu.MainActivity;
import cl.smapdev.curimapu.clases.relaciones.CheckListCapCompleto;
import cl.smapdev.curimapu.clases.relaciones.CheckListLimpiezaCamionesCompleto;
import cl.smapdev.curimapu.clases.relaciones.GsonDescargas;
import cl.smapdev.curimapu.clases.relaciones.Respuesta;
import cl.smapdev.curimapu.clases.retrofit.ApiService;
import cl.smapdev.curimapu.clases.retrofit.RetrofitClient;
import cl.smapdev.curimapu.clases.tablas.AnexoCorreoFechas;
import cl.smapdev.curimapu.clases.tablas.CheckListCapacitacionSiembra;
import cl.smapdev.curimapu.clases.tablas.CheckListCapacitacionSiembraDetalle;
import cl.smapdev.curimapu.clases.tablas.CheckListCosecha;
import cl.smapdev.curimapu.clases.tablas.CheckListLimpiezaCamiones;
import cl.smapdev.curimapu.clases.tablas.CheckListSiembra;
import cl.smapdev.curimapu.clases.tablas.CheckListSiembraEvento;
import cl.smapdev.curimapu.clases.tablas.ChecklistDevolucionSemilla;
import cl.smapdev.curimapu.clases.tablas.ChecklistLimpiezaCamionesDetalle;
import cl.smapdev.curimapu.clases.tablas.Config;
import cl.smapdev.curimapu.clases.tablas.Evaluaciones;
import cl.smapdev.curimapu.clases.tablas.FichasNew;
import cl.smapdev.curimapu.clases.tablas.MuestraHumedad;
import cl.smapdev.curimapu.clases.tablas.Visitas;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Descargas {

    public static void primeraDescarga(final MainActivity activity, String imei, int id, String version) {

        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(activity);
        progressDialog.setTitle("Espere un momento...");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(100);
        progressDialog.show();

        Config config = MainActivity.myAppDB.myDao().getConfig();

        ApiService apiService = RetrofitClient.getClient(config.getServidorSeleccionado()).create(ApiService.class);
        Call<GsonDescargas> call = apiService.descargaPrimera(imei, id, version);
        call.enqueue(new Callback<GsonDescargas>() {
            @Override
            public void onResponse(@NonNull Call<GsonDescargas> call, @NonNull Response<GsonDescargas> response) {
                int problema = 0;
                GsonDescargas gsonDescargas = response.body();
                if (gsonDescargas != null) {
                    int id = gsonDescargas.getId_dispo();

                    int codigoRespuesta = 1;
                    if (gsonDescargas.getRespuestas() != null && gsonDescargas.getRespuestas().size() > 0) {

                        for (Respuesta rsp : gsonDescargas.getRespuestas()) {
                            if (rsp.getCodigoRespuesta() == 5) {
                                codigoRespuesta = 5;
                                break;
                            }
                        }
                    }

                    if (codigoRespuesta > 1) {

                        Utilidades.avisoListo(activity, "ATENCION", "NO TIENES LA ULTIMA VERSION DE LA APLICACION, FAVOR ACTUALIZAR", "ENTIENDO");
                        problema = 1;
                    } else {
                        if (id > 0) {
                            Config config = MainActivity.myAppDB.myDao().getConfig();
                            if (config == null) {
                                Config config1 = new Config();
                                config1.setId(id);
                                config1.setServidorSeleccionado(Utilidades.URL_SERVER_API);
                                MainActivity.myAppDB.myDao().setConfig(config1);
                            } else {
                                MainActivity.myAppDB.myDao().updateConfig(id);
                            }
                        }
                        if (gsonDescargas.getUsuarios() != null && gsonDescargas.getUsuarios().size() > 0) {
                            try {
                                MainActivity.myAppDB.myDao().deleteUsuario();
                                List<Long> inserts = MainActivity.myAppDB.myDao().setUsuarios(gsonDescargas.getUsuarios());
                                int c = 1;
                                for (long l : inserts) {
                                    if (l <= 0) {
                                        problema = 1;
                                    } else {
                                        progressDialog.setProgress(c * 100 / inserts.size());
                                        c++;
                                    }
                                }
                            } catch (SQLiteException e) {
                                problema = 1;
                                Log.e("SQLITE", e.getMessage());
                            }
                        }
                        if (gsonDescargas.getTemporadas() != null && gsonDescargas.getTemporadas().size() > 0) {
                            try {

                                MainActivity.myAppDB.myDao().deleteTemporadas();
                                List<Long> inserts = MainActivity.myAppDB.myDao().insertTemporada(gsonDescargas.getTemporadas());

                            } catch (SQLiteException e) {
                                Toasty.error(activity, e.getMessage(), Toast.LENGTH_LONG, true).show();
                                Log.e("SQLITE", e.getMessage());
                            }
                        } else {
                            MainActivity.myAppDB.myDao().deleteTemporadas();
                        }

                        MainActivity.myAppDB.myDao().deleteEspecie();
                        if (gsonDescargas.getEspecieList() != null && !gsonDescargas.getEspecieList().isEmpty()) {
                            try {
                                MainActivity.myAppDB.myDao().insertEspecie(gsonDescargas.getEspecieList());

                            } catch (SQLiteException e) {
                                Toasty.error(activity, e.getMessage(), Toast.LENGTH_LONG, true).show();
                                Log.e("SQLITE", e.getMessage());
                            }
                        }
                    }
                } else {
                    problema = 1;
                }

                if (problema > 0) {
                    Toasty.error(activity, "No se pudo descargar todo", Toast.LENGTH_SHORT, true).show();
                    progressDialog.dismiss();
                } else {
                    Toasty.success(activity, "Todo descargado con exito", Toast.LENGTH_SHORT, true).show();
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(@NonNull Call<GsonDescargas> call, @NonNull Throwable t) {
                t.printStackTrace();
                System.out.println(t.getMessage());
                Toasty.error(activity, "No se pudo descargar todo", Toast.LENGTH_SHORT, true).show();
                progressDialog.dismiss();
            }
        });

    }


    // TICKET 2494 - 2026-09-30: helper de cronometraje por seccion, para saber cual de los
    // ~20 bloques de volqueoDatos() se lleva el tiempo cuando el guardado demora varios minutos.
    // TICKET 2494 - 2026-10-01: bug corregido - esto solo escribia a Logcat (Log.d), por eso no
    // aparecia en timing_descarga.log. Ahora usa Utilidades.logTiempoDescarga() igual que el resto.
    private static long tSeccion;
    private static Context contextTiming;

    private static void iniciaSeccion() {
        tSeccion = System.currentTimeMillis();
    }

    private static void terminaSeccion(String nombre, int cantidad) {
        long ms = System.currentTimeMillis() - tSeccion;
        if (cantidad > 0 || ms > 50) {
            String linea = "  seccion=" + nombre + " cantidad=" + cantidad + " tiempo=" + ms + "ms";
            if (contextTiming != null) {
                Utilidades.logTiempoDescarga(contextTiming, linea);
            } else {
                Log.d("TIMING_DESCARGA", linea);
            }
        }
    }

    public static boolean[] volqueoDatos(GsonDescargas gsonDescargas, Context context) throws RuntimeException {
        contextTiming = context;

        boolean[] problema = {false, false};

        iniciaSeccion();
        int cantFechasAnexos = (gsonDescargas.getArray_fechas_anexos() != null) ? gsonDescargas.getArray_fechas_anexos().size() : 0;
        if (gsonDescargas.getArray_fechas_anexos() != null && !gsonDescargas.getArray_fechas_anexos().isEmpty()) {
            try {
                // TICKET 2494 - 2026-09-30: mismo patron que en visitas - una consulta+escritura por
                // cada anexo sin transaccion. Se agrupa en una transaccion propia de esta seccion.
                MainActivity.myAppDB.runInTransaction(() -> {
                for (AnexoCorreoFechas fch : gsonDescargas.getArray_fechas_anexos()) {
                    AnexoCorreoFechas f = MainActivity.myAppDB.DaoAnexosFechas().getAnexoCorreoFechasByAnexo(fch.getId_ac_corr_fech());
                    if (f != null) {
                        f.setCorreo_cinco_porciento_floracion(fch.getCorreo_cinco_porciento_floracion());
                        f.setCorreo_inicio_corte_seda(fch.getCorreo_inicio_corte_seda());
                        f.setCorreo_inicio_cosecha(fch.getCorreo_inicio_cosecha());
                        f.setCorreo_inicio_despano(fch.getCorreo_inicio_despano());
                        f.setCorreo_termino_cosecha(fch.getCorreo_termino_cosecha());
                        f.setCorreo_termino_labores_post_cosechas(fch.getCorreo_termino_labores_post_cosechas());
                        f.setCorreo_destruccion_semillero(fch.getCorreo_destruccion_semillero());
                        f.setCorreo_siembra_temprana(fch.getCorreo_siembra_temprana());
                        f.setCorreo_inicio_siembra(fch.getCorreo_inicio_siembra());

                        f.setInicio_siembra(fch.getInicio_siembra());
                        f.setCinco_porciento_floracion(fch.getCinco_porciento_floracion());
                        f.setInicio_corte_seda(fch.getInicio_corte_seda());
                        f.setInicio_cosecha(fch.getInicio_cosecha());
                        f.setInicio_despano(fch.getInicio_despano());
                        f.setTermino_cosecha(fch.getTermino_cosecha());
                        f.setTermino_labores_post_cosechas(fch.getTermino_labores_post_cosechas());

                        f.setCantidad_has_destruidas(fch.getCantidad_has_destruidas());
                        f.setDestruc_semill_ensayo(fch.getDestruc_semill_ensayo());
                        f.setMotivo_destruccion(fch.getMotivo_destruccion());
                        f.setTipo_graminea(fch.getTipo_graminea());
                        f.setSiem_tempra_grami(fch.getSiem_tempra_grami());
                        f.setId_fieldman(fch.getId_fieldman());
                        f.setHora_destruccion_semillero(fch.getHora_destruccion_semillero());
                        f.setFecha_destruccion_semillero(fch.getFecha_destruccion_semillero());
                        f.setHora_inicio_cosecha(fch.getHora_inicio_cosecha());
                        f.setDetalle_labores(fch.getDetalle_labores());

                        // TICKET 2491 - 2026-09-16: faltaban estos 4 campos aca - por eso una
                        // edicion hecha desde la web (Libro de Campo) despues de que el anexo ya
                        // existia localmente (creado por la APK) nunca se reflejaba al Descargar,
                        // se quedaba pegada la fecha/estado original de la APK.
                        f.setFecha_floracion_hembra(fch.getFecha_floracion_hembra());
                        f.setCorreo_floracion_hembra(fch.getCorreo_floracion_hembra());
                        f.setFecha_incremento_linea(fch.getFecha_incremento_linea());
                        f.setCorreo_incremento_linea(fch.getCorreo_incremento_linea());

                        // TICKET 2515 - 2026-10-02: fechas de siembra nuevas (fecha + estado de correo)
                        f.setTermino_siembra_hembra(fch.getTermino_siembra_hembra());
                        f.setCorreo_termino_siembra_hembra(fch.getCorreo_termino_siembra_hembra());
                        f.setInicio_siembra_macho1(fch.getInicio_siembra_macho1());
                        f.setCorreo_inicio_siembra_macho1(fch.getCorreo_inicio_siembra_macho1());
                        f.setTermino_siembra_macho1(fch.getTermino_siembra_macho1());
                        f.setCorreo_termino_siembra_macho1(fch.getCorreo_termino_siembra_macho1());
                        f.setInicio_siembra_macho2(fch.getInicio_siembra_macho2());
                        f.setCorreo_inicio_siembra_macho2(fch.getCorreo_inicio_siembra_macho2());
                        f.setTermino_siembra_macho2(fch.getTermino_siembra_macho2());
                        f.setCorreo_termino_siembra_macho2(fch.getCorreo_termino_siembra_macho2());
                        f.setInicio_siembra_macho3(fch.getInicio_siembra_macho3());
                        f.setCorreo_inicio_siembra_macho3(fch.getCorreo_inicio_siembra_macho3());
                        f.setTermino_siembra_macho3(fch.getTermino_siembra_macho3());
                        f.setCorreo_termino_siembra_macho3(fch.getCorreo_termino_siembra_macho3());

                        f.setEstado_sincro_corr_fech(1);

                        MainActivity.myAppDB.DaoAnexosFechas().UpdateFechasAnexos(f);
                    } else {
                        MainActivity.myAppDB.DaoAnexosFechas().insertFechasAnexos(fch);
                    }
                }
                });
            } catch (SQLiteException e) {
                Log.e("SQLITE", e.getMessage());
                problema[0] = true;
            }
        }
        terminaSeccion("fechas_anexos", cantFechasAnexos);

//        MainActivity.myAppDB.myDao().deleteProCliMat();
        iniciaSeccion();
        int cantProCliMat = (gsonDescargas.getPro_cli_matList() != null) ? gsonDescargas.getPro_cli_matList().size() : 0;
        if (gsonDescargas.getPro_cli_matList() != null && !gsonDescargas.getPro_cli_matList().isEmpty()) {
            try {
                MainActivity.myAppDB.myDao().insertInterfaz(gsonDescargas.getPro_cli_matList());
            } catch (SQLiteException ignored) {
            }
        }
        terminaSeccion("pro_cli_mat", cantProCliMat);

        iniciaSeccion();
        int cantDevSemilla = (gsonDescargas.getChecklistDevolucionSemillas() != null) ? gsonDescargas.getChecklistDevolucionSemillas().size() : 0;
        if (gsonDescargas.getChecklistDevolucionSemillas() != null && !gsonDescargas.getChecklistDevolucionSemillas().isEmpty()) {
            try {
                MainActivity.myAppDB.runInTransaction(() -> {
                    for (ChecklistDevolucionSemilla ck : gsonDescargas.getChecklistDevolucionSemillas()) {
                        ChecklistDevolucionSemilla chk = MainActivity.myAppDB.DaoCheckListDevolucionSemilla().getCLDevolucionSemillaByClaveUnica(ck.getClave_unica());
                        if (chk != null) {
                            ck.setId_cl_devolucion_semilla(chk.getId_cl_devolucion_semilla());
                            MainActivity.myAppDB.DaoCheckListDevolucionSemilla().updateClDevolucionSemilla(ck);
                        } else {
                            MainActivity.myAppDB.DaoCheckListDevolucionSemilla().insertClDevolucionSemilla(ck);
                        }
                    }
                });
            } catch (SQLiteException ignored) {
            }
        }
        terminaSeccion("checklist_devolucion_semilla", cantDevSemilla);

        iniciaSeccion();
        int cantClSiembra = (gsonDescargas.getCheckListSiembras() != null) ? gsonDescargas.getCheckListSiembras().size() : 0;
        if (gsonDescargas.getCheckListSiembras() != null && !gsonDescargas.getCheckListSiembras().isEmpty()) {
            try {
                MainActivity.myAppDB.runInTransaction(() -> {
                    for (CheckListSiembra ck : gsonDescargas.getCheckListSiembras()) {
                        CheckListSiembra chk = MainActivity.myAppDB.DaoClSiembra().getCLSiembraByClaveUnica(ck.getClave_unica());
                        if (chk != null) {
                            ck.setId_cl_siembra(chk.getId_cl_siembra());
                            MainActivity.myAppDB.DaoClSiembra().updateClSiembra(ck);
                        } else {
                            MainActivity.myAppDB.DaoClSiembra().insertClSiembra(ck);
                        }

                        // TICKET 2494 - 2026-09-30: eventos de siembra (H/M1/M2/M3) del checklist
                        if (ck.getEventos_siembra() != null) {
                            for (CheckListSiembraEvento evento : ck.getEventos_siembra()) {
                                CheckListSiembraEvento eventoLocal = MainActivity.myAppDB
                                        .DaoClSiembra()
                                        .getEventoByClaveUnica(evento.getClave_unica_evento());
                                if (eventoLocal != null) {
                                    evento.setId_evento(eventoLocal.getId_evento());
                                    MainActivity.myAppDB.DaoClSiembra().updateEvento(evento);
                                } else {
                                    MainActivity.myAppDB.DaoClSiembra().insertEvento(evento);
                                }
                            }
                        }
                    }
                });
            } catch (SQLiteException ignored) {
            }
        }
        terminaSeccion("checklist_siembra_eventos", cantClSiembra);

        iniciaSeccion();
        int cantClCosecha = (gsonDescargas.getCheckListCosecha() != null) ? gsonDescargas.getCheckListCosecha().size() : 0;
        if (gsonDescargas.getCheckListCosecha() != null && !gsonDescargas.getCheckListCosecha().isEmpty()) {
            try {
                MainActivity.myAppDB.runInTransaction(() -> {
                    for (CheckListCosecha ck : gsonDescargas.getCheckListCosecha()) {
                        CheckListCosecha chk = MainActivity.myAppDB.DaoCheckListCosecha().getCLCosechaByClaveUnica(ck.getClave_unica());

                        if (chk != null) {
                            ck.setId_cl_siembra(chk.getId_cl_siembra());
                            MainActivity.myAppDB.DaoCheckListCosecha().updateClCosecha(ck);
                        } else {
                            MainActivity.myAppDB.DaoCheckListCosecha().insertClCosecha(ck);
                        }
                    }
                });
            } catch (SQLiteException ignored) {
            }

        }
        terminaSeccion("checklist_cosecha", cantClCosecha);

        // TICKET 2494 - 2026-10-01: mismo patron sin transaccion que evaluaciones/visitas (consulta
        // + escritura por cabecera y por cada detalle), envuelto en una transaccion propia.
        iniciaSeccion();
        int cantLimpiezaCamiones = (gsonDescargas.getCheckListLimpiezaCamionesCompletos() != null) ? gsonDescargas.getCheckListLimpiezaCamionesCompletos().size() : 0;
        if (gsonDescargas.getCheckListLimpiezaCamionesCompletos() != null && !gsonDescargas.getCheckListLimpiezaCamionesCompletos().isEmpty()) {
            MainActivity.myAppDB.runInTransaction(() -> {
            for (CheckListLimpiezaCamionesCompleto ck : gsonDescargas.getCheckListLimpiezaCamionesCompletos()) {
                CheckListLimpiezaCamiones chk = MainActivity.myAppDB
                        .DaoCheckListLimpiezaCamiones()
                        .getClLimpiezaCamionesByClaveUnica(ck.getCabecera().getClave_unica());
                //update
                if (chk != null) {
                    ck.getCabecera().setId_cl_limpieza_camiones(chk.getId_cl_limpieza_camiones());
                    MainActivity.myAppDB.DaoCheckListLimpiezaCamiones()
                            .updateLimpiezaCamiones(ck.getCabecera());
                } else {
                    //insert
                    MainActivity.myAppDB.DaoCheckListLimpiezaCamiones()
                            .insertLimpiezaCamiones(ck.getCabecera());
                }

                for (ChecklistLimpiezaCamionesDetalle detalle : ck.getDetalles()) {

                    ChecklistLimpiezaCamionesDetalle chkDF = MainActivity.myAppDB
                            .DaoCheckListLimpiezaCamiones()
                            .getLimpiezaCamionesDetallesByClaveUnica(
                                    detalle.getClave_unica_cl_limpieza_camiones_detalle());


                    if (chkDF != null) {
                        detalle.setId_ac_cl_limpieza_camiones_detalle(chkDF.getId_ac_cl_limpieza_camiones_detalle());
                        MainActivity.myAppDB.DaoCheckListLimpiezaCamiones()
                                .updateDetalle(detalle);
                    } else {
                        //insert
                        ChecklistLimpiezaCamionesDetalle det = new ChecklistLimpiezaCamionesDetalle();
                        det.setLimpieza_anterior_limpieza_camiones(detalle.getLimpieza_anterior_limpieza_camiones());
                        det.setClave_unica_cl_limpieza_camiones_detalle(detalle.getClave_unica_cl_limpieza_camiones_detalle());
                        det.setClave_unica_cl_limpieza_camiones(detalle.getClave_unica_cl_limpieza_camiones());
                        det.setNombre_chofer_limpieza_camiones(detalle.getNombre_chofer_limpieza_camiones());
                        det.setPatente_camion_limpieza_camiones(detalle.getPatente_camion_limpieza_camiones());
                        det.setPatente_carro_limpieza_camiones(detalle.getPatente_carro_limpieza_camiones());
                        det.setEstado_general_recepcion_camion_campo_limpieza_camiones(detalle.getEstado_general_recepcion_camion_campo_limpieza_camiones());
                        det.setEquipo_utilizado_limpieza_camiones(detalle.getEquipo_utilizado_limpieza_camiones());
                        det.setLimpieza_puertas_laterales_limpieza_camiones(detalle.getLimpieza_puertas_laterales_limpieza_camiones());
                        det.setLimpieza_puertas_traseras_limpieza_camiones(detalle.getLimpieza_puertas_traseras_limpieza_camiones());
                        det.setLimpieza_piso_limpieza_camiones(detalle.getLimpieza_piso_limpieza_camiones());
                        det.setInspeccion_rejillas_mallas_limpieza_camiones(detalle.getInspeccion_rejillas_mallas_limpieza_camiones());
                        det.setPisos_costados_batea_sin_orificios_limpieza_camiones(detalle.getPisos_costados_batea_sin_orificios_limpieza_camiones());
                        det.setCamion_carro_limpio_limpieza_camiones(detalle.getCamion_carro_limpio_limpieza_camiones());
                        det.setCarpa_limpia_limpieza_camiones(detalle.getCarpa_limpia_limpieza_camiones());
                        det.setSistema_cerrado_puertas_limpieza_camiones(detalle.getSistema_cerrado_puertas_limpieza_camiones());
                        det.setNivel_llenado_carga_limpieza_camiones(detalle.getNivel_llenado_carga_limpieza_camiones());
                        det.setSello_color_indica_condicion_limpieza_camiones(detalle.getSello_color_indica_condicion_limpieza_camiones());
                        det.setEtiqueta_cosecha_adherida_camion_jumbo_limpieza_camiones(detalle.getEtiqueta_cosecha_adherida_camion_jumbo_limpieza_camiones());
                        det.setSello_verde_curimapu_cierre_camion_limpieza_camiones(detalle.getSello_verde_curimapu_cierre_camion_limpieza_camiones());
                        det.setFirma_cl_limpieza_camiones_detalle(detalle.getFirma_cl_limpieza_camiones_detalle());
                        det.setStringed_cl_limpieza_camiones_detalle(detalle.getStringed_cl_limpieza_camiones_detalle());
                        det.setEstado_sincronizacion_detalle(detalle.getEstado_sincronizacion_detalle());
                        MainActivity.myAppDB.DaoCheckListLimpiezaCamiones()
                                .insertLimpiezaCamionesDetalle(det);
                    }

                }

            }
            });
        }
        terminaSeccion("checklist_limpieza_camiones", cantLimpiezaCamiones);

        // TICKET 2494 - 2026-10-01: mismo patron sin transaccion que evaluaciones/visitas,
        // envuelto en una transaccion propia.
        iniciaSeccion();
        int cantCapSiembra = (gsonDescargas.getCheckListCapCompletos() != null) ? gsonDescargas.getCheckListCapCompletos().size() : 0;
        if (gsonDescargas.getCheckListCapCompletos() != null && !gsonDescargas.getCheckListCapCompletos().isEmpty()) {
            MainActivity.myAppDB.runInTransaction(() -> {
            for (CheckListCapCompleto ck : gsonDescargas.getCheckListCapCompletos()) {
                CheckListCapacitacionSiembra chk = MainActivity.myAppDB
                        .DaoCheckListCapSiembra()
                        .getClCapSiembraByClaveUnica(ck.getCabecera().getClave_unica());
                //update
                if (chk != null) {
                    ck.getCabecera().setId_cl_cap_siembra(chk.getId_cl_cap_siembra());
                    MainActivity.myAppDB.DaoCheckListCapSiembra()
                            .updateCapacitacionSiembra(ck.getCabecera());
                } else {
                    //insert
                    MainActivity.myAppDB.DaoCheckListCapSiembra()
                            .insertCapacitacionSiembra(ck.getCabecera());
                }


                for (CheckListCapacitacionSiembraDetalle detalle : ck.getDetalles()) {

                    CheckListCapacitacionSiembraDetalle chkDF = MainActivity.myAppDB
                            .DaoCheckListCapSiembra()
                            .getCapSiembraDetallesByClaveUnica(
                                    detalle.getClave_unica_cl_cap_siembra_detalle());

                    if (chkDF != null) {
                        detalle.setId_cl_cap_siembra_detalle(chkDF.getId_cl_cap_siembra_detalle());
                        MainActivity.myAppDB.DaoCheckListCapSiembra()
                                .updateDetalle(detalle);
                    } else {
                        //insert
                        MainActivity.myAppDB.DaoCheckListCapSiembra()
                                .insertCapacitacionSiembraDetalle(detalle);
                    }
                }
            }
            });
        }
        terminaSeccion("checklist_capacitacion_siembra", cantCapSiembra);

        // TICKET 2494 - 2026-10-01: medido con timing_descarga.log - esta seccion sola se llevaba
        // 124 de 201 segundos del guardado total (18455 registros, 1 consulta + 1 escritura cada
        // uno sin transaccion). Mismo arreglo que en visitas/fechas_anexos: una transaccion propia
        // de esta seccion, para no repetir el bug del intento anterior de envolver todo el metodo.
        iniciaSeccion();
        int cantEvaluaciones = (gsonDescargas.getEvaluaciones() != null) ? gsonDescargas.getEvaluaciones().size() : 0;
        if (gsonDescargas.getEvaluaciones() != null && !gsonDescargas.getEvaluaciones().isEmpty()) {
            MainActivity.myAppDB.runInTransaction(() -> {
                for (Evaluaciones ck : gsonDescargas.getEvaluaciones()) {
                    Evaluaciones chk = MainActivity.myAppDB.DaoEvaluaciones().getEvaluacionesByClaveUnica(ck.getClave_unica_recomendacion());
                    if (chk != null) {
                        ck.setId_ac_recom(chk.getId_ac_recom());
                        MainActivity.myAppDB.DaoEvaluaciones().updateEvaluaciones(ck);
                    } else {
                        MainActivity.myAppDB.DaoEvaluaciones().insertEvaluaciones(ck);
                    }
                }
            });
        }
        terminaSeccion("evaluaciones", cantEvaluaciones);


//        MainActivity.myAppDB.myDao().deleteDetalle();
        iniciaSeccion();
        int cantDetalleVisita = (gsonDescargas.getDetalle_visita_props() != null) ? gsonDescargas.getDetalle_visita_props().size() : 0;
        if (gsonDescargas.getDetalle_visita_props() != null && !gsonDescargas.getDetalle_visita_props().isEmpty()) {
            try {
                MainActivity.myAppDB.myDao().insertDetalle(gsonDescargas.getDetalle_visita_props());
            } catch (SQLiteException ignored) {
            }
        }
        terminaSeccion("detalle_visita_prop", cantDetalleVisita);


//        MainActivity.myAppDB.myDao().deleteVisitas();
        // TICKET 2494 - 2026-09-30: la lectura+escritura de cada visita se hacia sin transaccion
        // (un commit por visita). Con miles de visitas eso era el cuello de botella real del
        // guardado (confirmado con timing_descarga.log). Se envuelve SOLO este loop en una
        // transaccion propia -no todo volqueoDatos() como se probo antes y se reviertio- para que
        // si algo falla aca se deshaga nada mas esta seccion, sin afectar el resto de la temporada.
        iniciaSeccion();
        int cantVisitas = (gsonDescargas.getVisitasList() != null) ? gsonDescargas.getVisitasList().size() : 0;
        if (gsonDescargas.getVisitasList() != null && !gsonDescargas.getVisitasList().isEmpty()) {
            try {
                MainActivity.myAppDB.runInTransaction(() -> {
                    for (Visitas ln : gsonDescargas.getVisitasList()) {
                        Visitas viCU = MainActivity.myAppDB.myDao().getVisitasByClaveUnica(ln.getClave_unica_visita());
                        Visitas viId = MainActivity.myAppDB.myDao().getVisitaById(ln.getId_visita(), ln.getClave_unica_visita());
                        if (viId == null && viCU != null) {
                            MainActivity.myAppDB.myDao().deleteVisitaDescarga(viCU);
                        }
                        MainActivity.myAppDB.myDao().updateFotos(ln.getId_visita(), ln.getId_visita_local(), ln.getId_dispo());
                    }
                    MainActivity.myAppDB.myDao().setVisita(gsonDescargas.getVisitasList());
                });
            } catch (SQLiteException e) {
                Log.e("SQLITE", e.getMessage());
                problema[0] = true;
            }
        }
        terminaSeccion("visitas", cantVisitas);

//        MainActivity.myAppDB.myDao().deleteAnexos();
        iniciaSeccion();
        int cantAnexos = (gsonDescargas.getAnexoContratoList() != null) ? gsonDescargas.getAnexoContratoList().size() : 0;
        if (gsonDescargas.getAnexoContratoList() != null && !gsonDescargas.getAnexoContratoList().isEmpty()) {
            try {
                MainActivity.myAppDB.myDao().insertAnexo(gsonDescargas.getAnexoContratoList());
            } catch (SQLiteException e) {
                Log.e("SQLITE", e.getMessage());
                problema[0] = true;
            }
        }
        terminaSeccion("anexos", cantAnexos);


//        MainActivity.myAppDB.myDao().deleteQuotation();
        if (gsonDescargas.getQuotations() != null && !gsonDescargas.getQuotations().isEmpty()) {
            try {
                MainActivity.myAppDB.myDao().insertQuotation(gsonDescargas.getQuotations());
            } catch (SQLiteException e) {
                problema[0] = true;
                Log.e("SQLITE", e.getMessage());
            }
        }

//        MainActivity.myAppDB.myDao().deleteCliPCM();
        if (gsonDescargas.getCli_pcms() != null && !gsonDescargas.getCli_pcms().isEmpty()) {
            try {
                MainActivity.myAppDB.myDao().insertPCM(gsonDescargas.getCli_pcms());
            } catch (SQLiteException e) {
                Log.e("SQLITE", e.getMessage());
                problema[0] = true;
            }
        }

//        MainActivity.myAppDB.myDao().deleteFichas();
        iniciaSeccion();
        int cantFichas = (gsonDescargas.getFichasList() != null) ? gsonDescargas.getFichasList().size() : 0;
        if (gsonDescargas.getFichasList() != null && !gsonDescargas.getFichasList().isEmpty()) {
            try {

                MainActivity.myAppDB.myDao().insertFicha(gsonDescargas.getFichasList());
                Config config = MainActivity.myAppDB.myDao().getConfig();
                if (config != null) {
                    for (FichasNew ln : gsonDescargas.getFichasList()) {
                        MainActivity.myAppDB.myDao().updateFotosFichas(ln.getId_ficha(), ln.getId_ficha_local_ficha(), config.getId());
                    }
                }

            } catch (SQLiteException e) {
                problema[0] = true;
                Log.e("SQLITE", e.getMessage());
            }
        }
        terminaSeccion("fichas", cantFichas);

//        MainActivity.myAppDB.myDao().deleteProspectos();
        iniciaSeccion();
        int cantProspectos = (gsonDescargas.getProspectosList() != null) ? gsonDescargas.getProspectosList().size() : 0;
        if (gsonDescargas.getProspectosList() != null && !gsonDescargas.getProspectosList().isEmpty()) {
            try {
                MainActivity.myAppDB.myDao().insertProsectos(gsonDescargas.getProspectosList());
            } catch (SQLiteException e) {
                problema[0] = true;
                Log.e("SQLITE", e.getMessage());
            }
        }
        terminaSeccion("prospectos", cantProspectos);

        iniciaSeccion();
        try {

            if (gsonDescargas.getPred_agr_temp() != null && !gsonDescargas.getPred_agr_temp().isEmpty()) {
                MainActivity.myAppDB.myDao().insertAgriPredTemp(gsonDescargas.getPred_agr_temp());
            }

            if (gsonDescargas.getUsuarios() != null && !gsonDescargas.getUsuarios().isEmpty()) {
                MainActivity.myAppDB.myDao().setUsuarios(gsonDescargas.getUsuarios());
            }


            if (gsonDescargas.getArray_muestra_humedad() != null && !gsonDescargas.getArray_muestra_humedad().isEmpty()) {
                for (MuestraHumedad mh : gsonDescargas.getArray_muestra_humedad()) {
                    MuestraHumedad m = MainActivity.myAppDB.DaoMuestraHumedad().getMuestraByClaveUnica(mh.clave_unica_muestra);
                    if (m != null) {
                        MainActivity.myAppDB.DaoMuestraHumedad().deleteMuestraHumedad(m);
                    }
                }
                MainActivity.myAppDB.DaoMuestraHumedad().insertMuestraHumedad(gsonDescargas.getArray_muestra_humedad());
            }


            if (gsonDescargas.getTemporadas() != null && !gsonDescargas.getTemporadas().isEmpty()) {
                MainActivity.myAppDB.myDao().insertTemporada(gsonDescargas.getTemporadas());
            }

            if (gsonDescargas.getCropRotations() != null && !gsonDescargas.getCropRotations().isEmpty()) {
                MainActivity.myAppDB.myDao().insertCrop(gsonDescargas.getCropRotations());
            }

            if (gsonDescargas.getAgricultorList() != null && !gsonDescargas.getAgricultorList().isEmpty()) {
                MainActivity.myAppDB.myDao().insertAgricultor(gsonDescargas.getAgricultorList());
            }


            if (gsonDescargas.getRegionList() != null && !gsonDescargas.getRegionList().isEmpty()) {
                MainActivity.myAppDB.myDao().insertRegiones(gsonDescargas.getRegionList());
            }

            if (gsonDescargas.getEspecieList() != null && !gsonDescargas.getEspecieList().isEmpty()) {
                MainActivity.myAppDB.myDao().insertEspecie(gsonDescargas.getEspecieList());
            }


            if (gsonDescargas.getClientes() != null && !gsonDescargas.getClientes().isEmpty()) {
                MainActivity.myAppDB.myDao().insertClientes(gsonDescargas.getClientes());
            }

            if (gsonDescargas.getFichaMaquinarias() != null && !gsonDescargas.getFichaMaquinarias().isEmpty()) {
                MainActivity.myAppDB.myDao().insertFichaMaquinaria(gsonDescargas.getFichaMaquinarias());
            }

            if (gsonDescargas.getTipoTenenciaTerrenos() != null && !gsonDescargas.getTipoTenenciaTerrenos().isEmpty()) {
                MainActivity.myAppDB.myDao().insertTipoTenenciaTerreno(gsonDescargas.getTipoTenenciaTerrenos());
            }

            if (gsonDescargas.getTipoTenenciaMaquinarias() != null && !gsonDescargas.getTipoTenenciaMaquinarias().isEmpty()) {
                MainActivity.myAppDB.myDao().insertTipoTenenciaMaquinaria(gsonDescargas.getTipoTenenciaMaquinarias());
            }

            if (gsonDescargas.getMaquinarias() != null && !gsonDescargas.getMaquinarias().isEmpty()) {
                MainActivity.myAppDB.myDao().insertMaquinara(gsonDescargas.getMaquinarias());
            }

            if (gsonDescargas.getTipoSuelos() != null && !gsonDescargas.getTipoSuelos().isEmpty()) {
                MainActivity.myAppDB.myDao().insertTipoSuelo(gsonDescargas.getTipoSuelos());
            }

            if (gsonDescargas.getTipoRiegos() != null && !gsonDescargas.getTipoRiegos().isEmpty()) {
                MainActivity.myAppDB.myDao().insertTipoRiego(gsonDescargas.getTipoRiegos());
            }

            if (gsonDescargas.getUnidadMedidas() != null && !gsonDescargas.getUnidadMedidas().isEmpty()) {
                MainActivity.myAppDB.myDao().insertUM(gsonDescargas.getUnidadMedidas());
            }

            if (gsonDescargas.getComunaList() != null && !gsonDescargas.getComunaList().isEmpty()) {
                MainActivity.myAppDB.myDao().insertComunas(gsonDescargas.getComunaList());
            }

            if (gsonDescargas.getProvinciaList() != null && !gsonDescargas.getProvinciaList().isEmpty()) {
                MainActivity.myAppDB.myDao().insertProvincias(gsonDescargas.getProvinciaList());
            }

            if (gsonDescargas.getLotes() != null && !gsonDescargas.getLotes().isEmpty()) {
                MainActivity.myAppDB.myDao().insertLotes(gsonDescargas.getLotes());
            }

            if (gsonDescargas.getPredios() != null && !gsonDescargas.getPredios().isEmpty()) {
                MainActivity.myAppDB.myDao().insertPredios(gsonDescargas.getPredios());
            }

            if (gsonDescargas.getVariedadList() != null && !gsonDescargas.getVariedadList().isEmpty()) {
                MainActivity.myAppDB.myDao().insertVariedad(gsonDescargas.getVariedadList());
            }


        } catch (SQLiteException ignored) {
            problema[0] = true;
        }
        terminaSeccion("catalogos_varios", 1);


        Config config = MainActivity.myAppDB.myDao().getConfig();

        if (config != null) {
            config.setHoraDescarga(Utilidades.hora());
            MainActivity.myAppDB.myDao().updateConfig(config);
        }


        return problema;
    }
}
