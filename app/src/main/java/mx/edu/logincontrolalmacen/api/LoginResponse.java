package mx.edu.logincontrolalmacen.api;

public class LoginResponse {
    private boolean success;
    private String message;
    private String token;  // Asegúrate de que coincida con la respuesta de tu API

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getToken() {
        return token;
    }
}
