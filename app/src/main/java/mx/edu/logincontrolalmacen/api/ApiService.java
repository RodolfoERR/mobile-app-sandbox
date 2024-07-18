// ApiService.java
package mx.edu.logincontrolalmacen.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {
    @POST("v1/users/log-in")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @GET("v1/refactions/all-minus")
    Call<ApiResponse> searchParts(@Query("name") String name);
}


