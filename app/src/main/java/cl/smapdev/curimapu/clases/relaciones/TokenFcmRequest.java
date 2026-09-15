package cl.smapdev.curimapu.clases.relaciones;

import com.google.gson.annotations.SerializedName;

public class TokenFcmRequest {

    @SerializedName("idUsuario")
    private int idUsuario;

    @SerializedName("token")
    private String token;

    public TokenFcmRequest(int idUsuario, String token) {
        this.idUsuario = idUsuario;
        this.token = token;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
