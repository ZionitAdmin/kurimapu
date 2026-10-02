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
        sb.append("Hilo: ").append(hilo != null ? hilo.getName() : "?").append("\n\n");
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

    // Si hay un cierre inesperado guardado, lo muestra para copiarlo/compartirlo. Se queda guardado hasta que se
    // presione "Borrar y cerrar", por si hace falta verlo de nuevo.
    public static void mostrarSiHay(final Activity activity) {
        try {
            if (activity == null || activity.isFinishing()) return;
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

            ScrollView scroll = new ScrollView(activity);
            scroll.addView(texto);

            new AlertDialog.Builder(activity)
                    .setTitle("La app se cerro inesperadamente")
                    .setMessage("Este es el detalle del error. Compartelo para poder corregirlo.")
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
                    .setNegativeButton("Borrar y cerrar", (d, w) -> borrar(activity))
                    .show();
        } catch (Throwable ignorada) {
            // mostrar el error nunca debe tumbar la app
        }
    }
}
