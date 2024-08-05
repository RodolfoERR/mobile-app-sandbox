package mx.edu.logincontrolalmacen.api;

public class PartDetailResponse {
    private String message;
    private PartDetail data;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public PartDetail getData() {
        return data;
    }

    public void setData(PartDetail data) {
        this.data = data;
    }
}
