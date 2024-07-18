package mx.edu.logincontrolalmacen.api;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ApiResponse {
    @SerializedName("message")
    private String message;
    @SerializedName("data")
    private List<Part> data;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Part> getData() {
        return data;
    }

    public void setData(List<Part> data) {
        this.data = data;
    }
}

