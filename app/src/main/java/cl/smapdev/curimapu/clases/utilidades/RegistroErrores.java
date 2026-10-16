package cl.smapdev.curimapu.clases.utilidades;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.util.TypedValue;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

// TICKET 2515 - 2026-10-02: registro de cierres inesperados de la APK.
// Cuando la app se cae por una excepcion no controlada, guarda el detalle tecnico (fecha, version, telefono y
// la traza completa con la linea exacta) en un archivo interno de la app (no pide permisos), y despues deja que
// Android cierre la app igual que siempre. La proxima vez que se abre, muestra ese detalle para copiarlo o
// compartirlo (WhatsApp, correo, etc.). Todo va protegido: si el registro mismo fallara, se ignora y la app se
// comporta exactamente como antes. No modifica la base de datos ni ninguna pantalla.
public class RegistroErrores {

    private static final String ARCHIVO = "registro_errores_apk.txt";
    private static final long MAX_BYTES = 200 * 1024;
    private static final int MAX_CARACTERES_VISTA = 15000;
    private static boolean instalado = false;

    public static synchronized void instalar(Context context) {
        if (instalado) return;
        try {
            final Context app = context.getApplicationContext();
            final Thread.UncaughtExceptionHandler anterior = Thread.getDefaultUncaughtExceptionHandler();
            Thread.setDefaultUncaughtExceptionHandler((hilo, error) -> {
                try {
                    guardar(app, hilo, error);
                } catch (Throwable ignorada) {
                    // el registro nunca debe agregar un segundo error
                }
                if (anterior != null) {
                    anterior.uncaughtException(hilo, error);
                }
            });
            instalado = true;
        } catch (Throwable ignorada) {
        }
    }

    private static void guardar(Context app, Thread hilo, Throwable error) throws Exception {
        File archivo = new File(app.getFilesDir(), ARCHIVO);
        if (archivo.exists() && archivo.length() > MAX_BYTES) {
            archivo.delete();
        }

        String version = "?";
        try {
            PackageInfo info = app.getPackageManager().getPackageInfo(app.getPackageName(), 0);
            version = info.versionName + " (" + info.versionCode + ")";
        } catch (Throwable ignorada) {
        }

        StringWriter traza = new StringWriter();
        PrintWriter pw = new PrintWriter(traza);
        error.printStackTrace(pw);
        pw.flush();

        StringBuilder sb = new StringBuilder();
        sb.append("==================== CIERRE INESPERADO ====================\n");
        sb.append("Fecha: ").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ROOT).format(new Date())).append("\n");
        sb.append("App: ").append(version).append("\n");
        sb.append("Telefono: ").append(Build.MANUFACTURER).append(" ").append(Build.MODEL)
                .append(" | Android ").append(Build.VERSION.RELEASE).append(" (API ").append(Build.VERSION.SDK_INT).append(")\n");
        sb.append("Hilo: ").append(hilo != null ? hilo.getName() : "?").append("\n");
        // TICKET 2515 (extra) - 2026-10-16: datos de instalacion y cierres recientes del sistema (solo informativo)
        sb.append(datosInstalacion(app)).append("\n");
        sb.append(contextoCierresDelSistema(app)).append("\n");
        sb.append(traza.toString()).append("\n");

        FileOutputStream out = new FileOutputStream(archivo, true);
        try {
            out.write(sb.toString().getBytes("UTF-8"));
            out.flush();
        } finally {
            out.close();
        }
    }

    private static String leer(Context context) {
        try {
            File archivo = new File(context.getFilesDir(), ARCHIVO);
            if (!archivo.exists() || archivo.length() == 0) return null;
            byte[] datos = new byte[(int) archivo.length()];
            FileInputStream in = new FileInputStream(archivo);
            try {
                int leidos = 0;
                while (leidos < datos.length) {
                    int n = in.read(datos, leidos, datos.length - leidos);
                    if (n < 0) break;
                    leidos += n;
                }
            } finally {
                in.close();
            }
            return new String(datos, "UTF-8");
        } catch (Throwable e) {
            return null;
        }
    }

    private static void borrar(Context context) {
        try {
            new File(context.getFilesDir(), ARCHIVO).delete();
        } catch (Throwable ignorada) {
        }
    }

    // TICKET 2515 (extra) - 2026-10-16: dice si la app es una instalacion limpia o una actualizacion encima de otra version
    // (Android guarda la fecha de primera instalacion y la de ultima actualizacion; si son distintas se actualizo).
    private static String datosInstalacion(Context app) {
        try {
            PackageInfo info = app.getPackageManager().getPackageInfo(app.getPackageName(), 0);
            SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ROOT);
            boolean actualizada = Math.abs(info.lastUpdateTime - info.firstInstallTime) > 2000;
            return "Instalada: " + formato.format(new Date(info.firstInstallTime))
                    + " | Ultima actualizacion: " + formato.format(new Date(info.lastUpdateTime))
                    + " (" + (actualizada ? "ACTUALIZADA sobre una version anterior" : "instalacion limpia, sin actualizaciones") + ")";
        } catch (Throwable ignorada) {
            return "Instalada: ?";
        }
    }

    // TICKET 2515 (extra) - 2026-10-16: MOTIVO DE MUERTE DE LA APP (Android 11 o superior). Al abrir la app se le pregunta a
    // Android como termino la vez anterior:
    //  - congelada (ANR) o cierre nativo: nunca es normal -> se anota como "error no recuperado" y se muestra el cuadro
    //    con Compartir / Copiar al abrir.
    //  - poca memoria o senal de cierre del sistema (puede pasar normalmente en segundo plano, por ejemplo al abrir la
    //    camara): NO se avisa; solo queda una nota en un archivo aparte que se adjunta a un informe si despues ocurre un error.
    //  - cierre normal, error de programacion (ya lo atrapa el registro de arriba) u otro: se ignora.
    // La primera vez que corre solo marca el punto de partida (no reporta el historial viejo). Todo va protegido.
    private static final String ARCHIVO_CIERRES = "registro_cierres_sistema_apk.txt";
    private static final String PREFS_CIERRES = "registro_errores_prefs";
    private static final String PREF_ULTIMA_MUERTE = "ultima_muerte_ts";
    private static final int MAX_NOTAS = 5;

    public static void registrarCierresDelSistema(Context context) {
        try {
            if (context == null || Build.VERSION.SDK_INT < Build.VERSION_CODES.R) return;
            registrarCierresDelSistemaR(context.getApplicationContext());
        } catch (Throwable ignorada) {
            // leer el historial nunca debe tumbar la app
        }
    }

    @androidx.annotation.RequiresApi(api = Build.VERSION_CODES.R)
    private static void registrarCierresDelSistemaR(Context app) {
        android.app.ActivityManager am = (android.app.ActivityManager) app.getSystemService(Context.ACTIVITY_SERVICE);
        if (am == null) return;
        java.util.List<android.app.ApplicationExitInfo> lista = am.getHistoricalProcessExitReasons(app.getPackageName(), 0, 10);
        if (lista == null || lista.isEmpty()) return;

        android.content.SharedPreferences prefs = app.getSharedPreferences(PREFS_CIERRES, Context.MODE_PRIVATE);
        long anterior = prefs.getLong(PREF_ULTIMA_MUERTE, -1L);
        long mayor = anterior;
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ROOT);

        for (int i = lista.size() - 1; i >= 0; i--) { // del mas antiguo al mas nuevo
            android.app.ApplicationExitInfo info = lista.get(i);
            long ts = info.getTimestamp();
            if (ts > mayor) mayor = ts;
            if (anterior < 0 || ts <= anterior) continue; // primera vez o ya revisado

            String motivo;
            boolean avisar;
            switch (info.getReason()) {
                case android.app.ApplicationExitInfo.REASON_ANR:
                    motivo = "la app se congelo (ANR) y el sistema la cerro";
                    avisar = true;
                    break;
                case android.app.ApplicationExitInfo.REASON_CRASH_NATIVE:
                    motivo = "cierre nativo (fallo interno del sistema/librerias)";
                    avisar = true;
                    break;
                case android.app.ApplicationExitInfo.REASON_LOW_MEMORY:
                    motivo = "el sistema la cerro por poca memoria";
                    avisar = false;
                    break;
                case android.app.ApplicationExitInfo.REASON_SIGNALED:
                    motivo = "el sistema la cerro con una senal de cierre";
                    avisar = false;
                    break;
                default:
                    continue; // cierre normal, error ya registrado u otro motivo: no se informa
            }

            String donde = info.getImportance() <= android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE
                    ? "con la app en uso" : "con la app en segundo plano";
            String detalle = formato.format(new Date(ts)) + " | " + motivo + " | " + donde
                    + (info.getDescription() != null && !info.getDescription().trim().isEmpty() ? " | " + info.getDescription() : "");

            if (avisar) {
                guardarIncidente(app, "El sistema cerro la app: " + detalle, null, "ERROR NO RECUPERADO (el sistema cerro la app)");
            } else {
                anotarCierre(app, detalle);
            }
        }

        if (mayor != anterior) {
            prefs.edit().putLong(PREF_ULTIMA_MUERTE, mayor).apply();
        }
    }

    // deja una nota (maximo MAX_NOTAS, las mas nuevas) en un archivo aparte; NO activa ningun aviso
    private static void anotarCierre(Context app, String detalle) {
        try {
            File archivo = new File(app.getFilesDir(), ARCHIVO_CIERRES);
            java.util.ArrayList<String> notas = new java.util.ArrayList<>();
            String actual = leerArchivo(archivo);
            if (actual != null) {
                for (String linea : actual.split("\n")) {
                    if (!linea.trim().isEmpty()) notas.add(linea);
                }
            }
            notas.add(detalle);
            while (notas.size() > MAX_NOTAS) notas.remove(0);

            StringBuilder sb = new StringBuilder();
            for (String nota : notas) sb.append(nota).append("\n");
            FileOutputStream out = new FileOutputStream(archivo, false);
            try {
                out.write(sb.toString().getBytes("UTF-8"));
                out.flush();
            } finally {
                out.close();
            }
        } catch (Throwable ignorada) {
        }
    }

    // texto con las notas de cierres recientes del sistema, para adjuntar a un informe ("" si no hay)
    private static String contextoCierresDelSistema(Context app) {
        try {
            String notas = leerArchivo(new File(app.getFilesDir(), ARCHIVO_CIERRES));
            if (notas == null || notas.trim().isEmpty()) return "";
            return "Cierres recientes hechos por el sistema (informativo, no son errores por si solos):\n" + notas.trim() + "\n";
        } catch (Throwable ignorada) {
            return "";
        }
    }

    private static String leerArchivo(File archivo) {
        try {
            if (!archivo.exists() || archivo.length() == 0) return null;
            byte[] datos = new byte[(int) archivo.length()];
            FileInputStream in = new FileInputStream(archivo);
            try {
                int leidos = 0;
                while (leidos < datos.length) {
                    int n = in.read(datos, leidos, datos.length - leidos);
                    if (n < 0) break;
                    leidos += n;
                }
            } finally {
                in.close();
            }
            return new String(datos, "UTF-8");
        } catch (Throwable e) {
            return null;
        }
    }

    // Si hay un cierre inesperado guardado, lo muestra para copiarlo/compartirlo. Se queda guardado hasta que se
    // presione "Borrar y cerrar", por si hace falta verlo de nuevo.
    public static void mostrarSiHay(final Activity activity) {
        // TICKET 2515 (extra) - 2026-10-16: si lo ultimo guardado es un error NO RECUPERADO (la app no se cerro) el cuadro
        // de inicio usa el mismo titulo que el aviso inmediato, para no decir "se cerro" cuando no fue asi.
        String contenido = (activity != null) ? leer(activity) : null;
        if (contenido != null && ultimoRegistroEsNoRecuperado(contenido)) {
            mostrarDialogo(activity, "Ocurrio un problema",
                    "Este es el detalle de un problema que no se pudo recuperar. Compartelo para poder corregirlo.", false, false);
            return;
        }
        mostrarDialogo(activity, "La app se cerro inesperadamente",
                "Este es el detalle del error. Compartelo para poder corregirlo.", true, false);
    }

    // el ultimo bloque del archivo empieza con "==================== <TITULO> ===..." (20 signos igual y un espacio)
    private static boolean ultimoRegistroEsNoRecuperado(String contenido) {
        int i = contenido.lastIndexOf("==================== ");
        return i >= 0 && contenido.startsWith("ERROR NO RECUPERADO", i + 21);
    }

    // TICKET 2515 (extra) - 2026-10-16: ERROR QUE NO SE PUDO RECUPERAR (la app NO se cerro). Lo anota en el mismo archivo
    // de cierres y lo muestra de inmediato en pantalla, con los mismos botones Compartir / Copiar, para que el usuario
    // sepa que tuvo un problema y pueda enviarlo. Solo se usa cuando no hay forma de seguir (si la app se recupera sola,
    // no se muestra nada). Se puede llamar desde cualquier hilo.
    public static void informarErrorNoRecuperado(final Activity activity, final String queOcurrio, final Throwable error) {
        try {
            if (activity == null) return;
            guardarIncidente(activity.getApplicationContext(), queOcurrio, error);
            activity.runOnUiThread(() -> mostrarDialogo(activity, "Ocurrio un problema",
                    queOcurrio + "\n\nComparte este detalle para poder corregirlo.", false, true));
        } catch (Throwable ignorada) {
            // informar un error nunca debe tumbar la app
        }
    }

    private static void guardarIncidente(Context app, String queOcurrio, Throwable error) {
        guardarIncidente(app, queOcurrio, error, "ERROR NO RECUPERADO (la app no se cerro)");
    }

    // el titulo debe empezar con "ERROR NO RECUPERADO" para que el cuadro de inicio use el titulo "Ocurrio un problema"
    private static void guardarIncidente(Context app, String queOcurrio, Throwable error, String tituloBloque) {
        try {
            File archivo = new File(app.getFilesDir(), ARCHIVO);
            if (archivo.exists() && archivo.length() > MAX_BYTES) {
                archivo.delete();
            }

            String version = "?";
            try {
                PackageInfo info = app.getPackageManager().getPackageInfo(app.getPackageName(), 0);
                version = info.versionName + " (" + info.versionCode + ")";
            } catch (Throwable ignorada) {
            }

            StringBuilder sb = new StringBuilder();
            sb.append("==================== ").append(tituloBloque).append(" ====================\n");
            sb.append("Fecha: ").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ROOT).format(new Date())).append("\n");
            sb.append("App: ").append(version).append("\n");
            sb.append("Telefono: ").append(Build.MANUFACTURER).append(" ").append(Build.MODEL)
                    .append(" | Android ").append(Build.VERSION.RELEASE).append(" (API ").append(Build.VERSION.SDK_INT).append(")\n");
            sb.append(datosInstalacion(app)).append("\n");
            sb.append("Que ocurrio: ").append(queOcurrio).append("\n");
            sb.append(contextoCierresDelSistema(app)).append("\n");
            if (error != null) {
                StringWriter traza = new StringWriter();
                PrintWriter pw = new PrintWriter(traza);
                error.printStackTrace(pw);
                pw.flush();
                sb.append("\n").append(traza.toString());
            }
            sb.append("\n");

            FileOutputStream out = new FileOutputStream(archivo, true);
            try {
                out.write(sb.toString().getBytes("UTF-8"));
                out.flush();
            } finally {
                out.close();
            }
        } catch (Throwable ignorada) {
            // si no se puede escribir el archivo igual se muestra el aviso en pantalla
        }
    }

    // Muestra el contenido del archivo de errores en un cuadro con Compartir / Copiar. Si cierreConBorrado es true el
    // boton negativo dice "Borrar y cerrar" (cierre inesperado, se puede salir tocando fuera); si es false (error no
    // recuperado) dice "Cerrar", el cuadro solo se cierra con el boton y tambien limpia el archivo para que no vuelva a salir.
    // TICKET 2515 (extra) - 2026-10-16: nunca hay dos cuadros a la vez. Si ya hay uno mostrandose: el de inicio no se repite
    // y el aviso inmediato de un error no recuperado lo REEMPLAZA (se vuelve a leer el archivo, que ya trae el error nuevo).
    private static AlertDialog dialogoMostrado = null;

    private static void mostrarDialogo(final Activity activity, final String titulo, final String mensaje, final boolean cancelable, final boolean reemplazar) {
        try {
            if (activity == null || activity.isFinishing()) return;
            if (dialogoMostrado != null && dialogoMostrado.isShowing()) {
                if (!reemplazar) return;
                dialogoMostrado.dismiss();
            }
            final String completo = leer(activity);
            if (completo == null || completo.trim().isEmpty()) return;

            // en pantalla solo lo mas reciente (el final del archivo); al compartir/copiar va todo el ultimo tramo
            final String recorte = completo.length() > MAX_CARACTERES_VISTA
                    ? "...\n" + completo.substring(completo.length() - MAX_CARACTERES_VISTA)
                    : completo;

            TextView texto = new TextView(activity);
            texto.setText(recorte);
            texto.setTextIsSelectable(true);
            texto.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
            texto.setTypeface(android.graphics.Typeface.MONOSPACE);
            int pad = (int) (12 * activity.getResources().getDisplayMetrics().density);
            texto.setPadding(pad, pad, pad, pad);

            // TICKET 2515 (extra) - 2026-10-16: el area de texto se limita al 45% del alto de la pantalla. Sin esto la traza
            // larga ocupaba TODO el cuadro y los botones Compartir / Copiar / Cerrar quedaban fuera de la pantalla (el
            // usuario no podia tocarlos). Si el texto es mas largo, se desplaza dentro de esa area.
            ScrollView scroll = new ScrollView(activity) {
                @Override
                protected void onMeasure(int anchoSpec, int altoSpec) {
                    int maximo = (int) (getResources().getDisplayMetrics().heightPixels * 0.45f);
                    super.onMeasure(anchoSpec, android.view.View.MeasureSpec.makeMeasureSpec(maximo, android.view.View.MeasureSpec.AT_MOST));
                }
            };
            scroll.addView(texto);

            dialogoMostrado = new AlertDialog.Builder(activity)
                    .setTitle(titulo)
                    .setMessage(mensaje)
                    .setCancelable(cancelable)
                    .setView(scroll)
                    .setPositiveButton("Compartir", (d, w) -> {
                        try {
                            Intent envio = new Intent(Intent.ACTION_SEND);
                            envio.setType("text/plain");
                            envio.putExtra(Intent.EXTRA_SUBJECT, "Error APK Curimapu");
                            envio.putExtra(Intent.EXTRA_TEXT, recorte);
                            activity.startActivity(Intent.createChooser(envio, "Compartir error"));
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                    })
                    .setNeutralButton("Copiar", (d, w) -> {
                        try {
                            ClipboardManager cm = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                            if (cm != null) {
                                cm.setPrimaryClip(ClipData.newPlainText("Error APK Curimapu", recorte));
                                Toast.makeText(activity, "Error copiado", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                    })
                    .setNegativeButton(cancelable ? "Borrar y cerrar" : "Cerrar", (d, w) -> borrar(activity))
                    .show();
        } catch (Throwable ignorada) {
            // mostrar el error nunca debe tumbar la app
        }
    }
}
