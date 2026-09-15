package cl.smapdev.curimapu.clases.firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import cl.smapdev.curimapu.MainActivity;
import cl.smapdev.curimapu.R;
import cl.smapdev.curimapu.clases.tablas.Config;

public class MiFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MiFirebaseMsgService";
    private static final String CHANNEL_ID = "alertas_curimapu";

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "Nuevo token FCM: " + token);

        try {
            Config config = MainActivity.myAppDB.myDao().getConfig();
            if (config != null && config.getId_usuario() > 0) {
                FcmTokenHelper.enviarToken(config.getServidorSeleccionado(), config.getId_usuario(), token);
            }
        } catch (Exception e) {
            Log.e(TAG, "No se pudo reenviar el token renovado: " + e.getMessage());
        }
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // Blindaje total: una notificación puede llegar en cualquier momento, con la app
        // en cualquier pantalla. Ninguna falla acá (payload raro, error del sistema, etc.)
        // puede tener permitido tumbar la app — en el peor caso, simplemente no se muestra.
        try {
            String titulo = "CURIMAPU";
            String cuerpo = "";

            // El backend debe mandar el mensaje como "data" (no como bloque "notification"):
            // con "notification", el sistema lo muestra solo y omite onMessageReceived()
            // cuando la app está en segundo plano o cerrada, que es justo el caso que más nos importa.
            if (remoteMessage.getData().containsKey("titulo")) {
                titulo = remoteMessage.getData().get("titulo");
            }
            if (remoteMessage.getData().containsKey("cuerpo")) {
                cuerpo = remoteMessage.getData().get("cuerpo");
            }

            // Respaldo por si en algún momento llega como "notification" en vez de "data"
            if (remoteMessage.getNotification() != null) {
                if (titulo.equals("CURIMAPU") && remoteMessage.getNotification().getTitle() != null) {
                    titulo = remoteMessage.getNotification().getTitle();
                }
                if (cuerpo.isEmpty() && remoteMessage.getNotification().getBody() != null) {
                    cuerpo = remoteMessage.getNotification().getBody();
                }
            }

            mostrarNotificacion(titulo, cuerpo);
        } catch (Exception e) {
            Log.e(TAG, "No se pudo procesar/mostrar la notificación entrante: " + e.getMessage());
        }
    }

    private void mostrarNotificacion(String titulo, String cuerpo) {
        try {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

            int flags = PendingIntent.FLAG_UPDATE_CURRENT;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                flags |= PendingIntent.FLAG_IMMUTABLE;
            }
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, flags);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager == null) {
                Log.w(TAG, "NotificationManager no disponible, se omite la notificación");
                return;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(
                        CHANNEL_ID,
                        "Alertas Curimapu",
                        NotificationManager.IMPORTANCE_HIGH);
                channel.setDescription("Avisos de vencimientos y recomendaciones");
                notificationManager.createNotificationChannel(channel);
            }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(titulo)
                    .setContentText(cuerpo)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(cuerpo))
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent);

            notificationManager.notify((int) System.currentTimeMillis(), builder.build());
        } catch (Exception e) {
            Log.e(TAG, "Fallo al construir/mostrar la notificación: " + e.getMessage());
        }
    }
}
