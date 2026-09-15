package cl.smapdev.curimapu.clases.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessaging;

import cl.smapdev.curimapu.clases.relaciones.Respuesta;
import cl.smapdev.curimapu.clases.relaciones.TokenFcmRequest;
import cl.smapdev.curimapu.clases.retrofit.ApiService;
import cl.smapdev.curimapu.clases.retrofit.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FcmTokenHelper {

    private static final String TAG = "FcmTokenHelper";

    /**
     * Pide el token FCM del dispositivo y lo asocia al usuario en el servidor.
     */
    public static void registrarTokenDelUsuario(String servidor, int idUsuario) {
        // Cualquier falla de Firebase (mala inicialización, sin Google Play Services, etc.)
        // debe quedar aislada acá y nunca interrumpir el flujo de login.
        try {
            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Log.w(TAG, "No se pudo obtener el token FCM", task.getException());
                    return;
                }
                String token = task.getResult();
                enviarToken(servidor, idUsuario, token);
            });
        } catch (Exception e) {
            Log.w(TAG, "Firebase no disponible, se omite el registro del token: " + e.getMessage());
        }
    }

    static void enviarToken(String servidor, int idUsuario, String token) {
        if (token == null || token.isEmpty() || idUsuario <= 0) {
            return;
        }

        try {
            ApiService apiService = RetrofitClient.getClient(servidor).create(ApiService.class);
            Call<Respuesta> call = apiService.guardarTokenFcm(new TokenFcmRequest(idUsuario, token));
            call.enqueue(new Callback<Respuesta>() {
                @Override
                public void onResponse(@NonNull Call<Respuesta> call, @NonNull Response<Respuesta> response) {
                    if (response.body() != null && response.body().getCodigoRespuesta() != 0) {
                        Log.w(TAG, "Servidor rechazó el token FCM: " + response.body().getMensajeRespuesta());
                    } else {
                        Log.d(TAG, "Token FCM registrado para usuario " + idUsuario);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Respuesta> call, @NonNull Throwable t) {
                    Log.w(TAG, "Fallo al enviar el token FCM: " + t.getMessage());
                }
            });
        } catch (Exception e) {
            Log.w(TAG, "No se pudo enviar el token FCM: " + e.getMessage());
        }
    }
}
