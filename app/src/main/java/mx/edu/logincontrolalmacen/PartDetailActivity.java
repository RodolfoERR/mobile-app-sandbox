package mx.edu.logincontrolalmacen;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.io.IOException;

import mx.edu.logincontrolalmacen.api.ApiService;
import mx.edu.logincontrolalmacen.api.PartDetail;
import mx.edu.logincontrolalmacen.api.PartDetailResponse;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PartDetailActivity extends AppCompatActivity {

    private ApiService apiService;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_part_detail);

        // Ocultar la barra de acción si está presente
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("id")) {
            int id = intent.getIntExtra("id", 0);
            token = getIntent().getStringExtra("TOKEN");

            if (token == null || token.isEmpty()) {
                Toast.makeText(this, "No token provided", Toast.LENGTH_SHORT).show();
                return;
            }

            // Inicializa Retrofit y ApiService
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://quintaesencia.website/api/")  // Reemplaza con la URL de tu API
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(getClientWithAuthHeader(token))
                    .build();

            apiService = retrofit.create(ApiService.class);

            // Obtener los detalles de la refacción
            getPartDetails(id);
        } else {
            Toast.makeText(this, "No ID passed", Toast.LENGTH_SHORT).show();
        }
    }

    private OkHttpClient getClientWithAuthHeader(String token) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        return new OkHttpClient.Builder()
                .addInterceptor(logging)
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        Request original = chain.request();
                        Request.Builder requestBuilder = original.newBuilder()
                                .header("Authorization", "Bearer " + token)
                                .method(original.method(), original.body());
                        Request request = requestBuilder.build();
                        return chain.proceed(request);
                    }
                })
                .build();
    }

    private void getPartDetails(int id) {
        if (apiService == null) {
            Log.e("PartDetailActivity", "ApiService is null");
            return;
        }

        Call<PartDetailResponse> call = apiService.getPartDetails(id);
        call.enqueue(new Callback<PartDetailResponse>() {
            @Override
            public void onResponse(Call<PartDetailResponse> call, Response<PartDetailResponse> response) {
                Log.d("PartDetailActivity", "Response code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    PartDetail partDetail = response.body().getData();
                    if (partDetail != null) {
                        displayPartDetails(partDetail);
                    } else {
                        Log.e("PartDetailActivity", "PartDetail is null");
                        Toast.makeText(PartDetailActivity.this, "Failed to load details", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        // Imprime el cuerpo de la respuesta para depurar
                        String errorBody = response.errorBody().string();
                        Log.e("PartDetailActivity", "Error response: " + errorBody);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(PartDetailActivity.this, "Failed to load details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PartDetailResponse> call, Throwable t) {
                Log.e("PartDetailActivity", "Network error: " + t.getMessage());
                Toast.makeText(PartDetailActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayPartDetails(PartDetail partDetail) {
        TextView nameTextView = findViewById(R.id.part_name);
        TextView locationTextView = findViewById(R.id.part_location);
        TextView descriptionTextView = findViewById(R.id.part_description);
        TextView quantityTextView = findViewById(R.id.part_quantity);
        ImageView imageView = findViewById(R.id.part_image);

        nameTextView.setText(partDetail.getName());
        locationTextView.setText(partDetail.getLocation().getName());
        descriptionTextView.setText(partDetail.getDescription());
        quantityTextView.setText(String.valueOf(partDetail.getTotal_quantity()));
        Glide.with(this).load(partDetail.getImage_url()).into(imageView);
    }
}
